package com.shop.dto;

import lombok.Getter;

@Getter
public class AddressDTO {

	private String zipcode;
	private String province;
	private String road;
	private String building;
	private String oldaddr;
	
	public String getZipcode() {
		return zipcode;
	}
	public String getProvince() {
		return province;
	}
	public String getRoad() {
		return road;
	}
	public String getBuilding() {
		return building;
	}
	public String getOldaddr() {
		return oldaddr;
	}
	
}
