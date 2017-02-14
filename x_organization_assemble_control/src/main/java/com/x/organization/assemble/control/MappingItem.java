package com.x.organization.assemble.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class MappingItem {

	public static List<MappingItem> personMappings() {
		List<MappingItem> list = new ArrayList<>();
		list.add(new MappingItem("姓名", "name", MappingItemValueType.string));
		list.add(new MappingItem("显示名", "display", MappingItemValueType.string));
		list.add(new MappingItem("唯一编码", "unique", MappingItemValueType.string));
		list.add(new MappingItem("工号", "employee", MappingItemValueType.string));
		list.add(new MappingItem("性别", "genderType", MappingItemValueType.genderType));
		list.add(new MappingItem("密码", "password", MappingItemValueType.string));
		list.add(new MappingItem("密码到期时间", "passwordExpiredTime", MappingItemValueType.date));
		list.add(new MappingItem("排序号", "orderNumber", MappingItemValueType.integer));
		list.add(new MappingItem("标识", "id", MappingItemValueType.string));
		list.add(new MappingItem("邮件", "mail", MappingItemValueType.string));
		list.add(new MappingItem("手机", "mobile", MappingItemValueType.string));
		list.add(new MappingItem("固定电话", "officePhone", MappingItemValueType.string));
		list.add(new MappingItem("创建时间", "createTime", MappingItemValueType.date));
		list.add(new MappingItem("入职时间", "boardDate", MappingItemValueType.date));
		list.add(new MappingItem("出生日期", "birthday", MappingItemValueType.date));
		list.add(new MappingItem("头像", "icon", MappingItemValueType.string));
		list.add(new MappingItem("签名", "signature", MappingItemValueType.string));
		list.add(new MappingItem("微信", "weixin", MappingItemValueType.string));
		list.add(new MappingItem("QQ", "qq", MappingItemValueType.string));
		list.add(new MappingItem("管理者", "controllerList", MappingItemValueType.stringList));
		return list;
	}

	public static List<MappingItem> personMappings(Sheet sheet) throws Exception {
		List<MappingItem> list = personMappings();
		mappingColumns(list, sheet);
		return list;
	}

	public static List<MappingItem> groupMappings() {
		List<MappingItem> list = new ArrayList<>();
		list.add(new MappingItem("名称", "name", MappingItemValueType.string));
		list.add(new MappingItem("显示名", "display", MappingItemValueType.string));
		list.add(new MappingItem("唯一编码", "unique", MappingItemValueType.string));
		list.add(new MappingItem("排序号", "orderNumber", MappingItemValueType.integer));
		list.add(new MappingItem("标识", "id", MappingItemValueType.string));
		list.add(new MappingItem("创建时间", "createTime", MappingItemValueType.date));
		list.add(new MappingItem("人员成员", "personList", MappingItemValueType.stringList));
		list.add(new MappingItem("群组成员", "groupList", MappingItemValueType.stringList));
		return list;
	}

	public static List<MappingItem> groupMappings(Sheet sheet) throws Exception {
		List<MappingItem> list = groupMappings();
		mappingColumns(list, sheet);
		return list;
	}

	public static List<MappingItem> roleMappings() {
		List<MappingItem> list = new ArrayList<>();
		list.add(new MappingItem("名称", "name", MappingItemValueType.string));
		list.add(new MappingItem("显示名", "display", MappingItemValueType.string));
		list.add(new MappingItem("唯一编码", "unique", MappingItemValueType.string));
		list.add(new MappingItem("排序号", "orderNumber", MappingItemValueType.integer));
		list.add(new MappingItem("标识", "id", MappingItemValueType.string));
		list.add(new MappingItem("创建时间", "createTime", MappingItemValueType.date));
		list.add(new MappingItem("人员成员", "personList", MappingItemValueType.stringList));
		list.add(new MappingItem("群组成员", "groupList", MappingItemValueType.stringList));
		return list;
	}

	public static List<MappingItem> roleMappings(Sheet sheet) throws Exception {
		List<MappingItem> list = roleMappings();
		mappingColumns(list, sheet);
		return list;
	}

	public static List<MappingItem> companyMappings() {
		List<MappingItem> list = new ArrayList<>();
		list.add(new MappingItem("名称", "name", MappingItemValueType.string));
		list.add(new MappingItem("显示名", "display", MappingItemValueType.string));
		list.add(new MappingItem("唯一编码", "unique", MappingItemValueType.string));
		list.add(new MappingItem("简称", "shortName", MappingItemValueType.string));
		list.add(new MappingItem("上级公司", "superior", MappingItemValueType.string));
		list.add(new MappingItem("级别", "level", MappingItemValueType.integer));
		list.add(new MappingItem("排序号", "orderNumber", MappingItemValueType.integer));
		list.add(new MappingItem("标识", "id", MappingItemValueType.string));
		list.add(new MappingItem("创建时间", "createTime", MappingItemValueType.date));
		list.add(new MappingItem("管理者", "controllerList", MappingItemValueType.stringList));
		return list;
	}

	public static List<MappingItem> companyMappings(Sheet sheet) throws Exception {
		List<MappingItem> list = companyMappings();
		mappingColumns(list, sheet);
		return list;
	}

	public static List<MappingItem> departmentMappings() {
		List<MappingItem> list = new ArrayList<>();
		list.add(new MappingItem("名称", "name", MappingItemValueType.string));
		list.add(new MappingItem("显示名", "display", MappingItemValueType.string));
		list.add(new MappingItem("唯一编码", "unique", MappingItemValueType.string));
		list.add(new MappingItem("简称", "shortName", MappingItemValueType.string));
		list.add(new MappingItem("上级部门", "superior", MappingItemValueType.string));
		list.add(new MappingItem("所属公司", "company", MappingItemValueType.string));
		list.add(new MappingItem("级别", "level", MappingItemValueType.integer));
		list.add(new MappingItem("排序号", "orderNumber", MappingItemValueType.integer));
		list.add(new MappingItem("标识", "id", MappingItemValueType.string));
		list.add(new MappingItem("创建时间", "createTime", MappingItemValueType.date));
		return list;
	}

	public static List<MappingItem> departmentMappings(Sheet sheet) throws Exception {
		List<MappingItem> list = departmentMappings();
		mappingColumns(list, sheet);
		return list;
	}

	public static List<MappingItem> identityMappings() {
		List<MappingItem> list = new ArrayList<>();
		list.add(new MappingItem("名称", "name", MappingItemValueType.string));
		list.add(new MappingItem("显示名", "display", MappingItemValueType.string));
		list.add(new MappingItem("唯一编码", "unique", MappingItemValueType.string));
		list.add(new MappingItem("所属人员", "person", MappingItemValueType.string));
		list.add(new MappingItem("所属部门", "department", MappingItemValueType.string));
		list.add(new MappingItem("排序号", "orderNumber", MappingItemValueType.integer));
		list.add(new MappingItem("标识", "id", MappingItemValueType.string));
		list.add(new MappingItem("下属身份", "juniorList", MappingItemValueType.stringList));
		list.add(new MappingItem("创建时间", "createTime", MappingItemValueType.date));
		return list;
	}

	public static List<MappingItem> identityMappings(Sheet sheet) throws Exception {
		List<MappingItem> list = identityMappings();
		mappingColumns(list, sheet);
		return list;
	}

	public static List<MappingItem> personAttributeMappings() {
		List<MappingItem> list = new ArrayList<>();
		list.add(new MappingItem("名称", "name", MappingItemValueType.string));
		list.add(new MappingItem("唯一编码", "unique", MappingItemValueType.string));
		list.add(new MappingItem("人员", "person", MappingItemValueType.string));
		list.add(new MappingItem("标识", "id", MappingItemValueType.string));
		list.add(new MappingItem("属性值", "attributeList", MappingItemValueType.stringList));
		list.add(new MappingItem("创建时间", "createTime", MappingItemValueType.date));
		return list;
	}

	public static List<MappingItem> personAttributeMappings(Sheet sheet) throws Exception {
		List<MappingItem> list = personAttributeMappings();
		mappingColumns(list, sheet);
		return list;
	}

	public static List<MappingItem> companyAttributeMappings() {
		List<MappingItem> list = new ArrayList<>();
		list.add(new MappingItem("名称", "name", MappingItemValueType.string));
		list.add(new MappingItem("唯一编码", "unique", MappingItemValueType.string));
		list.add(new MappingItem("公司", "company", MappingItemValueType.string));
		list.add(new MappingItem("标识", "id", MappingItemValueType.string));
		list.add(new MappingItem("属性值", "attributeList", MappingItemValueType.stringList));
		list.add(new MappingItem("创建时间", "createTime", MappingItemValueType.date));
		return list;
	}

	public static List<MappingItem> companyAttributeMappings(Sheet sheet) throws Exception {
		List<MappingItem> list = companyAttributeMappings();
		mappingColumns(list, sheet);
		return list;
	}

	public static List<MappingItem> companyDutyMappings() {
		List<MappingItem> list = new ArrayList<>();
		list.add(new MappingItem("名称", "name", MappingItemValueType.string));
		list.add(new MappingItem("唯一编码", "unique", MappingItemValueType.string));
		list.add(new MappingItem("公司", "company", MappingItemValueType.string));
		list.add(new MappingItem("标识", "id", MappingItemValueType.string));
		list.add(new MappingItem("成员", "identityList", MappingItemValueType.stringList));
		list.add(new MappingItem("创建时间", "createTime", MappingItemValueType.date));
		return list;
	}

	public static List<MappingItem> companyDutyMappings(Sheet sheet) throws Exception {
		List<MappingItem> list = companyDutyMappings();
		mappingColumns(list, sheet);
		return list;
	}

	public static List<MappingItem> departmentAttributeMappings() {
		List<MappingItem> list = new ArrayList<>();
		list.add(new MappingItem("名称", "name", MappingItemValueType.string));
		list.add(new MappingItem("唯一编码", "unique", MappingItemValueType.string));
		list.add(new MappingItem("部门", "department", MappingItemValueType.string));
		list.add(new MappingItem("标识", "id", MappingItemValueType.string));
		list.add(new MappingItem("属性值", "attributeList", MappingItemValueType.stringList));
		list.add(new MappingItem("创建时间", "createTime", MappingItemValueType.date));
		return list;
	}

	public static List<MappingItem> departmentAttributeMappings(Sheet sheet) throws Exception {
		List<MappingItem> list = departmentAttributeMappings();
		mappingColumns(list, sheet);
		return list;
	}

	public static List<MappingItem> departmentDutyMappings() {
		List<MappingItem> list = new ArrayList<>();
		list.add(new MappingItem("名称", "name", MappingItemValueType.string));
		list.add(new MappingItem("唯一编码", "unique", MappingItemValueType.string));
		list.add(new MappingItem("部门", "department", MappingItemValueType.string));
		list.add(new MappingItem("标识", "id", MappingItemValueType.string));
		list.add(new MappingItem("成员", "identityList", MappingItemValueType.stringList));
		list.add(new MappingItem("创建时间", "createTime", MappingItemValueType.date));
		return list;
	}

	public static List<MappingItem> departmentDutyMappings(Sheet sheet) throws Exception {
		List<MappingItem> list = departmentDutyMappings();
		mappingColumns(list, sheet);
		return list;
	}

	private static void mappingColumns(List<MappingItem> list, Sheet sheet) {
		Row row = sheet.getRow(sheet.getFirstRowNum());
		if (null != row) {
			Cell cell = null;
			for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
				cell = row.getCell(i);
				if (null != cell) {
					String str = cell.getStringCellValue();
					for (MappingItem o : list) {
						if (StringUtils.equalsIgnoreCase(o.getName(), str)) {
							o.setColumn(i);
						}
					}
				}
			}
		}
	}

	private String name;
	private String field;
	private MappingItemValueType type;
	private Integer column;

	public MappingItem(String name, String field, MappingItemValueType type) {
		this.name = name;
		this.field = field;
		this.type = type;
		this.column = -1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public MappingItemValueType getType() {
		return type;
	}

	public void setType(MappingItemValueType type) {
		this.type = type;
	}

	public Integer getColumn() {
		return column;
	}

	public void setColumn(Integer column) {
		this.column = column;
	}
}