package com.x.attendance.assemble.control.jaxrs.v2.leave;

import com.google.gson.JsonParser;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.x_attendance_assemble_control;
import com.x.general.core.entity.GeneralFile;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * excel 导入请假数据
 * Created by fancyLou on 2023/4/6.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionImportExcel extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionImportExcel.class);

    private static ReentrantLock lock = new ReentrantLock();

    ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
            throws Exception {
        lock.lock();
        LOGGER.info("开始导入请假数据。。。。。。。。。。");
        try (InputStream is = new ByteArrayInputStream(bytes);
             XSSFWorkbook workbook = new XSSFWorkbook(is);
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.getSheetAt(0); // 第一个sheet
            // 固定模版
            int firstRow = sheet.getFirstRowNum() + 1 ; // 第一行是标题跳过
            int lastRow = sheet.getLastRowNum();
            for (int i = firstRow; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                String person = getCellStringValue(row.getCell(0)); // 第一条是person
                if (StringUtils.isEmpty(person)) {
                    setError(row, "用户标识不能为空");
                    continue;
                }
                String type = getCellStringValue(row.getCell(1)); // 第二条是请假类型:带薪年休假|带薪病假|带薪福利假|扣薪事假|出差|培训|其他
                if (StringUtils.isEmpty(type)) {
                    setError(row, "请假类型不能为空");
                    continue;
                }
                String start = getCellStringValue(row.getCell(2)); // 开始时间：yyyy-MM-dd HH:mm:ss
                if (StringUtils.isEmpty(start)) {
                    setError(row, "开始时间不能为空");
                    continue;
                }
                Date startDate;
                try {
                    startDate = DateTools.parse(start, DateTools.format_yyyyMMddHHmmss);
                } catch (Exception e){
                    setError(row, "开始时间格式不正确");
                    continue;
                }
                String end = getCellStringValue(row.getCell(3)); // 结束时间：yyyy-MM-dd HH:mm:ss
                if (StringUtils.isEmpty(end)) {
                    setError(row, "结束时间不能为空");
                    continue;
                }
                Date endDate;
                try {
                    endDate = DateTools.parse(end, DateTools.format_yyyyMMddHHmmss);
                } catch (Exception e){
                    setError(row, "结束时间格式不正确");
                    continue;
                }
                String desc = getCellStringValue(row.getCell(4)); // 请假说明
                if (StringUtils.isEmpty(desc)) {
                    desc = "";
                }
                String job = getCellStringValue(row.getCell(5)); // 流程的jobId,可为空
                if (StringUtils.isEmpty(job)) {
                    job = "";
                }
                ActionPost.Wi wi = new ActionPost.Wi();
                wi.setPerson(person);
                wi.setLeaveType(type);
                wi.setStartTime(startDate);
                wi.setEndTime(endDate);
                wi.setDescription(desc);
                wi.setJobId(job);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("导入数据，row ： " + i + " ， " + wi.toString());
                }
                try {
                    ActionPost.Wo postResult = ThisApplication.context().applications().postQuery(x_attendance_assemble_control.class, "v2/leave", wi).getData(ActionPost.Wo.class);
                    if (postResult != null) {
                        LOGGER.info("保持数据结果：" + postResult.getValue());
                    }
                }catch (Exception e) {
                    setError(row, e.getLocalizedMessage());
                }
            }
            ActionResult<Wo> result = new ActionResult<>();
            String name = "attendance_leave_data_input_" + DateTools.formatDate(new Date()) + ".xlsx";
            workbook.write(os);
            String flag = saveAttachment(os.toByteArray(), name, effectivePerson);
            Wo wo = new Wo();
            wo.setFlag(flag);
            result.setData(wo);
            return result;
        }finally {
            lock.unlock();
            LOGGER.info("导入结束。。。。。。。。。。。。");
        }
    }

    private void setError(Row row, String error) {
        Cell cell = CellUtil.getCell(row, 7);
        cell.setCellValue(error);
    }

    private String getCellStringValue(Cell cell) {
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
                    return cell.getStringCellValue();
            }
        }
        return "";
    }

    private String saveAttachment(byte[] bytes, String attachmentName, EffectivePerson effectivePerson)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
            GeneralFile generalFile = new GeneralFile(gfMapping.getName(), attachmentName,
                    effectivePerson.getDistinguishedName());
            generalFile.saveContent(gfMapping, bytes, attachmentName);
            emc.beginTransaction(GeneralFile.class);
            emc.persist(generalFile, CheckPersistType.all);
            emc.commit();
            return generalFile.getId();
        }
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
