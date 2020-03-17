package com.x.okr.assemble.control.schedule;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;

/**
 * 定时代理，对中心工作的工作总数，完成情况以及中心工作的状态进行统计分析。 1、查询所有未完成的中心工作
 * 2、遍历所有中心工作，对中心工作所有的具体工作进行统计，按状态进行数量查询
 * 
 * @author LIYI
 *
 */
public class St_CenterWorkCount extends AbstractJob  {

	private static Logger logger = LoggerFactory.getLogger(St_CenterWorkCount.class);
	private OkrCenterWorkQueryService okrCenterWorkInfoService = new OkrCenterWorkQueryService();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		List<String> ids = null;
		// 草稿|待审核|待确认|执行中|已完成|已撤消
		List<String> processStatus = new ArrayList<String>();
		List<String> status = new ArrayList<String>();

		// 1、查询所有未完成的中心工作
		status.add("正常");
		processStatus.add("执行中");
		try {
			ids = okrCenterWorkInfoService.listAllProcessingCenterWorkIds(processStatus, status);
		} catch (Exception e) {
			logger.warn("system query uncomplete center work ids got an exception.");
			logger.error(e);
		}

		if (ids != null && !ids.isEmpty()) {
			for (String centerId : ids) {
				try {
					okrCenterWorkInfoService.countWorkWithCenterId(centerId, status);
				} catch (Exception e) {
					logger.warn("system count work info by center info got an exception.");
					logger.error(e);
				}
			}
		}
		logger.info("Timertask OKR_St_CenterWorkCount completed and excute success.");
	}
}