package com.x.report.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_R_CreateTime;

/**
 * 记录所有类别的汇报上一次和下一次生成时间的信息服务类
 * @author O2LEE
 *
 */
public class Report_R_CreateTimeService {

	public Report_R_CreateTime get(EntityManagerContainer emc, String id) throws Exception {
		return emc.find( id, Report_R_CreateTime.class );
	}

	/**
	 * 根据指定的ID列示汇报生成时间记录信息
	 * @param emc
	 * @param ids 汇报生成依据记录信息ID列表
	 * @return
	 * @throws Exception
	 */
	public List<Report_R_CreateTime> listAll(EntityManagerContainer emc ) throws Exception {
		Business business = new Business( emc );
		return business.report_R_CreateTimeFactory().listAll();
	}
	
}
