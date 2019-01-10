package com.x.strategydeploy.assemble.control.inputmeasures;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.core.express.unit.UnitFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.measures.tools.VerifySequenceNumberTools;
import com.x.strategydeploy.core.entity.MeasuresInfo;

import net.sf.ehcache.Element;

public class ActionInput extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionInput.class);
	private static String DEPT_SEPARATOR = "、";
	private static int beginRow = 3;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				InputStream is = new ByteArrayInputStream(bytes);
				XSSFWorkbook workbook = new XSSFWorkbook(is);
				ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			// this.scan(business, workbook);
			String name = "measuers_result_" + DateTools.formatDate(new Date()) + ".xlsx";
			this.scanAndComment(business, workbook);
			// this.TestNumber(business, workbook);
			workbook.write(os);

			CacheInputResult cacheInputResult = new CacheInputResult();
			cacheInputResult.setName(name);
			cacheInputResult.setBytes(os.toByteArray());
			String flag = StringTools.uniqueToken();
			cache.put(new Element(flag, cacheInputResult));
			// ApplicationCache.notify(Person.class);
			// ApplicationCache.notify(Group.class);
			// ApplicationCache.notify(Role.class);
			// ApplicationCache.notify(Identity.class);
			// ApplicationCache.notify(PersonAttribute.class);
			Wo wo = new Wo();
			wo.setFlag(flag);
			result.setData(wo);
			return result;
		}
	}

	XSSFWorkbook scanAndComment(Business business, XSSFWorkbook workbook) {
		int NumberOfSheets = workbook.getNumberOfSheets();
		for (int i = 0; i < NumberOfSheets; i++) {
			logger.info("NumberOfSheet:" + i + ":" + workbook.getSheetName(i));
			XSSFSheet sheet = workbook.getSheetAt(i);
			int lastRowNum = sheet.getLastRowNum();
			Row row = null;
			XSSFComment comment = null;

			List<String> SequenceNumberList = new ArrayList<String>();
			boolean isRightNumber = true;
			String tmpsn = "";
			String measuresTitle = "";
			String workContent = "";
			String targetValue = "";
			String deptStringList = "";

			for (int j = beginRow; j <= lastRowNum; j++) {
				row = sheet.getRow(j);
				MeasuresInfo measuresinfo = new MeasuresInfo();
				for (Cell cell : row) {
					int ColumnIndex = cell.getColumnIndex();
					if (ColumnIndex == 0) {
						// 序号校验
						isRightNumber = VerifySequenceNumberTools.VerifySequenceNumber(this.getCellValue(cell));
						if (!isRightNumber) {
							comment = creatComment(sheet, workbook, row, cell, "序号不符合格式");
							cell.setCellComment(comment);
						} else {
							tmpsn = this.getCellValue(cell);
							if (SequenceNumberList.indexOf(tmpsn) >= 0) {
								logger.info("CellType:" + cell.getCellType());
								logger.info("SequenceNumberList:"
										+ SequenceNumberList.get(SequenceNumberList.indexOf(tmpsn)) + "=" + tmpsn);
								comment = creatComment(sheet, workbook, row, cell, "序号重复");
								cell.setCellComment(comment);
							} else {
								logger.info("页签：" + i + "，行：" + j + "，列：" + ColumnIndex + "。序号：：" + tmpsn);
								SequenceNumberList.add("" + tmpsn + "");
							}
						}

					}

					if (ColumnIndex == 1) {
						// 关键举措校验
						measuresTitle = this.getCellValue(cell);
						if (null == StringUtils.trimToNull(measuresTitle)) {
							comment = creatComment(sheet, workbook, row, cell, "关键举措为空");
							cell.setCellComment(comment);
						}
					}

					if (ColumnIndex == 2) {
						// 工作内容校验
						workContent = this.getCellValue(cell);
						if (null == StringUtils.trimToNull(workContent)) {
							comment = creatComment(sheet, workbook, row, cell, "工作内容为空");
							cell.setCellComment(comment);
						}
					}
					if (ColumnIndex == 3) {
						// 目标值校验
						targetValue = this.getCellValue(cell);
						if (null == StringUtils.trimToNull(targetValue)) {
							comment = creatComment(sheet, workbook, row, cell, "目标值为空");
							cell.setCellComment(comment);
						}
					}
					if (ColumnIndex == 4) {
						// 牵头部门
						deptStringList = this.getCellValue(cell);
						if (StringUtils.isEmpty(StringUtils.trim(deptStringList))) {
							// 牵头部门，如果字段为空，就什么都不做。
						} else {
							List<String> deptlist = new ArrayList<String>();
							deptlist.addAll(Arrays.asList(deptStringList.split(DEPT_SEPARATOR)));
							for (String deptstring : deptlist) {
								deptstring = StringUtils.trim(deptstring);
								try {
									if (!checkUnitByUnitName(business, deptstring)) {
										comment = creatComment(sheet, workbook, row, cell, "名称与组织名称不对相应");
										cell.setCellComment(comment);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}

					if (ColumnIndex == 5) {
						// 责任部门
						deptStringList = this.getCellValue(cell);
						if (StringUtils.isEmpty(StringUtils.trim(deptStringList))) {
							comment = creatComment(sheet, workbook, row, cell, "责任部门不能为空");
							cell.setCellComment(comment);
						} else {
							List<String> deptlist = new ArrayList<String>();
							deptlist.addAll(Arrays.asList(deptStringList.split(DEPT_SEPARATOR)));
							for (String deptstring : deptlist) {
								deptstring = StringUtils.trim(deptstring);
								try {
									if (!checkUnitByUnitName(business, deptstring)) {
										comment = creatComment(sheet, workbook, row, cell, "名称与组织名称不对相应");
										cell.setCellComment(comment);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}

					// if (ColumnIndex == 6) {
					// //支撑部门
					// deptStringList = this.getCellValue(cell);
					// if (StringUtils.isEmpty(StringUtils.trim(deptStringList))) {
					// //支撑部门，如果字段为空，就什么都不做。
					// } else {
					// List<String> deptlist = new ArrayList<String>();
					// deptlist.addAll(Arrays.asList(deptStringList.split(DEPT_SEPARATOR)));
					// for (String deptstring : deptlist) {
					// deptstring = StringUtils.trim(deptstring);
					// try {
					// if (!checkUnitByUnitName(business, deptstring)) {
					// //throw new Exception("第:" + (j + 1) + "行，第:" + ColumnIndex + "列，支撑部门中的:" +
					// deptstring + "名称与组织名称不对相应。");
					// comment = creatComment(sheet, workbook, row, cell, "名称与组织名称不对相应");
					// cell.setCellComment(comment);
					// }
					// } catch (Exception e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					// }
					// }
					// }

				}
			}

		}
		return workbook;
	}

	XSSFComment creatComment(XSSFSheet sheet, XSSFWorkbook workbook, Row row, Cell cell, String commentStr) {
		// https://www.songliguo.com/java-poi-excel-import-data.html
		XSSFDrawing p = ((XSSFSheet) sheet).createDrawingPatriarch();
		// XSSFCell cell = (XSSFCell) sheet.getRow(row).getCell(col);
		if (cell == null) {
			// 如果你获取的单元格是空进行创建新的cell，再追加批注，不然null指针
			cell = row.createCell((short) 0);
		}
		XSSFComment comment = p.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
		XSSFRichTextString rtf = new XSSFRichTextString(commentStr);
		XSSFFont commentFormatter = (XSSFFont) workbook.createFont();
		rtf.applyFont(commentFormatter);
		comment.setString(rtf);
		return comment;
	}

	boolean checkUnitByUnitName(Business business, String unitName) throws Exception {
		UnitFactory unitFactory = business.organization().unit();
		// 获取unitName的distinguishedName
		String distinguishedName = "";
		distinguishedName = unitFactory.get(unitName);
		if (StringUtils.isEmpty(distinguishedName)) {
			return false;
		} else {
			return true;
		}
	}

	String getCellValue(Cell cell) {
		if (cell == null)
			return "";
		if (cell.getCellType() == CellType.STRING) {
			return cell.getStringCellValue();
		} else if (cell.getCellType() == CellType.BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		} else if (cell.getCellType() == CellType.FORMULA) {
			return cell.getCellFormula();
		} else if (cell.getCellType() == CellType.NUMERIC) {
			DataFormatter formatter = new DataFormatter();
			String _str = formatter.formatCellValue(cell);
			return String.valueOf(_str);
		}
		return "";
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("返回的结果标识")
		private String flag;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

	}

	XSSFWorkbook TestNumber(Business business, XSSFWorkbook workbook) {
		int NumberOfSheets = workbook.getNumberOfSheets();
		for (int i = 0; i < NumberOfSheets; i++) {
			XSSFSheet sheet = workbook.getSheetAt(i);
			int lastRowNum = sheet.getLastRowNum();
			Row row = null;
			XSSFComment comment = null;
			List<String> SequenceNumberList = new ArrayList<String>();
			boolean isRightNumber = true;
			String tmpsn = "";
			String measuresTitle = "";
			String workContent = "";
			String targetValue = "";
			String deptStringList = "";
			for (int j = beginRow; j <= lastRowNum; j++) {
				row = sheet.getRow(j);
				for (Cell cell : row) {
					int ColumnIndex = cell.getColumnIndex();
					if (ColumnIndex == 0) {
						DataFormatter formatter = new DataFormatter();
						tmpsn = formatter.formatCellValue(cell);
						logger.info("页签：" + sheet.getSheetName() + "，行：" + j + "，列：" + ColumnIndex + "。序号：："
								+ String.valueOf(tmpsn) + "=");
					}
					if (ColumnIndex == 1) {
					}
					if (ColumnIndex == 2) {
					}
					if (ColumnIndex == 3) {
					}
					if (ColumnIndex == 4) {
					}
					if (ColumnIndex == 5) {
					}
				}
			}
			logger.info("-------------------------------------------------");
		}
		return workbook;
	}

}
