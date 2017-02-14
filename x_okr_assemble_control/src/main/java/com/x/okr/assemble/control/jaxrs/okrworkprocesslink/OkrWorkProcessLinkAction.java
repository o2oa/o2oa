package com.x.okr.assemble.control.jaxrs.okrworkprocesslink;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.service.OkrWorkProcessLinkService;
import com.x.okr.entity.OkrWorkProcessLink;


@Path( "okrworkprocesslink" )
public class OkrWorkProcessLinkAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkProcessLinkAction.class );
	private BeanCopyTools<OkrWorkProcessLink, WrapOutOkrWorkProcessLink> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkProcessLink.class, WrapOutOkrWorkProcessLink.class, null, WrapOutOkrWorkProcessLink.Excludes);
	private OkrWorkProcessLinkService okrWorkProcessLinkService = new OkrWorkProcessLinkService();

	@HttpMethodDescribe(value = "新建或者更新OkrWorkProcessLink对象.", request = WrapInOkrWorkProcessLink.class, response = WrapOutOkrWorkProcessLink.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrWorkProcessLink wrapIn) {
		ActionResult<WrapOutOkrWorkProcessLink> result = new ActionResult<>();
		OkrWorkProcessLink okrWorkProcessLink = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to save OkrWorkProcessLink......" );
		if( wrapIn != null ){
			try {
				okrWorkProcessLink = okrWorkProcessLinkService.save( wrapIn );
				if( okrWorkProcessLink != null ){
					result.setUserMessage( okrWorkProcessLink.getId() );
				}else{
					result.error( new Exception( "系统在保存信息时发生异常!" ) );
					result.setUserMessage( "系统在保存信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存信息时发生异常!" );
				logger.error( "OkrWorkProcessLinkService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkProcessLink数据对象.", response = WrapOutOkrWorkProcessLink.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkProcessLink> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to delete okrWorkProcessLink{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrWorkProcessLinkService.delete( id );
			result.setUserMessage( "成功删除工作处理过程数据信息。id=" + id );
		}catch(Exception e){
			logger.error( "system delete okrWorkProcessLinkService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除工作处理过程数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkProcessLink对象.", response = WrapOutOkrWorkProcessLink.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkProcessLink> result = new ActionResult<>();
		WrapOutOkrWorkProcessLink wrap = null;
		OkrWorkProcessLink okrWorkProcessLink = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to get okrWorkProcessLink{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrWorkProcessLink = okrWorkProcessLinkService.get( id );
			if( okrWorkProcessLink != null ){
				wrap = wrapout_copier.copy( okrWorkProcessLink );
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
