package com.qweri.phonenumbermanager;

public class ContactBean {

	private String name;
	private String telephone;
	private boolean isInBlackList = false;

	public ContactBean(){}
	public ContactBean(String name, String telephone) {
		this.name = name;
		this.telephone = telephone;
	}

	public boolean isInBlackList() {
		return isInBlackList;
	}

	public void setInBlackList(boolean isInBlackList) {
		this.isInBlackList = isInBlackList;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	
}
