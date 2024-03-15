package com.x.attendance.assemble.control;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.entity.AttendanceDingtalkDetail;
import com.x.attendance.entity.AttendanceDingtalkDetail_;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

public class QueueDingdingAttendance extends AbstractQueue<DingdingQywxSyncRecord> {

    private static final Logger logger = LoggerFactory.getLogger(QueueDingdingAttendance.class);

    @Override
    protected void execute(DingdingQywxSyncRecord record) throws Exception {
        logger.info("开始执行钉钉打卡数据同步，from:" + record.getDateFrom() + ", to:"+record.getDateTo());
        if (DingdingQywxSyncRecord.syncType_dingding.equals(record.getType())) {
            try {
                dingdingSync(record);
            }catch (Exception e) {
                logger.error(e);
                updateSyncRecord(record, e.getMessage());
            }
        }
    }

    private boolean isSameDay(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime( startDate );
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        return (start.get(Calendar.YEAR) == end.get(Calendar.YEAR) && start.get(Calendar.DAY_OF_YEAR) == end.get(Calendar.DAY_OF_YEAR));
    }
    private void dingdingSync(DingdingQywxSyncRecord record) throws Exception {
        Application app = ThisApplication.context().applications().randomWithWeight(x_organization_assemble_control.class.getName());
        //开始分页查询人员
        boolean hasNextPerson = true;
        int personPageSize = 50;
        //开始时间和结束时间
        Date fromDate = new Date();
        fromDate.setTime(record.getDateFrom());
        Date toDate =new Date();
        toDate.setTime(record.getDateTo());
        //先删除
        deleteDingdingAttendance(fromDate, toDate);
        //人员查询地址
        String uri = "person/list/(0)/next/50";
        //钉钉考勤同步接口地址
        String dingdingUrl = Config.dingding().getOapiAddress() + "/attendance/list?access_token=" + Config.dingding().corpAccessToken();
        int saveNumber = 0;
        while (hasNextPerson) {
            logger.info("uri:"+ uri);
            List<Person> list = ThisApplication.context().applications().getQuery(false, app, uri).getDataAsList(Person.class);
            if (list != null && list.size() > 0) {
                //钉钉用户id
                List<String> ddUsers = list.stream().filter(person -> StringUtils.isNotEmpty(person.getDingdingId()))
                        .map(Person::getDingdingId).collect(Collectors.toList());
                if (ListTools.isNotEmpty(ddUsers)) {
                    //分页查询
                    int page = 0;
                    boolean hasMoreResult = true;
                    while (hasMoreResult) {
                        DingdingAttendancePost post = new DingdingAttendancePost();
                        //post传入 时间（时间间隔不能超过7天） 人员（人员数量不能超过50个）
                        post.setLimit(50L);
                        post.setOffset(page * 50L);//从0开始翻页
                        //这里的开始时间和结束时间 查询的是钉钉返回结果中的workDate
                        //查询考勤打卡记录的起始工作日
                        post.setWorkDateFrom(DateTools.format(fromDate));
                        //查询考勤打卡记录的结束工作日
                        post.setWorkDateTo(DateTools.format(toDate));
                        post.setUserIdList(ddUsers);
                        DingdingAttendanceResult result = HttpConnection.postAsObject(dingdingUrl, null, post.toString(), DingdingAttendanceResult.class);
                        if (result.errcode != null && result.errcode == 0) {
                            List<DingdingAttendanceResultItem> resultList = result.getRecordresult();
                            saveDingdingAttendance(resultList, list);
                            saveNumber += resultList.size();
                            if (result.hasMore) {
                                page++;
                            } else {
                                logger.info("同步钉钉考勤结束！");
                                hasMoreResult = false;
                            }
                        } else {
                            //请求结果异常 结束
                            throw new ExceptionDingDingRequest(result.errmsg);
                        }
                    }
                }
                //是否还有更多用户
                if (list.size() < personPageSize) {
                    logger.info("同步钉钉考勤 没有更多用户了，结束！");
                    hasNextPerson = false;
                    updateSyncRecord(record, null);
                } else {
                    //还有更多用户继续查询
                    uri = Applications.joinQueryUri("person","list", list.get(list.size() - 1).getDistinguishedName(), "next", "50");
//                    uri = "person/list/" + list.get(list.size() - 1).getDistinguishedName() + "/next/50";
                }
            } else {
                //没有用户查询到结束
                logger.info("同步钉钉考勤 查询不到用户了，结束！");
                hasNextPerson = false;
                updateSyncRecord(record, null);
            }
        }
        logger.info("结束 插入："+saveNumber+" 条");
        //插入数据成功 开始统计程序

        boolean hasNextDate = true;
        Date statisticDate = fromDate;
        while (hasNextDate) {
            logger.info("发起钉钉考勤数据统计， date:"+ DateTools.format(statisticDate));
            ThisApplication.personStatisticQueue.send(statisticDate);
            ThisApplication.unitStatisticQueue.send(statisticDate);
            if (!isSameDay(statisticDate, toDate)) {
                statisticDate = DateTools.addDay(statisticDate, 1);
            }else {
                hasNextDate = false;
            }
        }
        logger.info("发起数据统计程序 完成！");
    }
    private void deleteDingdingAttendance(Date fromDate, Date toDate) throws Exception{
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            //先删除 再同步
            EntityManager em = emc.get(AttendanceDingtalkDetail.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<AttendanceDingtalkDetail> query = cb.createQuery(AttendanceDingtalkDetail.class);
            Root<AttendanceDingtalkDetail> root = query.from(AttendanceDingtalkDetail.class);
            long start = fromDate.getTime();
            long end = toDate.getTime();
            Predicate p = cb.between(root.get(AttendanceDingtalkDetail_.workDate), start, end);
            query.select(root).where(p);
            List<AttendanceDingtalkDetail> detailList = em.createQuery(query).getResultList();
            //先删除
            logger.info("删除");
            if (detailList != null) {
                logger.info("删除 list:"+detailList.size());
                emc.beginTransaction(AttendanceDingtalkDetail.class);
                for (int i = 0; i < detailList.size(); i++) {
                    emc.remove(detailList.get(i));
                }
                emc.commit();
            }
            logger.info("删除结束");
        }
    }

    private void saveDingdingAttendance(List<DingdingAttendanceResultItem> list, List<Person> personList) throws Exception {
        if (list != null && !list.isEmpty()) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                emc.beginTransaction(AttendanceDingtalkDetail.class);
                for (int i = 0; i < list.size(); i++) {
                    DingdingAttendanceResultItem item = list.get(i);
                    AttendanceDingtalkDetail detail = DingdingAttendanceResultItem.copier.copy(item);
                    detail.setDdId(item.getId());
                    if (detail.getUserCheckTime() > 0) {
                        Date date = new Date(detail.getUserCheckTime());
                        detail.setUserCheckTimeDate(date);
                    }
                    //添加o2组织和用户
                    Optional<Person> first = personList.stream().filter(p -> item.userId.equals(p.getDingdingId())).findFirst();
                    if (first.isPresent()) {
                        Person person = first.get();
                        String unit = getUnitWithPerson(person.getDistinguishedName(), business);
                        detail.setO2Unit(unit);
                        detail.setO2User(person.getDistinguishedName());
                    }
                    emc.persist(detail);
                }
                emc.commit();
            }
        }
    }
    private String getUnitWithPerson(String person, Business business) throws Exception {
        String result = null;
        Integer level = 0;
        Unit unit = null;
        List<String> unitNames = business.organization().unit().listWithPerson( person );
        if( ListTools.isNotEmpty( unitNames ) ) {
            for( String unitName : unitNames ) {
                if( StringUtils.isNotEmpty( unitName ) && !"null".equals( unitName ) ) {
                    unit = business.organization().unit().getObject( unitName );
                    if( level < unit.getLevel() ) {
                        level = unit.getLevel();
                        result = unitName;
                    }
                }
            }
        }
        return result;
    }

    private void updateSyncRecord(DingdingQywxSyncRecord record, String errMsg) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            emc.beginTransaction(DingdingQywxSyncRecord.class);
            DingdingQywxSyncRecord entity = emc.find(record.getId(), DingdingQywxSyncRecord.class);
            entity.setEndTime(new Date());
            if (errMsg == null || errMsg.isEmpty()) {
                entity.setStatus(DingdingQywxSyncRecord.status_end);
            } else {
                entity.setStatus(DingdingQywxSyncRecord.status_error);
                entity.setExceptionMessage(errMsg);
            }
            emc.commit();
        }
    }


    public static class DingdingAttendancePost extends GsonPropertyObject {
        //        {
//            "workDateFrom": "yyyy-MM-dd HH:mm:ss",
//                "workDateTo": "yyyy-MM-dd HH:mm:ss",
//                "userIdList":["员工UserId列表"],    // 必填，与offset和limit配合使用
//            "offset":0,    // 必填，第一次传0，如果还有多余数据，下次传之前的offset加上limit的值
//                "limit":1,     // 必填，表示数据条数，最大不能超过50条
//        }
        private String workDateFrom;
        private String workDateTo;
        private List<String> userIdList;
        private Long offset;
        private Long limit;

        public String getWorkDateFrom() {
            return workDateFrom;
        }

        public void setWorkDateFrom(String workDateFrom) {
            this.workDateFrom = workDateFrom;
        }

        public String getWorkDateTo() {
            return workDateTo;
        }

        public void setWorkDateTo(String workDateTo) {
            this.workDateTo = workDateTo;
        }

        public List<String> getUserIdList() {
            return userIdList;
        }

        public void setUserIdList(List<String> userIdList) {
            this.userIdList = userIdList;
        }

        public Long getOffset() {
            return offset;
        }

        public void setOffset(Long offset) {
            this.offset = offset;
        }

        public Long getLimit() {
            return limit;
        }

        public void setLimit(Long limit) {
            this.limit = limit;
        }
    }

    public static class DingdingAttendanceResult extends GsonPropertyObject {
        private Integer errcode;
        private String errmsg;
        private Boolean hasMore;
        private List<DingdingAttendanceResultItem> recordresult;

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public Boolean getHasMore() {
            return hasMore;
        }

        public void setHasMore(Boolean hasMore) {
            this.hasMore = hasMore;
        }

        public List<DingdingAttendanceResultItem> getRecordresult() {
            return recordresult;
        }

        public void setRecordresult(List<DingdingAttendanceResultItem> recordresult) {
            this.recordresult = recordresult;
        }
    }

    public static class DingdingAttendanceResultItem extends GsonPropertyObject {

        private static final long serialVersionUID = 1618421987561110713L;

        static WrapCopier<DingdingAttendanceResultItem, AttendanceDingtalkDetail> copier = WrapCopierFactory.wi(DingdingAttendanceResultItem.class,
                AttendanceDingtalkDetail.class, null, JpaObject.FieldsUnmodify);

        private long id;
        private long ddId;
        private String userId;
        private long baseCheckTime;
        private long userCheckTime;
        private long workDate;
        private String timeResult;
        private String checkType;
        private String locationResult;
        private String sourceType;
        private long groupId;
        private long planId;
        private long recordId;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getDdId() {
            return ddId;
        }

        public void setDdId(long ddId) {
            this.ddId = ddId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public long getBaseCheckTime() {
            return baseCheckTime;
        }

        public void setBaseCheckTime(long baseCheckTime) {
            this.baseCheckTime = baseCheckTime;
        }

        public long getUserCheckTime() {
            return userCheckTime;
        }

        public void setUserCheckTime(long userCheckTime) {
            this.userCheckTime = userCheckTime;
        }

        public long getWorkDate() {
            return workDate;
        }

        public void setWorkDate(long workDate) {
            this.workDate = workDate;
        }

        public String getTimeResult() {
            return timeResult;
        }

        public void setTimeResult(String timeResult) {
            this.timeResult = timeResult;
        }

        public String getCheckType() {
            return checkType;
        }

        public void setCheckType(String checkType) {
            this.checkType = checkType;
        }

        public String getLocationResult() {
            return locationResult;
        }

        public void setLocationResult(String locationResult) {
            this.locationResult = locationResult;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public long getGroupId() {
            return groupId;
        }

        public void setGroupId(long groupId) {
            this.groupId = groupId;
        }

        public long getPlanId() {
            return planId;
        }

        public void setPlanId(long planId) {
            this.planId = planId;
        }

        public long getRecordId() {
            return recordId;
        }

        public void setRecordId(long recordId) {
            this.recordId = recordId;
        }
    }
}
