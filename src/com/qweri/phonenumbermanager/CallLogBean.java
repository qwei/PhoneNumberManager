package com.qweri.phonenumbermanager;

public class CallLogBean {

	private String name;
	private String telephone;
	private String time;
	private int count;
	private boolean isInBlackList = false;
	
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	public boolean isSameCallLog(CallLogBean bean) {
		if((bean != null) && (bean.getTelephone() != null) && bean.getTelephone().equals(telephone)) {
			return true;
		}
		
		return false;
	}
}
