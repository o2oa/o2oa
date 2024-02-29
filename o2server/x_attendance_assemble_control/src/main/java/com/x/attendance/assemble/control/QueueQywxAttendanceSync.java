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

import com.x.attendance.entity.AttendanceQywxDetail;
import com.x.attendance.entity.AttendanceQywxDetail_;
import com.x.attendance.entity.DingdingQywxSyncRecord;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Application;
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

public class QueueQywxAttendanceSync extends AbstractQueue<DingdingQywxSyncRecord> {

    private static final Logger logger = LoggerFactory.getLogger(QueueQywxAttendanceSync.class);


    @Override
    protected void execute(DingdingQywxSyncRecord record) throws Exception {
        logger.info("开始执行企业微信打卡数据同步，from:" + record.getDateFrom() + ", to:"+record.getDateTo());
        if (DingdingQywxSyncRecord.syncType_qywx.equals(record.getType())) {
            try {
                qywxSync(record);
            }catch (Exception e) {
                logger.error(e);
                updateSyncRecord(record, e.getMessage());
            }
        }
    }


    private void qywxSync(DingdingQywxSyncRecord record) throws Exception{
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
        deleteQywxAttendance(fromDate, toDate);
        //人员查询地址
        String uri = "person/list/(0)/next/50";
        String qywxuri = Config.qiyeweixin().getApiAddress() + "/cgi-bin/checkin/getcheckindata?access_token="+ Config.qiyeweixin().attendanceAccessToken();
        int saveNumber = 0;
        while (hasNextPerson) {
            List<Person> list = ThisApplication.context().applications().getQuery(false, app, uri).getDataAsList(Person.class);
            if (list != null && list.size() > 0) {
                //钉钉用户id
                List<String> qywxUsers = list.stream().filter(person -> StringUtils.isNotEmpty(person.getQiyeweixinId()))
                        .map(Person::getQiyeweixinId).collect(Collectors.toList());

                if(ListTools.isNotEmpty(qywxUsers)){
                    QywxPost post = new QywxPost();
                    post.setStarttime(DateTools.toUnixTimeStamp(fromDate.getTime()));
                    post.setEndtime(DateTools.toUnixTimeStamp(toDate.getTime()));
                    post.setUseridlist(qywxUsers);
                    post.setOpencheckindatatype(3);//全部打卡信息
                    logger.info("企业微信 post ：" + post.toString());
                    QywxResult result = HttpConnection.postAsObject(qywxuri, null, post.toString(), QywxResult.class);
                    logger.info("返回结果："+result.toString());
                    if (result.errcode == 0) {
                        List<QywxResultItem> resultList = result.getCheckindata();
                        saveQywxAttendance(resultList, list);
                        saveNumber += resultList.size();
                    } else {
                        //请求结果异常 结束
                        throw new ExceptionDingDingRequest(result.errmsg);
                    }

                }

                //是否还有更多用户
                if (list.size() < personPageSize) {
                    logger.info("同步钉钉考勤 没有更多用户了，结束！");
                    hasNextPerson = false;
                    updateSyncRecord(record, null);
                } else {
                    //还有更多用户继续查询
                    uri = "person/list/" + list.get(list.size() - 1).getDistinguishedName() + "/next/50";
                }
            } else {
                //没有用户查询到结束
                logger.info("同步钉钉考勤 查询不到用户了，结束！");
                hasNextPerson = false;
                updateSyncRecord(record, null);
            }
        }
        logger.info("结束 插入："+saveNumber+" 条");

        boolean hasNextDate = true;
        Date statisticDate = fromDate;
        while (hasNextDate) {
            logger.info("发起企业微信考勤数据统计， date:"+ DateTools.format(statisticDate));
            ThisApplication.personQywxStatisticQueue.send(statisticDate);
            ThisApplication.unitQywxStatisticQueue.send(statisticDate);
            if (!isSameDay(statisticDate, toDate)) {
                statisticDate = DateTools.addDay(statisticDate, 1);
            }else {
                hasNextDate = false;
            }
        }
        logger.info("发起数据统计程序 完成！");

    }

    private boolean isSameDay(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime( startDate );
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        return (start.get(Calendar.YEAR) == end.get(Calendar.YEAR) && start.get(Calendar.DAY_OF_YEAR) == end.get(Calendar.DAY_OF_YEAR));
    }

    private void saveQywxAttendance(List<QywxResultItem> resultList, List<Person> personList) throws Exception {
        if (resultList != null && !resultList.isEmpty()) {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                emc.beginTransaction(AttendanceQywxDetail.class);
                for (int i = 0; i < resultList.size(); i++) {
                    QywxResultItem item = resultList.get(i);
                    AttendanceQywxDetail detail = QywxResultItem.copier.copy(item);
                    if (detail.getCheckin_time() > 0) {
                        long timestamp = DateTools.toTimestamp(detail.getCheckin_time());
                        detail.setCheckin_time_date(new Date(timestamp));
                    }
                    //添加o2组织和用户
                    Optional<Person> first = personList.stream().filter(p -> item.userid.equals(p.getQiyeweixinId())).findFirst();
                    if (first.isPresent()) {
                        Person person = first.get();
                        String unit = getUnitWithPerson(person.getDistinguishedName(), business);
                        detail.setO2Unit(unit);
                        detail.setO2User(person.getDistinguishedName());
                    }
                    //设置正常的类型
                    if (StringUtils.isEmpty(detail.getException_type())){
                        detail.setException_type(AttendanceQywxDetail.EXCEPTION_TYPE_NORMAL);
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

    private void deleteQywxAttendance(Date fromDate, Date toDate) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            //先删除 再同步
            EntityManager em = emc.get(AttendanceQywxDetail.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<AttendanceQywxDetail> query = cb.createQuery(AttendanceQywxDetail.class);
            Root<AttendanceQywxDetail> root = query.from(AttendanceQywxDetail.class);
            Predicate p = cb.between(root.get(AttendanceQywxDetail_.checkin_time_date), fromDate, toDate);
            query.select(root).where(p);
            List<AttendanceQywxDetail> detailList = em.createQuery(query).getResultList();
            //先删除
            logger.info("删除");
            if (detailList != null) {
                logger.info("删除 list:"+detailList.size());
                emc.beginTransaction(AttendanceQywxDetail.class);
                for (int i = 0; i < detailList.size(); i++) {
                    emc.remove(detailList.get(i));
                }
                emc.commit();
            }
            logger.info("删除结束");
        }
    }


    private void updateSyncRecord(DingdingQywxSyncRecord record, String message) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            emc.beginTransaction(DingdingQywxSyncRecord.class);
            DingdingQywxSyncRecord entity = emc.find(record.getId(), DingdingQywxSyncRecord.class);
            entity.setEndTime(new Date());
            if (message == null || message.isEmpty()) {
                entity.setStatus(DingdingQywxSyncRecord.status_end);
            } else {
                entity.setStatus(DingdingQywxSyncRecord.status_error);
                entity.setExceptionMessage(message);
            }
            emc.commit();
        }
    }


    public static class QywxPost extends GsonPropertyObject {
        //{
        //   "opencheckindatatype": 3,
        //   "starttime": 1492617600,
        //   "endtime": 1492790400,
        //   "useridlist": ["james","paul"]
        //}

        //打卡类型。1：上下班打卡；2：外出打卡；3：全部打卡
        private int opencheckindatatype;
        private long starttime;
        private long endtime;
        private List<String> useridlist;

        public int getOpencheckindatatype() {
            return opencheckindatatype;
        }

        public void setOpencheckindatatype(int opencheckindatatype) {
            this.opencheckindatatype = opencheckindatatype;
        }

        public long getStarttime() {
            return starttime;
        }

        public void setStarttime(long starttime) {
            this.starttime = starttime;
        }

        public long getEndtime() {
            return endtime;
        }

        public void setEndtime(long endtime) {
            this.endtime = endtime;
        }

        public List<String> getUseridlist() {
            return useridlist;
        }

        public void setUseridlist(List<String> useridlist) {
            this.useridlist = useridlist;
        }
    }

    public static class QywxResult extends GsonPropertyObject {
        private int errcode;
        private String errmsg;
        private List<QywxResultItem> checkindata;

        public int getErrcode() {
            return errcode;
        }

        public void setErrcode(int errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public List<QywxResultItem> getCheckindata() {
            return checkindata;
        }

        public void setCheckindata(List<QywxResultItem> checkindata) {
            this.checkindata = checkindata;
        }
    }
    public static class QywxResultItem extends GsonPropertyObject {


        static WrapCopier<QywxResultItem, AttendanceQywxDetail> copier = WrapCopierFactory.wi(QywxResultItem.class,
                AttendanceQywxDetail.class, null, JpaObject.FieldsUnmodify);

        private String userid;
        private String groupname;
        private String checkin_type;
        private long checkin_time;
        private String exception_type;
        private String location_title;
        private String location_detail;
        private String wifiname;
        private String notes;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getGroupname() {
            return groupname;
        }

        public void setGroupname(String groupname) {
            this.groupname = groupname;
        }

        public String getCheckin_type() {
            return checkin_type;
        }

        public void setCheckin_type(String checkin_type) {
            this.checkin_type = checkin_type;
        }

        public long getCheckin_time() {
            return checkin_time;
        }

        public void setCheckin_time(long checkin_time) {
            this.checkin_time = checkin_time;
        }

        public String getException_type() {
            return exception_type;
        }

        public void setException_type(String exception_type) {
            this.exception_type = exception_type;
        }

        public String getLocation_title() {
            return location_title;
        }

        public void setLocation_title(String location_title) {
            this.location_title = location_title;
        }

        public String getLocation_detail() {
            return location_detail;
        }

        public void setLocation_detail(String location_detail) {
            this.location_detail = location_detail;
        }

        public String getWifiname() {
            return wifiname;
        }

        public void setWifiname(String wifiname) {
            this.wifiname = wifiname;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}
