package com.x.organization.assemble.control.jaxrs.inputperson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.project.gson.GsonPropertyObject;

public class PersonSheetConfigurator extends GsonPropertyObject {

	private static final Pattern attributePattern = Pattern.compile("^\\((.+?)\\)$");

	private Integer sheetIndex;
	private Integer memoColumn;
	private Integer firstRow;
	private Integer lastRow;

	private Integer nameColumn;
	private Integer uniqueColumn;
	private Integer employeeColumn;
	private Integer mobileColumn;
	private Integer officePhoneColumn;
	private Integer genderTypeColumn;
	private Integer mailColumn;

	private Map<String, Integer> attributes = new HashMap<>();

	public PersonSheetConfigurator(XSSFWorkbook workbook, Sheet sheet) {
		this.sheetIndex = workbook.getSheetIndex(sheet);
		Row row = sheet.getRow(sheet.getFirstRowNum());
		this.firstRow = sheet.getFirstRowNum() + 1;
		this.lastRow = sheet.getLastRowNum();
		memoColumn = row.getLastCellNum() + 1;
		for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
			Cell cell = row.getCell(i);
			if (null != cell) {
				String str = this.getCellStringValue(cell);
				if (StringUtils.isNotEmpty(str)) {
					if (nameItems.contains(str)) {
						this.nameColumn = i;
					} else if (uniqueItems.contains(str)) {
						this.uniqueColumn = i;
					} else if (employeeItems.contains(str)) {
						this.employeeColumn = i;
					} else if (mobileItems.contains(str)) {
						this.mobileColumn = i;
					} else if (mailItems.contains(str)) {
						this.mailColumn = i;
					} else if (genderTypeItems.contains(str)) {
						this.genderTypeColumn = i;
					} else if (officePhoneItems.contains(str)) {
						this.officePhoneColumn = i;
					} else {
						Matcher matcher = attributePattern.matcher(str);
						if (matcher.matches()) {
							String attribute = matcher.group(1);
							this.attributes.put(attribute, new Integer(i));
						}
					}
				}
			}
		}
	}

	private static List<String> nameItems = Arrays.asList(new String[] { "人员姓名 *", "人员姓名", "name" });
	private static List<String> employeeItems = Arrays.asList(new String[] { "人员编号 *", "人员编号", "unique" });
	private static List<String> uniqueItems = Arrays.asList(new String[] { "唯一编码 *", "员工账号 *", "employee" });
	private static List<String> mobileItems = Arrays.asList(new String[] { "手机号码 *", "手机", "联系电话", "phone", "mobile" });
	private static List<String> officePhoneItems = Arrays.asList(new String[] { "办公电话", "办公室电话", "工作电话", "officePhone" });
	private static List<String> mailItems = Arrays.asList(new String[] { "电子邮件", "邮件", "邮箱", "邮件地址", "mail", "email" });
	private static List<String> genderTypeItems = Arrays.asList(new String[] { "性别", "gender", "genderType" });

	public String getCellStringValue(Cell cell) {
		if (null != cell) {
			switch (cell.getCellType()) {
			case BLANK:
				return "";
			case BOOLEAN:
				return BooleanUtils.toString(cell.getBooleanCellValue(), "true", "false", "false");
			case ERROR:
				return "";
			case FORMULA:
				return "";
			case NUMERIC:
				Double d = cell.getNumericCellValue();
				Long l = d.longValue();
				if (l.doubleValue() == d) {
					return l.toString();
				} else {
					return d.toString();
				}
			default:
				return cell.getStringCellValue();
			}
		}
		return "";
	}

	public Integer getMemoColumn() {
		return memoColumn;
	}

	public Integer getNameColumn() {
		return nameColumn;
	}

	public Integer getUniqueColumn() {
		return uniqueColumn;
	}

	public Integer getEmployeeColumn() {
		return employeeColumn;
	}

	public Integer getMobileColumn() {
		return mobileColumn;
	}

	public Map<String, Integer> getAttributes() {
		return attributes;
	}

	public Integer getFirstRow() {
		return firstRow;
	}

	public Integer getLastRow() {
		return lastRow;
	}

	public Integer getGenderTypeColumn() {
		return genderTypeColumn;
	}

	public Integer getMailColumn() {
		return mailColumn;
	}
	
	public Integer getOfficePhoneColumn() {
		return officePhoneColumn; 
	}

	public Integer getSheetIndex() {
		return sheetIndex;
	}

}