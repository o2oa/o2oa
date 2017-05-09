package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.CenterWorkAuditLeaderEmptyException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.CenterWorkSaveException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.CenterWorkTitleEmptyException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.CompleteDateLimitFormatException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.UserOrganizationQueryException;
import com.x.okr.assemble.control.service.OkrCenterWorkOperationService;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ExcuteSave extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	private OkrCenterWorkOperationService okrCenterWorkOperationService = new OkrCenterWorkOperationService();
	private BeanCopyTools<WrapInOkrCenterWorkInfo, OkrCenterWorkInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrCenterWorkInfo.class, OkrCenterWorkInfo.class, null, WrapInOkrCenterWorkInfo.Excludes );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInOkrCenterWorkInfo wrapIn ) throws Exception {
		
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		WrapOutId wrapOutId = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		
		if( check ){
			okrUserCache = checkUserLogin( effectivePerson.getName() );
			if( okrUserCache == null ){
				check = false;
				Exception exception = new UserNoLoginException( effectivePerson.getName() );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
//		
//		if( check && wrapIn == null ){
//			check = false;
//			result.error( new Exception( "请求传入的参数为空，无法继续保存中心工作!" ) );
//			result.setUserMessage( "请求传入的参数为空，无法继续保存中心工作!" );
//		}
		
		if( check ){
			if( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty() ){
				check = false;
				Exception exception = new CenterWorkTitleEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( wrapIn.getTitle().length() > 70 ){
				check = false;
				Exception exception = new CenterWorkTitleEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			okrCenterWorkInfo = wrapin_copier.copy( wrapIn );
			okrCenterWorkInfo.setId( wrapIn.getId() );
			okrCenterWorkInfo.setProcessStatus( "草稿" );
			okrCenterWorkInfo.setDeployerName( okrUserCache.getLoginUserName() );
			okrCenterWorkInfo.setDeployerOrganizationName( okrUserCache.getLoginUserOrganizationName());
			okrCenterWorkInfo.setDeployerCompanyName( okrUserCache.getLoginUserCompanyName());
			okrCenterWorkInfo.setDeployerIdentity( okrUserCache.getLoginIdentityName() );
			if( effectivePerson.getName().equals( okrUserCache.getLoginUserName())){
				okrCenterWorkInfo.setCreatorName( effectivePerson.getName() );
				okrCenterWorkInfo.setCreatorOrganizationName( okrUserCache.getLoginUserOrganizationName());
				okrCenterWorkInfo.setCreatorCompanyName( okrUserCache.getLoginUserCompanyName());
				okrCenterWorkInfo.setCreatorIdentity( okrUserCache.getLoginIdentityName() );
			}else{
				try{
					//需要查询创建者的相关身份
					wrapIn.setCreatorIdentity( okrUserManagerService.getFistIdentityNameByPerson( effectivePerson.getName() ));
					wrapIn.setCreatorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( wrapIn.getCreatorIdentity() ));
					wrapIn.setCreatorCompanyName( okrUserManagerService.getCompanyNameByIdentity( wrapIn.getCreatorIdentity() ) );
				}catch(Exception e){
					check = false;
					Exception exception = new UserOrganizationQueryException( e, effectivePerson.getName() );
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
					Exception exception = new CompleteDateLimitFormatException( e, okrCenterWorkInfo.getDefaultCompleteDateLimitStr() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		//中心工作部署后，根据需求，有的需要进行审批后才能继续部署，如果需要审批，那么isNeedAudit值为true，并且auditLeaderName不可为空。
		if( check ){
			if( okrCenterWorkInfo.getIsNeedAudit() && ( okrCenterWorkInfo.getAuditLeaderName() == null || okrCenterWorkInfo.getAuditLeaderName().isEmpty() ) ){
				check = false;
				Exception exception = new CenterWorkAuditLeaderEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		//开始保存中心工作信息
		if( check ){
			try {
				okrCenterWorkInfo = okrCenterWorkOperationService.save( okrCenterWorkInfo );
				wrapOutId = new WrapOutId( okrCenterWorkInfo.getId() );
				result.setData( wrapOutId );
				okrWorkDynamicsService.workDynamic( okrCenterWorkInfo.getId(),  null,
						okrCenterWorkInfo.getTitle(), "保存中心工作",  effectivePerson.getName(), 
						okrUserCache.getLoginUserName(),  okrUserCache.getLoginIdentityName() , 
						"保存中心工作：" + okrCenterWorkInfo.getTitle(), 
						"中心工作保存成功！"
				);
			} catch (Exception e) {
				Exception exception = new CenterWorkSaveException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}