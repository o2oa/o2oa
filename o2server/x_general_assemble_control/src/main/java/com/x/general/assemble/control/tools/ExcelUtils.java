package com.x.general.assemble.control.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel 工具类(兼容xls和xlsx)
 *
 * @author qijian
 * @version 1.0.0
 *
 */
public class ExcelUtils {

    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * 输出数据到自定义模版的Excel输出流
     *
     * @param excelTemplate 自定义模版文件
     * @param data 数据
     * @param outputStream Excel输出流
     * @throws IOException 错误时抛出异常，由调用者处理
     */
    public static void writeDataToTemplateOutputStream(File excelTemplate, List<List<Object>> data, OutputStream outputStream) throws IOException {
        Workbook book = ExcelUtils.getWorkbookFromExcel(excelTemplate);
        ExcelUtils.writeDataToWorkbook(null, data, book, 0);
        ExcelUtils.writeWorkbookToOutputStream(book, outputStream);
    }

    public static void writeDataToTemplateFile(byte[] excelTemplateBytes, List<SheetData> sheetDataList, OutputStream outputStream) throws IOException {
        Workbook book = ExcelUtils.getWorkbookFromExcel(excelTemplateBytes);
        ExcelUtils.writeDataToWorkbook(sheetDataList, book);
        ExcelUtils.writeWorkbookToOutputStream(book, outputStream);
    }

    /**
     * 从Excel文件获取Workbook对象
     *
     * @param excelTemplateBytes Excel文件
     * @return Workbook对象
     * @throws IOException 错误时抛出异常，由调用者处理
     */
    public static Workbook getWorkbookFromExcel(byte[] excelTemplateBytes) throws IOException {
        try (
                InputStream inputStream = new ByteArrayInputStream(excelTemplateBytes);
        ) {
            return new XSSFWorkbook(inputStream);
        }
    }

    /**
     * 从Excel文件获取Workbook对象
     *
     * @param excelFile Excel文件
     * @return Workbook对象
     * @throws IOException 错误时抛出异常，由调用者处理
     */
    public static Workbook getWorkbookFromExcel(File excelFile) throws IOException {
        try (
                InputStream inputStream = new FileInputStream(excelFile);
        ) {

            if (excelFile.getName().endsWith(XLS)) {
                return new HSSFWorkbook(inputStream);
            }

            else if (excelFile.getName().endsWith(XLSX)) {
                return new XSSFWorkbook(inputStream);
            }

            else {
                throw new IOException("文件类型错误");
            }
        }
    }

    /**
     * 把Workbook对象内容输出到Excel文件
     *
     * @param book Workbook对象
     * @param file Excel文件
     * @throws FileNotFoundException 找不到文件异常，文件已创建，实际不存在该异常
     * @throws IOException 输入输出异常
     */
    public static void writeWorkbookToFile(Workbook book, File file) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        try (
                OutputStream outputStream = new FileOutputStream(file);
        ) {
            writeWorkbookToOutputStream(book, outputStream);
        }
    }

    /**
     * 把Workbook对象输出到Excel输出流
     *
     * @param book Workbook对象
     * @param outputStream Excel输出流
     * @throws IOException 错误时抛出异常，由调用者处理
     */
    public static void writeWorkbookToOutputStream(Workbook book, OutputStream outputStream) throws IOException {
        book.write(outputStream);
    }

    /**
     * 输出数据到Workbook对象中
     * @param sheetDataList List<SheetData>
     * @param book Workbook
     */
    public static void writeDataToWorkbook(List<SheetData> sheetDataList, Workbook book) {
        int numberOfSheets = book.getNumberOfSheets();
        for (int page=0; page<sheetDataList.size(); page++) {
            SheetData sheetData = sheetDataList.get(page);
            Sheet sheet;
            if(page < numberOfSheets) {
                sheet = book.getSheetAt(page);
                if(StringUtils.isNotBlank(sheetData.getSheetName())){
                    book.setSheetName(page, sheetData.getSheetName());
                }
            }else{
                if(StringUtils.isNotBlank(sheetData.getSheetName())){
                    sheet = book.createSheet(sheetData.getSheetName());
                }else{
                    sheet = book.createSheet();
                }
            }
            Row row = null;
            Cell cell = null;
            List<String> rowData = null;
            int startRow = sheetData.getStartRow() == null || sheetData.getStartRow() < 0 ? 0 : sheetData.getStartRow();
            for (int i = 0; i < sheetData.getDataList().size(); i++) {
                row = sheet.getRow(i + startRow);
                if (null == row) {
                    row = sheet.createRow(i + startRow);
                }
                rowData = sheetData.getDataList().get(i);
                if (null == rowData) {
                    continue;
                }
                for (int j = 0; j < rowData.size(); j++) {
                    cell = row.getCell(j);
                    if (null == cell) {
                        cell = row.createCell(j);
                    }
                    setValue(cell, rowData.get(j));
                }
            }
        }
    }

    /**
     * 输出数据到Workbook对象中指定页码
     *
     * @param title 标题，写在第一行，可传null
     * @param data 数据
     * @param book Workbook对象
     * @param page 输出数据到Workbook指定页码的页面数
     */
    public static void writeDataToWorkbook(List<String> title, List<List<Object>> data, Workbook book, int page) {
        Sheet sheet = book.getSheetAt(page);

        Row row = null;
        Cell cell = null;

        // 设置表头
        if (null != title && !title.isEmpty()) {
            row = sheet.getRow(0);
            if (null == row) {
                row = sheet.createRow(0);
            }

            for (int i = 0; i < title.size(); i++) {
                cell = row.getCell(i);
                if (null == cell) {
                    cell = row.createCell(i);
                }
                cell.setCellValue(title.get(i));
            }
        }

        List<Object> rowData = null;
        for (int i = 0; i < data.size(); i++) {

            row = sheet.getRow(i + 1);
            if (null == row) {
                row = sheet.createRow(i + 1);
            }

            rowData = data.get(i);
            if (null == rowData) {
                continue;
            }
            for (int j = 0; j < rowData.size(); j++) {
                cell = row.getCell(j);
                if (null == cell) {
                    cell = row.createCell(j);
                }
                setValue(cell, rowData.get(j));
            }
        }
    }

    /**
     * 读取Excel文件第一页
     *
     * @param pathname 文件路径名
     * @return 第一页数据集合
     * @throws IOException 错误时抛出异常，由调用者处理
     */
    public static List<List<Object>> readExcelFirstSheet(String pathname) throws IOException {
        File file = new File(pathname);
        return readExcelFirstSheet(file);

    }

    /**
     * 读取Excel文件第一页
     *
     * @param file Excel文件
     * @return 第一页数据集合
     * @throws IOException 错误时抛出异常，由调用者处理
     */
    public static List<List<Object>> readExcelFirstSheet(File file) throws IOException {
        try (
                InputStream inputStream = new FileInputStream(file);
        ) {

            if (file.getName().endsWith(XLS)) {
                return readXlsFirstSheet(inputStream);
            }

            else if (file.getName().endsWith(XLSX)) {
                return readXlsxFirstSheet(inputStream);
            }

            else {
                throw new IOException("文件类型错误");
            }
        }

    }

    /**
     * 读取xls格式Excel文件第一页
     *
     * @param inputStream Excel文件输入流
     * @return 第一页数据集合
     * @throws IOException 错误时抛出异常，由调用者处理
     */
    public static List<List<Object>> readXlsFirstSheet(InputStream inputStream) throws IOException {
        Workbook workbook = new HSSFWorkbook(inputStream);
        return readExcelFirstSheet(workbook);
    }

    /**
     * 读取xlsx格式Excel文件第一页
     *
     * @param inputStream Excel文件输入流
     * @return 第一页数据集合
     * @throws IOException 错误时抛出异常，由调用者处理
     */
    public static List<List<Object>> readXlsxFirstSheet(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        return readExcelFirstSheet(workbook);
    }

    /**
     * 读取Workbook第一页
     *
     * @param book Workbook对象
     * @return 第一页数据集合
     */
    public static List<List<Object>> readExcelFirstSheet(Workbook book) {
        return readExcel(book, 0, 0);
    }

    /**
     * 读取指定页面的Excel
     *
     * @param book Workbook对象
     * @param page 页码
     * @param startRow 起始行
     * @return 指定页面数据集合
     */
    public static List<List<Object>> readExcel(Workbook book, int page, int startRow) {
        List<List<Object>> list = new ArrayList<>();
        Sheet sheet = book.getSheetAt(page);
        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (null == row) {
                continue;
            }

            List<Object> columns = new ArrayList<>();
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                columns.add(getValue(cell));
            }

            list.add(columns);
        }

        return list;
    }

    /**
     * 解析单元格中的值
     *
     * @param cell 单元格
     * @return 单元格内的值
     */
    private static Object getValue(Cell cell) {
        if (null != cell) {
            switch (cell.getCellType()) {
                case BLANK:
                    return "";
                case BOOLEAN:
                    return BooleanUtils.toString(cell.getBooleanCellValue(), "true", "false", "false");
                case ERROR:
                    return "";
                case FORMULA:
                    return "";
                case NUMERIC:
                    Double d = cell.getNumericCellValue();
                    Long l = d.longValue();
                    if (l.doubleValue() == d) {
                        return l.toString();
                    } else {
                        return d.toString();
                    }
                default:
                    return cell.getStringCellValue().trim();
            }
        }
        return "";
    }

    /**
     * 设置单元格值
     *
     * @param cell 单元格
     * @param value 值
     */
    private static void setValue(Cell cell, Object value) {
        if (null == cell) {
            return;
        }

        if (null == value) {
            cell.setCellValue((String) null);
        }

        else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }

        else if (value instanceof Date) {
            cell.setCellValue(FORMAT.format((Date) value));
        }

        else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }

        else {
            cell.setCellValue(value.toString());
        }

    }

}
