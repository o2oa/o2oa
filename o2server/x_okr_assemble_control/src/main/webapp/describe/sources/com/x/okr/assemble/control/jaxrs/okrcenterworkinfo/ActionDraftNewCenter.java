package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionUserNoLogin;
import com.x.okr.entity.OkrConfigWorkType;

public class ActionDraftNewCenter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDraftNewCenter.class );
	
	/**
	 * 1、生成新的中心工作ID<br/>
	 * 2、查询系统配置的汇报审核领导身份<br/>
	 * 3、查询系统中配置的所有工作类别列表<br/>
	 * 4、给出中心工作草稿应该有的操作列表：<br/>
	 *   1）创建具体工作<br/>
	 *   2）导入具体工作<br/>
	 *   3）关闭<br/>
	 * @param effectivePerson
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo okrCenterWorkDraft = new Wo();
		List<WoOkrWorkType> wrapOutTypes = new ArrayList<>();
		List<String> operation = new ArrayList<>();
		List<OkrConfigWorkType> types = null;
		
		WoOkrWorkType wrapOutType = null;
		String report_audit_leader = null;
		OkrUserCache  okrUserCache  = null;
		
		String cfg_report_audit_leader = null;
		Boolean check = true;
		
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}
		if( check && okrUserCache.getLoginUserName() == null ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}
		if( check ){
			okrCenterWorkDraft.setCreatorName( okrUserCache.getLoginUserName() );
			okrCenterWorkDraft.setCreatorIdentity( okrUserCache.getLoginIdentityName() );
			okrCenterWorkDraft.setCreatorUnitName( okrUserCache.getLoginUserUnitName() );
			okrCenterWorkDraft.setCreatorTopUnitName( okrUserCache.getLoginUserTopUnitName() );
			okrCenterWorkDraft.setDeployerName( okrUserCache.getLoginUserName() );
			okrCenterWorkDraft.setDeployerIdentity( okrUserCache.getLoginIdentityName() );
			okrCenterWorkDraft.setDeployerUnitName( okrUserCache.getLoginUserUnitName() );
			okrCenterWorkDraft.setDeployerTopUnitName( okrUserCache.getLoginUserTopUnitName() );
		}
		if( check ){
			String[] array = null;
			List<String> identies = new ArrayList<>();
			List<String> names = new ArrayList<>();
			cfg_report_audit_leader = okrConfigSystemService.getValueWithConfigCode( "REPORT_AUDIT_LEADER" );
			if( cfg_report_audit_leader != null && !cfg_report_audit_leader.isEmpty() ){
				array = cfg_report_audit_leader.split(",");
				if( array != null && array.length > 0 ){
					for( String identity : array ){
						//查询该领导对应的员工信息,并且取出姓名
						report_audit_leader = okrUserManagerService.getPersonNameByIdentity( identity );
						if( report_audit_leader != null ){
							identies.add( identity );
							names.add( report_audit_leader );
						}
					}
				}
//				okrCenterWorkDraft.setAuditLeaderIdentityList(identies);
//				okrCenterWorkDraft.setAuditLeaderNameList(names);
				okrCenterWorkDraft.setDefaultLeaderIdentityList(identies);
				okrCenterWorkDraft.setDefaultLeaderList(names);
				okrCenterWorkDraft.setReportAuditLeaderIdentityList(identies);
				okrCenterWorkDraft.setReportAuditLeaderNameList(names);
			}
		}
		if( check ){
			types = okrConfigWorkTypeService.listAll();
			if( types != null && !types.isEmpty() ){
				for( OkrConfigWorkType type : types ){
					wrapOutType = new WoOkrWorkType( type.getId(), type.getWorkTypeName(), type.getOrderNumber() );
					wrapOutTypes.add( wrapOutType );
				}
				SortTools.asc( wrapOutTypes, "orderNumber");
				okrCenterWorkDraft.setWorkTypes( wrapOutTypes );
			}
		}
		if( check ){
			operation.add( "CREATEWORK" );
			operation.add( "IMPORTWORK" );
			operation.add( "CLOSE" );
			okrCenterWorkDraft.setOperation(operation);
			okrCenterWorkDraft.setStatus( "正常" );
			okrCenterWorkDraft.setProcessStatus( "草稿" );
			okrCenterWorkDraft.setTitle("无标题");
			okrCenterWorkDraft.setDescription( "无" );
			okrCenterWorkDraft.setCreateTime( new Date() );
		}
		result.setData( okrCenterWorkDraft );
		result.setCount( 1L );
		return result;
	}
}