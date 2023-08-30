package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.StatisticWi;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.StatisticWo;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.general.core.entity.GeneralFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by fancyLou on 2023/3/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionStatisticExportExcel extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionStatisticExportExcel.class);
    private static final ReentrantLock lock = new ReentrantLock();
    ActionResult<Wo> execute(EffectivePerson effectivePerson, String filter, String start, String end) throws Exception {
        lock.lock();
        LOGGER.info("开始统计考勤数据。。。filter: {}, start:{}, end:{} ", filter, start, end);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
                EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

            if (StringUtils.isEmpty(filter)) {
                throw new ExceptionEmptyParameter("过滤人员或组织");
            }
            if (StringUtils.isEmpty(start)) {
                throw new ExceptionEmptyParameter("开始日期");
            }
            if (StringUtils.isEmpty(end)) {
                throw new ExceptionEmptyParameter("结束日期");
            }
            // 日期检查
            Date startDate = DateTools.parse(start, DateTools.format_yyyyMMdd); // 检查格式
            Date endDate = DateTools.parse(end, DateTools.format_yyyyMMdd); // 检查格式
            if (startDate.after(endDate)) {
                throw new ExceptionDateEndBeforeStartError();
            }
            List<String> userList = new ArrayList<>();
            Business business = new Business(emc);
            if (filter.endsWith("@U")) { // 组织转化成人员列表
                List<String> users = business.organization().person().listWithUnitSubNested(filter); // 递归查询下级成员
                if (users != null && !users.isEmpty()) {
                    userList.addAll(users);
                }
            } else if (filter.endsWith("@P")) {
                userList.add(filter);
            }
            if (userList.isEmpty()) {
                throw new ExceptionEmptyParameter("过滤人员或组织");
            }
            ActionResult<Wo> result = new ActionResult<>();
            // 根据人员循环查询 并统计数据
            List<StatisticWo> wos = new ArrayList<>();
            // 统计数据计算
            StatisticWi wi = new StatisticWi();
            wi.setFilter(filter);
            wi.setStartDate(start);
            wi.setEndDate(end);
            statisticDetail(wi, userList, business, wos);
            // 数据excel文件传教
            Workbook wb = new XSSFWorkbook();
            createExcelFile(wb, wos);
            // 生成excel
            String name = filter;
            if (name.contains("@")) {
                name = name.split("@")[0];
            }
            String fileName = name+"的考勤统计_"+start + "-" + end + "_" +DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss)+".xlsx";
            wb.write(os);
            StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
            GeneralFile generalFile = new GeneralFile(gfMapping.getName(), fileName,
                    effectivePerson.getDistinguishedName());
            generalFile.saveContent(gfMapping, os.toByteArray(), fileName);
            emc.beginTransaction(GeneralFile.class);
            emc.persist(generalFile, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setFlag(generalFile.getId());
            result.setData(wo);
            return result;
        } finally {
            lock.unlock();
            LOGGER.info("统计结束。。。。。。。。。。。。");
        }
    }

    private void createExcelFile(Workbook wb, List<StatisticWo> wos) throws Exception {
        // 创建新的表格
        Sheet sheet = wb.createSheet("考勤统计");
        sheet.setDefaultColumnWidth(25);
        // 先创建表头
        Row row  = sheet.createRow(0);
        row.createCell(0).setCellValue("姓名");
        row.createCell(1).setCellValue("平均工时(小时)");
        row.createCell(2).setCellValue("工作时长");
        row.createCell(3).setCellValue("出勤天数");
        row.createCell(4).setCellValue("休息天数");
        row.createCell(5).setCellValue("旷工天数");
        row.createCell(6).setCellValue("迟到次数");
        row.createCell(7).setCellValue("早退次数");
        row.createCell(8).setCellValue("缺卡次数");
        row.createCell(9).setCellValue("外勤次数");
        if (wos != null && !wos.isEmpty()) {
            for (int i = 0; i < wos.size(); i++) {
                Row _row = sheet.createRow( i + 1 );
                StatisticWo wo = wos.get(i);
                String person = wo.getUserId();
                if (person.contains("@")) {
                    person = person.split("@")[0];
                }
                _row.createCell(0).setCellValue(person);
                _row.createCell(1).setCellValue(wo.getAverageWorkTimeDuration());
                _row.createCell(2).setCellValue(convertMinutesToHoursAndMinutes(wo.getWorkTimeDuration().intValue()));
                _row.createCell(3).setCellValue(wo.getAttendance());
                _row.createCell(4).setCellValue(wo.getRest());
                _row.createCell(5).setCellValue(wo.getAbsenteeismDays());
                _row.createCell(6).setCellValue(wo.getLateTimes());
                _row.createCell(7).setCellValue(wo.getLeaveEarlierTimes());
                _row.createCell(8).setCellValue(wo.getAbsenceTimes());
                _row.createCell(9).setCellValue(wo.getFieldWorkTimes());
            }
        }

    }

    // 格式化分钟数为 xx小时xx分钟
    private String convertMinutesToHoursAndMinutes(int workTimeDuration) {
        int hours = workTimeDuration / 60; // 取得小时数
        int remainingMinutes = workTimeDuration % 60; // 取得剩余的分钟数
        String result = "";

        if (hours > 0) {
            result += hours + " 小时";
        }

        if (remainingMinutes > 0 || StringUtils.isEmpty(result)) {
            result += remainingMinutes + " 分钟" ;
        }

        return result;
    }

    public static class Wo extends GsonPropertyObject {

        @FieldDescribe("返回的结果标识，下载结果文件使用")
        private String flag;


        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

    }
}
