package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.service.OkrAttachmentFileInfoService;
import com.x.okr.assemble.control.service.OkrCenterWorkInfoService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportBaseInfoService;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;


@Path( "okrattachmentfileinfo" )
public class OkrAttachmentFileInfoAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrAttachmentFileInfoAction.class );
	private BeanCopyTools<OkrAttachmentFileInfo, WrapOutOkrAttachmentFileInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrAttachmentFileInfo.class, WrapOutOkrAttachmentFileInfo.class, null, WrapOutOkrAttachmentFileInfo.Excludes);
	private OkrAttachmentFileInfoService okrAttachmentFileInfoService = new OkrAttachmentFileInfoService();
	private OkrCenterWorkInfoService okrCenterWorkInfoService = new OkrCenterWorkInfoService();
	private OkrWorkBaseInfoService okrWorkBaseInfoService = new OkrWorkBaseInfoService();
	private OkrWorkReportBaseInfoService okrWorkReportBaseInfoService = new OkrWorkReportBaseInfoService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();

	@HttpMethodDescribe(value = "根据中心工作ID获取OkrAttachmentFileInfo列表.", response = WrapOutOkrAttachmentFileInfo.class)
	@GET
	@Path( "list/center/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByCenterId(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<List<WrapOutOkrAttachmentFileInfo>> result = new ActionResult<List<WrapOutOkrAttachmentFileInfo>>();
		List<WrapOutOkrAttachmentFileInfo> wrapOutOkrAttachmentFileInfoList = null;
		List<OkrAttachmentFileInfo> fileInfoList = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {	
			okrCenterWorkInfo = okrCenterWorkInfoService.get( id );
			if( okrCenterWorkInfo != null ){
				if( okrCenterWorkInfo.getAttachmentList() != null && okrCenterWorkInfo.getAttachmentList().size() > 0 ){
					fileInfoList = okrAttachmentFileInfoService.list( okrCenterWorkInfo.getAttachmentList() );
				}else{
					fileInfoList = new ArrayList<OkrAttachmentFileInfo>();
				}
				wrapOutOkrAttachmentFileInfoList = wrapout_copier.copy( fileInfoList );
			}else{
				logger.error( "okrCenterWorkInfo {'id':'"+id+"'} is not exsits. " );
			}
		} catch (Throwable th) {
			logger.error( "system get okrCenterWorkInfo by id got an exception" );
			th.printStackTrace();
			result.error(th);
		}
		if( wrapOutOkrAttachmentFileInfoList == null ){
			wrapOutOkrAttachmentFileInfoList = new ArrayList<WrapOutOkrAttachmentFileInfo>();
		}
		result.setData( wrapOutOkrAttachmentFileInfoList );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据工作ID获取OkrAttachmentFileInfo列表.", response = WrapOutOkrAttachmentFileInfo.class)
	@GET
	@Path( "list/work/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByWorkId(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<List<WrapOutOkrAttachmentFileInfo>> result = new ActionResult<List<WrapOutOkrAttachmentFileInfo>>();
		List<WrapOutOkrAttachmentFileInfo> wrapOutOkrAttachmentFileInfoList = null;
		List<OkrAttachmentFileInfo> fileInfoList = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {	
			okrWorkBaseInfo = okrWorkBaseInfoService.get( id );
			if( okrWorkBaseInfo != null ){
				if( okrWorkBaseInfo.getAttachmentList() != null && okrWorkBaseInfo.getAttachmentList().size() > 0 ){
					fileInfoList = okrAttachmentFileInfoService.list( okrWorkBaseInfo.getAttachmentList() );
				}else{
					fileInfoList = new ArrayList<OkrAttachmentFileInfo>();
				}
				wrapOutOkrAttachmentFileInfoList = wrapout_copier.copy( fileInfoList );
			}else{
				logger.error( "okrWorkBaseInfo {'id':'"+id+"'} is not exsits. " );
			}
		} catch (Throwable th) {
			logger.error( "system get okrWorkBaseInfo by id got an exception" );
			th.printStackTrace();
			result.error(th);
		}
		if( wrapOutOkrAttachmentFileInfoList == null ){
			wrapOutOkrAttachmentFileInfoList = new ArrayList<WrapOutOkrAttachmentFileInfo>();
		}
		result.setData( wrapOutOkrAttachmentFileInfoList );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据工作ID获取OkrAttachmentFileInfo列表.", response = WrapOutOkrAttachmentFileInfo.class)
	@GET
	@Path( "list/report/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByWorkReportId(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<List<WrapOutOkrAttachmentFileInfo>> result = new ActionResult<List<WrapOutOkrAttachmentFileInfo>>();
		List<WrapOutOkrAttachmentFileInfo> wrapOutOkrAttachmentFileInfoList = null;
		List<OkrAttachmentFileInfo> fileInfoList = null;
		OkrWorkReportBaseInfo workReportBaseInfo = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			workReportBaseInfo = okrWorkReportBaseInfoService.get( id );
			if( workReportBaseInfo != null ){
				if( workReportBaseInfo.getAttachmentList() != null && workReportBaseInfo.getAttachmentList().size() > 0 ){
					fileInfoList = okrAttachmentFileInfoService.list( workReportBaseInfo.getAttachmentList() );
				}else{
					fileInfoList = new ArrayList<OkrAttachmentFileInfo>();
				}
				wrapOutOkrAttachmentFileInfoList = wrapout_copier.copy( fileInfoList );
			}else{
				logger.error( "workReportBaseInfo {'id':'"+id+"'} is not exsits. " );
			}
		} catch (Throwable th) {
			logger.error( "system get workReportBaseInfo by id got an exception" );
			th.printStackTrace();
			result.error(th);
		}
		if( wrapOutOkrAttachmentFileInfoList == null ){
			wrapOutOkrAttachmentFileInfoList = new ArrayList<WrapOutOkrAttachmentFileInfo>();
		}
		result.setData( wrapOutOkrAttachmentFileInfoList );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除中心工作的OkrAttachmentFileInfo数据对象.", response = WrapOutOkrAttachmentFileInfo.class)
	@DELETE
	@Path( "center/attachment/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteCenterAttachment(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrAttachmentFileInfo> result = new ActionResult<>();
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		StorageMapping mapping = null;
		boolean hasDeletePermission = false;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( id == null || id.isEmpty() ){
			check = false;
			result.error( new Exception( "附件ID为空，无法进行删除操作。" ) );
			result.setUserMessage( "附件ID为空，无法进行删除操作。" );
			logger.error( "id is null, system can not delete any object." );
		}
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrAttachmentFileInfo = emc.find( id, OkrAttachmentFileInfo.class );
				if (null == okrAttachmentFileInfo) {
					check = false;
					result.error( new Exception( "附件信息不存在，无法进行删除操作。" ) );
					result.setUserMessage( "附件信息不存在，无法进行删除操作。" );
					logger.error( "okrAttachmentFileInfo{id:" + id + "} is not exists." );
				}
			}catch(Exception e){
				check = false;
				result.setUserMessage( "系统根据ID获取附件信息时发生异常。" );
				result.error( e );
				logger.error( "system get okrAttachmentFileInfo{id:" + id + "} from database got an exception.", e );
			}
		}		
		if( check ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrCenterWorkInfo = emc.find( okrAttachmentFileInfo.getKey(), OkrCenterWorkInfo.class );
				if ( null == okrCenterWorkInfo ) {
					hasDeletePermission = true;
					logger.warn( "okrCenterWorkInfo{id:" + okrAttachmentFileInfo.getKey() + "} is not exists, anyone can delete the attachments." );
				}else{
					//根据工作信息查询工作信息的干系人信息，判断是否有权限删除附件信息。
					//判断是否有权限删除附件
					if( !okrCenterWorkInfo.getDeployerName().equalsIgnoreCase( okrUserCache.getLoginUserName())){
						hasDeletePermission = false;
					}else{
						hasDeletePermission = true;
					}
				}
			}catch(Exception e){
				check = false;
				result.error( new Exception( "系统根据ID获取中心工作信息时发生异常。" ) );
				result.setUserMessage( "系统根据ID获取中心工作信息时发生异常。" );
				result.error( e );
				logger.error( "system get okrCenterWorkInfo{id:" + okrAttachmentFileInfo.getKey() + "} from database got an exception.", e );
			}
		}		
		if( check ){
			if( hasDeletePermission ){
				if( okrAttachmentFileInfo != null ){
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						mapping = ThisApplication.storageMappings.get(StorageType.okr, okrAttachmentFileInfo.getStorage());
						okrAttachmentFileInfo.deleteContent( mapping );
						okrAttachmentFileInfo = emc.find( id, OkrAttachmentFileInfo.class );
						okrCenterWorkInfo = emc.find( okrAttachmentFileInfo.getKey(), OkrCenterWorkInfo.class );
						emc.beginTransaction( OkrAttachmentFileInfo.class );
						emc.beginTransaction( OkrCenterWorkInfo.class);
						if( okrCenterWorkInfo != null && okrCenterWorkInfo.getAttachmentList() != null ){
							okrCenterWorkInfo.getAttachmentList().remove( okrAttachmentFileInfo.getId() );
							emc.check( okrCenterWorkInfo, CheckPersistType.all );
						}
						emc.remove( okrAttachmentFileInfo, CheckRemoveType.all );
						emc.commit();
						result.setUserMessage( "附件信息已经成功删除。" );
					}catch(Exception e){
						check = false;
						result.setUserMessage( "系统根据ID获取中心工作信息时发生异常。" );
						result.error( e );
						logger.error( "system get attachment{id:" + okrAttachmentFileInfo.getId() + "} from database got an exception.", e );
					}
				}
			}else{
				result.error( new Exception( "用户没有附件信息的删除权限。" ) );
				result.setUserMessage( "用户没有附件信息的删除权限。" );
				logger.error( "Unauthorized operation, no permission." );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除OkrAttachmentFileInfo数据对象.", response = WrapOutOkrAttachmentFileInfo.class)
	@DELETE
	@Path( "work/attachment/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWorkAttachment(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrAttachmentFileInfo> result = new ActionResult<>();
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		StorageMapping mapping = null;
		boolean hasDeletePermission = false;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( id == null || id.isEmpty() ){
			check = false;
			result.error( new Exception( "附件ID为空，无法进行删除操作。" ) );
			result.setUserMessage( "附件ID为空，无法进行删除操作。" );
			logger.error( "id is null, system can not delete any object." );
		}
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrAttachmentFileInfo = emc.find( id, OkrAttachmentFileInfo.class );
				if (null == okrAttachmentFileInfo) {
					check = false;
					result.error( new Exception( "附件信息不存在，无法进行删除操作。" ) );
					result.setUserMessage( "附件信息不存在，无法进行删除操作。" );
					logger.error( "okrAttachmentFileInfo{id:" + id + "} is not exists." );
				}
			}catch(Exception e){
				check = false;
				result.setUserMessage( "系统根据ID获取附件信息时发生异常。" );
				result.error( e );
				logger.error( "system get okrAttachmentFileInfo{id:" + id + "} from database got an exception.", e );
			}
		}		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkBaseInfo = emc.find( okrAttachmentFileInfo.getKey(), OkrWorkBaseInfo.class );
				if ( null == okrWorkBaseInfo ) {
					hasDeletePermission = true;
					logger.warn( "okrWorkBaseInfo{id:" + okrAttachmentFileInfo.getKey() + "} is not exists, anyone can delete the attachments." );
				}else{
					//根据工作信息查询工作信息的干系人信息，判断是否有权限删除附件信息。
					//判断是否有权限删除附件
					if( !okrWorkBaseInfo.getDeployerName().equalsIgnoreCase( okrUserCache.getLoginUserName())){
						hasDeletePermission = false;
					}else{
						hasDeletePermission = true;
					}
				}
			}catch(Exception e){
				check = false;
				result.error( new Exception( "系统根据ID获取工作基础信息时发生异常。" ) );
				result.setUserMessage( "系统根据ID获取工作基础信息时发生异常。" );
				result.error( e );
				logger.error( "system get okrWorkBaseInfo{id:" + okrAttachmentFileInfo.getKey() + "} from database got an exception.", e );
			}
		}		
		if( check ){
			if( hasDeletePermission ){
				if( okrAttachmentFileInfo != null ){
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						mapping = ThisApplication.storageMappings.get(StorageType.okr, okrAttachmentFileInfo.getStorage());
						//对文件进行删除
						okrAttachmentFileInfo.deleteContent( mapping );
						//对数据库记录进行删除
						okrAttachmentFileInfo = emc.find( id, OkrAttachmentFileInfo.class );
						okrWorkBaseInfo = emc.find( okrAttachmentFileInfo.getWorkInfoId(), OkrWorkBaseInfo.class );
						emc.beginTransaction( OkrAttachmentFileInfo.class );
						emc.beginTransaction( OkrWorkBaseInfo.class);
						if( okrWorkBaseInfo != null && okrWorkBaseInfo.getAttachmentList() != null ){
							okrWorkBaseInfo.getAttachmentList().remove( okrAttachmentFileInfo.getId() );
							emc.check( okrWorkBaseInfo, CheckPersistType.all );
						}
						emc.remove( okrAttachmentFileInfo, CheckRemoveType.all );
						emc.commit();
						result.setUserMessage( "附件信息已经成功删除。" );
					}catch(Exception e){
						check = false;
						result.setUserMessage( "系统根据ID获取工作基础信息时发生异常。" );
						result.error( e );
						logger.error( "system get attachment{id:" + okrAttachmentFileInfo.getId() + "} from database got an exception.", e );
					}
				}
			}else{
				result.error( new Exception( "用户没有附件信息的删除权限。" ) );
				result.setUserMessage( "用户没有附件信息的删除权限。" );
				logger.error( "Unauthorized operation, no permission." );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除OkrAttachmentFileInfo数据对象.", response = WrapOutOkrAttachmentFileInfo.class)
	@DELETE
	@Path( "report/attachment/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteReportAttachment(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrAttachmentFileInfo> result = new ActionResult<>();
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		OkrWorkReportBaseInfo workReportBaseInfo = null;
		StorageMapping mapping = null;
		boolean hasDeletePermission = false;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( id == null || id.isEmpty() ){
			check = false;
			result.error( new Exception( "附件ID为空，无法进行删除操作。" ) );
			result.setUserMessage( "附件ID为空，无法进行删除操作。" );
			logger.error( "id is null, system can not delete any object." );
		}
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrAttachmentFileInfo = emc.find( id, OkrAttachmentFileInfo.class );
				if (null == okrAttachmentFileInfo) {
					check = false;
					result.error( new Exception( "附件信息不存在，无法进行删除操作。" ) );
					result.setUserMessage( "附件信息不存在，无法进行删除操作。" );
					logger.error( "okrAttachmentFileInfo{id:" + id + "} is not exists." );
				}
			}catch(Exception e){
				check = false;
				result.setUserMessage( "系统根据ID获取附件信息时发生异常。" );
				result.error( e );
				logger.error( "system get okrAttachmentFileInfo{id:" + id + "} from database got an exception.", e );
			}
		}		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				workReportBaseInfo = emc.find( okrAttachmentFileInfo.getKey(), OkrWorkReportBaseInfo.class );
				if ( null == workReportBaseInfo ) {
					hasDeletePermission = true;
					logger.warn( "workReportBaseInfo{id:" + okrAttachmentFileInfo.getKey() + "} is not exists, anyone can delete the attachments." );
				}else{
					//根据工作信息查询工作信息的干系人信息，判断是否有权限删除附件信息。判断是否有权限删除附件
					if( !workReportBaseInfo.getCreatorName().equalsIgnoreCase( okrUserCache.getLoginUserName())){
						hasDeletePermission = false;
					}else{
						hasDeletePermission = true;
					}
				}
			}catch(Exception e){
				check = false;
				result.error( new Exception( "系统根据ID获取工作基础信息时发生异常。" ) );
				result.setUserMessage( "系统根据ID获取工作基础信息时发生异常。" );
				result.error( e );
				logger.error( "system get okrWorkBaseInfo{id:" + okrAttachmentFileInfo.getKey() + "} from database got an exception.", e );
			}
		}		
		if( check ){
			if( hasDeletePermission ){
				if( okrAttachmentFileInfo != null ){
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						mapping = ThisApplication.storageMappings.get(StorageType.okr, okrAttachmentFileInfo.getStorage());
						//对文件进行删除
						okrAttachmentFileInfo.deleteContent( mapping );
						//对数据库记录进行删除
						okrAttachmentFileInfo = emc.find( id, OkrAttachmentFileInfo.class );
						workReportBaseInfo = emc.find( okrAttachmentFileInfo.getKey(), OkrWorkReportBaseInfo.class );
						emc.beginTransaction( OkrAttachmentFileInfo.class );
						emc.beginTransaction( OkrWorkBaseInfo.class);
						if( workReportBaseInfo != null && workReportBaseInfo.getAttachmentList() != null ){
							workReportBaseInfo.getAttachmentList().remove( okrAttachmentFileInfo.getId() );
							emc.check( workReportBaseInfo, CheckPersistType.all );
						}
						emc.remove( okrAttachmentFileInfo, CheckRemoveType.all );
						emc.commit();
						result.setUserMessage( "附件信息已经成功删除。" );
					}catch(Exception e){
						check = false;
						result.setUserMessage( "系统根据ID获取工作基础信息时发生异常。" );
						result.error( e );
						logger.error( "system get attachment{id:" + okrAttachmentFileInfo.getId() + "} from database got an exception.", e );
					}
				}
			}else{
				result.error( new Exception( "用户没有附件信息的删除权限。" ) );
				result.setUserMessage( "用户没有附件信息的删除权限。" );
				logger.error( "Unauthorized operation, no permission." );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrAttachmentFileInfo对象.", response = WrapOutOkrAttachmentFileInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrAttachmentFileInfo> result = new ActionResult<>();
		WrapOutOkrAttachmentFileInfo wrap = null;
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrAttachmentFileInfo = okrAttachmentFileInfoService.get( id );
			if( okrAttachmentFileInfo != null ){
				wrap = wrapout_copier.copy( okrAttachmentFileInfo );
				result.setData(wrap);
			}else{
				logger.error( "system can not get any object by {'id':'"+id+"'}. " );
			}
		} catch (Throwable th) {
			logger.error( "system get by id got an exception" );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}	
}
