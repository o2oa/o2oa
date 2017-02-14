package com.x.base.core.http.annotation.tools;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.http.annotation.Wrap;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class HttpMethodDescribeWriter {

	private static List<String> webServletMethods = new ArrayList<>();

	static {
		webServletMethods.add("doPost");
		webServletMethods.add("doPut");
		webServletMethods.add("doGet");
	}

	public static void main(String... args) {
		HttpMethodDescribeWriter httpMethodDescribeWriter = new HttpMethodDescribeWriter();
		httpMethodDescribeWriter.write(args[0], args[1]);
	}

	public void write(String directory, String name) {
		try {
			List<Class<?>> classes = this.scanHttpServiceClass(name);
			if (!classes.isEmpty()) {
				Workbook wb = new HSSFWorkbook();
				CellStyle style = this.valueCellStyle(wb);
				for (Class<?> clz : classes) {
					Sheet sheet = this.createSheet(wb, clz);
					if (clz.isAnnotationPresent(Path.class)) {
						List<Method> methods = this.scanJaxrsMethod(clz);
						Path typePath = clz.getAnnotation(Path.class);
						for (Method method : methods) {
							addRow(sheet, style, typePath, method);
						}
					} else if (clz.isAnnotationPresent(ServerEndpoint.class)) {
						ServerEndpoint serverEndpoint = clz.getAnnotation(ServerEndpoint.class);
						addRow(sheet, style, serverEndpoint.value(), "", "");
					} else if (clz.isAnnotationPresent(WebServlet.class)) {
						WebServlet webServlet = clz.getAnnotation(WebServlet.class);
						for (Method m : MethodUtils.getMethodsWithAnnotation(clz, HttpMethodDescribe.class)) {
							if (webServletMethods.contains(m.getName())) {
								addRow(sheet, style, StringUtils.join(webServlet.value()), m.getName(),
										webServlet.description());
							}
						}
					}
				}
				String filePath = directory + "/" + name + ".xls";
				FileOutputStream fileOut = new FileOutputStream(filePath);
				wb.write(fileOut);
				wb.close();
				fileOut.close();
				System.out.println("create:" + filePath);
				for (Class<?> clz : this.scanWrapClass(name)) {
					createWrapDescribe(clz, directory);
				}
				// for (Class<?> clz : this.scanWrapOutClass(name)) {
				// createWrapOutDescribe(clz, directory);
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getHttpMethod(Method m) throws Exception {
		if (null != m.getAnnotation(GET.class)) {
			return "GET";
		} else if (null != m.getAnnotation(POST.class)) {
			return "POST";
		} else if (null != m.getAnnotation(PUT.class)) {
			return "PUT";
		} else if (null != m.getAnnotation(DELETE.class)) {
			return "DELETE";
		} else if (null != m.getAnnotation(OPTIONS.class)) {
			return "OPTIONS";
		} else if (null != m.getAnnotation(HEAD.class)) {
			return "HEAD";
		} else {
			return "OTHER";
		}
	}

	private List<Class<?>> scanHttpServiceClass(String name) throws Exception {
		String pack = "com." + name.replaceAll("_", ".");
		ScanResult scanResult = new FastClasspathScanner(pack).scan();
		SetUniqueList<String> list = SetUniqueList.setUniqueList(new ArrayList<String>());
		list.addAll(scanResult.getNamesOfClassesWithAnnotation(Path.class));
		list.addAll(scanResult.getNamesOfClassesWithAnnotation(ServerEndpoint.class));
		list.addAll(scanResult.getNamesOfClassesWithAnnotation(WebServlet.class));
		List<Class<?>> sortedList = new ArrayList<Class<?>>();
		for (String str : list) {
			sortedList.add(Class.forName(str));
		}
		Collections.sort(sortedList, new Comparator<Class<?>>() {
			public int compare(Class<?> c1, Class<?> c2) {
				return c1.getName().compareTo(c2.getName());
			}
		});
		return sortedList;
	}

	@SuppressWarnings("unchecked")
	private List<Method> scanJaxrsMethod(Class<?> clz) throws Exception {
		Set<Method> methods = new HashSet<>();
		for (Method m : MethodUtils.getMethodsWithAnnotation(clz, Path.class)) {
			methods.add(m);
		}
		for (Method m : MethodUtils.getMethodsWithAnnotation(clz, GET.class)) {
			methods.add(m);
		}
		for (Method m : MethodUtils.getMethodsWithAnnotation(clz, POST.class)) {
			methods.add(m);
		}
		for (Method m : MethodUtils.getMethodsWithAnnotation(clz, PUT.class)) {
			methods.add(m);
		}
		for (Method m : MethodUtils.getMethodsWithAnnotation(clz, DELETE.class)) {
			methods.add(m);
		}
		for (Method m : MethodUtils.getMethodsWithAnnotation(clz, OPTIONS.class)) {
			methods.add(m);
		}
		for (Method m : MethodUtils.getMethodsWithAnnotation(clz, HEAD.class)) {
			methods.add(m);
		}
		List<Method> sortedList = new ArrayList<>(methods);
		Collections.sort(sortedList, new Comparator<Method>() {
			public int compare(Method m1, Method m2) {
				Path p1 = m1.getAnnotation(Path.class);
				String v1 = "/";
				if (null != p1 && null != p1.value()) {
					v1 = p1.value();
				}
				String v2 = "/";
				Path p2 = m2.getAnnotation(Path.class);
				if (null != p2 && null != p2.value()) {
					v2 = p2.value();
				}
				return v1.compareTo(v2);
			}
		});
		return sortedList;
	}

	private Sheet createSheet(Workbook wb, Class<?> clz) throws Exception {
		System.out.println("create sheet:" + clz.getName() + "(" + clz.getSimpleName() + ")");
		Sheet sheet = wb.createSheet(clz.getSimpleName());
		CellStyle style = this.headCellStyle(wb);
		Row row = sheet.createRow(0);
		row.setHeightInPoints(25);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
		Cell cell = row.createCell(0);
		cell.setCellValue(clz.getCanonicalName());
		cell.setCellStyle(style);
		row = sheet.createRow(1);
		row.setHeightInPoints(25);
		cell = row.createCell(0);
		cell.setCellValue("path");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("method");
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue("request");
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue("describe");
		cell.setCellStyle(style);
		sheet.setColumnWidth(0, 256 * 80);
		sheet.setColumnWidth(1, 256 * 12);
		sheet.setColumnWidth(2, 256 * 30);
		sheet.setColumnWidth(3, 256 * 100);
		return sheet;
	}

	private void addRow(Sheet sheet, CellStyle style, Path typePath, Method method) throws Exception {
		Path methodPath = method.getAnnotation(Path.class);
		HttpMethodDescribe httpMethodDescribe = method.getAnnotation(HttpMethodDescribe.class);
		Row row = sheet.createRow(sheet.getLastRowNum() + 1);
		row.setHeightInPoints(25);
		// cell 0
		Cell cell = row.createCell(0);
		String path = "jaxrs/" + typePath.value();
		if (null != methodPath) {
			path = path + "/" + methodPath.value();
		}
		cell.setCellValue(path);
		cell.setCellStyle(style);
		// cell 1
		cell = row.createCell(1);
		cell.setCellValue(getHttpMethod(method));
		cell.setCellStyle(style);
		// cell 2
		cell = row.createCell(2);
		List<String> requests = new ArrayList<String>();
		for (Class<?> clz : method.getParameterTypes()) {
			if (clz.isAnnotationPresent(Wrap.class)) {
				requests.add(clz.getSimpleName());
			}
		}
		cell.setCellValue(StringUtils.join(requests, ","));
		cell.setCellStyle(style);
		// cell 3
		cell = row.createCell(3);
		String describe = "";
		if (null != httpMethodDescribe) {
			describe = httpMethodDescribe.value();
		}
		cell.setCellValue(describe);
		cell.setCellStyle(style);
	}

	private void addRow(Sheet sheet, CellStyle style, String path, String method, String describe) throws Exception {
		Row row = sheet.createRow(sheet.getLastRowNum() + 1);
		row.setHeightInPoints(25);
		// cell 0
		Cell cell = row.createCell(0);
		cell.setCellValue(path);
		cell.setCellStyle(style);
		// cell 1
		cell = row.createCell(1);
		cell.setCellValue(method);
		cell.setCellStyle(style);
		// cell 2
		cell = row.createCell(2);
		cell.setCellValue("");
		cell.setCellStyle(style);
		// cell 3
		cell = row.createCell(3);
		cell.setCellValue(describe);
		cell.setCellStyle(style);
	}

	private Set<Class<?>> scanWrapClass(String name) throws Exception {
		Set<Class<?>> set = new HashSet<Class<?>>();
		String pack = "com." + name.replaceAll("_", ".");
		ScanResult scanResult = new FastClasspathScanner(pack).scan();
		for (String str : scanResult.getNamesOfClassesWithAnnotation(Wrap.class)) {
			Class<?> clz = Class.forName(str);
			if (!Modifier.isAbstract(clz.getModifiers())) {
				set.add(clz);
			}
		}
		return set;
	}

	private void createWrapDescribe(Class<?> clz, String directory) throws Exception {
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet(clz.getCanonicalName());
		sheet.setColumnWidth(0, 256 * 40);
		sheet.setColumnWidth(1, 256 * 80);
		sheet.setColumnWidth(2, 256 * 100);
		Row row = sheet.createRow(0);
		row.setHeightInPoints(25);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
		CellStyle headStyle = this.headCellStyle(wb);
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
		Wrap wrapIn = clz.getAnnotation(Wrap.class);
		List<Field> wrappedFields = new ArrayList<Field>();
		if (null != wrapIn) {
			wrappedFields = FieldUtils.getAllFieldsList(wrapIn.value());
		}
		
		EntityFieldDescribe efd = null;
		for (Field fld : FieldUtils.getAllFields(clz)) {
			row = sheet.createRow(i++);
			cell = row.createCell(0);
			cell.setCellValue(fld.getName());
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue(fld.getType().getTypeName());
			cell.setCellStyle(style);
			cell = row.createCell(2);
			String describe = "---";
			
			/**
			 * 2016-11-10 李义修改
			 * 如果Wrap类中对属性有注释@EntityFieldDescribe内容，则使用Wrap类自己对属性的注释
			 * 如果没有，则取相应的Wrap所指定的类中名称相同的属性的@EntityFieldDescribe注释内容。
			 * ================================================================================================
			 */
			efd = fld.getAnnotation(EntityFieldDescribe.class);
			if ( null != efd ) { 
				describe = efd.value();
			}else{
				if ( null != wrapIn ) {
					for ( Field f : wrappedFields ) {
						if ( f.getName().equals(fld.getName() ) ) {
							efd = f.getAnnotation(EntityFieldDescribe.class);
							if (null != efd) { describe = efd.value(); }
							break;
						}
					}
				}
			}
			/**
			 * ================================================================================================
			 */
			cell.setCellValue(describe);
			cell.setCellStyle(style);
		}
		String filePath = directory + "/" + clz.getSimpleName() + ".xls";
		FileOutputStream fileOut = new FileOutputStream(filePath);
		wb.write(fileOut);
		wb.close();
		fileOut.close();
		System.out.println("create:" + filePath);
	}

	// private void createWrapOutDescribe(Class<?> clz, String directory) throws
	// Exception {
	// Workbook wb = new HSSFWorkbook();
	// Sheet sheet = wb.createSheet(clz.getCanonicalName());
	// sheet.setColumnWidth(0, 256 * 40);
	// sheet.setColumnWidth(1, 256 * 80);
	// sheet.setColumnWidth(2, 256 * 100);
	// Row row = sheet.createRow(0);
	// row.setHeightInPoints(25);
	// sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
	// CellStyle headStyle = this.headCellStyle(wb);
	// Cell cell = row.createCell(0);
	// cell.setCellValue(clz.getCanonicalName());
	// cell.setCellStyle(headStyle);
	// row = sheet.createRow(1);
	// row.setHeightInPoints(25);
	// cell = row.createCell(0);
	// cell.setCellValue("Field");
	// cell.setCellStyle(headStyle);
	// cell = row.createCell(1);
	// cell.setCellValue("Type");
	// cell.setCellStyle(headStyle);
	// cell = row.createCell(2);
	// cell.setCellValue("Describe");
	// cell.setCellStyle(headStyle);
	// int i = 2;
	// CellStyle style = this.valueCellStyle(wb);
	// Wrap wrapOut = clz.getAnnotation(Wrap.class);
	// List<Field> wrappedFields = new ArrayList<Field>();
	// if (null != wrapOut) {
	// wrappedFields = FieldUtils.getAllFieldsList(wrapOut.value());
	// }
	// for (Field fld : FieldUtils.getAllFields(clz)) {
	// row = sheet.createRow(i++);
	// cell = row.createCell(0);
	// cell.setCellValue(fld.getName());
	// cell.setCellStyle(style);
	// cell = row.createCell(1);
	// cell.setCellValue(fld.getType().getTypeName());
	// cell.setCellStyle(style);
	// cell = row.createCell(2);
	// String describe = "---";
	// if (null != wrapOut) {
	// for (Field f : wrappedFields) {
	// if (f.getName().equals(fld.getName())) {
	// EntityFieldDescribe efd = f.getAnnotation(EntityFieldDescribe.class);
	// if (null != efd) {
	// describe = efd.value();
	// }
	// break;
	// }
	// }
	// }
	// cell.setCellValue(describe);
	// cell.setCellStyle(style);
	// }
	// String filePath = directory + "/" + clz.getSimpleName() + ".xls";
	// FileOutputStream fileOut = new FileOutputStream(filePath);
	// wb.write(fileOut);
	// wb.close();
	// fileOut.close();
	// System.out.println("create:" + filePath);
	// }

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