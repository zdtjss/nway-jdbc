package com.nway.spring.jdbc.performance.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.baomidou.mybatisplus.annotation.TableName;

@Entity
@Table(name = "t_computer")
@TableName("t_computer")
@com.nway.spring.jdbc.annotation.Table("t_computer")
public class Computer {

	private int id;
	/** 品牌 **/
	private String brand;
	/** 型号 **/
	private String model;
	/** 价格 **/
	private float price;
	/** 主机编号，nway或spring jdbc使用 **/
	private int mainframeId;
	/** 主机 **/
	private Mainframe mainframe;
	/** 显示器编号，nway或spring jdbc使用 **/
	private int monitorId;
	/** 显示器 **/
	private Monitor monitor;
	/** 鼠标编号，nway或spring jdbc使用 **/
	private int mouseId;
	/** 鼠标 **/
	private Mouse mouse;
	/** 键盘编号，nway或spring jdbc使用 **/
	private int keyboardId;
	/** 键盘 **/
	private Keyboard keyboard;
	/** 生产日期 **/
	private Date productionDate;
	/** 设备图片 **/
	private byte[] photo;
	/** 预装软件 **/
	private List<Software> software;

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

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "mainframe_id")
	@Fetch(FetchMode.JOIN)
	public Mainframe getMainframe() {
		return mainframe;
	}

	public void setMainframe(Mainframe mainframe) {
		this.mainframe = mainframe;
	}

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "monitor_id")
	@Fetch(FetchMode.JOIN)
	public Monitor getMonitor() {
		return monitor;
	}

	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "mouse_id")
	@Fetch(FetchMode.JOIN)
	public Mouse getMouse() {
		return mouse;
	}

	public void setMouse(Mouse mouse) {
		this.mouse = mouse;
	}

	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "keyboard_id")
	@Fetch(FetchMode.JOIN)
	public Keyboard getKeyboard() {
		return keyboard;
	}

	public void setKeyboard(Keyboard keyboard) {
		this.keyboard = keyboard;
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

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "t_computer_software")
	@Fetch(FetchMode.JOIN)
	public List<Software> getSoftware() {
		return software;
	}

	public void setSoftware(List<Software> software) {
		this.software = software;
	}

	@Transient
	public int getMainframeId() {
		return mainframeId;
	}

	public void setMainframeId(int mainframeId) {
		this.mainframeId = mainframeId;
	}

	@Transient
	public int getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(int monitorId) {
		this.monitorId = monitorId;
	}

	@Transient
	public int getMouseId() {
		return mouseId;
	}

	public void setMouseId(int mouseId) {
		this.mouseId = mouseId;
	}

	@Transient
	public int getKeyboardId() {
		return keyboardId;
	}

	public void setKeyboardId(int keyboardId) {
		this.keyboardId = keyboardId;
	}

	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("Computer [id=");
		builder.append(id);
		builder.append(", brand=");
		builder.append(brand);
		builder.append(", model=");
		builder.append(model);
		builder.append(", price=");
		builder.append(price);
		builder.append(", mainframeId=");
		builder.append(mainframeId);
		builder.append(", mainframe=");
		builder.append(mainframe);
		builder.append(", monitorId=");
		builder.append(monitorId);
		builder.append(", monitor=");
		builder.append(monitor);
		builder.append(", mouseId=");
		builder.append(mouseId);
		builder.append(", mouse=");
		builder.append(mouse);
		builder.append(", keyboardId=");
		builder.append(keyboardId);
		builder.append(", keyboard=");
		builder.append(keyboard);
		builder.append(", productionDate=");
		builder.append(productionDate);
		builder.append(", photo=");
		builder.append(Arrays.toString(photo));
		builder.append("]");
		
		return builder.toString();
	}

}
