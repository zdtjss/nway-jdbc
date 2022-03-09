package com.nway.spring.jdbc.performance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nway.spring.jdbc.annotation.enums.ColumnType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_software")
@TableName("t_software")
@com.nway.spring.jdbc.annotation.Table("t_software")
public class Software {

	@TableId
	@com.nway.spring.jdbc.annotation.Column(type = ColumnType.ID)
	private int id;
	private String vender;
	private String name;
	private String version;
	@TableField(value = "file_size")
	@com.nway.spring.jdbc.annotation.Column("file_size")
	private long size;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column
	public String getVender() {
		return vender;
	}

	public void setVender(String vender) {
		this.vender = vender;
	}

	@Column
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Column(name="file_size")
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
