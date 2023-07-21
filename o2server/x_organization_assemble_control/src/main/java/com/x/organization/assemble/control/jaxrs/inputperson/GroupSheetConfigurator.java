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

public class GroupSheetConfigurator extends GsonPropertyObject {

	private static final Pattern attributePattern = Pattern.compile("^\\((.+?)\\)$");

	private Integer sheetIndex;
	private Integer memoColumn;
	private Integer firstRow;
	private Integer lastRow;

	private Integer nameColumn;
	private Integer uniqueColumn;
	private Integer personCodeColumn;
	private Integer identityCodeColumn;
	private Integer unitCodeColumn;
	private Integer groupCodeColumn;
	private Integer descriptionColumn;

	private Map<String, Integer> attributes = new HashMap<>();

	public GroupSheetConfigurator(XSSFWorkbook workbook, Sheet sheet) {
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
					if (uniqueItems.contains(str)) {
						this.uniqueColumn = i;
					} else if (nameItems.contains(str)) {
						this.nameColumn = i;
					}else if (personCodeItems.contains(str)) {
						this.personCodeColumn = i;
					}else if (identityCodeItems.contains(str)) {
						this.identityCodeColumn = i;
					}else if (unitCodeItems.contains(str)) {
						this.unitCodeColumn = i;
					}else if (groupCodeItems.contains(str)) {
						this.groupCodeColumn = i;
					}else if (descriptionItems.contains(str)) {
						this.descriptionColumn = i;
					}else {
						Matcher matcher = attributePattern.matcher(str);
						if (matcher.matches()) {
							String attribute = matcher.group(1);
							this.attributes.put(attribute, 1);
						}
					}
				}
			}
		}
	}

	private static List<String> uniqueItems = Arrays.asList(new String[] { "群组编码 *",  "unique" });
	private static List<String> nameItems = Arrays.asList(new String[] { "群组名称 *", "name" });
	private static List<String> personCodeItems = Arrays.asList(new String[] { "人员编号", "人员唯一编码" });
	private static List<String> identityCodeItems = Arrays.asList(new String[] { "身份编号", "身份唯一编码" });
	private static List<String> unitCodeItems = Arrays.asList(new String[] { "组织编号", "组织唯一编码" });
	private static List<String> groupCodeItems = Arrays.asList(new String[] { "子群组编码", "groupCode" });
	private static List<String> descriptionItems = Arrays.asList(new String[] { "描述","群组描述", "description" });

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

	public Integer getUniqueColumn() {
		return uniqueColumn;
	}

	public Integer getNameColumn() {
		return nameColumn;
	}

	public Integer getPersonCodeColumn() {
		return personCodeColumn;
	}

	public Integer getIdentityCodeColumn() {
		return identityCodeColumn;
	}

	public Integer getUnitCodeColumn() {
		return unitCodeColumn;
	}

	public Integer getGroupCodeColumn() {
		return groupCodeColumn;
	}

	public Integer getDescriptionColumn() {
		return descriptionColumn;
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

	public Integer getSheetIndex() {
		return sheetIndex;
	}

}
