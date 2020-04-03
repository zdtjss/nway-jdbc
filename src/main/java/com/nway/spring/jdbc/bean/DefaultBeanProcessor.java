package com.nway.spring.jdbc.bean;

import com.nway.spring.jdbc.annotation.Column;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DefaultBeanProcessor implements BeanProcessor {

	@Override
	public <T> List<T> toBeanList(ResultSet rs, Class<T> mappedClass) throws SQLException {

		BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<T>(mappedClass);

		final List<T> results = new ArrayList<T>();

		while (rs.next()) {
			results.add(mapper.mapRow(rs));
		} 

		return results;
	}

	@Override
	public <T> T toBean(ResultSet rs, Class<T> mappedClass) throws SQLException {

		BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<T>(mappedClass);
		
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

		/** ConversionService for binding JDBC values to bean properties. */
		@Nullable
		private ConversionService conversionService = DefaultConversionService.getSharedInstance();

		/** Map of the fields we provide mapping for. */
		@Nullable
		private Map<String, Field> mappedFields;

		/** Set of bean properties we provide mapping for. */
		@Nullable
		private Set<String> mappedProperties;


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
		 * <p>Consider using the {@link #newInstance} factory method instead,
		 * which allows for specifying the mapped type once only.
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
			this.mappedFields = new HashMap<String, Field>();
			this.mappedProperties = new HashSet<String>();

			Field[] fields = mappedClass.getDeclaredFields();

			for (Field field : fields) {

				String columnName = annotationName(field);

				this.mappedFields.put(lowerCaseName(field.getName()), field);

				if (columnName == null) {

					columnName = underscoreName(field.getName());

					if (!lowerCaseName(field.getName()).equals(columnName)) {

						this.mappedFields.put(columnName, field);
					}
				} else if (!lowerCaseName(field.getName()).equals(columnName)) {

					this.mappedFields.put(columnName, field);
				}

				this.mappedProperties.add(field.getName());
			}
		}

		private String annotationName(Field field) {

			Column columnAnnotation = field.getAnnotation(Column.class);

			if (columnAnnotation != null) {

				String columnName = columnAnnotation.value().length() == 0 ? columnAnnotation.name()
						: columnAnnotation.value();

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
			if (!StringUtils.hasLength(name)) {
				return "";
			}
			StringBuilder result = new StringBuilder();
			result.append(lowerCaseName(name.substring(0, 1)));
			for (int i = 1; i < name.length(); i++) {
				String s = name.substring(i, i + 1);
				String slc = lowerCaseName(s);
				if (!s.equals(slc)) {
					result.append("_").append(slc);
				}
				else {
					result.append(s);
				}
			}
			return result.toString();
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
		 * Extract the values for all columns in the current row.
		 * <p>Utilizes public setters and result set meta-data.
		 * @see java.sql.ResultSetMetaData
		 */
		public T mapRow(ResultSet rs) throws SQLException {
			Assert.state(this.mappedClass != null, "Mapped class was not specified");
			T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
			BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
			initBeanWrapper(bw);

			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			Set<String> populatedProperties = (isCheckFullyPopulated() ? new HashSet<>() : null);

			for (int index = 1; index <= columnCount; index++) {
				String column = JdbcUtils.lookupColumnName(rsmd, index);
				String field = lowerCaseName(StringUtils.delete(column, " "));
				Field pd = (this.mappedFields != null ? this.mappedFields.get(field) : null);
				if (pd != null) {
					try {
						Object value = getColumnValue(rs, index, pd);
						logger.debug("Mapping column '" + column + "' to property '" + pd.getName() +
								"' of type '" + ClassUtils.getQualifiedName(pd.getType()) + "'");
						try {
							bw.setPropertyValue(pd.getName(), value);
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
					logger.debug("No property found for column '" + column + "' mapped to field '" + field + "'");
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


		/**
		 * Static factory method to create a new {@code BeanPropertyRowMapper}
		 * (with the mapped class specified only once).
		 * @param mappedClass the class that each row should be mapped to
		 * @see #newInstance(Class, ConversionService)
		 */
		public <T> org.springframework.jdbc.core.BeanPropertyRowMapper<T> newInstance(Class<T> mappedClass) {
			return new org.springframework.jdbc.core.BeanPropertyRowMapper<>(mappedClass);
		}

		/**
		 * Static factory method to create a new {@code BeanPropertyRowMapper}
		 * (with the required type specified only once).
		 * @param mappedClass the class that each row should be mapped to
		 * @param conversionService the {@link ConversionService} for binding
		 * JDBC values to bean properties, or {@code null} for none
		 * @since 5.2.3
		 * @see #newInstance(Class)
		 * @see #setConversionService
		 */
		public <T> org.springframework.jdbc.core.BeanPropertyRowMapper<T> newInstance(
				Class<T> mappedClass, @Nullable ConversionService conversionService) {

			org.springframework.jdbc.core.BeanPropertyRowMapper<T> rowMapper = newInstance(mappedClass);
			rowMapper.setConversionService(conversionService);
			return rowMapper;
		}

	}

}
