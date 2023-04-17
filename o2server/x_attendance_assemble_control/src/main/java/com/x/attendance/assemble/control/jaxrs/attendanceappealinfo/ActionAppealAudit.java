package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

public class ActionAppealAudit extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionAppealAudit.class);

    protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
            JsonElement jsonElement) throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        List<String> ids = null;
        List<ProcessWo> wos = new ArrayList<>();
        ProcessWo processWo = null;
        String unitName = null;
        String topUnitName = null;
        AttendanceAppealInfo attendanceAppealInfo = null;
        Wi wrapIn = null;
        Date now = new Date();
        Boolean check = true;
        Boolean subProcessCheck = true;

        try {
            wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
        } catch (Exception e) {
            check = false;
            Exception exception = new ExceptionWrapInConvert(e, jsonElement);
            result.error(exception);
            LOGGER.error(e, effectivePerson, request, null);
        }

        if (check) {
            if (wrapIn.getIds() == null || wrapIn.getIds().isEmpty()) {
                check = false;
                Exception exception = new ExceptionAttendanceAppealProcess("需要审核的申诉ID为空！");
                result.error(exception);
            } else {
                ids = wrapIn.getIds();
                wo.setTotal(ids.size());
            }
        }

        if (check) {

            for (String id : ids) {

                subProcessCheck = true;
                processWo = new ProcessWo();
                processWo.setId(id);

                try {
                    attendanceAppealInfo = attendanceAppealInfoServiceAdv.get(id);
                    if (attendanceAppealInfo == null) {
                        subProcessCheck = false;
                        processWo.setSuccess(false);
                        processWo.setTitle("无标题");
                        processWo.setDiscription("考勤申诉信息不存在！");
                        Exception exception = new ExceptionAttendanceAppealNotExists(id);
                        result.error(exception);
                    } else {
                        processWo.setTitle("考勤申诉-" + attendanceAppealInfo.getEmpName() + "-"
                                + attendanceAppealInfo.getRecordDateString());
                    }
                } catch (Exception e) {
                    subProcessCheck = false;
                    processWo.setSuccess(false);
                    processWo.setTitle("无标题");
                    processWo.setDiscription("考勤申诉信息查询发生异常！");
                    Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在根据ID查询考勤申诉信息数据时发生异常。ID:" + id);
                    result.error(exception);
                    LOGGER.error(e, effectivePerson, request, null);
                }

                if (subProcessCheck) {
                    try {
                        unitName = userManagerService.getUnitNameWithPersonName(effectivePerson.getDistinguishedName());
                        if (unitName != null) {
                            topUnitName = userManagerService.getTopUnitNameWithUnitName(unitName);
                        } else {
                            check = false;
                            processWo.setSuccess(false);
                            processWo.setDiscription("申诉人员组织不存在。" + effectivePerson.getDistinguishedName());
                            Exception exception = new ExceptionPersonHasNoUnit(effectivePerson.getDistinguishedName());
                            result.error(exception);
                        }
                    } catch (Exception e) {
                        check = false;
                        processWo.setSuccess(false);
                        processWo.setDiscription("申诉人员组织查询发生异常！" + effectivePerson.getDistinguishedName());
                        Exception exception = new ExceptionAttendanceAppealProcess(e,
                                "系统根据员工姓名查询组织信息时发生异常！name:" + effectivePerson.getDistinguishedName());
                        result.error(exception);
                        LOGGER.error(e, effectivePerson, request, null);
                    }
                }
                if (subProcessCheck) {
                    try {
                        attendanceAppealInfo = attendanceAppealInfoServiceAdv.firstProcessAttendanceAppeal(id, unitName,
                                topUnitName, effectivePerson.getDistinguishedName(), // processorName
                                now, // processTime
                                wrapIn.getOpinion1(), // opinion
                                wrapIn.getStatus() // status审批状态:0-待处理，1-审批通过，-1-审批不能过，2-需要下一次审批
                        );
                        // 2023-04-10 更新几个字段内容
                        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                            AttendanceAppealInfo appealInfo = emc.find(attendanceAppealInfo.getId(),
                                    AttendanceAppealInfo.class);
                            if (StringUtils.isNotEmpty(wrapIn.getAppealReason())) {
                                appealInfo.setAppealReason(wrapIn.getAppealReason());
                            }
                            if (StringUtils.isNotEmpty(wrapIn.getSelfHolidayType())) {
                                appealInfo.setSelfHolidayType(wrapIn.getSelfHolidayType());
                            }
                            if (StringUtils.isNotEmpty(wrapIn.getAddress())) {
                                appealInfo.setAddress(wrapIn.getAddress());
                            }
                            if (StringUtils.isNotEmpty(wrapIn.getReason())) {
                                appealInfo.setReason(wrapIn.getReason());
                            }
                            if (StringUtils.isNotEmpty(wrapIn.getAppealDescription())) {
                                appealInfo.setAppealDescription(wrapIn.getAppealDescription());
                            }
                            emc.beginTransaction(AttendanceAppealInfo.class);
                            emc.persist(appealInfo, CheckPersistType.all);
                            emc.commit();
                        }
                        processWo.setDiscription("申诉处理成功！");
                    } catch (Exception e) {
                        check = false;
                        processWo.setSuccess(false);
                        processWo.setDiscription("申诉处理失败！");
                        Exception exception = new ExceptionAttendanceAppealProcess(e, id);
                        result.error(exception);
                        LOGGER.error(e, effectivePerson, request, null);
                    }
                }
                if (subProcessCheck) {
                    wo.setSuccessCount(wo.getSuccessCount() + 1);
                } else {
                    wo.setErrorCount(wo.getErrorCount() + 1);
                }
                wos.add(processWo);
            }
            wo.setProcessRecord(wos);
            result.setData(wo);
        }
        return result;
    }

    public static class Wi extends AttendanceAppealInfo {

        private static final long serialVersionUID = -5076990764713538973L;

        public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

        @FieldDescribe("需要复核的ID列表")
        private List<String> ids = null;

        @FieldDescribe("审批意见")
        private String opinion1;

        @FieldDescribe("申诉人的身份，考勤人员身份：如果考勤人员属于多个组织，可以选择一个身份进行申诉信息绑定.")
        private String identity = null;

        public String getIdentity() {
            return identity;
        }

        public void setIdentity(String identity) {
            this.identity = identity;
        }

        public List<String> getIds() {
            return ids;
        }

        public void setIds(List<String> ids) {
            this.ids = ids;
        }

        public String getOpinion1() {
            return opinion1;
        }

        public void setOpinion1(String opinion1) {
            this.opinion1 = opinion1;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private Integer total = 0;

        private Integer successCount = 0;

        private Integer errorCount = 0;

        private List<ProcessWo> processRecord = null;

        public Integer getTotal() {
            return total;
        }

        public Integer getSuccessCount() {
            return successCount;
        }

        public Integer getErrorCount() {
            return errorCount;
        }

        public List<ProcessWo> getProcessRecord() {
            return processRecord;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public void setSuccessCount(Integer successCount) {
            this.successCount = successCount;
        }

        public void setErrorCount(Integer errorCount) {
            this.errorCount = errorCount;
        }

        public void setProcessRecord(List<ProcessWo> processRecord) {
            this.processRecord = processRecord;
        }
    }

    public static class ProcessWo {

        private String id = null;

        private String title = null;

        private String discription = null;

        private Boolean success = true;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDiscription() {
            return discription;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDiscription(String discription) {
            this.discription = discription;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }
    }
}