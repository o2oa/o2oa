package com.x.attendance.assemble.control.jaxrs.v2.record;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.x_attendance_assemble_control;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.general.core.entity.GeneralFile;

/**
 * Created by fancyLou on 2023/4/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionImportExcel extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionImportExcel.class);

    private static ReentrantLock lock = new ReentrantLock();

    ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
            throws Exception {
        lock.lock();
        LOGGER.info("开始导入打卡记录数据。。。。。。。。。。");
        try (InputStream is = new ByteArrayInputStream(bytes);
             XSSFWorkbook workbook = new XSSFWorkbook(is);
             ByteArrayOutputStream os = new ByteArrayOutputStream();
             EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            Sheet sheet = workbook.getSheetAt(0); // 第一个sheet
            // 固定模版
            int firstRow = sheet.getFirstRowNum() + 1; // 第一行是标题跳过
            int lastRow = sheet.getLastRowNum();
            // count 一下 错误条数
            int errorRowNumber = 0;
            for (int i = firstRow; i <= lastRow; i++) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("处理行数：" + i + " ========================================");
                }
                Row row = sheet.getRow(i);
                try {
                    ActionPostDailyRecord.Wi thisWi = new ActionPostDailyRecord.Wi();
                    thisWi.setPerson(AttendanceV2Helper.getExcelCellStringValue(row.getCell(0)));
                    thisWi.setDate(AttendanceV2Helper.getExcelCellStringValue(row.getCell(1)));
                    thisWi.setOnDutyTime1(AttendanceV2Helper.getExcelCellStringValue(row.getCell(2)));
                    thisWi.setOffDutyTime1(AttendanceV2Helper.getExcelCellStringValue(row.getCell(3)));
                    thisWi.setOnDutyTime2(AttendanceV2Helper.getExcelCellStringValue(row.getCell(4)));
                    thisWi.setOffDutyTime2(AttendanceV2Helper.getExcelCellStringValue(row.getCell(5)));
                    thisWi.setOnDutyTime3(AttendanceV2Helper.getExcelCellStringValue(row.getCell(6)));
                    thisWi.setOffDutyTime3(AttendanceV2Helper.getExcelCellStringValue(row.getCell(7)));
                    ActionPostDailyRecord.Wo result = ThisApplication.context().applications().postQuery(x_attendance_assemble_control.class, "v2/record/import/daily", thisWi).getData(ActionPostDailyRecord.Wo.class);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("处理结果，{}", result.toString());
                    }
                } catch (Exception e) {
                    AttendanceV2Helper.setExcelCellError(row, e.getLocalizedMessage(), 8);
                    errorRowNumber++;
                }
            }
            ActionResult<Wo> result = new ActionResult<>();
            // 存储 excel 文件
            String name = "attendance_record_data_input_" + DateTools.formatDate(new Date()) + ".xlsx";
            workbook.write(os);
            StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
            GeneralFile generalFile = new GeneralFile(gfMapping.getName(), name,
                    effectivePerson.getDistinguishedName());
            generalFile.saveContent(gfMapping, os.toByteArray(), name);
            emc.beginTransaction(GeneralFile.class);
            emc.persist(generalFile, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setFlag(generalFile.getId());
            wo.setErrorRows(errorRowNumber);
            result.setData(wo);
            return result;
        } finally {
            lock.unlock();
            LOGGER.info("导入结束。。。。。。。。。。。。");
        }
    }

    public static class Wo extends GsonPropertyObject {

        @FieldDescribe("返回的结果标识，下载结果文件使用")
        private String flag;
        @FieldDescribe("异常错误数据条目数")
        private int errorRows;

        public int getErrorRows() {
            return errorRows;
        }

        public void setErrorRows(int errorRows) {
            this.errorRows = errorRows;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

    }
}
