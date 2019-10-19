package com.nway.spring.jdbc.performance.entity;

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

import com.baomidou.mybatisplus.annotation.TableName;

@Entity
@Table(name = "t_monitor")
@TableName("t_monitor")
@com.nway.spring.jdbc.annotation.Table("t_monitor")
public class Monitor {

	/** 编号 **/
	private int id;
	/** 品牌 **/
	private String brand;
	/** 型号 **/
	private String model;
	/** 价格 **/
	private float price;
	/** 类型：CRT、LCD、LED **/
	private int type;
	/** 最大分辨率 **/
	private String maxResolution;
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

	@Column
	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "max_resolution")
	public String getMaxResolution() {
		return maxResolution;
	}

	public void setMaxResolution(String maxResolution) {
		this.maxResolution = maxResolution;
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
		
		builder.append("Monitor [id=");
		builder.append(id);
		builder.append(", brand=");
		builder.append(brand);
		builder.append(", model=");
		builder.append(model);
		builder.append(", price=");
		builder.append(price);
		builder.append(", type=");
		builder.append(type);
		builder.append(", maxResolution=");
		builder.append(maxResolution);
		builder.append(", productionDate=");
		builder.append(productionDate);
		builder.append(", photo=");
		builder.append(Arrays.toString(photo));
		builder.append("]");
		
		return builder.toString();
	}

}
