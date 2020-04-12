package com.nway.spring.jdbc.bean;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.nway.spring.jdbc.annotation.Column;
import com.nway.spring.jdbc.util.ReflectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
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

		BeanPropertyRowMapper<T> mapper = Optional.ofNullable(localCache.get(mappedClass))
				.orElseGet(() -> {
					BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<>(mappedClass);
					localCache.put(mappedClass, rowMapper);
					return rowMapper;
				});

		final List<T> results = new ArrayList<>();

		while (rs.next()) {
			results.add(mapper.mapRow(rs));
		} 

		return results;
	}

	@Override
	public <T> T toBean(ResultSet rs, Class<T> mappedClass) throws SQLException {

		BeanPropertyRowMapper<T> mapper = Optional.ofNullable(localCache.get(mappedClass))
				.orElseGet(() -> {
					BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<>(mappedClass);
					localCache.put(mappedClass, rowMapper);
					return rowMapper;
				});

		return mapper.mapRow(rs);
	}

	/**
	 * {@link org.springframework.jdbc.core.RowMapper} implementation that
	 * converts a row into a new instance of the specified mapped target class.
	 * The mapped target class must be a top-level class and it must have a
	 * default or no-arg constructor.
	 *
	 * <p>
	 * Column values are mapped based on matching the column name as obtained
	 * from result set metadata to public setters for the corresponding
	 * properties. The names are matched either directly or by transforming a
	 * name separating the parts with underscores to the same name using "camel"
	 * case.
	 *
	 * <p>
	 * Mapping is provided for fields in the target class for many common types,
	 * e.g.: String, boolean, Boolean, byte, Byte, short, Short, int, Integer,
	 * long, Long, float, Float, double, Double, BigDecimal,
	 * {@code java.util.Date}, etc.
	 *
	 * <p>
	 * To facilitate mapping between columns and fields that don't have matching
	 * names, try using column aliases in the SQL statement like
	 * "select fname as first_name from customer".
	 *
	 * <p>
	 * For 'null' values read from the databasem, we will attempt to call the
	 * setter, but in the case of Java primitives, this causes a
	 * TypeMismatchException. This class can be configured (using the
	 * primitivesDefaultedForNullValue property) to trap this exception and use
	 * the primitives default value. Be aware that if you use the values from
	 * the generated bean to update the database the primitive value will have
	 * been set to the primitive's default value instead of null.
	 *
	 * <p>
	 * Please note that this class is designed to provide convenience rather
	 * than high performance. For best performance, consider using a custom
	 * {@link org.springframework.jdbc.core.RowMapper} implementation.
	 *
	 * <p>
	 * 注：本类使用了{@link org.springframework.jdbc.core.BeanPropertyRowMapper}
	 * 源码，新增了通过 {@link com.nway.spring.jdbc.annotation.Column}自定义表字段与bean属性的映射
	 * 
	 * @author Thomas Risberg
	 * @author Juergen Hoeller
	 * @author zdtjss@163.com
	 */
	class BeanPropertyRowMapper<T> {

		/** Logger available to subclasses. */
		protected final Log logger = LogFactory.getLog(getClass());

		/** The class we are mapping to. */
		@Nullable
		private Class<T> mappedClass;

		/** Whether we're strictly validating. */
		private boolean checkFullyPopulated = false;

		/** Whether we're defaulting primitives when mapping a null value. */
		private boolean primitivesDefaultedForNullValue = false;

		/** Map of the fields we provide mapping for. */
		@Nullable
		private Map<String, Field> mappedFields;

		private Map<String, String> writerMethodMap;

		private Map<Integer, String> columnIndexMap;

		private int columnCount;

		/** Set of bean properties we provide mapping for. */
		@Nullable
		private Set<String> mappedProperties;

		/**
		 * Create a new {@code BeanPropertyRowMapper}, accepting unpopulated
		 * properties in the target bean.
		 * which allows for specifying the mapped type once only.
		 * @param mappedClass the class that each row should be mapped to
		 */
		public BeanPropertyRowMapper(Class<T> mappedClass) {
			initialize(mappedClass);
		}

		/**
		 * Return whether we're strictly validating that all bean properties have been
		 * mapped from corresponding database fields.
		 */
		public boolean isCheckFullyPopulated() {
			return this.checkFullyPopulated;
		}

		/**
		 * Initialize the mapping meta-data for the given class.
		 * @param mappedClass the mapped class
		 */
		/**
		 * Initialize the mapping metadata for the given class.
		 *
		 * 添加了通过注解方式自定义映射表字段和类属性关系的方式
		 *
		 * @param mappedClass
		 *            the mapped class
		 */
		protected void initialize(Class<T> mappedClass) {
			this.mappedClass = mappedClass;
			this.mappedFields = new HashMap<>();
			this.mappedProperties = new HashSet<>();
			Field[] fields = ReflectionUtils.getDeclaredFields(mappedClass);
			for (Field field : fields) {
				String columnName = annotationName(field);
				if (columnName == null) {
					columnName = underscoreName(field.getName());
				}
				this.mappedFields.put(columnName, field);
				this.mappedProperties.add(field.getName());
			}

			writerMethodMap = new HashMap<>();
			PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
			for (PropertyDescriptor pd : pds) {
				if (pd.getWriteMethod() != null) {
					this.writerMethodMap.put(pd.getName(), pd.getWriteMethod().getName());
				}
			}
		}

		private String annotationName(Field field) {
			Column columnAnnotation = field.getAnnotation(Column.class);
			if (columnAnnotation != null) {
				String columnName = columnAnnotation.value().length() == 0 ? columnAnnotation.name() : columnAnnotation.value();
				if (!(columnName == null || columnName.length() == 0)) {
					return lowerCaseName(columnName);
				}
			}
			return null;
		}

		/**
		 * Convert a name in camelCase to an underscored name in lower case.
		 * Any upper case letters are converted to lower case with a preceding underscore.
		 * @param name the original name
		 * @return the converted name
		 * @since 4.2
		 * @see #lowerCaseName
		 */
		protected String underscoreName(String name) {
			StringBuilder columnName = new StringBuilder(name.length());
			for(char c : name.toCharArray()) {
				if(Character.isUpperCase(c)) {
					columnName.append('_').append(Character.toLowerCase(c));
				}
				else {
					columnName.append(c);
				}
			}
			return columnName.toString();
		}

		/**
		 * Convert the given name to lower case.
		 * By default, conversions will happen within the US locale.
		 * @param name the original name
		 * @return the converted name
		 * @since 4.2
		 */
		protected String lowerCaseName(String name) {
			return name.toLowerCase(Locale.US);
		}

		private ConcurrentMap<Class, MethodAccess> localCache = new ConcurrentHashMap<>();

		private MethodAccess getMethodAccess() {
			return Optional.ofNullable(localCache.get(mappedClass))
					.orElseGet(() -> {
						MethodAccess method = MethodAccess.get(mappedClass);
						localCache.put(mappedClass, method);
						return method;
					});
		}

		/**
		 * Extract the values for all columns in the current row.
		 * <p>Utilizes public setters and result set meta-data.
		 * @see java.sql.ResultSetMetaData
		 */
		public T mapRow(ResultSet rs) throws SQLException {
			Assert.state(this.mappedClass != null, "Mapped class was not specified");

			Set<String> populatedProperties = (isCheckFullyPopulated() ? new HashSet<>() : null);

			T mappedObject = ConstructorAccess.get(mappedClass).newInstance();

			MethodAccess methodAccess = getMethodAccess();

			if(columnIndexMap == null) {
				ResultSetMetaData rsmd = rs.getMetaData();
				columnCount = rsmd.getColumnCount();
				columnIndexMap = new HashMap<>(columnCount);
				for (int index = 1; index <= columnCount; index++) {
					String column = JdbcUtils.lookupColumnName(rsmd, index);
					columnIndexMap.put(index, lowerCaseName(column));
				}
			}

			for (int index = 1; index <= columnCount; index++) {
				String column = columnIndexMap.get(index);
				Field pd = this.mappedFields.get(column);
				if (pd != null) {
					try {
						Object value = getColumnValue(rs, index, pd);
						if(logger.isTraceEnabled()) {
							logger.trace("Mapping column '" + column + "' to property '" + pd.getName() +
									"' of type '" + ClassUtils.getQualifiedName(pd.getType()) + "'");
						}
						try {
							methodAccess.invoke(mappedObject, writerMethodMap.get(pd.getName()), value);
						} catch (TypeMismatchException ex) {
							if (value == null && this.primitivesDefaultedForNullValue) {
								logger.debug("Intercepted TypeMismatchException for column '" + column + "' with null value when setting property '" +
										pd.getName() + "' of type '" +
										ClassUtils.getQualifiedName(pd.getType()) +
										"' on object: " + mappedObject, ex);
							} else {
								throw ex;
							}
						}
						if (populatedProperties != null) {
							populatedProperties.add(pd.getName());
						}
					} catch (NotWritablePropertyException ex) {
						throw new DataRetrievalFailureException(
								"Unable to map column '" + column + "' to property '" + pd.getName() + "'", ex);
					}
				} else {
					// No PropertyDescriptor found
					logger.debug("No property found for column '" + column);
				}
			}

			if (populatedProperties != null && !populatedProperties.equals(this.mappedProperties)) {
				throw new InvalidDataAccessApiUsageException("Given ResultSet does not contain all fields " +
						"necessary to populate object of class [" + this.mappedClass.getName() + "]: " +
						this.mappedProperties);
			}

			return mappedObject;
		}

		/**
		 * Retrieve a JDBC object value for the specified column.
		 * <p>The default implementation calls
		 * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}.
		 * Subclasses may override this to check specific value types upfront,
		 * or to post-process values return from {@code getResultSetValue}.
		 * @param rs is the ResultSet holding the data
		 * @param index is the column index
		 * @param pd the bean property that each result object is expected to match
		 * @return the Object value
		 * @throws SQLException in case of extraction failure
		 * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)
		 */
		@Nullable
		protected Object getColumnValue(ResultSet rs, int index, Field pd) throws SQLException {
			return JdbcUtils.getResultSetValue(rs, index, pd.getType());
		}

	}

}
