package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.MethodExcuteResult;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;

@Path("section")
public class SectionInfoAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( SectionInfoAction.class );
	private BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	private BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	private UserManagerService userManagerService = new UserManagerService();
	private BeanCopyTools< BBSSectionInfo, WrapOutSectionInfo > wrapout_copier = BeanCopyToolsBuilder.create( BBSSectionInfo.class, WrapOutSectionInfo.class, null, WrapOutSectionInfo.Excludes);

	@HttpMethodDescribe(value = "根据论坛ID获取所有版块的信息列表.", response = WrapOutSectionInfo.class)
	@GET
	@Path("viewforum/{forumId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response viewWithForum( @Context HttpServletRequest request, @PathParam("forumId") String forumId ) {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		List<WrapOutSectionInfo> wraps = new ArrayList<>();
		List<BBSSectionInfo> sectionInfoList = null;
		List<String> viewableSectionIds = new ArrayList<String>();
		BBSForumInfo forumInfo = new BBSForumInfo();
		Boolean check = true;
		MethodExcuteResult methodExcuteResult = null;
		EffectivePerson currentPerson = null;		
		if( check ){
			try {
				currentPerson = this.effectivePerson(request);
			} catch (Exception e) {
				currentPerson = null;
			}
		}
		if( check ){
			if( forumId == null || forumId.isEmpty() ){
				check = false;
				Exception exception = new ForumIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){ //查询论坛信息是否存在
			try{
				forumInfo = forumInfoServiceAdv.get( forumId );
			}catch( Exception e ){
				check = false;
				Exception exception = new ForumInfoQueryByIdException( e, forumId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( forumInfo == null ){
				check = false;
				Exception exception = new ForumInfoNotExistsException( forumId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		//如果不是匿名用户，则查询该用户所有能访问的版块信息
		if( check ){
			methodExcuteResult = userManagerService.getViewSectionIdsFromUserPermission( currentPerson );
			if( methodExcuteResult.getSuccess() ){
				if( methodExcuteResult.getBackObject() != null ){
					viewableSectionIds = (List<String>)methodExcuteResult.getBackObject();
				}else{
					viewableSectionIds = new ArrayList<String>();
				}
			}else{
				result.error( methodExcuteResult.getError() );
				logger.warn( methodExcuteResult.getMessage() );
			}
		}
		if( check ){//从数据库查询主版块列表
			try {
				sectionInfoList = sectionInfoServiceAdv.viewMainSectionByForumId( forumId, viewableSectionIds );
				if (sectionInfoList == null) {
					sectionInfoList = new ArrayList<BBSSectionInfo>();
				}
			} catch (Exception e) {
				result.error(e);
				Exception exception = new SectionListByForumException( e, forumId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( sectionInfoList != null && sectionInfoList.size() > 0 ){
				try {
					wraps = wrapout_copier.copy( sectionInfoList );
					result.setData(wraps);
				} catch (Exception e) {
					Exception exception = new SectionWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}		
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "根据主版块ID查询所有的子版块信息列表.", response = WrapOutSectionInfo.class)
	@GET
	@Path("viewsub/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubSectionByMainSectionId( @Context HttpServletRequest request, @PathParam("sectionId") String sectionId ) {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		List<WrapOutSectionInfo> wraps = new ArrayList<>();
		List<BBSSectionInfo> sectionInfoList = null;
		List<String> viewableSectionIds = new ArrayList<String>();
		BBSSectionInfo sectionInfo = new BBSSectionInfo();
		Boolean check = true;
		MethodExcuteResult methodExcuteResult = null;
		EffectivePerson currentPerson = null;		
		if( check ){
			try {
				currentPerson = this.effectivePerson(request);
			} catch (Exception e) {
				currentPerson = null;
			}
		}	
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){
			try{
				sectionInfo = sectionInfoServiceAdv.get( sectionId );
			}catch( Exception e ){
				check = false;
				Exception exception = new SectionQueryByIdException( e, sectionId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){
			if( sectionInfo == null ){
				check = false;
				Exception exception = new SectionNotExistsException( sectionId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		//如果不是匿名用户，则查询该用户所有能访问的版块信息
		if (check) {
			methodExcuteResult = userManagerService.getViewSectionIdsFromUserPermission( currentPerson );
			if (methodExcuteResult.getSuccess()) {
				if (methodExcuteResult.getBackObject() != null) {
					viewableSectionIds = (List<String>) methodExcuteResult.getBackObject();
				} else {
					viewableSectionIds = new ArrayList<String>();
				}
			} else {
				result.error(methodExcuteResult.getError());
				logger.warn(methodExcuteResult.getMessage());
			}
		}
		if( check ){
			try {
				sectionInfoList = sectionInfoServiceAdv.viewSubSectionByMainSectionId( sectionId, viewableSectionIds );
			} catch (Exception e) {
				result.error(e);
				Exception exception = new SectionListByParentException( e, sectionId );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){
			if( sectionInfoList != null && sectionInfoList.size() > 0 ){
				try {
					wraps = wrapout_copier.copy( sectionInfoList );
					result.setData(wraps);
				} catch (Exception e) {
					Exception exception = new SectionWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}		
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "根据指定ID获取版块信息.", response = WrapOutSectionInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSectionInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapOutSectionInfo wrap = null;
		BBSSectionInfo sectionInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				sectionInfo = sectionInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new SectionQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( sectionInfo != null ){
				try {
					wrap = wrapout_copier.copy( sectionInfo );
					result.setData( wrap );
				} catch (Exception e) {
					check = false;
					Exception exception = new SectionWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}else{
				Exception exception = new SectionNotExistsException( id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}