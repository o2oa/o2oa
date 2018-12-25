package com.x.strategydeploy.assemble.control.strategy;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.core.express.unit.UnitFactory;
import com.x.strategydeploy.assemble.common.date.DateOperation;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.assemble.control.factory.MeasuresInfoFactory;
//import com.x.strategydeploy.assemble.control.service.StrategyDeployOperationService;
import com.x.strategydeploy.assemble.control.service.StrategyDeployQueryService;
import com.x.strategydeploy.core.entity.MeasuresInfo;
//import com.x.strategydeploy.assemble.control.strategy.BaseAction.Wo;
import com.x.strategydeploy.core.entity.StrategyDeploy;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class ActionExportExcelStreamXLSX2 extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionExportExcelStreamXLSX2.class);
	private String filename = "StrategyAndMeasures";
	private String _suffix = ".xlsx";
	private String excelfilename = "";
	private List<StrategyDeploy> strategydeploy_list;
	private List<MeasuresInfo> measures_list;
	// private Integer dataBeginRow = 3;

	ActionResult<WoCacheFileId> execute(HttpServletRequest request, EffectivePerson effectivePerson, String year,
			Boolean stream) throws Exception {
		String timeFlag = new DateOperation().getNowTimeChar();
		excelfilename = filename + "_" + year + "_" + timeFlag + _suffix;
		// ActionResult<WoExcel> result = new ActionResult<>();
		ActionResult<WoCacheFileId> result = new ActionResult<>();
		logger.info("导出excel，公司工作重点和举措关系.");
		XSSFWorkbook workbook = new XSSFWorkbook();

		strategydeploy_list = ActionExportExcelStreamXLSX2.listStrategyDeploysByYear(effectivePerson, year);
		for (StrategyDeploy strategydeploy : strategydeploy_list) {
			XSSFSheet sheet = workbook
					.createSheet(strategydeploy.getSequencenumber() + "." + strategydeploy.getStrategydeploytitle());

			// 从第三行开始
			Integer dataBeginRow = 3;

			// short RowCounter = (short) 0;
			// int firstRow; //合并单元格，开始行
			// int lastRow; //合并单元格，结束行

			// 创建前3行（也可以称为表头）
			// ====================前三行，表头开始=============
			XSSFCellStyle titlstyle = workbook.createCellStyle();
			titlstyle.setAlignment(HorizontalAlignment.CENTER);
			titlstyle.setBorderTop(BorderStyle.THIN);
			titlstyle.setBorderBottom(BorderStyle.THIN);
			titlstyle.setBorderLeft(BorderStyle.THIN);
			titlstyle.setBorderRight(BorderStyle.THIN);

			XSSFFont font = workbook.createFont();
			font.setFontName("微软雅黑");
			font.setFontHeightInPoints((short) 11);// 设置字体大小

			titlstyle.setFont(font);

			XSSFRow row0 = sheet.createRow(0);
			CellRangeAddress cellRangeAddress0 = new CellRangeAddress(0, 0, 0, 6);
			XSSFCell cell0 = row0.createCell(0);
			cell0.setCellValue("香港公司" + strategydeploy.getStrategydeployyear() + "年度工作纲要");
			cell0.setCellStyle(titlstyle);

			XSSFRow row1 = sheet.createRow(1);
			CellRangeAddress cellRangeAddress1 = new CellRangeAddress(1, 1, 0, 6);
			XSSFCell cell1 = row1.createCell(0);

			String _Sequencenumber = this.toChinese(strategydeploy.getSequencenumber());
			// cell1.setCellValue("工作路径" + strategydeploy.getSequencenumber() + ":" +
			// strategydeploy.getStrategydeploytitle());
			cell1.setCellValue("工作路径" + _Sequencenumber + ":" + strategydeploy.getStrategydeploytitle());
			cell1.setCellStyle(titlstyle);

			sheet.addMergedRegion(cellRangeAddress0);
			sheet.addMergedRegion(cellRangeAddress1);

			ActionExportExcelStreamXLSX2.setRegionStyle(sheet, cellRangeAddress0, titlstyle);
			ActionExportExcelStreamXLSX2.setRegionStyle(sheet, cellRangeAddress1, titlstyle);

			// 序号 关键举措 工作内容 2018年目标值 牵头部门 责任部门 支撑部门
			XSSFRow row2 = sheet.createRow(2);
			XSSFCell titlecell = row2.createCell(0);
			titlecell.setCellValue("序号");
			titlecell.setCellStyle(titlstyle);

			titlecell = row2.createCell(1);
			titlecell.setCellValue("关键举措");
			titlecell.setCellStyle(titlstyle);

			titlecell = row2.createCell(2);
			titlecell.setCellValue("工作内容");
			titlecell.setCellStyle(titlstyle);

			titlecell = row2.createCell(3);
			titlecell.setCellValue(strategydeploy.getStrategydeployyear() + "年目标值");
			titlecell.setCellStyle(titlstyle);

			titlecell = row2.createCell(4);
			titlecell.setCellValue("牵头部门");
			titlecell.setCellStyle(titlstyle);

			titlecell = row2.createCell(5);
			titlecell.setCellValue("责任部门");
			titlecell.setCellStyle(titlstyle);

			titlecell = row2.createCell(6);
			titlecell.setCellValue("支撑部门");
			titlecell.setCellStyle(titlstyle);
			// =============== 前三行，表头结束=========================

			// =============== 数据区域 ,开始 ==========================
			XSSFCellStyle style = workbook.createCellStyle();
			style.setWrapText(true);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setFont(font);
			style.setBorderTop(BorderStyle.THIN);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);

			measures_list = ActionExportExcelStreamXLSX2.listMeasuresInfoByParentId(strategydeploy.getId());
			// short MeasuresSize = (short) measures_list.size();
			for (MeasuresInfo measures : measures_list) {
				XSSFRow row = sheet.createRow(dataBeginRow);
				// logger.info("有举措：" + list.get(i).getSequencenumber() + ":" +
				// list.get(i).getStrategydeploytitle());
				XSSFCell cell = row.createCell(0);
				if (null == measures.getSequencenumber()) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(measures.getSequencenumber());
					cell.setCellStyle(style);
				}

				// DataFormatter formatter = new DataFormatter();
				// String _str = formatter.formatCellValue(cell);
				cell = row.createCell(1);
				cell.setCellValue(measures.getMeasuresinfotitle());
				cell.setCellStyle(style);

				cell = row.createCell(2);
				cell.setCellValue(measures.getMeasuresinfodescribe());
				cell.setCellStyle(style);

				cell = row.createCell(3);
				cell.setCellValue(measures.getMeasuresinfotargetvalue());
				cell.setCellStyle(style);

				cell = row.createCell(4);
				List<String> _units = new ArrayList<String>();
				List<String> _unames = new ArrayList<String>();
				_units.add(measures.getMeasuresdutydept());
				_unames = ActionExportExcelStreamXLSX2.getUnitNameByStringList(_units);
				cell.setCellValue(StringUtils.join(_unames, "、"));
				cell.setCellStyle(style);

				cell = row.createCell(5);
				List<String> _units1 = new ArrayList<String>();
				List<String> _unames1 = new ArrayList<String>();
				// _units1.add(measures.getMeasuresdutydept());
				_units1 = measures.getDeptlist();
				_unames1 = ActionExportExcelStreamXLSX2.getUnitNameByStringList(_units1);
				cell.setCellValue(StringUtils.join(_unames1, "、"));
				cell.setCellStyle(style);

				cell = row.createCell(6);
				cell.setCellValue(measures.getMeasuressupportdepts());
				cell.setCellStyle(style);

				dataBeginRow++;
			}

			// =============== 数据区域 ,结束 ==========================

			// ===========设置列宽，开始===========
			// 第一个参数为列索引，第二个参数 为256*excel中列宽的数值。
			sheet.setColumnWidth(0, 256 * 8);
			sheet.setColumnWidth(1, 256 * 32);
			sheet.setColumnWidth(2, 256 * 45);
			sheet.setColumnWidth(3, 256 * 45);
			sheet.setColumnWidth(4, 256 * 18);
			sheet.setColumnWidth(5, 256 * 18);
			sheet.setColumnWidth(6, 256 * 18);
			// ===========设置列宽，结束===========
		}

		// logger.info("共有合并区域：" + sheet.getNumMergedRegions());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		workbook.write(baos);
		// byte[] bytes = baos.toByteArray();
		// WoExcel wo = new WoExcel(bytes, this.contentType(stream, excelfilename),
		// this.contentDisposition(stream, excelfilename));
		// result.setData(wo);

		CacheInputResult cacheInputResult = new CacheInputResult();
		cacheInputResult.setName(excelfilename);
		cacheInputResult.setBytes(baos.toByteArray());
		String flag = StringTools.uniqueToken();
		cache.put(new Element(flag, cacheInputResult));
		WoCacheFileId wo = new WoCacheFileId();
		wo.setFlag(flag);
		result.setData(wo);

		workbook.close();
		return result;
	}

	public static class WoCacheFileId extends GsonPropertyObject {

		@FieldDescribe("返回的结果标识")
		private String flag;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

	}

	// excel结果文件?文件流?
	public static class WoExcel extends WoFile {
		public WoExcel(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}

	// 根据年份获得工作纲要列表
	protected static List<StrategyDeploy> listStrategyDeploysByYear(EffectivePerson effectivePerson, String year)
			throws Exception {
		StrategyDeployQueryService strategyDeployQueryService = new StrategyDeployQueryService();
		// StrategyDeployOperationService strategyDeployOperationService = new
		// StrategyDeployOperationService();
		List<StrategyDeploy> strategydeployList = new ArrayList<StrategyDeploy>();
		// List<Wo> wrapOutList = new ArrayList<Wo>();
		strategydeployList = strategyDeployQueryService.getListByYear(year);
		// wrapOutList = Wo.copier.copy(strategydeployList);
		// wrapOutList =
		// strategyDeployOperationService.setActions(wrapOutList,effectivePerson);
		return strategydeployList;
	}

	// 根据工作纲要id获得举措列表
	protected static List<MeasuresInfo> listMeasuresInfoByParentId(String _parentId) throws Exception {
		List<MeasuresInfo> result = new ArrayList<MeasuresInfo>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			MeasuresInfoFactory measuresInfoFactory = business.measuresInfoFactory();
			result = measuresInfoFactory.getListByParentId(_parentId);
		}
		return result;
	}

	// Unit对象方式，获得unit名字列表
	protected static List<String> getUnitNameByDistinguishName(List<String> distinguishNameList) throws Exception {
		List<String> result = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			UnitFactory unitFactory = business.organization().unit();
			for (String distinguishName : distinguishNameList) {
				Unit unit = unitFactory.getObject(distinguishName);
				result.add(unit.getName());
			}
		}
		return result;
	}

	protected static List<String> getUnitNameByStringList(List<String> distinguishNameList) {
		List<String> result = new ArrayList<String>();
		for (String distinguishName : distinguishNameList) {
			if (StringUtils.indexOf(distinguishName, "@") > 0) { // 必须包含“@”
				String _tmplist[] = StringUtils.split(distinguishName, "@");
				if (_tmplist.length == 3 && StringUtils.equals(_tmplist[2], "U")) {
					result.add(_tmplist[0]);
				} else {
					result.add(distinguishName);
				}
			} else {
				result.add(distinguishName);
			}
		}

		return result;
	}

	Ehcache cache = ApplicationCache.instance().getCache(CacheInputResult.class);

	public static class CacheInputResult {

		private String name;

		private byte[] bytes;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

	}

	public static void setRegionStyle(XSSFSheet sheet, CellRangeAddress region, XSSFCellStyle cs) {

		for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {

			XSSFRow row = sheet.getRow(i);
			if (row == null)
				row = sheet.createRow(i);
			for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
				XSSFCell cell = row.getCell(j);
				if (cell == null) {
					cell = row.createCell(j);
					cell.setCellValue("");
				}
				cell.setCellStyle(cs);

			}
		}
	}

	private static final String[] numArray = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };

	private static String toChinese(int amountPart) {

		if (amountPart < 0 || amountPart > 10000) {
			throw new IllegalArgumentException("参数必须是大于等于 0，小于 10000 的整数！");
		}

		String[] units = new String[] { "", "十", "百", "千" };

		int temp = amountPart;

		String amountStr = new Integer(amountPart).toString();
		int amountStrLength = amountStr.length();
		boolean lastIsZero = true; // 在从低位往高位循环时，记录上一位数字是不是 0
		String chineseStr = "";

		for (int i = 0; i < amountStrLength; i++) {
			if (temp == 0) // 高位已无数据
				break;
			int digit = temp % 10;
			if (digit == 0) { // 取到的数字为 0
				if (!lastIsZero) // 前一个数字不是 0，则在当前汉字串前加“零”字;
					chineseStr = "零" + chineseStr;
				lastIsZero = true;
			} else { // 取到的数字不是 0
				chineseStr = numArray[digit] + units[i] + chineseStr;
				lastIsZero = false;
			}
			temp = temp / 10;
		}
		return chineseStr;
	}
}
