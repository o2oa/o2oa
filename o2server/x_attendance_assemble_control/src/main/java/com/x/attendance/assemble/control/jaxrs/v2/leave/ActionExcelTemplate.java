package com.x.attendance.assemble.control.jaxrs.v2.leave;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.stream.IntStream;

/**
 * Created by fancyLou on 2023/4/6.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionExcelTemplate extends BaseAction {

    final String tempName = "attendance_leave_data_input_template.xlsx";

    ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ActionResult<Wo> result = new ActionResult<>();

            XSSFSheet sheet = workbook.createSheet("请假数据导入");
            sheet.setDefaultColumnWidth(25);
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("用户标识");
            cell = row.createCell(1);
            cell.setCellValue("请假类型:带薪年休假|带薪病假|带薪福利假|扣薪事假|出差|培训|其他");
            cell = row.createCell(2);
            cell.setCellValue("开始时间：yyyy-MM-dd HH:mm:ss");
            cell = row.createCell(3);
            cell.setCellValue("结束时间：yyyy-MM-dd HH:mm:ss");
            cell = row.createCell(4);
            cell.setCellValue("请假说明");
            cell = row.createCell(5);
            cell.setCellValue("流程的jobId,可为空");
            cell = row.createCell(7);
            cell.setCellValue("导入错误信息反馈");
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);
            IntStream.rangeClosed(0, 6).forEach(i -> {
                sheet.setDefaultColumnStyle(i, cellStyle);
            });

            workbook.write(os);
            Wo wo = new Wo(os.toByteArray(), this.contentType(true, tempName), this.contentDisposition(true, tempName));
            result.setData(wo);
            return result;
        }
    }
    public static class Wo extends WoFile {

        public Wo(byte[] bytes, String contentType, String contentDisposition) {
            super(bytes, contentType, contentDisposition);
        }

    }
}
