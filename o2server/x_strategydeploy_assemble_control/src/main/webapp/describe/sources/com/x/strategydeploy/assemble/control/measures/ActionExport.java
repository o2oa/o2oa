package com.x.strategydeploy.assemble.control.measures;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.factory.MeasuresInfoFactory;
import com.x.strategydeploy.assemble.control.factory.StrategyDeployFactory;
import com.x.strategydeploy.core.entity.MeasuresInfo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

public class ActionExport extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionExport.class);
	private static String name = "strategy_measures_export.xlsx";
	private static Integer beginrow = 3;
	private static String DEPT_SPLIT = "、";
	private static List<String> titleList = new ArrayList<String>();

	private static List<String> initTitleList(String year) {
		//序号	关键举措	工作内容	2018年目标值	牵头部门	责任部门	支撑部门
		titleList.add("序号");
		titleList.add("关键举措");
		titleList.add("工作内容");
		titleList.add(year + "年目标值");
		titleList.add("牵头部门");
		titleList.add("责任部门");
		titleList.add("支撑部门");
		return titleList;
	}

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String year) throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ActionResult<Wo> result = new ActionResult<>();
			initTitleList(year);
			//this.template(workbook);
			this.createdata(workbook, year);

			workbook.write(os);
			Wo wo = new Wo(os.toByteArray(), this.contentType(true, name), this.contentDisposition(true, name));
			result.setData(wo);
			return result;
		}
	}

	private void createdata(XSSFWorkbook workbook, String year) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			StrategyDeployFactory strategyDeployFactory = business.strategyDeployFactory();
			MeasuresInfoFactory measuresInfoFactory = business.measuresInfoFactory();
			List<StrategyDeploy> strategyDeployList = strategyDeployFactory.getListByYear(year);

			Integer strategyIndex = 0;
			for (StrategyDeploy strategyDeploy : strategyDeployList) {
				strategyIndex++;

				String strategyTitle = strategyDeploy.getStrategydeploytitle();
				XSSFSheet sheet = workbook.createSheet(strategyIndex + "：" + strategyTitle);

				XSSFCellStyle cellStyle = workbook.createCellStyle();
				XSSFFont font = workbook.createFont();
				font.setFontName("黑体");
				;
				cellStyle.setBorderBottom(BorderStyle.MEDIUM);//上边框    
				cellStyle.setBorderLeft(BorderStyle.MEDIUM);//左边框    
				cellStyle.setBorderTop(BorderStyle.MEDIUM);//上边框    
				cellStyle.setBorderRight(BorderStyle.MEDIUM);//右边框 

				CellRangeAddress cellRangeAddress0 = new CellRangeAddress(0, 0, 0, 6);
				CellRangeAddress cellRangeAddress1 = new CellRangeAddress(1, 1, 0, 6);
				sheet.addMergedRegion(cellRangeAddress0);
				sheet.addMergedRegion(cellRangeAddress1);

				XSSFRow row0 = sheet.createRow(0);
				XSSFCell cell0 = row0.createCell(0);
				cell0.setCellValue("香港公司" + year + "年度工作纲要");

				XSSFRow row1 = sheet.createRow(1);
				XSSFCell cell1 = row1.createCell(0);
				cell1.setCellValue("工作路径" + strategyIndex + "：" + strategyTitle);

				XSSFRow rowtitle = sheet.createRow(2);

				Integer columnIndex = 0;
				for (String title : titleList) {
					XSSFCell titleCell = rowtitle.createCell(columnIndex);
					titleCell.setCellValue(title);
					columnIndex++;
				}

				String _parentId = strategyDeploy.getId();
				List<MeasuresInfo> measuresInfoList = measuresInfoFactory.getListByParentId(_parentId);
				columnIndex = 0;//初始化列索引
				Integer _beginrow = beginrow;
				for (MeasuresInfo measuresInfo : measuresInfoList) {
					XSSFRow datarow = sheet.createRow(_beginrow);
					Cell cell = datarow.createCell(0);
					cell.setCellValue(measuresInfo.getSequencenumber());

					cell = datarow.createCell(1);
					cell.setCellValue(measuresInfo.getMeasuresinfotitle());
					cell = datarow.createCell(2);
					cell.setCellValue(measuresInfo.getMeasuresinfodescribe());
					cell = datarow.createCell(3);
					cell.setCellValue(measuresInfo.getMeasuresinfotargetvalue());
					cell = datarow.createCell(4);

					List<String> deptlist = measuresInfo.getDeptlist();
					List<String> deptnamelist = new ArrayList<String>();
					for (String string : deptlist) {
						if (StringUtils.isBlank(StringUtils.trim(string))) {

						} else {
							deptnamelist.add(StringUtils.split(string, "@")[0].toString());
						}
					}

					cell.setCellValue(StringUtils.join(deptnamelist, DEPT_SPLIT));
					cell = datarow.createCell(5);
					cell.setCellValue(StringUtils.split(measuresInfo.getMeasuresdutydept(), "@")[0].toString());
					cell = datarow.createCell(6);
					cell.setCellValue(measuresInfo.getMeasuressupportdepts());
					_beginrow++;
				}
			}
		}
	}

	private void template(XSSFWorkbook workbook) throws Exception {
		XSSFSheet sheet = workbook.createSheet("人员");
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("姓名");
		cell = row.createCell(1);
		cell.setCellValue("手机号");
		cell = row.createCell(2);
		cell.setCellValue("电子邮件");
		cell = row.createCell(3);
		cell.setCellValue("唯一编码");
		cell = row.createCell(4);
		cell.setCellValue("员工号");
		cell = row.createCell(5);
		cell.setCellValue("性别");
		cell = row.createCell(6);
		cell.setCellValue("(地址)");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 6).forEach(i -> {
			sheet.setDefaultColumnStyle(i, cellStyle);
		});
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}