package com.nway.spring.jdbc.bean;

import com.nway.spring.jdbc.annotation.Column;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultBeanProcessor implements BeanProcessor {

    private static final ConcurrentMap<Class, BeanPropertyRowMapper> localCache = new ConcurrentHashMap<>(256);

    @Override
    public <T> List<T> toBeanList(ResultSet rs, Class<T> mappedClass) throws SQLException {

        int rowNum = 0;
        Map<String, Integer> columnIndex = getColumnIndex(rs);

        BeanPropertyRowMapper<T> mapper = Optional.ofNullable(localCache.get(mappedClass))
                .orElseGet(() -> {
                    BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<>(mappedClass);
                    localCache.put(mappedClass, rowMapper);
                    return rowMapper;
                }).setColumnIndexMap(columnIndex);

        final List<T> results = new ArrayList<>();
        while (rs.next()) {
            results.add(mapper.mapRow(rs, rowNum++));
        }
        return results;
    }

    @Override
    public <T> T toBean(ResultSet rs, Class<T> mappedClass) throws SQLException {

        Map<String, Integer> columnIndex = getColumnIndex(rs);

        BeanPropertyRowMapper<T> mapper = Optional.ofNullable(localCache.get(mappedClass))
                .orElseGet(() -> {
                    BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<>(mappedClass);
                    localCache.put(mappedClass, rowMapper);
                    return rowMapper;
                }).setColumnIndexMap(columnIndex);

        return mapper.mapRow(rs, 0);
    }

    private Map<String, Integer> getColumnIndex(ResultSet rs) throws SQLException {

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Map<String, Integer> columnIndex = new HashMap<>(columnCount);

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            columnIndex.put(column, index);
        }
        return columnIndex;
    }

    /**
     * {@link RowMapper} implementation that converts a row into a new instance
     * of the specified mapped target class. The mapped target class must be a
     * top-level class and it must have a default or no-arg constructor.
     *
     * <p>Column values are mapped based on matching the column name as obtained from result set
     * meta-data to public setters for the corresponding properties. The names are matched either
     * directly or by transforming a name separating the parts with underscores to the same name
     * using "camel" case.
     *
     * <p>Mapping is provided for fields in the target class for many common types, e.g.:
     * String, boolean, Boolean, byte, Byte, short, Short, int, Integer, long, Long,
     * float, Float, double, Double, BigDecimal, {@code java.util.Date}, etc.
     *
     * <p>To facilitate mapping between columns and fields that don't have matching names,
     * try using column aliases in the SQL statement like "select fname as first_name from customer".
     *
     * <p>For 'null' values read from the database, we will attempt to call the setter, but in the case of
     * Java primitives, this causes a TypeMismatchException. This class can be configured (using the
     * primitivesDefaultedForNullValue property) to trap this exception and use the primitives default value.
     * Be aware that if you use the values from the generated bean to update the database the primitive value
     * will have been set to the primitive's default value instead of null.
     *
     * <p>Please note that this class is designed to provide convenience rather than high performance.
     * For best performance, consider using a custom {@link RowMapper} implementation.
     *
     * <p>
     * 注：本类使用了{@link org.springframework.jdbc.core.BeanPropertyRowMapper}
     * 源码，新增了通过 {@link com.nway.spring.jdbc.annotation.Column}自定义表字段与bean属性的映射
     *
     * @param <T> the result type
     * @author Thomas Risberg
     * @author Juergen Hoeller
     * @author zdtjss@163.com
     * @see DataClassRowMapper
     * @since 2.5
     */
    static class BeanPropertyRowMapper<T> implements RowMapper<T> {

        /**
         * Logger available to subclasses.
         */
        protected final Log logger = LogFactory.getLog(getClass());

        /**
         * The class we are mapping to.
         */
        @Nullable
        private Class<T> mappedClass;

        /**
         * Map of the fields we provide mapping for.
         */
        @Nullable
        private Map<String, Field> mappedFields;

        /**
         * Set of bean properties we provide mapping for.
         */
        @Nullable
        private Set<String> mappedProperties;

        private Map<String, Integer> columnIndexMap;

        /**
         * Create a new {@code BeanPropertyRowMapper} for bean-style configuration.
         *
         * @see #setMappedClass
         */
        public BeanPropertyRowMapper() {
        }

        /**
         * Create a new {@code BeanPropertyRowMapper}, accepting unpopulated
         * properties in the target bean.
         *
         * @param mappedClass the class that each row should be mapped to
         */
        public BeanPropertyRowMapper(Class<T> mappedClass) {
            initialize(mappedClass);
        }

        public BeanPropertyRowMapper<T> setColumnIndexMap(Map<String, Integer> columnIndexMap) {
            this.columnIndexMap = columnIndexMap;
            return this;
        }

        /**
         * Set the class that each row should be mapped to.
         */
        public void setMappedClass(Class<T> mappedClass) {
            if (this.mappedClass == null) {
                initialize(mappedClass);
            } else {
                if (this.mappedClass != mappedClass) {
                    throw new InvalidDataAccessApiUsageException("The mapped class can not be reassigned to map to " +
                            mappedClass + " since it is already providing mapping for " + this.mappedClass);
                }
            }
        }

        /**
         * Initialize the mapping meta-data for the given class.
         *
         * @param mappedClass the mapped class
         */
        protected void initialize(Class<T> mappedClass) {
            this.mappedClass = mappedClass;
            this.mappedFields = new HashMap<>();
            this.mappedProperties = new HashSet<>();

            for (Field field : mappedClass.getDeclaredFields()) {
                field.setAccessible(true);
                this.mappedFields.put(annotationName(field), field);
                this.mappedProperties.add(field.getName());
            }
        }

        /**
         * Convert a name in camelCase to an underscored name in lower case.
         * Any upper case letters are converted to lower case with a preceding underscore.
         *
         * @param name the original name
         * @return the converted name
         * @since 4.2
         */
        protected String underscoreName(String name) {
            if (!StringUtils.hasLength(name)) {
                return "";
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (Character.isUpperCase(c)) {
                    result.append('_').append(Character.toLowerCase(c));
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        }

        private String annotationName(Field field) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.value().length() == 0 ? columnAnnotation.name() : columnAnnotation.value();
                if (!(columnName == null || columnName.length() == 0)) {
                    return columnName;
                }
            }
            return underscoreName(field.getName());
        }

        /**
         * Extract the values for all columns in the current row.
         * <p>Utilizes public setters and result set meta-data.
         *
         * @see java.sql.ResultSetMetaData
         */
        @Override
        public T mapRow(ResultSet rs, int rowNumber) throws SQLException {

            T mappedObject = null;
            try {
                mappedObject = mappedClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            for (Map.Entry<String, Integer> entry : columnIndexMap.entrySet()) {
                String column = entry.getKey();
                Field pd = (this.mappedFields != null ? this.mappedFields.get(column) : null);
                if (pd != null) {
                    try {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Mapping column '" + column + "' to property '" + pd.getName() +
                                    "' of type '" + ClassUtils.getQualifiedName(pd.getType()) + "' index " + entry.getValue());
                        }
                        Object value = getColumnValue(rs, entry.getValue(), pd.getType());
                        pd.set(mappedObject, value);
                    } catch (IllegalAccessException ex) {
                        throw new DataRetrievalFailureException(
                                "Unable to map column '" + column + "' to property '" + pd.getName() + "'", ex);
                    }
                } else {
                    // No PropertyDescriptor found
                    if (rowNumber == 0 && logger.isDebugEnabled()) {
                        logger.debug("No property found for column '" + column + "' mapped");
                    }
                }
            }

            return mappedObject;
        }

        /**
         * Retrieve a JDBC object value for the specified column.
         * <p>The default implementation calls
         * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}.
         * Subclasses may override this to check specific value types upfront,
         * or to post-process values return from {@code getResultSetValue}.
         *
         * @param rs        is the ResultSet holding the data
         * @param index     is the column index
         * @param paramType the target parameter type
         * @return the Object value
         * @throws SQLException in case of extraction failure
         * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)
         * @since 5.3
         */
        @Nullable
        protected Object getColumnValue(ResultSet rs, int index, Class<?> paramType) throws SQLException {
            return JdbcUtils.getResultSetValue(rs, index, paramType);
        }

    }

}
