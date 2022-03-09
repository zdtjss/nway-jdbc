package com.nway.spring.jdbc.performance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nway.spring.jdbc.annotation.enums.ColumnType;

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
@Table(name = "t_mouse")
@TableName("t_mouse")
@com.nway.spring.jdbc.annotation.Table("t_mouse")
public class Mouse {

	/** 编号 **/
	@TableId
	@com.nway.spring.jdbc.annotation.Column(type = ColumnType.ID)
	private int id;
	/** 品牌 **/
	private String brand;
	/** 型号 **/
	private String model;
	/** 价格 **/
	private float price;
	/** 类型：机械鼠标、光机鼠标、光电鼠标、光学鼠标 **/
	private int type;
	/** 是否是无线 **/
	@TableField(value = "is_wireless")
	@com.nway.spring.jdbc.annotation.Column("is_wireless")
	private boolean isWireless;
	/** 颜色 **/
	private String color;
	/** 生产日期 **/
	private Date productionDate;
	/** 设备图片 **/
	private byte[] photo;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@Column
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Column
	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	@Column
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "is_wireless")
	public boolean isWireless() {
		return isWireless;
	}

	public void setWireless(boolean wireless) {
		isWireless = wireless;
	}

	@Column
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "production_date")
	public Date getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}

	@Lob
	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("Mouse [id=");
		builder.append(id);
		builder.append(", brand=");
		builder.append(brand);
		builder.append(", model=");
		builder.append(model);
		builder.append(", price=");
		builder.append(price);
		builder.append(", type=");
		builder.append(type);
		builder.append(", isWireless=");
		builder.append(isWireless);
		builder.append(", color=");
		builder.append(color);
		builder.append(", productionDate=");
		builder.append(productionDate);
		builder.append(", photo=");
		builder.append(Arrays.toString(photo));
		builder.append("]");
		
		return builder.toString();
	}

}
