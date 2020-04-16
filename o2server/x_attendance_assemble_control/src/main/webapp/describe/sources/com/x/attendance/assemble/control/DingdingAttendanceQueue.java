package com.x.attendance.assemble.control;

import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Application;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.x_organization_assemble_control;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DingdingAttendanceQueue extends AbstractQueue<DingdingQywxSyncRecord> {

    private static final Logger logger = LoggerFactory.getLogger(DingdingAttendanceQueue.class);

    @Override
    protected void execute(DingdingQywxSyncRecord record) throws Exception {
        logger.info("开始执行钉钉打卡数据同步，" + record.getWay());
        if (DingdingQywxSyncRecord.syncType_dingding.equals(record.getType())) {
            dingdingSync(record);
        } else {
            logger.info("其它类型：");
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
        logger.debug(record.toString());
        Application app = ThisApplication.context().applications().randomWithWeight(x_organization_assemble_control.class.getName());
        //开始分页查询人员
        boolean hasNextPerson = true;
        int personPageSize = 50;
        String uri = "person/list/(0)/next/50";
        String dingdingUrl = "https://oapi.dingtalk.com/attendance/list?access_token=" + Config.dingding().corpAccessToken();
        logger.debug("dingding url :"+dingdingUrl);
        while (hasNextPerson) {
            logger.info("查询人员 uri:" + uri);
            List<Person> list = ThisApplication.context().applications().getQuery(false, app, uri).getDataAsList(Person.class);
            if (list != null && list.size() > 0) {
                //钉钉用户id
                List<String> ddUsers = list.stream().map(Person::getDingdingId).collect(Collectors.toList());
                //week
                Date today = new Date();
                today = DateTools.floorDate(today, null);
                Date sevenDayBefore = DateTools.addDay(today, -7);
                //分页查询
                int page = 0;
                boolean hasMoreResult = true;
                while (hasMoreResult) {
                    DingdingAttendancePost post = new DingdingAttendancePost();
                    //post传入 时间（时间间隔不能超过7天） 人员（人员数量不能超过50个）
                    post.setLimit(50L);
                    post.setOffset(page * 50L);//从0开始翻页
                    post.setWorkDateFrom(DateTools.format(sevenDayBefore, DateTools.format_yyyyMMddHHmmss));
                    post.setWorkDateTo(DateTools.format(today, DateTools.format_yyyyMMddHHmmss));
                    post.setUserIdList(ddUsers);
                    String body = post.toString();
                    logger.info("查询钉钉打卡结果：" + body);
                    DingdingAttendanceResult result = HttpConnection.postAsObject(dingdingUrl, null, post.toString(), DingdingAttendanceResult.class);
                    if (result.errcode != null && result.errcode == 0) {
                        saveDingdingRecord(result.getRecordresult());
                        if (result.hasMore) {
                            page++;
                        } else {
                            logger.info("同步钉钉考勤结束。。。。。。。。。。。。。。。。");
                            hasMoreResult = false;
                        }
                    } else {
                        //请求结果异常 结束
                        logger.error(new DingDingRequestException(result.errmsg));
                        hasMoreResult = false;
                    }
                }
                //是否还有更多用户
                if (list.size() < personPageSize) {
                    logger.info("同步钉钉考勤 没有更多用户了，结束。。。。。。。。。。。。。。。");
                    hasNextPerson = false;
                    updateSyncRecord(record, null);
                } else {
                    //还有更多用户继续查询
                    uri = "person/list/" + list.get(list.size() - 1).getDistinguishedName() + "/next/50";
                }
            } else {
                //没有用户查询到结束
                logger.info("同步钉钉考勤 查询不到用户了，结束。。。。。。。。。。。。。。。");
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
        logger.info("发起数据统计程序 完成。。。。。。。。。。");
    }

    private void saveDingdingRecord(List<DingdingAttendanceResultItem> list) throws Exception {
        if (list != null && !list.isEmpty()) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                emc.beginTransaction(AttendanceDingtalkDetail.class);
                for (int i = 0; i < list.size(); i++) {
                    DingdingAttendanceResultItem item = list.get(i);
                    AttendanceDingtalkDetail detail = DingdingAttendanceResultItem.copier.copy(item);
                    detail.setDdId(item.getId());
                    emc.persist(detail);
                }
                emc.commit();
            }
        }
    }

    private void updateSyncRecord(DingdingQywxSyncRecord record, String errMsg) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            emc.beginTransaction(DingdingQywxSyncRecord.class);
            record.setEndTime(new Date());
            if (errMsg == null || errMsg.isEmpty()) {
                record.setExceptionMessage(errMsg);
                record.setStatus(DingdingQywxSyncRecord.status_error);
            } else {
                record.setStatus(DingdingQywxSyncRecord.status_end);
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
        private Integer offset;
        private Integer limit;

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

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }
    }
}
