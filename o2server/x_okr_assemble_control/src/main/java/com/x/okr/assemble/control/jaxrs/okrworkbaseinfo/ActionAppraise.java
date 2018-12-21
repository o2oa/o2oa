package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.dataadapter.workflow.WorkFlowReaderAdder;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWfWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkBaseInfoProcess;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkNotExists;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionAppraise extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionAppraise.class );
	
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request,EffectivePerson effectivePerson, String workId, String wf_workId ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		WrapOutBoolean wrapOutBoolean = new WrapOutBoolean();
		wrapOutBoolean.setValue( false );
		List<String> readerIdentities = null;
		
		if( workId == null || workId.isEmpty() ){
			check = false;
			Exception exception = new ExceptionWorkIdEmpty();
			result.error( exception );
		}
		
		if( wf_workId == null || wf_workId.isEmpty() ){
			check = false;
			Exception exception = new ExceptionWfWorkIdEmpty();
			result.error( exception );
		}
		
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName()  );
			result.error( exception );
		}
		
		if(check){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( workId );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new ExceptionWorkNotExists( workId );
					result.error( exception );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "查询指定ID的具体工作信息时发生异常。ID：" + workId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if(check){
			try {
				check = okrWorkBaseInfoService.bindAppraiseWfId( workId, wf_workId );
				wrapOutBoolean.setValue( check );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "为工作绑定考核流程ID时发生异常。ID：" + workId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if(check){
			try {
				readerIdentities = okrWorkPersonService.listWorkPersonIdentitiesWithWorkId(workId);
				if(ListTools.isNotEmpty(readerIdentities)) {
					List<String> addIdentities = new ArrayList<>();
					for( String identity: readerIdentities) {
						if( !addIdentities.contains(identity)) {
							addIdentities.add(identity);
						}
					}
					new WorkFlowReaderAdder().add(wf_workId, addIdentities );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "为流程添加读者权限时发生异常。ID：" + wf_workId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		result.setData(wrapOutBoolean);
		return result;
	}
}