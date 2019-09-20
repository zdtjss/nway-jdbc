package com.nway.spring.jdbc.bean;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.nway.spring.jdbc.annotation.Column;

public class DefaultBeanProcessor implements BeanProcessor {

	@Override
	public <T> List<T> toBeanList(ResultSet rs, Class<T> mappedClass) throws SQLException {

		BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<T>(mappedClass);

		final List<T> results = new ArrayList<T>();

		do {

			results.add(mapper.mapRow(rs, rs.getRow()));
		} 
		while (rs.next());

		return results;
	}

	@Override
	public <T> T toBean(ResultSet rs, Class<T> mappedClass) throws SQLException {

		BeanPropertyRowMapper<T> mapper = new BeanPropertyRowMapper<T>(mappedClass);
		
		return mapper.mapRow(rs, rs.getRow());
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
	class BeanPropertyRowMapper<T> implements RowMapper<T> {

		/** Logger available to subclasses */
		protected Log logger = LogFactory.getLog(getClass());

		/** The class we are mapping to */
		private Class<T> mappedClass;

		/** Whether we're strictly validating */
		private boolean checkFullyPopulated = false;

		/** Whether we're defaulting primitives when mapping a null value */
		private boolean primitivesDefaultedForNullValue = false;

		/** Map of the fields we provide mapping for */
		private Map<String, PropertyDescriptor> mappedFields;

		/** Set of bean properties we provide mapping for */
		private Set<String> mappedProperties;

		/**
		 * Create a new {@code BeanPropertyRowMapper} for bean-style
		 * configuration.
		 * 
		 * @see #setMappedClass
		 * @see #setCheckFullyPopulated
		 */
		public BeanPropertyRowMapper() {
		}

		/**
		 * Create a new {@code BeanPropertyRowMapper}, accepting unpopulated
		 * properties in the target bean.
		 * <p>
		 * Consider using the {@link #newInstance} factory method instead, which
		 * allows for specifying the mapped type once only.
		 * 
		 * @param mappedClass
		 *            the class that each row should be mapped to
		 */
		public BeanPropertyRowMapper(Class<T> mappedClass) {
			initialize(mappedClass);
		}

		/**
		 * Create a new {@code BeanPropertyRowMapper}.
		 * 
		 * @param mappedClass
		 *            the class that each row should be mapped to
		 * @param checkFullyPopulated
		 *            whether we're strictly validating that all bean properties
		 *            have been mapped from corresponding database fields
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
			} else {
				if (!this.mappedClass.equals(mappedClass)) {
					throw new InvalidDataAccessApiUsageException("The mapped class can not be reassigned to map to "
							+ mappedClass + " since it is already providing mapping for " + this.mappedClass);
				}
			}
		}

		/**
		 * Get the class that we are mapping to.
		 */
		public final Class<T> getMappedClass() {
			return this.mappedClass;
		}

		/**
		 * Set whether we're strictly validating that all bean properties have
		 * been mapped from corresponding database fields.
		 * <p>
		 * Default is {@code false}, accepting unpopulated properties in the
		 * target bean.
		 */
		public void setCheckFullyPopulated(boolean checkFullyPopulated) {
			this.checkFullyPopulated = checkFullyPopulated;
		}

		/**
		 * Return whether we're strictly validating that all bean properties
		 * have been mapped from corresponding database fields.
		 */
		public boolean isCheckFullyPopulated() {
			return this.checkFullyPopulated;
		}

		/**
		 * Set whether we're defaulting Java primitives in the case of mapping a
		 * null value from corresponding database fields.
		 * <p>
		 * Default is {@code false}, throwing an exception when nulls are mapped
		 * to Java primitives.
		 */
		public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
			this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
		}

		/**
		 * Return whether we're defaulting Java primitives in the case of
		 * mapping a null value from corresponding database fields.
		 */
		public boolean isPrimitivesDefaultedForNullValue() {
			return this.primitivesDefaultedForNullValue;
		}

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
			this.mappedFields = new HashMap<String, PropertyDescriptor>();
			this.mappedProperties = new HashSet<String>();

			PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);

			for (PropertyDescriptor pd : pds) {

				if (pd.getWriteMethod() != null) {

					String columnName = annotationName(pd);

					this.mappedFields.put(lowerCaseName(pd.getName()), pd);

					if (columnName == null) {

						columnName = underscoreName(pd.getName());

						if (!lowerCaseName(pd.getName()).equals(columnName)) {

							this.mappedFields.put(columnName, pd);
						}
					} else if (!lowerCaseName(pd.getName()).equals(columnName)) {

						this.mappedFields.put(columnName, pd);
					}

					this.mappedProperties.add(pd.getName());
				}
			}
		}

		private String annotationName(PropertyDescriptor pd) {

			Column columnAnnotation = pd.getReadMethod().getAnnotation(Column.class);

			if (columnAnnotation != null) {

				String columnName = columnAnnotation.value() == null ? columnAnnotation.name()
						: columnAnnotation.value();

				if (!(columnName == null || columnName.length() == 0)) {

					return lowerCaseName(columnName);
				}
			}

			return null;
		}

		/**
		 * Convert a name in camelCase to an underscored name in lower case. Any
		 * upper case letters are converted to lower case with a preceding
		 * underscore.
		 * 
		 * @param name
		 *            the original name
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
				} else {
					result.append(s);
				}
			}
			return result.toString();
		}

		/**
		 * Convert the given name to lower case. By default, conversions will
		 * happen within the US locale.
		 * 
		 * @param name
		 *            the original name
		 * @return the converted name
		 * @since 4.2
		 */
		protected String lowerCaseName(String name) {
			return name.toLowerCase(Locale.US);
		}

		/**
		 * Extract the values for all columns in the current row.
		 * <p>
		 * Utilizes public setters and result set metadata.
		 * 
		 * @see java.sql.ResultSetMetaData
		 */
		@Override
		public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
			Assert.state(this.mappedClass != null, "Mapped class was not specified");
			T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
			BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
			initBeanWrapper(bw);

			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			Set<String> populatedProperties = (isCheckFullyPopulated() ? new HashSet<String>() : null);

			for (int index = 1; index <= columnCount; index++) {
				String column = JdbcUtils.lookupColumnName(rsmd, index);
				PropertyDescriptor pd = this.mappedFields.get(lowerCaseName(column.replaceAll(" ", "")));
				if (pd != null) {
					try {
						Object value = getColumnValue(rs, index, pd);
						if (logger.isDebugEnabled() && rowNumber == 0) {
							logger.debug("Mapping column '" + column + "' to property '" + pd.getName() + "' of type "
									+ pd.getPropertyType());
						}
						try {
							bw.setPropertyValue(pd.getName(), value);
						} catch (TypeMismatchException ex) {
							if (value == null && this.primitivesDefaultedForNullValue) {
								logger.debug("Intercepted TypeMismatchException for row " + rowNumber + " and column '"
										+ column + "' with null value when setting property '" + pd.getName()
										+ "' of type " + pd.getPropertyType() + " on object: " + mappedObject);
							} else {
								throw ex;
							}
						}
						if (populatedProperties != null) {
							populatedProperties.add(pd.getName());
						}
					} catch (NotWritablePropertyException ex) {
						throw new DataRetrievalFailureException(
								"Unable to map column " + column + " to property " + pd.getName(), ex);
					}
				}
			}

			if (populatedProperties != null && !populatedProperties.equals(this.mappedProperties)) {
				throw new InvalidDataAccessApiUsageException("Given ResultSet does not contain all fields "
						+ "necessary to populate object of class [" + this.mappedClass + "]: " + this.mappedProperties);
			}

			return mappedObject;
		}

		/**
		 * Initialize the given BeanWrapper to be used for row mapping. To be
		 * called for each row.
		 * <p>
		 * The default implementation is empty. Can be overridden in subclasses.
		 * 
		 * @param bw
		 *            the BeanWrapper to initialize
		 */
		protected void initBeanWrapper(BeanWrapper bw) {
		}

		/**
		 * Retrieve a JDBC object value for the specified column.
		 * <p>
		 * The default implementation calls
		 * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}.
		 * Subclasses may override this to check specific value types upfront,
		 * or to post-process values return from {@code getResultSetValue}.
		 * 
		 * @param rs
		 *            is the ResultSet holding the data
		 * @param index
		 *            is the column index
		 * @param pd
		 *            the bean property that each result object is expected to
		 *            match (or {@code null} if none specified)
		 * @return the Object value
		 * @throws SQLException
		 *             in case of extraction failure
		 * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet,
		 *      int, Class)
		 */
		protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
			return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
		}
	}
}
