package com.nway.spring.jdbc.bean;

import com.nway.spring.jdbc.annotation.Column;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
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

		return mapper.setColumnIndexMap(columnIndex).mapRow(rs, 0);
	}

	private Map<String, Integer> getColumnIndex(ResultSet rs) throws SQLException {
		Map<String, Integer> columnIndex = new HashMap<>();

		ResultSetMetaData rsmd = rs.getMetaData();
		for (int index = 1; index <= rsmd.getColumnCount(); index++) {
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
	 * 	注：本类使用了{@link org.springframework.jdbc.core.BeanPropertyRowMapper}
	 * 	源码，新增了通过 {@link com.nway.spring.jdbc.annotation.Column}自定义表字段与bean属性的映射
	 *
	 * @author Thomas Risberg
	 * @author Juergen Hoeller
	 * @author zdtjss@163.com
	 * @since 2.5
	 * @param <T> the result type
	 * @see DataClassRowMapper
	 */
	static class BeanPropertyRowMapper<T> implements RowMapper<T> {

		/** Logger available to subclasses. */
		protected final Log logger = LogFactory.getLog(getClass());

		/** The class we are mapping to. */
		@Nullable
		private Class<T> mappedClass;

		/** Whether we're strictly validating. */
		private boolean checkFullyPopulated = false;

		/** Whether we're defaulting primitives when mapping a null value. */
		private boolean primitivesDefaultedForNullValue = false;

		/** ConversionService for binding JDBC values to bean properties. */
		@Nullable
		private ConversionService conversionService = DefaultConversionService.getSharedInstance();

		/** Map of the fields we provide mapping for. */
		@Nullable
		private Map<String, Field> mappedFields;

		/** Set of bean properties we provide mapping for. */
		@Nullable
		private Set<String> mappedProperties;

		private Map<String, Integer> columnIndexMap;

		/**
		 * Create a new {@code BeanPropertyRowMapper} for bean-style configuration.
		 * @see #setMappedClass
		 * @see #setCheckFullyPopulated
		 */
		public BeanPropertyRowMapper() {
		}

		/**
		 * Create a new {@code BeanPropertyRowMapper}, accepting unpopulated
		 * properties in the target bean.
		 * @param mappedClass the class that each row should be mapped to
		 */
		public BeanPropertyRowMapper(Class<T> mappedClass) {
			initialize(mappedClass);
		}

		/**
		 * Create a new {@code BeanPropertyRowMapper}.
		 * @param mappedClass the class that each row should be mapped to
		 * @param checkFullyPopulated whether we're strictly validating that
		 * all bean properties have been mapped from corresponding database fields
		 */
		public BeanPropertyRowMapper(Class<T> mappedClass, boolean checkFullyPopulated) {
			initialize(mappedClass);
			this.checkFullyPopulated = checkFullyPopulated;
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
			}
			else {
				if (this.mappedClass != mappedClass) {
					throw new InvalidDataAccessApiUsageException("The mapped class can not be reassigned to map to " +
							mappedClass + " since it is already providing mapping for " + this.mappedClass);
				}
			}
		}

		/**
		 * Get the class that we are mapping to.
		 */
		@Nullable
		public final Class<T> getMappedClass() {
			return this.mappedClass;
		}

		/**
		 * Set whether we're strictly validating that all bean properties have been mapped
		 * from corresponding database fields.
		 * <p>Default is {@code false}, accepting unpopulated properties in the target bean.
		 */
		public void setCheckFullyPopulated(boolean checkFullyPopulated) {
			this.checkFullyPopulated = checkFullyPopulated;
		}

		/**
		 * Return whether we're strictly validating that all bean properties have been
		 * mapped from corresponding database fields.
		 */
		public boolean isCheckFullyPopulated() {
			return this.checkFullyPopulated;
		}

		/**
		 * Set whether we're defaulting Java primitives in the case of mapping a null value
		 * from corresponding database fields.
		 * <p>Default is {@code false}, throwing an exception when nulls are mapped to Java primitives.
		 */
		public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
			this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
		}

		/**
		 * Return whether we're defaulting Java primitives in the case of mapping a null value
		 * from corresponding database fields.
		 */
		public boolean isPrimitivesDefaultedForNullValue() {
			return this.primitivesDefaultedForNullValue;
		}

		/**
		 * Set a {@link ConversionService} for binding JDBC values to bean properties,
		 * or {@code null} for none.
		 * <p>Default is a {@link DefaultConversionService}, as of Spring 4.3. This
		 * provides support for {@code java.time} conversion and other special types.
		 * @since 4.3
		 * @see #initBeanWrapper(BeanWrapper)
		 */
		public void setConversionService(@Nullable ConversionService conversionService) {
			this.conversionService = conversionService;
		}

		/**
		 * Return a {@link ConversionService} for binding JDBC values to bean properties,
		 * or {@code null} if none.
		 * @since 4.3
		 */
		@Nullable
		public ConversionService getConversionService() {
			return this.conversionService;
		}


		/**
		 * Initialize the mapping meta-data for the given class.
		 * @param mappedClass the mapped class
		 */
		protected void initialize(Class<T> mappedClass) {
			this.mappedClass = mappedClass;
			this.mappedFields = new HashMap<>();
			this.mappedProperties = new HashSet<>();

			for (Field field : mappedClass.getDeclaredFields()) {
				this.mappedFields.put(annotationName(field), field);
				this.mappedProperties.add(field.getName());
			}
		}

		/**
		 * Remove the specified property from the mapped fields.
		 * @param propertyName the property name (as used by property descriptors)
		 * @since 5.3.9
		 */
		protected void suppressProperty(String propertyName) {
			if (this.mappedFields != null) {
				this.mappedFields.remove(propertyName);
				this.mappedFields.remove(propertyName);
			}
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

		/**
		 * Convert a name in camelCase to an underscored name in lower case.
		 * Any upper case letters are converted to lower case with a preceding underscore.
		 * @param name the original name
		 * @return the converted name
		 * @since 4.2
		 * @see #lowerCaseName
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
				}
				else {
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
		 * @see java.sql.ResultSetMetaData
		 */
		@Override
		public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
			BeanWrapperImpl bw = new BeanWrapperImpl();
			initBeanWrapper(bw);

			T mappedObject = constructMappedInstance(rs, bw);
			bw.setBeanInstance(mappedObject);

			Set<String> populatedProperties = (isCheckFullyPopulated() ? new HashSet<>() : null);

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
						try {
							bw.setPropertyValue(pd.getName(), value);
						}
						catch (TypeMismatchException ex) {
							if (value == null && this.primitivesDefaultedForNullValue) {
								if (logger.isDebugEnabled()) {
									logger.debug("Intercepted TypeMismatchException for row " + rowNumber +
											" and column '" + column + "' with null value when setting property '" +
											pd.getName() + "' of type '" +
											ClassUtils.getQualifiedName(pd.getType()) +
											"' on object: " + mappedObject, ex);
								}
							}
							else {
								throw ex;
							}
						}
						if (populatedProperties != null) {
							populatedProperties.add(pd.getName());
						}
					}
					catch (NotWritablePropertyException ex) {
						throw new DataRetrievalFailureException(
								"Unable to map column '" + column + "' to property '" + pd.getName() + "'", ex);
					}
				}
				else {
					// No PropertyDescriptor found
					if (rowNumber == 0 && logger.isDebugEnabled()) {
						logger.debug("No property found for column '" + column + "' mapped");
					}
				}
			}

			if (populatedProperties != null && !populatedProperties.equals(this.mappedProperties)) {
				throw new InvalidDataAccessApiUsageException("Given ResultSet does not contain all fields " +
						"necessary to populate object of " + this.mappedClass + ": " + this.mappedProperties);
			}

			return mappedObject;
		}

		/**
		 * Construct an instance of the mapped class for the current row.
		 * @param rs the ResultSet to map (pre-initialized for the current row)
		 * @param tc a TypeConverter with this RowMapper's conversion service
		 * @return a corresponding instance of the mapped class
		 * @throws SQLException if an SQLException is encountered
		 * @since 5.3
		 */
		protected T constructMappedInstance(ResultSet rs, TypeConverter tc) throws SQLException  {
			Assert.state(this.mappedClass != null, "Mapped class was not specified");
			return BeanUtils.instantiateClass(this.mappedClass);
		}

		/**
		 * Initialize the given BeanWrapper to be used for row mapping.
		 * To be called for each row.
		 * <p>The default implementation applies the configured {@link ConversionService},
		 * if any. Can be overridden in subclasses.
		 * @param bw the BeanWrapper to initialize
		 * @see #getConversionService()
		 * @see BeanWrapper#setConversionService
		 */
		protected void initBeanWrapper(BeanWrapper bw) {
			ConversionService cs = getConversionService();
			if (cs != null) {
				bw.setConversionService(cs);
			}
		}

		/**
		 * Retrieve a JDBC object value for the specified column.
		 * <p>The default implementation delegates to
		 * {@link #getColumnValue(ResultSet, int, Class)}.
		 * @param rs is the ResultSet holding the data
		 * @param index is the column index
		 * @param pd the bean property that each result object is expected to match
		 * @return the Object value
		 * @throws SQLException in case of extraction failure
		 * @see #getColumnValue(ResultSet, int, Class)
		 */
		@Nullable
		protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
			return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
		}

		/**
		 * Retrieve a JDBC object value for the specified column.
		 * <p>The default implementation calls
		 * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}.
		 * Subclasses may override this to check specific value types upfront,
		 * or to post-process values return from {@code getResultSetValue}.
		 * @param rs is the ResultSet holding the data
		 * @param index is the column index
		 * @param paramType the target parameter type
		 * @return the Object value
		 * @throws SQLException in case of extraction failure
		 * @since 5.3
		 * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)
		 */
		@Nullable
		protected Object getColumnValue(ResultSet rs, int index, Class<?> paramType) throws SQLException {
			return JdbcUtils.getResultSetValue(rs, index, paramType);
		}


		/**
		 * Static factory method to create a new {@code BeanPropertyRowMapper}.
		 * @param mappedClass the class that each row should be mapped to
		 * @see #newInstance(Class, ConversionService)
		 */
		public static <T> org.springframework.jdbc.core.BeanPropertyRowMapper<T> newInstance(Class<T> mappedClass) {
			return new org.springframework.jdbc.core.BeanPropertyRowMapper<>(mappedClass);
		}

		/**
		 * Static factory method to create a new {@code BeanPropertyRowMapper}.
		 * @param mappedClass the class that each row should be mapped to
		 * @param conversionService the {@link ConversionService} for binding
		 * JDBC values to bean properties, or {@code null} for none
		 * @since 5.2.3
		 * @see #newInstance(Class)
		 * @see #setConversionService
		 */
		public static <T> org.springframework.jdbc.core.BeanPropertyRowMapper<T> newInstance(
				Class<T> mappedClass, @Nullable ConversionService conversionService) {

			org.springframework.jdbc.core.BeanPropertyRowMapper<T> rowMapper = newInstance(mappedClass);
			rowMapper.setConversionService(conversionService);
			return rowMapper;
		}

	}

}
