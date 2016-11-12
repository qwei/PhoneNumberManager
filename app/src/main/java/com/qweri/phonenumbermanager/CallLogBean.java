package com.qweri.phonenumbermanager;

public class CallLogBean {

	public static final int TYPE_IN = 1;
	public static final int TYPE_OUT = 2;
	
	private String name;
	private String telephone;
	private String time;
	private int count;
	private int type;	
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
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public boolean isSameCallLog(CallLogBean bean) {
		if((bean != null) && (bean.getTelephone() != null) && bean.getTelephone().equals(telephone)) {
			return true;
		}
		
		return false;
	}
}
