package com.x.okr.assemble.control.jaxrs.okrauthorize;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.AuthorizeOpinionEmptyException;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.AuthorizeTakerIdentityEmptyException;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.AuthorizeWorkIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.PersonNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.SystemConfigFetchException;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.SystemConfigNotFetchException;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.WorkAuthorizeNotOpenException;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.WorkAuthorizeProcessException;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.WorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.WorkQueryByIdException;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkAuthorizeService;
import com.x.okr.entity.OkrConfigSystem;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.organization.core.express.wrap.WrapPerson;

public class ExcuteWorkAuthorize extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteWorkAuthorize.class );
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	
	/**
	 * 工作处理授权服务<br/>
	 * 
	 * 问题：<br/>
	 * 1、授权后，原有责任者是否有处理权限，是否应用把身份转换为授权者，取消继续处理权限？<br/>
	 * 2、工作被拆解或者直接确认后，授权者是否需要收到一个通知，或者待阅等<br/>
	 * 
	 * 处理思路：<br/>
	 * 1、将工作责任者身份替换为被授权者<br/>
	 * 2、将干系人信息中的责任者身份转换为授权者（或者不用转换）<br/>
	 * 3、删除原责任者的待办，转换为原责任者已办<br/>
	 * 4、新增新的责任者待办<br/>
	 * <br/>
	 * PUT PARAMETER : workId (工作ID)<br/>
	 * PUT PARAMETER : authorizeOpinion (工作授权意见)<br/>
	 * PUT PARAMETER : authorizeIdentity (工作授权者身份)<br/>
	 * PUT PARAMETER : undertakerIdentity (工作授权承担人身份)<br/>
	 * 
	 * @param request
	 * @return
	 */
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilterWorkAuthorize wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<WrapOutId>();
		OkrConfigSystem okrConfigSystem  = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrUserCache  okrUserCache  = null;
		WrapPerson person = null;
		String workId = null;
		String authorizeOpinion = null;
		String authorizeIdentity = null;
		String undertakerIdentity = null;
		Boolean check = true;
		if( check ){
			if( wrapIn != null ){
				workId = wrapIn.getWorkId();
				authorizeOpinion = wrapIn.getAuthorizeOpinion();
				undertakerIdentity = wrapIn.getUndertakerIdentity();
			}
		}
		if( check ){
			try {//1、判断系统是否已经开启授权操作WORK_AUTHORIZE
				okrConfigSystem = okrConfigSystemService.getWithConfigCode( "WORK_AUTHORIZE" );
				if( okrConfigSystem == null ){
					check = false;
					Exception exception = new SystemConfigNotFetchException( "WORK_AUTHORIZE");
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new SystemConfigFetchException( e, "WORK_AUTHORIZE");
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( !"OPEN".equals( okrConfigSystem.getConfigValue() )){
				check = false;
				Exception exception = new WorkAuthorizeNotOpenException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			person = okrUserManagerService.getUserByIdentity( undertakerIdentity );
			if( person == null ){
				check = false;
				Exception exception = new PersonNotExistsException( undertakerIdentity );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			} catch (Exception e ) {
				check = false;
				Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			if( okrUserCache.getLoginUserName() == null ){
				check = false;
				Exception exception = new UserNoLoginException( effectivePerson.getName() );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
			authorizeIdentity = okrUserCache.getLoginIdentityName();
		}
		if( check ){
			if( workId == null || workId.isEmpty() ){
				check = false;
				Exception exception = new AuthorizeWorkIdEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( authorizeOpinion == null || authorizeOpinion.isEmpty() ){
				check = false;
				Exception exception = new AuthorizeOpinionEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( undertakerIdentity == null || undertakerIdentity.isEmpty() ){
				check = false;
				Exception exception = new AuthorizeTakerIdentityEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {//2、工作信息是否已经存在
				okrWorkBaseInfo = okrWorkBaseInfoService.get( workId );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new WorkNotExistsException( workId );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkQueryByIdException( e, workId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			OkrWorkAuthorizeService okrWorkAuthorizeService = new OkrWorkAuthorizeService();
			try {
				okrWorkAuthorizeService.authorize( okrWorkBaseInfo, authorizeIdentity, undertakerIdentity, authorizeOpinion );
				okrWorkDynamicsService.workDynamic(
						okrWorkBaseInfo.getCenterId(), 
						okrWorkBaseInfo.getId(),
						okrWorkBaseInfo.getTitle(),
						"工作授权", 
						effectivePerson.getName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"授权工作：" + okrWorkBaseInfo.getTitle(), 
						"工作授权成功"
				);
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkAuthorizeProcessException( e, workId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return result;
	}
	
}