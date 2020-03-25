package com.x.program.center.qiyeweixin;

import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class User extends GsonPropertyObject {

	// "userid": "zhangsan",
	// "name": "李四",
	// "department": [1, 2],
	// "order": [1, 2],
	// "position": "后台工程师",
	// "mobile": "15913215421",
	// "gender": "1",
	// "email": "zhangsan@gzdev.com",
	// "isleader": 0,
	// "avatar":
	// "http://wx.qlogo.cn/mmopen/ajNVdqHZLLA3WJ6DSZUfiakYe37PKnQhBIeOQBO4czqrnZDS79FH5Wm5m4X69TBicnHFlhiafvDwklOpZeXYQQ2icg/0",
	// "telephone": "020-123456",
	// "enable": 1,
	// "alias": "jackzhang",
	// "status": 1,
	// "extattr": {
	// "attrs": [{
	// "name": "爱好",
	// "value": "旅游"
	// }, {
	// "name": "卡号",
	// "value": "1234567234"
	// }]
	// },
	// "qr_code": "https://open.work.weixin.qq.com/wwopen/userQRCode?vcode=xxx",
	// "external_position": "产品经理",
	// "external_profile": {
	// "external_attr": [{
	// "type": 0,
	// "name": "文本名称",
	// "text": {
	// "value": "文本"
	// }
	// },
	// {
	// "type": 1,
	// "name": "网页名称",
	// "web": {
	// "url": "http://www.test.com",
	// "title": "标题"
	// }
	// },
	// {
	// "type": 2,
	// "name": "测试app",
	// "miniprogram": {
	// "appid": "wx8bd80126147df384",
	// "pagepath": "/index",
	// "title": "my miniprogram"
	// }
	// }
	// ]
	// }
	// }]
	private String userid;
	private String name;
	private List<Long> department;
	private List<Long> order;
	private String position;
	private String mobile;
	private String gender;
	private String email;
	private Integer isleader;
	private String avatar;
	private String telephone;
	private Integer enable;
	private String alias;
	private Integer status;
	private String qr_code;
	private String external_position;
	private Extattr extattr;

	public static class Extattr {

		private List<Attr> attrs;

		public List<Attr> getAttrs() {
			return attrs;
		}

		public void setAttrs(List<Attr> attrs) {
			this.attrs = attrs;
		}

		public static class Attr {

			private String name;
			private String value;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
			}
		}
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Long> getDepartment() {
		return department;
	}

	public void setDepartment(List<Long> department) {
		this.department = department;
	}

	public List<Long> getOrder() {
		return order;
	}

	public void setOrder(List<Long> order) {
		this.order = order;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getIsleader() {
		return isleader;
	}

	public void setIsleader(Integer isleader) {
		this.isleader = isleader;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getQr_code() {
		return qr_code;
	}

	public void setQr_code(String qr_code) {
		this.qr_code = qr_code;
	}

	public String getExternal_position() {
		return external_position;
	}

	public void setExternal_position(String external_position) {
		this.external_position = external_position;
	}

	public Extattr getExtattr() {
		return extattr;
	}

	public void setExtattr(Extattr extattr) {
		this.extattr = extattr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((userid == null) ? 0 : userid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (userid == null) {
			if (other.userid != null)
				return false;
		} else if (!userid.equals(other.userid))
			return false;
		return true;
	}

}