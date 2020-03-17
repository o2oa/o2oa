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

import java.util.List;

public class DingdingAttendanceQueue extends AbstractQueue<DingdingQywxSyncRecord> {

    private static final Logger logger = LoggerFactory.getLogger(DingdingAttendanceQueue.class);

    @Override
    protected void execute(DingdingQywxSyncRecord record) throws Exception {
        logger.info("开始执行钉钉打卡数据同步，" + record.getWay());

    }

    private void dingdingSync() throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Application app = ThisApplication.context().applications().randomWithWeight(x_organization_assemble_control.class.getName());
            //开始分页查询人员
            boolean hasNextPerson = true;
            String uri = "person/list/(0)/next/50";
            String dingdingUrl = "https://oapi.dingtalk.com/attendance/list?access_token="+ Config.dingding().corpAccessToken();

            while (hasNextPerson) {
                List<Person> list = ThisApplication.context().applications().getQuery(false, app, uri).getDataAsList(Person.class);
                if (list != null && list.size() > 0) {
                    DingdingAttendancePost post = new DingdingAttendancePost();

                }else {
                    //没有用户查询到结束
                    logger.info("查询不到用户了，结束。。。。。。。。。。。。。。。");
                    hasNextPerson = false;
                }
            }

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
