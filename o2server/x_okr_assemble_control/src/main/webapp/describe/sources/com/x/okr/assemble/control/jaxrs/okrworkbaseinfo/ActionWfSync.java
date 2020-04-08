package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkBaseInfoProcess;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoWfSycnService;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionWfSync extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionWfSync.class );
	private OkrWorkBaseInfoWfSycnService okrWorkBaseInfoWfSycnService = new OkrWorkBaseInfoWfSycnService();
	
	protected ActionResult<WoOkrWorkBaseInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String workInfoId, String wf_workId ) throws Exception {
		ActionResult<WoOkrWorkBaseInfo> result = new ActionResult<>();
		Boolean check = true;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		
		if( workInfoId == null || workInfoId.isEmpty() ){
			check = false;
			Exception exception = new ExceptionWorkIdEmpty();
			result.error( exception );
		}
		if( check ){
			try{
				okrWorkBaseInfo = okrWorkBaseInfoService.get( workInfoId );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new ExceptionWorkNotExists( workInfoId );
					result.error( exception );
				}
			}catch(Exception e){
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "查询指定ID的具体工作信息时发生异常。ID：" + workInfoId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			//如果存在正在流转的考核流程，那么对比一下该流程workID和wf_workId是否一致
			if( okrWorkBaseInfo.getCurrentAppraiseWorkId() != null && okrWorkBaseInfo.getCurrentAppraiseWorkId().length() > 10 ) {
				if( !okrWorkBaseInfo.getCurrentAppraiseWorkId().equalsIgnoreCase(wf_workId) ) {
					check = false;
					Exception exception = new ExceptionWorkBaseInfoProcess( "考核流程不一致：工作已经存在正在流转的考核流程，不允许再次绑定考核流程。" );
					result.error( exception );
				}else {
					//那么需要进行流转流程同步
					okrWorkBaseInfoWfSycnService.sync(workInfoId, wf_workId);
				}
			}else {
				//那么需要新建一个流程来进行流转流程同步
				okrWorkBaseInfoWfSycnService.sync(workInfoId, wf_workId);
			}
		}
		return result;
	}
	
}