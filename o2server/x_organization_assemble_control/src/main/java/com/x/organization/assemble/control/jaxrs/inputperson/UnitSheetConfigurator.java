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

public class UnitSheetConfigurator extends GsonPropertyObject {

	private static final Pattern attributePattern = Pattern.compile("^\\((.+?)\\)$");

	private Integer sheetIndex;
	private Integer memoColumn;
	private Integer firstRow;
	private Integer lastRow;

	private Integer nameColumn;
	private Integer uniqueColumn;
	private Integer unitTypeColumn;
	private Integer superiorColumn;
	private Integer orderNumberColumn;
	private Integer descriptionColumn;

	private Map<String, Integer> attributes = new HashMap<>();

	public UnitSheetConfigurator(XSSFWorkbook workbook, Sheet sheet) {
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
					} else if (unitTypeItems.contains(str)) {
						this.unitTypeColumn = i;
					} else if (superiorItems.contains(str)) {
						this.superiorColumn = i;
					} else if (orderNumberItems.contains(str)) {
						this.orderNumberColumn = i;
					} else if (descriptionItems.contains(str)) {
						this.descriptionColumn = i;
					}else {
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

	private static List<String> nameItems = Arrays.asList(new String[] { "组织名称 *", "name" });
	private static List<String> uniqueItems = Arrays.asList(new String[] { "唯一编码 *", "组织编号 *", "unique" });
	private static List<String> unitTypeItems = Arrays.asList(new String[] { "组织级别编号 *", "组织级别名称 *", "unitType" });
	private static List<String> superiorItems = Arrays.asList(new String[] { "上级组织", "上级组织编号", "superior"});
	private static List<String> orderNumberItems = Arrays.asList(new String[] { "排序", "排序号", "orderNumber" });
	private static List<String> descriptionItems = Arrays.asList(new String[] { "描述", "备注", "description" });

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

	public Integer getUnitTypeColumn() {
		return unitTypeColumn;
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

	public Integer getOrderNumberColumn() {
		return orderNumberColumn;
	}
	
	public Integer getDescriptionColumn() {
		return descriptionColumn;
	}
	
	public Integer getSuperiorColumn() {
		return superiorColumn;
	}

	public Integer getSheetIndex() {
		return sheetIndex;
	}
}