package com.x.attendance.assemble.control.jaxrs.v2.detail.model;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * Created by fancyLou on 2023/3/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class StatisticWi extends GsonPropertyObject {


    private static final long serialVersionUID = -4697552136228406338L;


    @FieldDescribe("过滤人员或组织，组织只支持单层: 用户或组织的DN，如xxx@xxx@P、xxx@xxx@U")
    private String filter;

    // 这个 filterList 字段为准 上面的 filter 字段已经弃用
    @FieldDescribe("过滤人员或组织，组织只支持单层: 用户或组织的DN，如xxx@xxx@P、xxx@xxx@U")
    private List<String> filterList;

    @FieldDescribe("开始日期，包含")
    private String startDate;

    @FieldDescribe("结束日期， 包含")
    private String endDate;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getFilterList() {
      return filterList;
    }

    public void setFilterList(List<String> filterList) {
      this.filterList = filterList;
    }

    
}
