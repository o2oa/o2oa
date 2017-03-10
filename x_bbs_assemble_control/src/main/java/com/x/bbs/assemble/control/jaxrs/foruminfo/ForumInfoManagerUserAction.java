package com.x.bbs.assemble.control.jaxrs.foruminfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSForumInfo;
import com.x.organization.core.express.wrap.WrapPerson;



@Path("user/forum")
public class ForumInfoManagerUserAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( ForumInfoManagerUserAction.class );
	private BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	private BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	private BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
	private BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	private UserManagerService userManagerService = new UserManagerService();
	private BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
	private BeanCopyTools< BBSForumInfo, WrapOutForumInfo > wrapout_copier = BeanCopyToolsBuilder.create( BBSForumInfo.class, WrapOutForumInfo.class, null, WrapOutForumInfo.Excludes);
	private BeanCopyTools<WrapInForumInfo, BBSForumInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInForumInfo.class, BBSForumInfo.class, null, WrapInForumInfo.Excludes );

	/**
	 * 访问论坛信息，登录用户访问
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "获取所有ForumInfo的信息列表.", response = WrapOutForumInfo.class)
	@GET
	@Path("all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutForumInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutForumInfo> wraps = new ArrayList<>();
		List<BBSForumInfo> forumInfoList = null;
		Boolean check = true;
		
		if( check ){
			//从数据库查询论坛列表
			try {
				forumInfoList = forumInfoServiceAdv.listAll();
			} catch (Exception e) {
				Exception exception = new ForumInfoListAllException(e);
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}	
		}
		if( check ){
			if( forumInfoList != null && !forumInfoList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( forumInfoList );
				} catch (Exception e) {
					Exception exception = new ForumInfoWrapOutException(e);
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
				result.setData( wraps );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	/**
	 * 保存论坛信息，登录用户访问
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "创建新的论坛信息或者更新论坛信息.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post( @Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInForumInfo wrapIn = null;
		WrapOutId wrap = null;
		Boolean check = true;
		String[] names = null;
		String[] typeCatagory = null;
		WrapPerson person = null;
		BBSForumInfo forumInfo_old = null;
		BBSForumInfo forumInfo = new BBSForumInfo();
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInForumInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		
		if( check ){
			try {
				if( !userManagerService.isHasRole( currentPerson.getName(), "BBSSystemAdmin") ){
					check = false;
					Exception exception = new InsufficientPermissionsException( currentPerson.getName(), "BBSSystemAdmin" );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} catch (Exception e1) {
				check = false;
				Exception exception = new InsufficientPermissionsException( currentPerson.getName(), "BBSSystemAdmin" );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		
		//校验论坛名称
		if( check ){
			if( wrapIn.getForumName() == null || wrapIn.getForumName().isEmpty() ){
				check = false;
				Exception exception = new ForumNameEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		//校验论坛分类:信息|问题|投票,只能是这三类中的
		if( check ){
			if( wrapIn.getTypeCatagory() == null || wrapIn.getTypeCatagory().isEmpty() ){
				check = false;
				Exception exception = new ForumTypeCatagoryEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			typeCatagory =  wrapIn.getTypeCatagory().split("\\|");
			if( typeCatagory != null && typeCatagory.length > 0 ){
				for( String catagory : typeCatagory ){
					if( !"信息".equals( catagory ) && !"问题".equals( catagory ) && !"投票".equals( catagory )){
						check = false;
						Exception exception = new ForumTypeCatagoryInvalidException( catagory );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
				}
			}
		}
		if( check ){
			if( wrapIn.getSubjectType() == null || wrapIn.getSubjectType().isEmpty() ){
				wrapIn.setSubjectType( "新闻|讨论" );
			}
		}
		if( check ){
			if( wrapIn.getForumManagerName() != null && !wrapIn.getForumManagerName().isEmpty() ){
				//判断指定的用户是否存在
				names = wrapIn.getForumManagerName().split( "," );
				for( String name : names ){
					try {
						person = userManagerService.getUserByFlag( name );
						if( person == null ){
							check = false;
							Exception exception = new PersonNotExistsException( name );
							result.error( exception );
							logger.error( exception, currentPerson, request, null);
							break;
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new PersonQueryException( e, name );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
						break;
					}
				}				
			}else{
				wrapIn.setForumManagerName( currentPerson.getName() );
			}
		}
		if( check ){
			wrapIn.setCreatorName( currentPerson.getName() );
		}
		if( check ){
			try {
				wrapin_copier.copy( wrapIn, forumInfo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ForumInfoWrapInException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( forumInfo.getId() != null && !forumInfo.getId().isEmpty() ){
				try {
					forumInfo_old = forumInfoServiceAdv.get( forumInfo.getId() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ForumInfoQueryByIdException( e, forumInfo.getId() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check ){
			try {
				forumInfo = forumInfoServiceAdv.save( forumInfo );
				wrap = new WrapOutId( forumInfo.getId() );
				result.setData( wrap );
				if( forumInfo_old != null ){
					operationRecordService.forumOperation( currentPerson.getName(), forumInfo, "MODIFY", hostIp, hostName );
				}else{
					operationRecordService.forumOperation( currentPerson.getName(), forumInfo, "CREATE", hostIp, hostName );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ForumInfoSaveException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				//论坛信息添加成功，继续添加权限和角色信息
				permissionInfoService.createForumPermission( forumInfo );
			} catch (Exception e) {
				check = false;
				logger.warn( "system create forum permission info got an exception!" );
				logger.error(e);
			}
		}
		if( check ){
			try {
				//论坛信息添加成功，继续添加权限和角色信息
				roleInfoService.createForumRole( forumInfo );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "system create forum role info got an exception!" );
				logger.error(e);
			}
		}
		if( check ){//检查论坛管理员权限的设置
			try {
				forumInfoServiceAdv.checkForumManager( forumInfo );
			} catch (Exception e) {
				logger.warn( "system bind role for forum manager got an exception!" );
				logger.error(e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 删除论坛信息，登录用户访问
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "根据ID删除指定的论坛信息，如果论坛里有版块或者贴子，则不允许删除.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		BBSForumInfo forumInfo = null;
		Long sectionCount = 0L;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		if( check ){
			try {
				if( !userManagerService.isHasRole( currentPerson.getName(), "BBSSystemAdmin") ){
					check = false;
					Exception exception = new InsufficientPermissionsException( currentPerson.getName(), "BBSSystemAdmin" );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} catch (Exception e1) {
				check = false;
				Exception exception = new InsufficientPermissionsException( currentPerson.getName(), "BBSSystemAdmin" );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ForumInfoIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			//查询论坛信息是否存在
			try{
				forumInfo = forumInfoServiceAdv.get(id);
			}catch( Exception e ){
				check = false;
				Exception exception = new ForumInfoQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( forumInfo == null ){
				check = false;
				Exception exception = new ForumInfoNotExistsException( id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			//查询论坛是否仍存在版块信息
			try{
				sectionCount = sectionInfoServiceAdv.countMainSectionByForumId( id );
			}catch( Exception e ){
				check = false;
				Exception exception = new CountSectionException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( sectionCount > 0 ){
				check = false;
				logger.warn( "论坛["+forumInfo.getForumName()+"]中仍存在"+ sectionCount+"个主版块，无法继续进行删除操作！ID=" + id  );
				Exception exception = new ForumCanNotDeleteException( "论坛["+forumInfo.getForumName()+"]中仍存在"+ sectionCount+"个版块，无法继续进行删除操作！" );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				forumInfoServiceAdv.delete( id );
				wrap = new WrapOutId( id );
				result.setData( wrap );
				operationRecordService.forumOperation( currentPerson.getName(), forumInfo, "DELETE", hostIp, hostName );
			} catch (Exception e) {
				check = false;
				Exception exception = new ForumInfoDeleteException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){//检查论坛管理员权限的设置
			try {
				forumInfoServiceAdv.deleteForumManager( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "system delete role for forum manager got an exception!" );
				logger.error(e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
}