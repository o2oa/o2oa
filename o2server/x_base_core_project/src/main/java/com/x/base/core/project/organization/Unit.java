package com.x.base.core.project.organization;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class Unit extends GsonPropertyObject {

	@FieldDescribe("组织名称")
	private String name;
	@FieldDescribe("唯一标识")
	private String unique;
	@FieldDescribe("识别名")
	private String distinguishedName;
	@FieldDescribe("组织类型")
	private List<String> typeList;
	@FieldDescribe("说明")
	private String description;
	@FieldDescribe("简称")
	private String shortName;
	@FieldDescribe("组织层次")
	private Integer level;
	@FieldDescribe("组织层次名")
	private String levelName;
	@FieldDescribe("上级组织")
	private String superior;
	@FieldDescribe("排序号")
	private Integer orderNumber;
	@FieldDescribe("企业微信Id")
	private String qiyeweixinId;
	@FieldDescribe("钉钉Id")
	private String dingdingId;
	@FieldDescribe("政务钉钉Id")
	private String zhengwuDingdingId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public List<String> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<String> typeList) {
		this.typeList = typeList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getQiyeweixinId() {
		return qiyeweixinId;
	}

	public void setQiyeweixinId(String qiyeweixinId) {
		this.qiyeweixinId = qiyeweixinId;
	}

	public String getDingdingId() {
		return dingdingId;
	}

	public void setDingdingId(String dingdingId) {
		this.dingdingId = dingdingId;
	}

	public String getZhengwuDingdingId() {
		return zhengwuDingdingId;
	}

	public void setZhengwuDingdingId(String zhengwuDingdingId) {
		this.zhengwuDingdingId = zhengwuDingdingId;
	}

}
