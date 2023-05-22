package com.x.attendance.assemble.control.jaxrs.v2.record;

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
public class ActionExcelTemplate extends  BaseAction {

    final String tempName = "attendance_record_data_input_template.xlsx";

    ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ActionResult<Wo> result = new ActionResult<>();

            XSSFSheet sheet = workbook.createSheet("打卡原始数据导入");
            sheet.setDefaultColumnWidth(35);
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("用户唯一标识");
            cell = row.createCell(1);
            cell.setCellValue("日期：yyyy-MM-dd，文本格式");
            cell = row.createCell(2);
            cell.setCellValue("第一次上班打卡时间：HH:mm，文本格式");
            cell = row.createCell(3);
            cell.setCellValue("第一次 下班打卡时间：HH:mm，文本格式");
            cell = row.createCell(4);
            cell.setCellValue("第二次上班打卡时间：HH:mm，文本格式");
            cell = row.createCell(5);
            cell.setCellValue("第二次 下班打卡时间：HH:mm，文本格式");
            cell = row.createCell(6);
            cell.setCellValue("第二次上班打卡时间：HH:mm，文本格式");
            cell = row.createCell(7);
            cell.setCellValue("第二次 下班打卡时间：HH:mm，文本格式");
            cell = row.createCell(8);
            cell.setCellValue("导入错误信息反馈");
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setWrapText(true);
            IntStream.rangeClosed(0, 8).forEach(i -> {
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
