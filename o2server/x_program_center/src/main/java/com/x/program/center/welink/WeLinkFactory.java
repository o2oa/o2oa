package com.x.program.center.welink;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.WeLink;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

/**
 * Created by fancyLou on 2020-07-24.
 * Copyright © 2020 O2. All rights reserved.
 */
public class WeLinkFactory {

    private static Logger logger = LoggerFactory.getLogger(WeLinkFactory.class);

    private String accessToken;

    private List<Department> orgs = new ArrayList<>();

    private List<User> users = new ArrayList<>();


    public WeLinkFactory(String accessToken) throws Exception {
        this.accessToken = accessToken;
        this.orgs();
        for (Department d: this.orgs) {
            this.usersWithDept(d.getDeptCode());
        }
        users = ListTools.trim(users, true, true);
    }


    public List<Department> roots() {
        return orgs.stream().filter(o -> "0".equals(o.getFatherCode())).collect(Collectors.toList());
    }



    public List<User> listUser(Department org) throws Exception {
        return users.stream().filter(o -> o.getDeptCode().equals(org.getDeptCode()))
                .collect(Collectors.toList());
    }

    public List<Department> listSub(Department org) throws Exception {
        return orgs.stream().filter(o -> {
            return Objects.equals(o.getFatherCode(), org.getDeptCode());
        }).collect(Collectors.toList());
    }

    private void orgs() throws Exception {
        OrgListResp root = this.orgs("0", "0", 1); //跟目录不能递归
        if (root.getDepartmentInfo()!=null && !root.getDepartmentInfo().isEmpty()) {
            for (int i = 0; i < root.getDepartmentInfo().size(); i++) {
                Department department = root.getDepartmentInfo().get(i);
                this.orgs.add(department);
                int offset = 1;
                int totalCount = 0;
                this.recursiveOrgs(department.getDeptCode(), offset, totalCount); //递归查询有所的组织
            }
        }
    }

    private void recursiveOrgs(String deptCode, int offset, int totalCount) throws Exception {
        logger.info("recursiveOrgs deptCode:"+deptCode+"， offset："+offset+" ，totalCount："+totalCount);
        OrgListResp subDepts = this.orgs(deptCode, "1", offset); //递归查询有所的组织
        if (!subDepts.getCode().equals("0") && !subDepts.getCode().equals("47009") && !subDepts.getCode().equals("47012")) {
            throw new ExceptionListOrg(subDepts.getCode(), subDepts.getMessage());
        }
        if (subDepts.getDepartmentInfo()!=null && !subDepts.getDepartmentInfo().isEmpty()) {
            this.orgs.addAll(subDepts.getDepartmentInfo());
            totalCount += subDepts.getDepartmentInfo().size();
            if (totalCount < subDepts.getTotalCount()) {
                offset++;
                recursiveOrgs(deptCode, offset, totalCount);
            }
        }
    }

    /**
     * 组织
     * @param deptCode 父组织id  顶级的父是0
     * @param recursiveflag 是否递归 0不递归 1递归
     * @return
     * @throws Exception
     */
    private OrgListResp orgs(String deptCode, String recursiveflag, Integer offset) throws Exception {
        String address = Config.weLink().getOapiAddress() + "/contact/v3/departments/list?recursiveflag="+recursiveflag+"&deptCode="+deptCode+"&offset="+offset;
        //deptCode
        //recursiveflag 0 ：查询下级部门信息 1 ：查询递归获取所有子部门
        List<NameValuePair> heads = new ArrayList<>();
        heads.add(new NameValuePair(WeLink.WeLink_Auth_Head_Key, this.accessToken));
        OrgListResp resp = HttpConnection.getAsObject(address, heads, OrgListResp.class);
        logger.info("orgs response:{}.", resp);
        if (!resp.getCode().equals("0") && !resp.getCode().equals("47009") && !resp.getCode().equals("47012")) {
            throw new ExceptionListOrg(resp.getCode(), resp.getMessage());
        }
        return resp;
    }

    private void usersWithDept(String deptCode) throws Exception {
        int pageNo = 1;
        this.usersPages(deptCode, pageNo);
    }

    //分页查询
    private void usersPages(String deptCode, int pageNo) throws Exception {
        UserListResp resp = this.users(deptCode, pageNo);
        if (resp.getData() != null && !resp.getData().isEmpty()) {
            this.users.addAll(resp.data);
            if (resp.getPages() > pageNo) {
                pageNo ++;
                usersPages(deptCode, pageNo);
            }
        }

    }

    /**
     * 查询用户
     * @param deptCode 所属组织id
     * @param pageNo 页码
     * @return
     * @throws Exception
     */
    private UserListResp users(String deptCode, int pageNo) throws Exception {
        String address = Config.weLink().getOapiAddress() + "/contact/v1/user/users?deptCode="+deptCode+"&pageNo="+pageNo+"&pageSize=50";
        List<NameValuePair> heads = new ArrayList<>();
        heads.add(new NameValuePair(WeLink.WeLink_Auth_Head_Key, this.accessToken));
        UserListResp resp = HttpConnection.getAsObject(address, heads, UserListResp.class);
        logger.info("users response:{}.", resp);
        if (!resp.getCode().equals("0") && !resp.getCode().equals("47009") && !resp.getCode().equals("47012")) {
            throw new ExceptionListUser(resp.getCode(), resp.getMessage());
        }
        return resp;
    }

    public static class OrgListResp extends GsonPropertyObject {
        //{ "code": "0",
        //  "message": "OK",
        //  "offset": 100,
        //  "limit": 25,
        //  "totalCount": 327,
        //  "departmentInfo": []
        //}
        private String code; //数据正常返回“0”，如果发生错误，会返回对应的错误码。
        private String message;
        private Long offset;
        private Long limit;
        private Long totalCount; //当前部门下所有部门数，如果当前部门为0级，仅能获取下一级的所有部门。
        private List<Department> departmentInfo;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
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

        public Long getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }

        public List<Department> getDepartmentInfo() {
            return departmentInfo;
        }

        public void setDepartmentInfo(List<Department> departmentInfo) {
            this.departmentInfo = departmentInfo;
        }
    }

    public static class UserListResp extends GsonPropertyObject {
        //{
        //    "code": "0",
        //    "message": "OK",
        //    "pageNo": 1,
        //    "pages": 1,
        //    "pageSize": "10",
        //    "total": 2,
        //    "data": []
        //}
        private String code; //数据正常返回“0”，如果发生错误，会返回对应的错误码。
        private String message;
        private Long pageNo; //当前页码
        private Long pages; //总共页数
        private Long total; //总用户数
        private List<User> data;


        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Long getPageNo() {
            return pageNo;
        }

        public void setPageNo(Long pageNo) {
            this.pageNo = pageNo;
        }

        public Long getPages() {
            return pages;
        }

        public void setPages(Long pages) {
            this.pages = pages;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public List<User> getData() {
            return data;
        }

        public void setData(List<User> data) {
            this.data = data;
        }
    }

}
