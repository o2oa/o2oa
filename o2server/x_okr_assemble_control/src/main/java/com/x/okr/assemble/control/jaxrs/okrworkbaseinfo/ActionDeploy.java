package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.dataadapter.webservice.sms.SmsMessageOperator;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionCenterWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkBaseInfoProcess;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoDeployService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoOperationService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionDeploy extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDeploy.class );
	private OkrWorkBaseInfoOperationService okrWorkBaseInfoOperationService = new OkrWorkBaseInfoOperationService();
	private OkrWorkBaseInfoDeployService okrWorkBaseInfoDeployService = new OkrWorkBaseInfoDeployService();
	
	/**
	 * 传入工作ID, 进行工作的部署
	 * @param effectivePerson
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<WoOkrWorkBaseInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<WoOkrWorkBaseInfo> result = new ActionResult<>();
		//前端传递所有需要部署的工作ID
		List<String> workIds = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = new ArrayList<OkrWorkBaseInfo>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo  = null;
		String centerId = null;
		Boolean check = true;
		WiOkrWorkBaseInfo wrapIn = null;
		OkrUserCache  okrUserCache  = null;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WiOkrWorkBaseInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			workIds = wrapIn.getWorkIds();
			if( workIds == null || workIds.isEmpty() ){
				check = false;
				Exception exception = new ExceptionWorkIdEmpty();
				result.error( exception );
			}
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
		//对wrapIn里的信息进行校验
		if( check && okrUserCache.getLoginUserName() == null ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName()  );
			result.error( exception );
		}
		
		if( check ){
			for ( String id : workIds ) {
				logger.debug( "system checking work, id:" + id );
				try {
					okrWorkBaseInfo = okrWorkBaseInfoService.get( id );
					// 判断工作信息是否全都存在
					if ( okrWorkBaseInfo == null ) {
						check = false;
						Exception exception = new ExceptionWorkNotExists( id  );
						result.error( exception );
						break;
					}
					okrWorkBaseInfoList.add( okrWorkBaseInfo );
					centerId = okrWorkBaseInfo.getCenterId();
					// 判断中心工作信息是否存在
					if (centerId != null) {
						okrCenterWorkInfo = okrCenterWorkInfoService.get( centerId );
						if ( okrCenterWorkInfo == null ) {
							check = false;
							Exception exception = new ExceptionCenterWorkNotExists( centerId  );
							result.error( exception );
							break;
						}
					}
				} catch ( Exception e ) {
					check = false;
					Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统校验需要部署的工作信息合法性时发生异常! WorkId:" + id  );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
					break;
				}
			}
		}
		//当所有的校验都通过后，再进行工作的部署
		if( check ){
			try {				
				okrWorkBaseInfoDeployService.deploy( workIds, okrUserCache.getLoginIdentityName()  );
			} catch( Exception e ){
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "部署具体工作过程中发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				okrWorkBaseInfoOperationService.createTasks( workIds, okrUserCache.getLoginIdentityName()  );
			}catch( Exception e ){
				logger.warn( "system createTasks got an exception." );
				logger.error( e );
			}
		}
		
		if( check ){
			for( OkrWorkBaseInfo _okrWorkBaseInfo : okrWorkBaseInfoList ){
				//工作的责任者接收短信
				SmsMessageOperator.sendWithPersonName( _okrWorkBaseInfo.getResponsibilityEmployeeName(), "您有工作'"+_okrWorkBaseInfo.getTitle()+"'，请及时办理！");
				
				//工作协助者接收短信
				if( ListTools.isNotEmpty( _okrWorkBaseInfo.getCooperateEmployeeNameList()) ) {
					for( String name : _okrWorkBaseInfo.getCooperateEmployeeNameList() ) {
						SmsMessageOperator.sendWithPersonName( name, "您有工作'"+_okrWorkBaseInfo.getTitle()+"'，请协助办理！");
					}
				}
				
				WrapInWorkDynamic.sendWithWorkInfo( _okrWorkBaseInfo, 
						effectivePerson.getDistinguishedName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"部署具体工作", 
						"具体工作部署成功！"
				);
			}
		}
		return result;
	}
}