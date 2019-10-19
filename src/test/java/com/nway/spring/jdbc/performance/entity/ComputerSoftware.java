package com.nway.spring.jdbc.performance.entity;

@com.nway.spring.jdbc.annotation.Table("t_computer_software")
public class ComputerSoftware {

	private int softwareId;

	private int computerId;

	public int getSoftwareId() {
		return softwareId;
	}

	public void setSoftwareId(int softwareId) {
		this.softwareId = softwareId;
	}

	public int getComputerId() {
		return computerId;
	}

	public void setComputerId(int computerId) {
		this.computerId = computerId;
	}

}
