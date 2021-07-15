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

public class IdentitySheetConfigurator extends GsonPropertyObject {

	private static final Pattern attributePattern = Pattern.compile("^\\((.+?)\\)$");

	private Integer sheetIndex;
	private Integer memoColumn;
	private Integer firstRow;
	private Integer lastRow;

	private Integer uniqueColumn;
	private Integer unitCodeColumn;
	private Integer majorColumn;
	private Integer identityUniqueColumn;

	private Map<String, Integer> attributes = new HashMap<>();

	public IdentitySheetConfigurator(XSSFWorkbook workbook, Sheet sheet) {
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
					} else if (unitCodeItems.contains(str)) {
						this.unitCodeColumn = i;
					}  else if (majorItems.contains(str)) {
						this.majorColumn = i;
					}else if (identityUniqueItems.contains(str)) {
						this.identityUniqueColumn = i;
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

	private static List<String> uniqueItems = Arrays.asList(new String[] { "人员唯一编码 *", "员工账号 *", "unique" });
	private static List<String> unitCodeItems = Arrays.asList(new String[] { "组织编号 *", "组织唯一编码 *", "unitCode" });
	private static List<String> majorItems = Arrays.asList(new String[] { "主兼职","major" });
	private static List<String> identityUniqueItems = Arrays.asList(new String[] { "身份编码",  "identityUnique"});

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

	public Integer getUnitCodeColumn() { 
		return unitCodeColumn;
	}

	public Integer getMajorColumn() {
		return majorColumn;
	}

	public Map<String, Integer> getAttributes() {
		return attributes;
	}

	public Integer getIdentityUniqueColumnColumn() {
		return identityUniqueColumn;
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