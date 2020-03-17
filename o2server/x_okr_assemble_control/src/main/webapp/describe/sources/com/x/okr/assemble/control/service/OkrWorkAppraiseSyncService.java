package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.dataadapter.workflow.WorkComplexGetter;
import com.x.okr.entity.OkrWorkAppraiseInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class OkrWorkAppraiseSyncService {

	public void updateAppraiseWfInfo(EffectivePerson effectivePerson, String title, String workId, String wf_jobId,
			String wf_workId, String status, WorkComplexGetter.Wo woWorkComplex) throws Exception {
		List<String> appraiseIds;
		OkrWorkAppraiseInfo appraiseInfo;
		Business business;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			OkrWorkBaseInfo workBase = emc.find(workId, OkrWorkBaseInfo.class);

			// 查询该工作所有的考核信息
			appraiseIds = business.okrWorkAppraiseInfoFactory().listIdsWithWorkId(workId, wf_workId);

			if (workBase != null) {

				if (ListTools.isEmpty(workBase.getAppraiseInfoList())) {
					workBase.setAppraiseInfoList(new ArrayList<>());
				}

				if (ListTools.isNotEmpty(appraiseIds)) {
					appraiseInfo = emc.find(appraiseIds.get(0), OkrWorkAppraiseInfo.class);
				} else {
					appraiseInfo = new OkrWorkAppraiseInfo();
					appraiseInfo.setId(null); // 先标识一下是新增加的
				}

				emc.beginTransaction(OkrWorkBaseInfo.class);
				emc.beginTransaction(OkrWorkAppraiseInfo.class);

				// 更新workBase相关流程信息和状态信息
				workBase.setCurrentAppraiseTitle(title);
				workBase.setCurrentAppraiseInfoId(appraiseInfo.getId());
				workBase.setCurrentAppraiseJobId(wf_jobId);
				workBase.setCurrentAppraiseWorkId(wf_workId);
				workBase.setCurrentAppraiseStatus(status);

				appraiseInfo.setTitle(title);
				appraiseInfo.setWf_jobId(wf_jobId);
				appraiseInfo.setWf_workId(wf_workId);

				appraiseInfo.setCenterId(workBase.getCenterId());
				appraiseInfo.setCenterTitle(workBase.getCenterTitle());
				appraiseInfo.setWorkId(workBase.getId());
				appraiseInfo.setWorkTitle(workBase.getTitle());
				
				if (woWorkComplex.getWork() != null) {
					if( StringUtils.isEmpty( wf_jobId )) {
						workBase.setCurrentAppraiseJobId( woWorkComplex.getWork().getJob() );
						appraiseInfo.setWf_jobId( woWorkComplex.getWork().getJob() );
					}
				}
				
				// 更新当前环节名称信息
				if (woWorkComplex.getWork().getActivityName() != null) {
					if ("结束".equals(woWorkComplex.getWork().getActivityName())
							|| "已完成".equals(woWorkComplex.getWork().getActivityName())) {
						workBase.setCurrentActivityName("结束");
						workBase.setCurrentAppraiseStatus("已完成");
						appraiseInfo.setActivityName("结束");
						appraiseInfo.setStatus("审核完成");
					} else if ("拟稿".equals(woWorkComplex.getWork().getActivityName())) {
						workBase.setCurrentActivityName("拟稿");
						workBase.setCurrentAppraiseStatus("审核中");
						appraiseInfo.setActivityName("拟稿");
						appraiseInfo.setStatus("拟稿");
					} else {
						workBase.setCurrentActivityName(woWorkComplex.getWork().getActivityName());
						workBase.setCurrentAppraiseStatus("审核中");
						appraiseInfo.setActivityName(woWorkComplex.getWork().getActivityName());
						appraiseInfo.setStatus(woWorkComplex.getWork().getActivityName());
					}
				} else {
					workBase.setCurrentActivityName("未知状态");
					workBase.setCurrentAppraiseStatus("未知状态");
					appraiseInfo.setActivityName("未知状态");
					appraiseInfo.setStatus("未知状态");
				}				
				if (StringUtils.isEmpty(appraiseInfo.getId())) {
					appraiseInfo.setId(OkrWorkAppraiseInfo.createId());
					workBase.getAppraiseInfoList().add(appraiseInfo.getId());
					emc.persist(appraiseInfo, CheckPersistType.all);
				} else {
					emc.check(appraiseInfo, CheckPersistType.all);
				}				
				emc.check(workBase, CheckPersistType.all);
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}
}