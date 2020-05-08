package com.nway.spring.jdbc;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.*;

import com.nway.spring.jdbc.annotation.enums.ColumnType;
import com.nway.spring.jdbc.sql.TestFillStrategy;
import com.nway.spring.jdbc.sql.TestPermissionStrategy;

@Entity
@Table(name = "t_nway")
@com.nway.spring.jdbc.annotation.Table(name = "t_nway")
public class ExampleEntity {

	@com.nway.spring.jdbc.annotation.Column(name = "pk_id", type = ColumnType.ID)
	private int id;
	@com.nway.spring.jdbc.annotation.Column(name = "p_Boolean")
	private boolean ppBoolean;
	@com.nway.spring.jdbc.annotation.Column(name = "p_Byte")
	private byte ppByte;
	@com.nway.spring.jdbc.annotation.Column(name = "p_Short")
	private short ppShort;
	@com.nway.spring.jdbc.annotation.Column(name = "p_Int")
	private int ppInt;
	@com.nway.spring.jdbc.annotation.Column(name = "p_Long")
	private long ppLong;
	@com.nway.spring.jdbc.annotation.Column(name = "p_Float")
	private float ppFloat;
	@com.nway.spring.jdbc.annotation.Column(name = "p_Double")
	private double ppDouble;
	@com.nway.spring.jdbc.annotation.Column(name = "p_Byte_Arr")
	private byte[] ppByteArr;

	@com.nway.spring.jdbc.annotation.Column(name = "w_Boolean")
	private Boolean wwBoolean;
	@com.nway.spring.jdbc.annotation.Column(name = "w_Byte")
	private Byte wwByte;
	@com.nway.spring.jdbc.annotation.Column(name = "w_Short")
	private Short wwShort;
	@com.nway.spring.jdbc.annotation.Column(name = "w_Int", fillStrategy = TestFillStrategy.class)
	private Integer wwInt;
	@com.nway.spring.jdbc.annotation.Column(name = "w_Long")
	private Long wwLong;
	@com.nway.spring.jdbc.annotation.Column(name = "w_Float")
	private Float wwFloat;
	@com.nway.spring.jdbc.annotation.Column(name = "w_Double")
	private Double wwDouble;

	private String string;
//	@com.nway.spring.jdbc.annotation.Column(permissionStrategy = TestPermissionStrategy.class)
	private Date utilDate;
	private java.sql.Date sqlDate;
	@com.nway.spring.jdbc.annotation.Column(name = "c_timestamp")
	private Timestamp timestamp;
	@com.nway.spring.jdbc.annotation.Column(name = "c_clob")
	private Clob clob;
	@com.nway.spring.jdbc.annotation.Column(name = "b_blob")
	private Blob blob;

	@com.nway.spring.jdbc.annotation.Column(type = ColumnType.NONE)
	private LocalDate localDate;

	@com.nway.spring.jdbc.annotation.Column(type = ColumnType.NONE)
	private LocalDateTime localDateTime;

	@com.nway.spring.jdbc.annotation.Column(type = ColumnType.NONE)
	private BigDecimal bigDecimal;

	@Transient
	@com.nway.spring.jdbc.annotation.Column(type = ColumnType.NONE)
	private List<String> list = Collections.singletonList("aa");

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "pk_id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "p_boolean")
	public boolean isPpBoolean() {
		return ppBoolean;
	}

	public void setPpBoolean(boolean pBoolean) {
		this.ppBoolean = pBoolean;
	}

	@Column(name = "p_byte")
	public byte getPpByte() {
		return ppByte;
	}

	public void setPpByte(byte ppByte) {
		this.ppByte = ppByte;
	}

	@Column(name = "p_short")
	public short getPpShort() {
		return ppShort;
	}

	public void setPpShort(short ppShort) {
		this.ppShort = ppShort;
	}

	@Column(name = "p_int")
	public int getPpInt() {
		return ppInt;
	}

	public void setPpInt(int pInt) {
		this.ppInt = pInt;
	}

	@Column(name = "p_long")
	public long getPpLong() {
		return ppLong;
	}

	public void setPpLong(long ppLong) {
		this.ppLong = ppLong;
	}

	@Column(name = "p_float")
	public float getPpFloat() {
		return ppFloat;
	}

	public void setPpFloat(float pFloat) {
		this.ppFloat = pFloat;
	}

	@Column(name = "p_double")
	public double getPpDouble() {
		return ppDouble;
	}

	public void setPpDouble(double pDouble) {
		this.ppDouble = pDouble;
	}

	@Column(name = "p_byte_arr")
	public byte[] getPpByteArr() {
		return ppByteArr;
	}

	public void setPpByteArr(byte[] pByteArr) {
		this.ppByteArr = pByteArr;
	}

	@Column(name = "w_boolean")
	public Boolean getWwBoolean() {
		return wwBoolean;
	}

	public void setWwBoolean(Boolean wwBoolean) {
		this.wwBoolean = wwBoolean;
	}

	@Column(name = "w_byte")
	public Byte getWwByte() {
		return wwByte;
	}

	public void setWwByte(Byte wwByte) {
		this.wwByte = wwByte;
	}

	@Column(name = "w_short")
	public Short getWwShort() {
		return wwShort;
	}

	public void setWwShort(Short wwShort) {
		this.wwShort = wwShort;
	}

	@Column(name = "w_int")
	public Integer getWwInt() {
		return wwInt;
	}

	public void setWwInt(Integer wwInt) {
		this.wwInt = wwInt;
	}

	@Column(name = "w_long")
	public Long getWwLong() {
		return wwLong;
	}

	public void setWwLong(Long wwLong) {
		this.wwLong = wwLong;
	}

	@Column(name = "w_float")
	public Float getWwFloat() {
		return wwFloat;
	}

	public void setWwFloat(Float wwFloat) {
		this.wwFloat = wwFloat;
	}

	@Column(name = "w_double")
	public Double getWwDouble() {
		return wwDouble;
	}

	public void setWwDouble(Double wwDouble) {
		this.wwDouble = wwDouble;
	}

	@Column(name = "string")
	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	@Column(name = "util_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getUtilDate() {
		return utilDate;
	}

	public void setUtilDate(Date utilDate) {
		this.utilDate = utilDate;
	}

	@Column(name = "sql_date")
	public java.sql.Date getSqlDate() {
		return sqlDate;
	}

	public void setSqlDate(java.sql.Date sqlDate) {
		this.sqlDate = sqlDate;
	}

	@Column(name = "c_timestamp")
	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Lob
	@Column(name = "c_clob")
	public Clob getClob() {
		return clob;
	}

	public void setClob(Clob clob) {
		this.clob = clob;
	}

	@Lob
	@Column(name = "b_blob")
	public Blob getBlob() {
		return blob;
	}

	public void setBlob(Blob blob) {
		this.blob = blob;
	}

	@Column(name = "local_date")
	public LocalDate getLocalDate() {
		return localDate;
	}

	public void setLocalDate(LocalDate localDate) {
		this.localDate = localDate;
	}

	@Column(name = "local_date_time")
	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}

	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

	public BigDecimal getBigDecimal() {
		return bigDecimal;
	}

	public void setBigDecimal(BigDecimal bigDecimal) {
		this.bigDecimal = bigDecimal;
	}

	@Transient
	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExampleEntity [id=");
		builder.append(id);
		builder.append(", pBoolean=");
		builder.append(ppBoolean);
		builder.append(", pByte=");
		builder.append(ppByte);
		builder.append(", pShort=");
		builder.append(ppShort);
		builder.append(", pInt=");
		builder.append(ppInt);
		builder.append(", pLong=");
		builder.append(ppLong);
		builder.append(", pFloat=");
		builder.append(ppFloat);
		builder.append(", pDouble=");
		builder.append(ppDouble);
		builder.append(", pByteArr=");
		builder.append(Arrays.toString(ppByteArr));
		builder.append(", wBoolean=");
		builder.append(wwBoolean);
		builder.append(", wByte=");
		builder.append(wwByte);
		builder.append(", wShort=");
		builder.append(wwShort);
		builder.append(", wInt=");
		builder.append(wwInt);
		builder.append(", wLong=");
		builder.append(wwLong);
		builder.append(", wFloat=");
		builder.append(wwFloat);
		builder.append(", wDouble=");
		builder.append(wwDouble);
		builder.append(", string=");
		builder.append(string);
		builder.append(", utilDate=");
		builder.append(utilDate);
		builder.append(", sqlDate=");
		builder.append(sqlDate);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", clob=");
		builder.append(clob);
		builder.append(", blob=");
		builder.append(blob);
		builder.append("]");
		return builder.toString();
	}

}
