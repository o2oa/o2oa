package com.x.bbs.assemble.control.jaxrs.login;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.bbs.assemble.control.jaxrs.foruminfo.WrapOutForumInfoForIndex;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.WrapOutSectionInfoForIndex;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSSectionInfo;


@Path( "mobile" )
public class MobileIndexAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( MobileIndexAction.class );
	private BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	private BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	private BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	private UserManagerService userManagerService = new UserManagerService();
	
	private BeanCopyTools< BBSForumInfo, WrapOutForumInfoForIndex > forum_wrapout_copier = BeanCopyToolsBuilder.create( BBSForumInfo.class, WrapOutForumInfoForIndex.class, null, WrapOutForumInfoForIndex.Excludes);
	private BeanCopyTools< BBSSectionInfo, WrapOutSectionInfoForIndex > section_wrapout_copier = BeanCopyToolsBuilder.create( BBSSectionInfo.class, WrapOutSectionInfoForIndex.class, null, WrapOutSectionInfoForIndex.Excludes);
	
	/**
	 * 手机用户访问论坛信息，首页所有的信息整合在一起
	 * 匿名用户可以访问
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "获取登录者可以访问到的所有ForumInfo的信息列表.", response = WrapOutForumInfoForIndex.class)
	@GET
	@Path("view/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response viewAllWithMyPermission( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutForumInfoForIndex>> result = new ActionResult<>();
		List<WrapOutForumInfoForIndex> wraps = new ArrayList<>();
		List<BBSPermissionInfo> permissionList = null;
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		if( check ){
			try {
				permissionList = getPermissionListByUser( currentPerson );
			} catch (Exception e) {
				permissionList = null;
			}
		}
		
		//根据登录的用户查询用户可以访问到的所有论坛信息列表
		if( check ){
			wraps = getForumInfoListByPerson( currentPerson, permissionList );
		}
		
		if( check ){
			if( wraps != null && !wraps.isEmpty() ){//在每个论坛里查询用户可以访问到的所有版块信息
				for( WrapOutForumInfoForIndex wrapOutForumInfoForIndex : wraps ){
					composeMainSectionForForumInIndex( wrapOutForumInfoForIndex, permissionList );
				}
			}
		}
		
		result.setData( wraps );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	private WrapOutForumInfoForIndex composeMainSectionForForumInIndex( WrapOutForumInfoForIndex wrapOutForumInfoForIndex, List<BBSPermissionInfo> permissionList ) {
		if( wrapOutForumInfoForIndex == null ){
			return null;
		}
		List<String> sectionIds = new ArrayList<String>();
		List<BBSSectionInfo> sectionInfoList = null;
		List<WrapOutSectionInfoForIndex> wrapSectionInfoList = null;
		List<BBSPermissionInfo> sectionViewPermissionList = null;
		
		try {
			sectionViewPermissionList = permissionInfoService.filterPermissionListByPermissionFunction( "SECTION_VIEW", permissionList );
		} catch (Exception e) {
			logger.warn( "system filter SECTION_VIEW permission from user permission list got an exception!" );
			logger.error(e);
			sectionViewPermissionList = null;
		}
		
		if( sectionViewPermissionList != null && !sectionViewPermissionList.isEmpty() ){
			for( BBSPermissionInfo permission : sectionViewPermissionList ){
				if( permission.getMainSectionId() != null && !sectionIds.contains( permission.getMainSectionId() )){
					sectionIds.add( permission.getMainSectionId() );
				}
			}
		}
		try {
			sectionInfoList = sectionInfoServiceAdv.viewMainSectionByForumId( wrapOutForumInfoForIndex.getId(), sectionIds );
		} catch (Exception e) {
			logger.warn( "system query all mainSection info got an exception!" );
			logger.error(e);
		}
		if( sectionInfoList != null && !sectionInfoList.isEmpty() ){
			try {
				wrapSectionInfoList = section_wrapout_copier.copy( sectionInfoList );
				wrapOutForumInfoForIndex.setSectionInfoList( wrapSectionInfoList );
			} catch (Exception e) {
				logger.warn( "system copy forum list to wraps got an exception!" );
				logger.error(e);
			}
		}
		return wrapOutForumInfoForIndex;
	}

	/**
	 * 根据登录者权限获取可以访问到的所有论坛信息列表
	 * @param currentPerson
	 * @param permissionList 
	 * @return
	 */
	private List<WrapOutForumInfoForIndex> getForumInfoListByPerson( EffectivePerson currentPerson, List<BBSPermissionInfo> permissionList ) {
		if( currentPerson == null ){
			return null;
		}
		List<String> forumIds = new ArrayList<String>();
		List<BBSForumInfo> forumInfoList = null;
		List<BBSPermissionInfo> forumViewPermissionList = null;	
		List<WrapOutForumInfoForIndex> wraps = new ArrayList<>();
		
		try {
			forumViewPermissionList = permissionInfoService.filterPermissionListByPermissionFunction( "FORUM_VIEW", permissionList );
		} catch (Exception e) {
			logger.warn( "system filter FORUM_VIEW permission from user permission list got an exception!");
			logger.error(e);
			forumViewPermissionList = null;
		}
		
		if( forumViewPermissionList != null && !forumViewPermissionList.isEmpty() ){
			for( BBSPermissionInfo permission : forumViewPermissionList ){
				forumIds.add( permission.getForumId() );
			}
		}
		
		try {
			forumInfoList = forumInfoServiceAdv.listAllViewAbleForumWithUserPermission( forumIds );
			if( forumInfoList == null ){
				forumInfoList = new ArrayList<BBSForumInfo>();
			}
		} catch (Exception e) {
			logger.warn( "system query all forum info got an exception!" );
			logger.error(e);
			return null;
		}
		
		if( forumInfoList != null && !forumInfoList.isEmpty() ){
			try {
				wraps = forum_wrapout_copier.copy( forumInfoList );
			} catch (Exception e) {
				logger.warn( "system copy forum list to wraps got an exception!" );
				logger.error(e);
				return null;
			}
			
		}
		return wraps;
	}

	/**
	 * 根据人员信息查询该用户拥有的所有权限列表
	 * @param currentPerson
	 * @return
	 */
	private List<BBSPermissionInfo> getPermissionListByUser(EffectivePerson currentPerson) {
		List<BBSPermissionInfo> permissionList = null;
		//如果不是匿名用户，则查询该用户所有能访问的论坛信息
		if( currentPerson !=null && !"anonymous".equalsIgnoreCase( currentPerson.getTokenType().name() )){
			try {
				permissionList = userManagerService.getUserPermissionInfoList( currentPerson.getName() );
			} catch (Exception e) {
				logger.warn( "system get all user permission list from ThisApplication.userPermissionInfoMap got an exception!" );
				logger.error(e);
				permissionList = null;
			}
		}
		return permissionList;
	}
}
