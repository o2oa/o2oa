package com.x.bbs.assemble.control.jaxrs.foruminfo;

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
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSForumInfo;



@Path("forum")
public class ForumInfoAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( ForumInfoAction.class );
	private BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	private UserManagerService userManagerService = new UserManagerService();
	private BeanCopyTools< BBSForumInfo, WrapOutForumInfo > wrapout_copier = BeanCopyToolsBuilder.create( BBSForumInfo.class, WrapOutForumInfo.class, null, WrapOutForumInfo.Excludes);
	/**
	 * 访问论坛信息，匿名用户可以访问
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "获取登录者可以访问到的所有ForumInfo的信息列表.", response = WrapOutForumInfo.class)
	@GET
	@Path("view/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response viewAllWithMyPermission( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutForumInfo>> result = new ActionResult<>();
		List<WrapOutForumInfo> wraps = new ArrayList<>();
		List<BBSForumInfo> forumInfoList = null;
		List<String> ids = null;
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
			methodExcuteResult = userManagerService.getViewForumIdsFromUserPermission( currentPerson );
			if( methodExcuteResult.getSuccess() ){
				if( methodExcuteResult.getBackObject() != null ){
					ids = (List<String>)methodExcuteResult.getBackObject();
				}else{
					ids = new ArrayList<String>();
				}
			}else{
				result.error( methodExcuteResult.getError() );
				logger.error( methodExcuteResult.getError(), currentPerson, request, null);
			}
		}
		if( check ){//从数据库查询论坛列表
			try {
				forumInfoList = forumInfoServiceAdv.listAllViewAbleForumWithUserPermission( ids );
				if( forumInfoList == null ){
					forumInfoList = new ArrayList<BBSForumInfo>();
				}
			} catch (Exception e) {
				Exception exception = new ForumInfoListByPermissionException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){//转换论坛列表为输出格式
			if( forumInfoList != null && !forumInfoList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( forumInfoList );
				} catch (Exception e) {
					Exception exception = new ForumInfoWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
				result.setData( wraps );
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	

	@HttpMethodDescribe(value = "根据指定ID获取论坛信息.", response = WrapOutForumInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutForumInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapOutForumInfo wrap = null;
		BBSForumInfo forumInfo = null;
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ForumInfoIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		
		if( check ){
			try {
				forumInfo = forumInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ForumInfoQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		
		if( check ){
			if( forumInfo != null ){
				try {
					wrap = wrapout_copier.copy( forumInfo );
					result.setData( wrap );
				} catch (Exception e) {
					check = false;
					Exception exception = new ForumInfoWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}else{
				Exception exception = new ForumInfoNotExistsException( id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}