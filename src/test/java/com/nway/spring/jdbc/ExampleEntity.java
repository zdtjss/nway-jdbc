package com.nway.spring.jdbc;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "t_nway")
@com.nway.spring.jdbc.annotation.Table(name = "t_nway")
public class ExampleEntity {

	@com.nway.spring.jdbc.annotation.Column(name = "pk_id")
	private int id;
	private boolean pBoolean;
	private byte pByte;
	private short pShort;
	private int pInt;
	private long pLong;
	private float pFloat;
	private double pDouble;
	private byte[] pByteArr;

	private Boolean wBoolean;
	private Byte wByte;
	private Short wShort;
	private Integer wInt;
	private Long wLong;
	private Float wFloat;
	private Double wDouble;

	private String string;
	private Date utilDate;
	private java.sql.Date sqlDate;
	@com.nway.spring.jdbc.annotation.Column(name = "c_timestamp")
	private Timestamp timestamp;
	@com.nway.spring.jdbc.annotation.Column(name = "c_clob")
	private Clob clob;
	@com.nway.spring.jdbc.annotation.Column(name = "b_blob")
	private Blob blob;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "pk_id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "p_boolean")
	public boolean ispBoolean() {
		return pBoolean;
	}

	public void setpBoolean(boolean pBoolean) {
		this.pBoolean = pBoolean;
	}

	@Column(name = "p_byte")
	public byte getpByte() {
		return pByte;
	}

	public void setpByte(byte pByte) {
		this.pByte = pByte;
	}

	@Column(name = "p_short")
	public short getpShort() {
		return pShort;
	}

	public void setpShort(short pShort) {
		this.pShort = pShort;
	}

	@Column(name = "p_int")
	public int getpInt() {
		return pInt;
	}

	public void setpInt(int pInt) {
		this.pInt = pInt;
	}

	@Column(name = "p_long")
	public long getpLong() {
		return pLong;
	}

	public void setpLong(long pLong) {
		this.pLong = pLong;
	}

	@Column(name = "p_float")
	public float getpFloat() {
		return pFloat;
	}

	public void setpFloat(float pFloat) {
		this.pFloat = pFloat;
	}

	@Column(name = "p_double")
	public double getpDouble() {
		return pDouble;
	}

	public void setpDouble(double pDouble) {
		this.pDouble = pDouble;
	}

	@Column(name = "p_byte_arr")
	public byte[] getpByteArr() {
		return pByteArr;
	}

	public void setpByteArr(byte[] pByteArr) {
		this.pByteArr = pByteArr;
	}

	@Column(name = "w_boolean")
	public Boolean getwBoolean() {
		return wBoolean;
	}

	public void setwBoolean(Boolean wBoolean) {
		this.wBoolean = wBoolean;
	}

	@Column(name = "w_byte")
	public Byte getwByte() {
		return wByte;
	}

	public void setwByte(Byte wByte) {
		this.wByte = wByte;
	}

	@Column(name = "w_short")
	public Short getwShort() {
		return wShort;
	}

	public void setwShort(Short wShort) {
		this.wShort = wShort;
	}

	@Column(name = "w_int")
	public Integer getwInt() {
		return wInt;
	}

	public void setwInt(Integer wInt) {
		this.wInt = wInt;
	}

	@Column(name = "w_long")
	public Long getwLong() {
		return wLong;
	}

	public void setwLong(Long wLong) {
		this.wLong = wLong;
	}

	@Column(name = "w_float")
	public Float getwFloat() {
		return wFloat;
	}

	public void setwFloat(Float wFloat) {
		this.wFloat = wFloat;
	}

	@Column(name = "w_double")
	public Double getwDouble() {
		return wDouble;
	}

	public void setwDouble(Double wDouble) {
		this.wDouble = wDouble;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExampleEntity [id=");
		builder.append(id);
		builder.append(", pBoolean=");
		builder.append(pBoolean);
		builder.append(", pByte=");
		builder.append(pByte);
		builder.append(", pShort=");
		builder.append(pShort);
		builder.append(", pInt=");
		builder.append(pInt);
		builder.append(", pLong=");
		builder.append(pLong);
		builder.append(", pFloat=");
		builder.append(pFloat);
		builder.append(", pDouble=");
		builder.append(pDouble);
		builder.append(", pByteArr=");
		builder.append(Arrays.toString(pByteArr));
		builder.append(", wBoolean=");
		builder.append(wBoolean);
		builder.append(", wByte=");
		builder.append(wByte);
		builder.append(", wShort=");
		builder.append(wShort);
		builder.append(", wInt=");
		builder.append(wInt);
		builder.append(", wLong=");
		builder.append(wLong);
		builder.append(", wFloat=");
		builder.append(wFloat);
		builder.append(", wDouble=");
		builder.append(wDouble);
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
