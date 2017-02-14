package com.x.base.core.entity.annotation.tools;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.x.base.core.entity.annotation.EntityFieldDescribe;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class EntityFieldDescribeWriter {
	public static void main(String... args) {
		EntityFieldDescribeWriter entityFieldDescribeWriter = new EntityFieldDescribeWriter();
		entityFieldDescribeWriter.write(args[0], args[1]);
	}

	public void write(String directory, String project) {
		try {
			String prefix = "com." + StringUtils.replace(project, "_", ".");
			List<Class<?>> classes = this.scanEntityClass(prefix);
			for (Class<?> clz : classes) {
				this.createEntityFieldDescribe(clz, directory);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<Class<?>> scanEntityClass(String prefix) throws Exception {
		ScanResult scanResult = new FastClasspathScanner(prefix).scan();
		List<Class<?>> sortedList = new ArrayList<Class<?>>();
		for (String str : scanResult.getNamesOfClassesWithAnnotation(Entity.class)) {
			sortedList.add(Class.forName(str));
		}
		Collections.sort(sortedList, new Comparator<Class<?>>() {
			public int compare(Class<?> c1, Class<?> c2) {
				return c1.getCanonicalName().compareTo(c2.getCanonicalName());
			}
		});
		return sortedList;
	}

	private void createEntityFieldDescribe(Class<?> clz, String directory) throws Exception {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet(clz.getSimpleName());
		sheet.setColumnWidth(0, 256 * 40);
		sheet.setColumnWidth(1, 256 * 80);
		sheet.setColumnWidth(2, 256 * 100);
		CellStyle headStyle = this.headCellStyle(wb);
		Row row = sheet.createRow(0);
		row.setHeightInPoints(25);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
		Cell cell = row.createCell(0);
		cell.setCellValue(clz.getCanonicalName());
		cell.setCellStyle(headStyle);
		row = sheet.createRow(1);
		row.setHeightInPoints(25);
		cell = row.createCell(0);
		cell.setCellValue("Field");
		cell.setCellStyle(headStyle);
		cell = row.createCell(1);
		cell.setCellValue("Type");
		cell.setCellStyle(headStyle);
		cell = row.createCell(2);
		cell.setCellValue("Describe");
		cell.setCellStyle(headStyle);
		int i = 2;
		CellStyle style = this.valueCellStyle(wb);
		List<Field> fields = Arrays.asList(FieldUtils.getAllFields(clz));
		Collections.sort(fields, new Comparator<Field>() {
			public int compare(Field f1, Field f2) {
				return f1.getName().compareTo(f2.getName());
			}
		});
		for (Field fld : fields) {
			if (!Modifier.isStatic(fld.getModifiers())) {
				row = sheet.createRow(i++);
				// cell 0
				cell = row.createCell(0);
				cell.setCellValue(fld.getName());
				cell.setCellStyle(style);
				// cell 1
				cell = row.createCell(1);
				cell.setCellValue(fld.getType().getTypeName());
				cell.setCellStyle(style);
				// cell 2
				String describe = "";
				EntityFieldDescribe entityFieldDescribe = fld.getAnnotation(EntityFieldDescribe.class);
				if (null != entityFieldDescribe) {
					describe = entityFieldDescribe.value();
				}
				cell = row.createCell(2);
				cell.setCellValue(describe);
				cell.setCellStyle(style);
			}
		}
		String filePath = directory + "/" + clz.getSimpleName() + ".xls";
		FileOutputStream fileOut = new FileOutputStream(filePath);
		wb.write(fileOut);
		wb.close();
		fileOut.close();
		System.out.println("create:" + filePath);
	}

	private CellStyle headCellStyle(Workbook wb) throws Exception {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 12);
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		return style;
	}

	private CellStyle valueCellStyle(Workbook wb) throws Exception {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 12);
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		return style;
	}

}