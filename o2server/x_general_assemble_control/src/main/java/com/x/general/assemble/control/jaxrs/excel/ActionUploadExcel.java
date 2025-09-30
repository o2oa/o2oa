package com.x.general.assemble.control.jaxrs.excel;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ActionUploadExcel extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionUploadExcel.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, Integer sheetIndex,Integer rowIndex, byte[] bytes, FormDataContentDisposition disposition)
            throws Exception {

        int sheetIndexValue = sheetIndex != null ? sheetIndex : 0;
        int rowIndexValue = rowIndex != null ? rowIndex : 0;

        try (InputStream is = new ByteArrayInputStream(bytes);
             XSSFWorkbook workbook = new XSSFWorkbook(is);) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();


                if (sheetIndexValue < 0 || sheetIndexValue >= workbook.getNumberOfSheets()) {
                    throw new Exception("无效的 sheet 索引: " + sheetIndexValue +
                            "，总共有 " + workbook.getNumberOfSheets() + " 个 sheet。");
                }
                Sheet sheet = workbook.getSheetAt(sheetIndexValue); // sheet

                int firstRow = sheet.getFirstRowNum(); // 第一行
                int lastRow = sheet.getLastRowNum();

                //  校验 rowIndex 是否在有效范围内
                if (rowIndexValue < firstRow) {
                    rowIndexValue = firstRow; // 自动修正为第一行
                }
                if (rowIndexValue > lastRow) {
                    // 表示从 rowIndex 开始无数据
                    LOGGER.warn("指定的起始行号 {} 超出数据范围（最大行号为 {}），返回空数据。", rowIndexValue, lastRow);
                    ActionResult<Wo> result = new ActionResult<>();
                    Wo wo = new Wo();
                    wo.setDataList(new ArrayList<>()); // 空列表
                    result.setData(wo);
                    return result;
                }

                //用于存储所有行的数据
                List<List<Object>> rowList = new ArrayList<>();

                // 先扫描一遍所有行，确定最大列数
                int maxColumnCount = 0;
                for (int i = rowIndexValue; i <= lastRow; i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        int lastCellNum = row.getLastCellNum(); // 注意：这是列数量（从0开始的索引+1）
                        if (lastCellNum > maxColumnCount) {
                            maxColumnCount = lastCellNum;
                        }
                    }
                }
//                LOGGER.info("excel内容最大列数：{}", maxColumnCount);
                for (int i = rowIndexValue; i <= lastRow; i++) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("处理行数：" + i + " ========================================");
                    }
                    Row row = sheet.getRow(i);
                    List<Object> colList = new ArrayList<>();

                    try {
                        if (row == null) {
//                            LOGGER.info("空行的时候最大列数：{}", maxColumnCount);
                            // 空行：添加 maxColumnCount 个空字符串
                            for (int j = 0; j < maxColumnCount; j++) {
                                colList.add("");
                            }
                        }else{
                            for (int j = 0; j < maxColumnCount; j++) {
//                                Cell cell = row != null ? row.getCell(j) : null;
                                Cell cell = row.getCell(j);
                                if (cell == null) {
                                    colList.add(""); // 完全不存在的行或单元格
                                } else {
                                    colList.add(getCellValueAsString(cell,evaluator));
                                }
                            }
                        }

                    } catch (Exception e) {

                        LOGGER.warn("处理第 {} 行时发生异常：{}", i, e.getMessage());
                        colList.clear();
                        for (int j = 0; j < maxColumnCount; j++) {
                            colList.add("");
                        }
                    }

                    rowList.add(colList);

                }
//                LOGGER.debug("解析的excel完整数据：{} ", rowList);
                ActionResult<Wo> result = new ActionResult<>();

                Wo wo = new Wo();
                wo.setDataList(rowList);
                result.setData(wo);
                return result;
        }
    }

    /**
     * 将 Excel 单元格的值安全地转换为字符串
     * 智能判断日期格式：有时间则输出时分秒，否则只输出日期
     */
    private Object getCellValueAsString(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return "";
        }

        // 处理公式
        if (cell.getCellType() == CellType.FORMULA) {
            try {
                CellValue evaluated = evaluator.evaluate(cell);
                if (evaluated == null) return "";

                switch (evaluated.getCellType()) {
                    case STRING:
                        return evaluated.getStringValue();
                    case NUMERIC:
                        // 注意：公式结果也可能是日期
                        return formatNumericValue(evaluated.getNumberValue(), cell);
                    case BOOLEAN:
                        return String.valueOf(evaluated.getBooleanValue());
                    case BLANK:
                        return "";
                    default:
                        return "";
                }
            } catch (Exception e) {
                return ""; // 公式错误返回空
            }
        }

        // 非公式，按类型处理
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return formatDateValue(cell.getDateCellValue(), cell);
                } else {
//                    LOGGER.info("处理值：{}", cell.getNumericCellValue());
                    return formatNumericValue(cell.getNumericCellValue(), null);
                }


            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case BLANK:
            case ERROR:
            default:
                return "";
        }
    }

    /**
     * 格式化数值（整数、小数），避免科学计数法
     */
    private Object formatNumericValue(double value, Cell cell) {
        // 处理特殊值
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return String.valueOf(value); // NaN 或 Infinity 无法用 BigDecimal 表示
        }

        // 优先判断是否为日期格式
        if (cell != null && DateUtil.isCellDateFormatted(cell)) {
            return formatDateValue(DateUtil.getJavaDate(value), cell);
        }

        // 使用 String.valueOf 避免 double 精度问题
        try {
            BigDecimal bd = new BigDecimal(String.valueOf(value));
//            LOGGER.info("处理后的值：{}", bd.stripTrailingZeros().toPlainString());
            return Double.valueOf(bd.stripTrailingZeros().toPlainString()); // 去掉末尾多余的 0
        } catch (NumberFormatException e) {
            // fallback
            return value; // 返回原始 double
        }
    }


    /**
     * 根据单元格的格式字符串智能格式化日期
     */
    private String formatDateValue(Date date, Cell cell) {
        if (date == null) return "";

        // 获取单元格的格式化字符串，如 "m/d/yy" 或 "yyyy-mm-dd hh:mm"
        short formatIndex = cell.getCellStyle().getDataFormat();
        String formatString = cell.getCellStyle().getDataFormatString();

        // 常见的含时间的关键字
        boolean hasTime = formatString != null &&
                (formatString.contains("H") || formatString.contains("h") ||
                        formatString.contains("M") && formatString.contains(":") ||
                        formatString.contains("m") && formatString.contains(":") ||
                        formatString.toLowerCase().contains("am") ||
                        formatString.toLowerCase().contains("pm"));

        SimpleDateFormat sdf;
        if (hasTime) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }

        return sdf.format(date);
    }


    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 6022979308455360363L;
        @FieldDescribe("excel解析出的内容，例如：['姓名','性别'],['小明','男']")
        public List<List<Object>> dataList;


        public List<List<Object>> getDataList() {
            return dataList;
        }

        public void setDataList(List<List<Object>> dataList) {
            this.dataList = dataList;
        }

    }
}
