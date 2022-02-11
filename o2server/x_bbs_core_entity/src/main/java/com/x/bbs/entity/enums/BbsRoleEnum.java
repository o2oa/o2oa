package com.x.bbs.entity.enums;


/**
 * 论坛角色枚举类
 * @author sword
 */
public enum BbsRoleEnum {

	FORUM_SUPER_MANAGER("FORUM_SUPER_MANAGER", "论坛管理员", "拥有该论坛管理的最大权限"),
	FORUM_VIEW("FORUM_VIEW", "论坛可见", "用户可以BBS系统中访问该论坛"),
	FORUM_INFO_MANAGEMENT("FORUM_INFO_MANAGEMENT", "论坛信息管理", "用户拥有对论坛的版块增加，删除，修改权限"),
	FORUM_SUBJECT_PUBLISH("FORUM_SUBJECT_PUBLISH", "论坛发布主题", "用户可以在论坛中所有版块发布主题"),
	FORUM_REPLY_PUBLISH("FORUM_REPLY_PUBLISH", "论坛发表回复", "用户可以回复论坛中所有主题"),

	SECTION_MANAGER("SECTION_MANAGER", "版块主", "拥有版块及版块内容管理的最大权限");

	private String value;
	private String name;
	private String desc;

	private BbsRoleEnum(String value, String name, String desc) {
		this.value = value;
		this.name = name;
		this.desc = desc;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value == null ? null : value.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

}
