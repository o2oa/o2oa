package jiguang.chat.location.bean;

import java.io.Serializable;

public class LocationBean implements /* Parcelable, */Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private String uid;
	public String locName;// 地名
	public String province;// 省名
	public String city;// 城市
	public String district;// 区名
	public String street;// 街道
	public String streetNum;// 街道號
	public Double latitude;// 纬度
	public Double longitude;// 经度
	public String time;
	public int locType;
	public float radius;
	// gps才有的
	public float speed;
	public int satellite;
	public float direction;
	// wifi才有的
	public String addStr;// 具体地址
	public int operationers;
	// 用户信息的
	public String userId;
	public String userName;
	public String userAvator;
	// 额外输入的详细地名
	public String detailAddInput;

	@Override
	public Object clone() {
		LocationBean o = null;
		try {
			// Object中的clone()识别出你要复制的是哪一个对象。
			o = (LocationBean) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return o;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getLocName() {
		return locName;
	}

	public void setLocName(String locName) {
		this.locName = locName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String Street) {
		this.street = Street;
	}

	public String getStreetNum() {
		return streetNum;
	}

	public void setStreetNum(String streetNum) {
		this.streetNum = streetNum;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getLocType() {
		return locType;
	}

	public void setLocType(int locType) {
		this.locType = locType;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public int getSatellite() {
		return satellite;
	}

	public void setSatellite(int satellite) {
		this.satellite = satellite;
	}

	public String getAddStr() {
		return addStr;
	}

	public void setAddStr(String addStr) {
		this.addStr = addStr;
	}

	public int getOperationers() {
		return operationers;
	}

	public void setOperationers(int operationers) {
		this.operationers = operationers;
	}

	public float getDirection() {
		return direction;
	}

	public void setDirection(float direction) {
		this.direction = direction;
	}

	// @Override
	// public int describeContents() {
	// return 0;
	// }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAvator() {
		return userAvator;
	}

	public void setUserAvator(String userAvator) {
		this.userAvator = userAvator;
	}

	public String getDetailAddInput() {
		return detailAddInput;
	}

	public void setDetailAddInput(String detailAddInput) {
		this.detailAddInput = detailAddInput;
	}

}
