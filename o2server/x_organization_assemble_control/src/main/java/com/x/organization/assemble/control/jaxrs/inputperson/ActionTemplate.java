package com.x.organization.assemble.control.jaxrs.inputperson;

import java.io.ByteArrayOutputStream;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionTemplate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTemplate.class);

	private static String name = "input_person_template.xlsx";

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ActionResult<Wo> result = new ActionResult<>();

			this.templateRemark(workbook);
			this.templateUnit(workbook);
			this.templatePerson(workbook);
			this.templateIdentity(workbook);
			this.templateDuty(workbook);
			this.templateGroup(workbook);

			workbook.write(os);
			Wo wo = new Wo(os.toByteArray(), this.contentType(true, name), this.contentDisposition(true, name));
			result.setData(wo);
			return result;
		}
	}

	private void templateRemark(XSSFWorkbook workbook) throws Exception {
		XSSFSheet sheet = workbook.createSheet("注意事项");
		Row row = sheet.createRow(0);
		// 先创建表头
		row = sheet.createRow(2);
		row.createCell(0).setCellValue("注意事项:");

		row = sheet.createRow(4);
		row.createCell(0).setCellValue("1. 表格内不要做合并(拆分)单元格操作，各列顺序不能变动，更不能删除，否则会造成数据混乱；");

		row = sheet.createRow(6);
		row.createCell(0).setCellValue("2. * 为必填项");

		row = sheet.createRow(8);
		row.createCell(0).setCellValue("3. 如有特殊要求的格式，详见列名批注；");

		row = sheet.createRow(10);
		row.createCell(0).setCellValue("4. 表中示例数据用于示范，实际导入时需删除；");

		row = sheet.createRow(12);
		row.createCell(0).setCellValue("5. 每个Sheet页顺序不能变动，本Sheet页不能删除。");

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 6).forEach(i -> {
			sheet.setDefaultColumnStyle(i, cellStyle);
		});
	}

	private void templateUnit(XSSFWorkbook workbook) throws Exception {
		XSSFSheet sheet = workbook.createSheet("组织信息");
		sheet.setDefaultColumnWidth(25);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("组织名称 *");
		cell = row.createCell(1);
		cell.setCellValue("唯一编码 *");
		cell = row.createCell(2);
		cell.setCellValue("组织级别名称 *");
		cell = row.createCell(3);
		cell.setCellValue("上级组织编号");
		cell = row.createCell(4);
		cell.setCellValue("描述");
		cell = row.createCell(5);
		cell.setCellValue("排序号");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 6).forEach(i -> {
			sheet.setDefaultColumnStyle(i, cellStyle);
		});
	}

	private void templatePerson(XSSFWorkbook workbook) throws Exception {
		XSSFSheet sheet = workbook.createSheet("人员基本信息");
		sheet.setDefaultColumnWidth(25);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("人员姓名 *");
		cell = row.createCell(1);
		cell.setCellValue("唯一编码 *");
		cell = row.createCell(2);
		cell.setCellValue("手机号码 *");
		cell = row.createCell(3);
		cell.setCellValue("人员编号");
		cell = row.createCell(4);
		cell.setCellValue("办公电话");
		cell = row.createCell(5);
		cell.setCellValue("性别");
		cell = row.createCell(6);
		cell.setCellValue("邮件");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 6).forEach(i -> {
			sheet.setDefaultColumnStyle(i, cellStyle);
		});
	}

	private void templateIdentity(XSSFWorkbook workbook) throws Exception {
		XSSFSheet sheet = workbook.createSheet("人员身份信息");
		sheet.setDefaultColumnWidth(25);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("人员唯一编码 *");
		cell = row.createCell(1);
		cell.setCellValue("组织唯一编码 *");
		cell = row.createCell(2);
		cell.setCellValue("身份编码");
		cell = row.createCell(3);
		cell.setCellValue("主兼职");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 6).forEach(i -> {
			sheet.setDefaultColumnStyle(i, cellStyle);
		});
	}

	private void templateDuty(XSSFWorkbook workbook) throws Exception {
		XSSFSheet sheet = workbook.createSheet("职务信息");
		sheet.setDefaultColumnWidth(45);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("职务名称 *");
		cell = row.createCell(1);
		cell.setCellValue("职务所在组织唯一编码 *");
		cell = row.createCell(2);
		cell.setCellValue("职务编码");
		cell = row.createCell(3);
		cell.setCellValue("职务描述");
		cell = row.createCell(4);
		cell.setCellValue("职务所含人员唯一编码");
		cell = row.createCell(5);
		cell.setCellValue("职务所含人员所在组织唯一编码");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 6).forEach(i -> {
			sheet.setDefaultColumnStyle(i, cellStyle);
		});
	}

	private void templateGroup(XSSFWorkbook workbook) throws Exception {
		XSSFSheet sheet = workbook.createSheet("群组信息");
		sheet.setDefaultColumnWidth(25);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("群组名称 *");
		cell = row.createCell(1);
		cell.setCellValue("群组编码 *");
		cell = row.createCell(2);
		cell.setCellValue("人员唯一编码");
		cell = row.createCell(3);
		cell.setCellValue("身份唯一编码");
		cell = row.createCell(4);
		cell.setCellValue("组织唯一编码");
		cell = row.createCell(5);
		cell.setCellValue("子群组编码");
		cell = row.createCell(6);
		cell.setCellValue("描述");
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 7).forEach(i -> {
			sheet.setDefaultColumnStyle(i, cellStyle);
		});
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}
