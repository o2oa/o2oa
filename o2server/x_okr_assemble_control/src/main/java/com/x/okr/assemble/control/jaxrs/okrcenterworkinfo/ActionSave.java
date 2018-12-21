package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkAuditLeaderEmpty;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkSave;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCenterWorkTitleEmpty;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionCompleteDateLimitFormat;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionUserUnitQuery;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );	
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		
		ActionResult<Wo> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		Wo wrapOutId = null;
		Boolean check = true;	
		Wi wrapIn = null;
		OkrUserCache  okrUserCache  = null;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			okrUserCache = checkUserLogin( effectivePerson.getDistinguishedName() );
			if( okrUserCache == null ){
				check = false;
				Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
				result.error( exception );
			}
		}
		
		if( check ){
			if( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty() ){
				check = false;
				Exception exception = new ExceptionCenterWorkTitleEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			if( wrapIn.getTitle().length() > 70 ){
				check = false;
				Exception exception = new ExceptionCenterWorkTitleEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			okrCenterWorkInfo = Wi.copier.copy( wrapIn );
			okrCenterWorkInfo.setId( wrapIn.getId() );
			okrCenterWorkInfo.setProcessStatus( "草稿" );
			okrCenterWorkInfo.setDeployerName( okrUserCache.getLoginUserName() );
			okrCenterWorkInfo.setDeployerUnitName( okrUserCache.getLoginUserUnitName());
			okrCenterWorkInfo.setDeployerTopUnitName( okrUserCache.getLoginUserTopUnitName());
			okrCenterWorkInfo.setDeployerIdentity( okrUserCache.getLoginIdentityName() );
			if( effectivePerson.getDistinguishedName().equals( okrUserCache.getLoginUserName())){
				okrCenterWorkInfo.setCreatorName( effectivePerson.getDistinguishedName() );
				okrCenterWorkInfo.setCreatorUnitName( okrUserCache.getLoginUserUnitName());
				okrCenterWorkInfo.setCreatorTopUnitName( okrUserCache.getLoginUserTopUnitName());
				okrCenterWorkInfo.setCreatorIdentity( okrUserCache.getLoginIdentityName() );
			}else{
				try{
					//需要查询创建者的相关身份
					wrapIn.setCreatorIdentity( okrUserManagerService.getIdentityWithPerson( effectivePerson.getDistinguishedName() ));
					wrapIn.setCreatorUnitName( okrUserManagerService.getUnitNameByIdentity( wrapIn.getCreatorIdentity() ));
					wrapIn.setCreatorTopUnitName( okrUserManagerService.getTopUnitNameByIdentity( wrapIn.getCreatorIdentity() ) );
				}catch(Exception e){
					check = false;
					Exception exception = new ExceptionUserUnitQuery( e, effectivePerson.getDistinguishedName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		//补充部署工作的年份和月份
		if( check ){
			try{
				okrCenterWorkInfo.setDeployYear( dateOperation.getYear( new Date() ));
				okrCenterWorkInfo.setDeployMonth( dateOperation.getMonth( new Date() ));
			}catch( Exception e ){
				check = false;
				result.error( e );
				logger.warn( "system get now date for year and month got an exception." );
				logger.error( e, effectivePerson, request, null);
			}	
		}
				
		//补充部署工作的默认最迟完成年份的日期形式
		if( check ){
			if( okrCenterWorkInfo.getDefaultCompleteDateLimitStr() != null && !okrCenterWorkInfo.getDefaultCompleteDateLimitStr().isEmpty() ){
				String date = null;
				try{
					date = dateOperation.getDateStringFromDate( dateOperation.getDateFromString ( wrapIn.getDefaultCompleteDateLimitStr()), "yyyy-MM-dd" ) + " 23:59:59";
					okrCenterWorkInfo.setDefaultCompleteDateLimit( dateOperation.getDateFromString( date ) );
				}catch( Exception e ){
					check = false;
					Exception exception = new ExceptionCompleteDateLimitFormat( e, okrCenterWorkInfo.getDefaultCompleteDateLimitStr() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		//中心工作部署后，根据需求，有的需要进行审批后才能继续部署，如果需要审批，那么isNeedAudit值为true，并且auditLeaderName不可为空。
		if( check ){
			if( okrCenterWorkInfo.getIsNeedAudit() && ListTools.isEmpty(okrCenterWorkInfo.getReportAuditLeaderIdentityList())){
				check = false;
				Exception exception = new ExceptionCenterWorkAuditLeaderEmpty();
				result.error( exception );
			}else {
				List<String> identities = new ArrayList<>();
				List<String> names = new ArrayList<>();
				List<String> unitNames = new ArrayList<>();
				List<String> topUnitNames = new ArrayList<>();
				for( String _identity : okrCenterWorkInfo.getReportAuditLeaderIdentityList() ) {
					String _name = userManagerService.getPersonNameByIdentity(_identity);
					String _unitName = userManagerService.getUnitNameByIdentity(_identity);
					String _topUnitName = userManagerService.getTopUnitNameByIdentity(_identity);
					identities.add( _identity );
					names.add( _name );
					unitNames.add( _unitName );
					topUnitNames.add( _topUnitName );
				}
				okrCenterWorkInfo.setReportAuditLeaderIdentityList(identities);
				okrCenterWorkInfo.setReportAuditLeaderNameList(names);
				okrCenterWorkInfo.setReportAuditLeaderUnitNameList(unitNames);
				okrCenterWorkInfo.setReportAuditLeaderTopUnitNameList(topUnitNames);
			}
		}
		//开始保存中心工作信息
		if( check ){
			try {
				okrCenterWorkInfo = okrCenterWorkOperationService.save( okrCenterWorkInfo );
				Wo wo = new Wo();
				wo.setId( okrCenterWorkInfo.getId() );
				result.setData( wo );
				result.setData( wrapOutId );
				if( okrCenterWorkInfo != null ) {
					WrapInWorkDynamic.sendWithCenterWorkInfo( 
							okrCenterWorkInfo, 
							effectivePerson.getDistinguishedName(),
							okrUserCache.getLoginUserName(),
							okrUserCache.getLoginUserName(),
							"保存中心工作",
							"中心工作保存成功！"
					);
				}
			} catch (Exception e) {
				Exception exception = new ExceptionCenterWorkSave( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends OkrCenterWorkInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		public static WrapCopier<Wi, OkrCenterWorkInfo> copier = WrapCopierFactory.wi( Wi.class, OkrCenterWorkInfo.class, null, JpaObject.FieldsUnmodify);
		
	}
	
	public static class Wo extends WoId {

	}
}