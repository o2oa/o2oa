package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.dataadapter.workflow.WorkComplexGetter;
import com.x.okr.assemble.control.dataadapter.workflow.WorkComplexGetter.Wo;
import com.x.okr.entity.OkrWorkAppraiseInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

/**
 * 工作考核流程信息同步
 * 
 * @author O2LEE
 *
 */
public class OkrWorkBaseInfoWfSycnService {
	
//	private static  Logger logger = LoggerFactory.getLogger( OkrWorkBaseInfoWfSycnService.class );
	private WorkComplexGetter workComplexGetter = new WorkComplexGetter();
	
	public void sync(String workInfoId, String wf_workId) throws Exception {
		//先获取wf_workId这个流程的信息，看看流程信息是否存在
		Wo woWorkComplex = workComplexGetter.getWorkComplex(wf_workId);
		OkrWorkAppraiseInfo okrWorkAppraiseInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		if( woWorkComplex != null && woWorkComplex.getActivity() != null && woWorkComplex.getWork() != null ) {
			//流程信息存在，看看OKR里是否有流程的信息
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				emc.beginTransaction(OkrWorkBaseInfo.class);
				emc.beginTransaction(OkrWorkAppraiseInfo.class);
				
				okrWorkBaseInfo = emc.find( workInfoId, OkrWorkBaseInfo.class );
				if( okrWorkBaseInfo != null ) {
					List<String> wf_info_list = business.okrWorkAppraiseInfoFactory().listIdsWithWorkId(workInfoId, wf_workId);
					if( ListTools.isNotEmpty(wf_info_list) ) {
						//存在流程信息
						okrWorkAppraiseInfo = emc.find( wf_info_list.get(0), OkrWorkAppraiseInfo.class );
						okrWorkAppraiseInfo.setActivityName(woWorkComplex.getActivity().getName());
						okrWorkAppraiseInfo.setTitle(woWorkComplex.getWork().getTitle());
						
						if( "拟稿".equals(woWorkComplex.getActivity().getName())) {
							okrWorkAppraiseInfo.setStatus("拟稿");
							okrWorkBaseInfo.setCurrentAppraiseStatus("拟稿");
						}else if( "结束".equals(woWorkComplex.getActivity().getName())){
							okrWorkAppraiseInfo.setStatus("结束");
							okrWorkBaseInfo.setCurrentAppraiseStatus("结束");
						}else {
							okrWorkAppraiseInfo.setStatus("审核中");
							okrWorkBaseInfo.setCurrentAppraiseStatus("审核中");
						}
						emc.check(okrWorkAppraiseInfo, CheckPersistType.all);
						emc.check(okrWorkBaseInfo, CheckPersistType.all);
					}else {
						//需要新建一个流程信息
						okrWorkAppraiseInfo = new OkrWorkAppraiseInfo();
						okrWorkAppraiseInfo.setId( workInfoId );
						okrWorkAppraiseInfo.setCenterId(okrWorkBaseInfo.getCenterId());
						okrWorkAppraiseInfo.setCenterTitle(okrWorkBaseInfo.getCenterTitle());
						okrWorkAppraiseInfo.setId(wf_workId);
						okrWorkAppraiseInfo.setWf_jobId(woWorkComplex.getWork().getJob());
						okrWorkAppraiseInfo.setWf_workId(wf_workId);
						okrWorkAppraiseInfo.setWorkId(workInfoId);
						okrWorkAppraiseInfo.setWorkTitle(okrWorkBaseInfo.getTitle());
						okrWorkAppraiseInfo.setActivityName(woWorkComplex.getActivity().getName());
						okrWorkAppraiseInfo.setTitle(woWorkComplex.getWork().getTitle());
						
						okrWorkBaseInfo.setCurrentAppraiseInfoId(okrWorkAppraiseInfo.getId());
						okrWorkBaseInfo.setCurrentAppraiseWorkId(workInfoId);
						
						if( okrWorkBaseInfo.getAppraiseInfoList() == null ) {
							okrWorkBaseInfo.setAppraiseInfoList( new ArrayList<>());
						}
						if( okrWorkBaseInfo.getAppraiseInfoList().contains( workInfoId )) {
							 okrWorkBaseInfo.getAppraiseInfoList().add( workInfoId );
						}
						
						if( "拟稿".equals(woWorkComplex.getActivity().getName())) {
							okrWorkAppraiseInfo.setStatus("拟稿");
							okrWorkBaseInfo.setCurrentAppraiseStatus("拟稿");
						}else if( "结束".equals(woWorkComplex.getActivity().getName())){
							okrWorkAppraiseInfo.setStatus("结束");
							okrWorkBaseInfo.setCurrentAppraiseStatus("结束");
						}else {
							okrWorkAppraiseInfo.setStatus("审核中");
							okrWorkBaseInfo.setCurrentAppraiseStatus("审核中");
						}
						emc.persist(okrWorkAppraiseInfo, CheckPersistType.all);
						emc.check(okrWorkBaseInfo, CheckPersistType.all);
					}
				}
			}catch( Exception e ){
				throw e;
			}
		}else {
			throw new Exception("流程不存在！WORKID:" + wf_workId );
		}
	}
}