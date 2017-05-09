package com.x.organization.assemble.control.alpha.jaxrs.inputperson;

import java.io.ByteArrayOutputStream;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.FileWo;

public class ActionTemplate extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionTemplate.class);

	private static String name = "input_person_template.xls";

	protected ActionResult<FileWo> execute(EffectivePerson effectivePerson) throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ActionResult<FileWo> result = new ActionResult<>();
			this.template(workbook);

			workbook.write(os);
			FileWo wo = new FileWo(os.toByteArray(), this.contentType(true, name), this.contentDisposition(true, name));
			result.setData(wo);
			return result;
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
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		IntStream.rangeClosed(0, 5).forEach(i -> {
			sheet.setDefaultColumnStyle(i, cellStyle);
		});
	}

}