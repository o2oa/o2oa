package com.x.okr.assemble.control.jaxrs.okrworkproblempersonlink;
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
import com.x.okr.assemble.control.service.OkrWorkProblemPersonLinkService;
import com.x.okr.entity.OkrWorkProblemPersonLink;


@Path( "okrworkproblempersonlink" )
public class OkrWorkProblemPersonLinkAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkProblemPersonLinkAction.class );
	private BeanCopyTools<OkrWorkProblemPersonLink, WrapOutOkrWorkProblemPersonLink> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkProblemPersonLink.class, WrapOutOkrWorkProblemPersonLink.class, null, WrapOutOkrWorkProblemPersonLink.Excludes);
	private OkrWorkProblemPersonLinkService okrWorkProblemPersonLinkService = new OkrWorkProblemPersonLinkService();

	@HttpMethodDescribe(value = "新建或者更新OkrWorkProblemPersonLink对象.", request = WrapInOkrWorkProblemPersonLink.class, response = WrapOutOkrWorkProblemPersonLink.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrWorkProblemPersonLink wrapIn) {
		ActionResult<WrapOutOkrWorkProblemPersonLink> result = new ActionResult<>();
		OkrWorkProblemPersonLink okrWorkProblemPersonLink = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to save OkrWorkProblemPersonLink......" );
		if( wrapIn != null ){
			try {
				okrWorkProblemPersonLink = okrWorkProblemPersonLinkService.save( wrapIn );
				if( okrWorkProblemPersonLink != null ){
					result.setUserMessage( okrWorkProblemPersonLink.getId() );
				}else{
					result.error( new Exception( "系统在保存信息时发生异常!" ) );
					result.setUserMessage( "系统在保存信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存信息时发生异常!" );
				logger.error( "OkrWorkProblemPersonLinkService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkProblemPersonLink数据对象.", response = WrapOutOkrWorkProblemPersonLink.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkProblemPersonLink> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to delete okrWorkProblemPersonLink{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrWorkProblemPersonLinkService.delete( id );
			result.setUserMessage( "成功删除问题请示处理链数据信息。id=" + id );
		}catch(Exception e){
			logger.error( "system delete okrWorkProblemPersonLinkService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除问题请示处理链数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkProblemPersonLink对象.", response = WrapOutOkrWorkProblemPersonLink.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkProblemPersonLink> result = new ActionResult<>();
		WrapOutOkrWorkProblemPersonLink wrap = null;
		OkrWorkProblemPersonLink okrWorkProblemPersonLink = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to get okrWorkProblemPersonLink{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrWorkProblemPersonLink = okrWorkProblemPersonLinkService.get( id );
			if( okrWorkProblemPersonLink != null ){
				wrap = wrapout_copier.copy( okrWorkProblemPersonLink );
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
