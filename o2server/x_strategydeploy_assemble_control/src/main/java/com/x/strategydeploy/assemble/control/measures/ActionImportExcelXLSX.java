package com.x.strategydeploy.assemble.control.measures;

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
import org.apache.poi.ss.usermodel.Sheet;
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
import com.x.strategydeploy.assemble.control.service.MeasuresInfoExcuteSave;
import com.x.strategydeploy.core.entity.MeasuresInfo;

import net.sf.ehcache.Element;

public class ActionImportExcelXLSX extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionImportExcelXLSX.class);
	private static String DEPT_SEPARATOR = "、";
	private static int beginRow = 3;
	//private static int maxRowNum = 299;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition, String year, String parentid, String sheetsequence) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create(); InputStream is = new ByteArrayInputStream(bytes);) {
			Business business = new Business(emc);
			XSSFWorkbook workbook = new XSSFWorkbook(is);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			String name = "measuers_result_" + DateTools.formatDate(new Date()) + ".xlsx";

			String flag = StringTools.uniqueToken();
			Wo wo = new Wo();

			Integer sheetnum = Integer.valueOf(sheetsequence);
			sheetnum--;
			WorkbookAndBoolean _workbookAndBoolean = new WorkbookAndBoolean();
			_workbookAndBoolean = this.scanAndCheck(business, workbook, sheetnum);
			workbook = _workbookAndBoolean.getWorkbook();
			workbook.write(os);

			CacheInputResult cacheInputResult = new CacheInputResult();
			cacheInputResult.setName(name);
			cacheInputResult.setBytes(os.toByteArray());
			cache.put(new Element(flag, cacheInputResult));

			if (_workbookAndBoolean.getIspass()) {
				logger.info("校验通过");
				this.doPersist(business, workbook, year, parentid, sheetnum);
				wo.setPersist(true);
			} else {
				logger.info("校验不通过，不存储数据库");
				wo.setPersist(false);
			}

			wo.setFlag(flag);
			wo.setDescribe(_workbookAndBoolean.getResultdescribe());
			result.setData(wo);
		}
		return result;
	}

	private WorkbookAndBoolean scanAndCheck(Business business, XSSFWorkbook workbook, Integer sheetnum) {
		WorkbookAndBoolean workbookAndBoolean = new WorkbookAndBoolean();
		boolean ispass = true;
		String resultDescribe = "";

		XSSFSheet sheet = workbook.getSheetAt(sheetnum);
		//int lastRowNum = sheet.getLastRowNum();
		//lastRowNum = Math.max(lastRowNum, maxRowNum);

		Row row = null;
		XSSFComment comment = null;

		List<String> SequenceNumberList = new ArrayList<String>();
		boolean isRightNumber = true;
		String tmpsn = "";
		String measuresTitle = "";
		String workContent = "";
		String targetValue = "";
		String deptStringList = "";

		//确定最后一行
		int lastRowNum = sheet.getLastRowNum();
		int noBlankLastRowNum = 0;
		for (int j = beginRow; j <= lastRowNum; j++) {
			row = sheet.getRow(j);
			Cell cell = row.getCell(0);
			String _tmpsn = StringUtils.trimToEmpty(this.getCellValue(cell));
			if (StringUtils.isBlank(_tmpsn) || StringUtils.isEmpty(_tmpsn)) {
				//noBlankLastRowNum--;
			} else {
				noBlankLastRowNum++;
			}
		}

		logger.info("非空行的noBlankLastRowNum：" + noBlankLastRowNum);
		int rowNumber = beginRow + noBlankLastRowNum - 1;
		logger.info("非空行的rowNumber：" + rowNumber);

		//for (int j = beginRow; j <= lastRowNum; j++) {
		for (int j = beginRow; j <= rowNumber; j++) {
			row = sheet.getRow(j);

			for (Cell cell : row) {
				int ColumnIndex = cell.getColumnIndex();
				if (ColumnIndex == 0) {
					//序号校验
					isRightNumber = VerifySequenceNumberTools.VerifySequenceNumber(this.getCellValue(cell));
					if (!isRightNumber) {
						logger.info("序号不符合格式");
						comment = creatComment(sheet, workbook, row, cell, "序号不符合格式");
						cell.setCellComment(comment);
						ispass = false;
						resultDescribe = connectResultdescribe(resultDescribe, j, ColumnIndex, this.getCellValue(cell) + ",序号不符合格式。");
					} else {
						tmpsn = this.getCellValue(cell);
						if (SequenceNumberList.indexOf(tmpsn) >= 0) {
							logger.info("序号重复：" + tmpsn);
							comment = creatComment(sheet, workbook, row, cell, this.getCellValue(cell) + ",序号重复");
							cell.setCellComment(comment);
							ispass = false;
							resultDescribe = connectResultdescribe(resultDescribe, j, ColumnIndex, this.getCellValue(cell) + ",序号重复。");
						} else {
							SequenceNumberList.add("" + tmpsn + "");
						}
					}
				}

				if (ColumnIndex == 1) {
					//关键举措校验
					measuresTitle = this.getCellValue(cell);
					if (null == StringUtils.trimToNull(measuresTitle)) {
						logger.info("关键举措为空");
						comment = creatComment(sheet, workbook, row, cell, "关键举措为空");
						cell.setCellComment(comment);
						ispass = false;
						resultDescribe = connectResultdescribe(resultDescribe, j, ColumnIndex, "关键举措为空。");
					}
				}

				if (ColumnIndex == 2) {
					//工作内容校验
					workContent = this.getCellValue(cell);
					if (null == StringUtils.trimToNull(workContent)) {
						logger.info("工作内容为空");
						comment = creatComment(sheet, workbook, row, cell, "工作内容为空");
						cell.setCellComment(comment);
						ispass = false;
						resultDescribe = connectResultdescribe(resultDescribe, j, ColumnIndex, "工作内容为空。");
					}
				}

				if (ColumnIndex == 3) {
					//目标值校验
					targetValue = this.getCellValue(cell);
					if (null == StringUtils.trimToNull(targetValue)) {
						logger.info("目标值为空");
						comment = creatComment(sheet, workbook, row, cell, "目标值为空");
						cell.setCellComment(comment);
						ispass = false;
						resultDescribe = connectResultdescribe(resultDescribe, j, ColumnIndex, "目标值为空。");
					}
				}

				if (ColumnIndex == 4) {
					//牵头部门
					deptStringList = this.getCellValue(cell);
					if (StringUtils.isEmpty(StringUtils.trim(deptStringList))) {
						//牵头部门，如果字段为空，就什么都不做。
					} else {
						List<String> deptlist = new ArrayList<String>();
						deptlist.addAll(Arrays.asList(deptStringList.split(DEPT_SEPARATOR)));
						for (String deptstring : deptlist) {
							deptstring = StringUtils.trim(deptstring);
							try {
								if (!checkUnitByUnitName(business, deptstring)) {
									logger.info("名称与组织名称不对相应");
									comment = creatComment(sheet, workbook, row, cell, deptstring + ",名称与组织名称不对相应");
									cell.setCellComment(comment);
									ispass = false;
									resultDescribe = connectResultdescribe(resultDescribe, j, ColumnIndex, deptstring + ",名称与组织名称不对相应。");
								}
							} catch (Exception e) {
								e.printStackTrace();
								ispass = false;
							}
						}
					}
				}

				if (ColumnIndex == 5) {
					//责任部门
					deptStringList = this.getCellValue(cell);
					if (StringUtils.isEmpty(StringUtils.trim(deptStringList))) {
						logger.info("责任部门不能为空");
						comment = creatComment(sheet, workbook, row, cell, "责任部门不能为空");
						cell.setCellComment(comment);
						ispass = false;
						resultDescribe = connectResultdescribe(resultDescribe, j, ColumnIndex, "责任部门不能为空。");
					} else {
						List<String> deptlist = new ArrayList<String>();
						deptlist.addAll(Arrays.asList(deptStringList.split(DEPT_SEPARATOR)));
						for (String deptstring : deptlist) {
							deptstring = StringUtils.trim(deptstring);
							try {
								if (!checkUnitByUnitName(business, deptstring)) {
									logger.info("名称与组织名称不对相应");
									comment = creatComment(sheet, workbook, row, cell, deptstring + ",名称与组织名称不对相应");
									cell.setCellComment(comment);
									ispass = false;
									resultDescribe = connectResultdescribe(resultDescribe, j, ColumnIndex, deptstring + ",名称与组织名称不对相应。");
								}
							} catch (Exception e) {
								e.printStackTrace();
								ispass = false;
							}
						}
					}
				}
			}
		}
		workbookAndBoolean.setWorkbook(workbook);
		workbookAndBoolean.setIspass(ispass);
		workbookAndBoolean.setResultdescribe(resultDescribe);
		return workbookAndBoolean;
	}

	private void doPersist(Business business, XSSFWorkbook workbook, String year, String parentid, Integer sheetnum) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Sheet sheet = workbook.getSheetAt(sheetnum);
		//int lastRowNum = sheet.getLastRowNum();		
		Row row = null;

		//确定最后一行
		int lastRowNum = sheet.getLastRowNum();
		int noBlankLastRowNum = 0;
		for (int j = beginRow; j <= lastRowNum; j++) {
			row = sheet.getRow(j);
			Cell cell = row.getCell(0);
			String _tmpsn = StringUtils.trimToEmpty(this.getCellValue(cell));
			if (StringUtils.isBlank(_tmpsn) || StringUtils.isEmpty(_tmpsn)) {
				//noBlankLastRowNum--;
			} else {
				noBlankLastRowNum++;
			}
		}

		logger.info("doPersist-->非空行的noBlankLastRowNum：" + noBlankLastRowNum);
		int rowNumber = beginRow + noBlankLastRowNum - 1;
		logger.info("doPersist-->非空行的rowNumber：" + rowNumber);

		//for (int i = beginRow; i <= lastRowNum; i++) {
		for (int i = beginRow; i <= rowNumber; i++) {
			row = sheet.getRow(i);
			//logger.info("---------------------------row number:" + i);
			MeasuresInfo measuresinfo = new MeasuresInfo();
			for (Cell cell : row) {
				int ColumnIndex = cell.getColumnIndex();
				if (ColumnIndex == 0) {
					//double _double = Double.valueOf(this.getCellValue(cell));
					//_double = Math.floor(_double);
					//measuresinfo.setSequencenumber("" + _double);
					measuresinfo.setSequencenumber(this.getCellValue(cell));
				}
				if (ColumnIndex == 1) {
					measuresinfo.setMeasuresinfotitle(this.getCellValue(cell));
				}
				if (ColumnIndex == 2) {
					measuresinfo.setMeasuresinfodescribe(this.getCellValue(cell));
				}
				if (ColumnIndex == 3) {
					measuresinfo.setMeasuresinfotargetvalue(this.getCellValue(cell));
				}

				if (ColumnIndex == 4) {
					// dutydept 为牵头部门
					String _dutydept = this.getUnitdistinguishedNameByunitName(business, this.getCellValue(cell));
					measuresinfo.setMeasuresdutydept(StringUtils.trim(_dutydept));
				}

				if (ColumnIndex == 5) {
					//deptlist 为责任部门
					List<String> deptlist = new ArrayList<String>();
					String[] _tmpdeptArray = this.getCellValue(cell).split(DEPT_SEPARATOR);

					for (String tmpdept : _tmpdeptArray) {
						deptlist.add(this.getUnitdistinguishedNameByunitName(business, StringUtils.trim(tmpdept)));
					}
					//this.getUnitdistinguishedNameByunitName(business, unitName);
					//deptlist.addAll(Arrays.asList(this.getCellValue(cell).split(DEPT_SEPARATOR)));
					measuresinfo.setDeptlist(deptlist);
				}

				/*				
								if (ColumnIndex == 4) {
									List<String> deptlist = new ArrayList<String>();
									String[] _tmpdeptArray = this.getCellValue(cell).split(DEPT_SEPARATOR);
				
									for (String tmpdept : _tmpdeptArray) {
										deptlist.add(this.getUnitdistinguishedNameByunitName(business, StringUtils.trim(tmpdept)));
									}
									measuresinfo.setDeptlist(deptlist);
								}
				
								if (ColumnIndex == 5) {
									String _dutydept = this.getUnitdistinguishedNameByunitName(business, this.getCellValue(cell));
									measuresinfo.setMeasuresdutydept(StringUtils.trim(_dutydept));
								}
				*/
				if (ColumnIndex == 6) {
					measuresinfo.setMeasuressupportdepts(this.getCellValue(cell));
				}

				measuresinfo.setMeasuresinfoparentid(parentid);
				measuresinfo.setMeasuresinfoyear(year);

				MeasuresInfoExcuteSave measuresInfoExcuteSave = new MeasuresInfoExcuteSave();
				//				emc.beginTransaction(MeasuresInfo.class);
				//				emc.persist(measuresinfo);
				//				emc.commit();
				measuresInfoExcuteSave.save(emc, measuresinfo);
			}
			//logger.info("-------------------------------");
		}
	}

	XSSFComment creatComment(XSSFSheet sheet, XSSFWorkbook workbook, Row row, Cell cell, String commentStr) {
		XSSFDrawing p = ((XSSFSheet) sheet).createDrawingPatriarch();
		if (cell == null) {
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
		//获取unitName的distinguishedName
		String distinguishedName = "";
		distinguishedName = unitFactory.get(unitName);
		if (StringUtils.isEmpty(distinguishedName)) {
			return false;
		} else {
			return true;
		}
	}

	String getUnitdistinguishedNameByunitName(Business business, String unitName) throws Exception {
		UnitFactory unitFactory = business.organization().unit();
		//获取unitName的distinguishedName
		String distinguishedName = "";
		distinguishedName = unitFactory.get(unitName);
		if (StringUtils.isEmpty(distinguishedName)) {
			return "";
		} else {
			return distinguishedName;
		}
	}

	/**
	 * 获取单元格的值
	 * 
	 * @param cell
	 * @return
	 */
	public String getCellValue(Cell cell) {
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

		@FieldDescribe("返回是否持久化到数据库")
		private boolean isPersist;

		@FieldDescribe("返回校验的描述")
		private String describe;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

		public boolean isPersist() {
			return isPersist;
		}

		public void setPersist(boolean isPersist) {
			this.isPersist = isPersist;
		}

		public String getDescribe() {
			return describe;
		}

		public void setDescribe(String describe) {
			this.describe = describe;
		}

	}

	public static class WorkbookAndBoolean {
		private Boolean ispass;

		private XSSFWorkbook workbook;

		private String resultdescribe = "";

		public Boolean getIspass() {
			return ispass;
		}

		public void setIspass(Boolean ispass) {
			this.ispass = ispass;
		}

		public XSSFWorkbook getWorkbook() {
			return workbook;
		}

		public void setWorkbook(XSSFWorkbook workbook) {
			this.workbook = workbook;
		}

		public String getResultdescribe() {
			return resultdescribe;
		}

		public void setResultdescribe(String resultdescribe) {
			this.resultdescribe = resultdescribe;
		}

	}

	public static String connectResultdescribe(String result, Integer rowIndex, Integer columnIndex, String describe) {
		rowIndex++;
		columnIndex++;
		result = result + "\r\n" + "第：" + rowIndex + " 行，第:" + columnIndex + "列，" + describe;
		return result;
	}

}
