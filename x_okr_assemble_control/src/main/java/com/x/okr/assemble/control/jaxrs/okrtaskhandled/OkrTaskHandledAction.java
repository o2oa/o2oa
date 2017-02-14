package com.x.okr.assemble.control.jaxrs.okrtaskhandled;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import com.x.okr.assemble.control.service.OkrTaskHandledService;
import com.x.okr.entity.OkrTaskHandled;


@Path( "okrtaskhandled" )
public class OkrTaskHandledAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrTaskHandledAction.class );
	private BeanCopyTools<OkrTaskHandled, WrapOutOkrTaskHandled> wrapout_copier = BeanCopyToolsBuilder.create( OkrTaskHandled.class, WrapOutOkrTaskHandled.class, null, WrapOutOkrTaskHandled.Excludes);
	private OkrTaskHandledService okrTaskHandledService = new OkrTaskHandledService();

	@HttpMethodDescribe(value = "新建或者更新OkrTaskHandled对象.", request = WrapInOkrTaskHandled.class, response = WrapOutOkrTaskHandled.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrTaskHandled wrapIn) {
		ActionResult<WrapOutOkrTaskHandled> result = new ActionResult<>();
		OkrTaskHandled okrTaskHandled = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to save OkrTaskHandled......" );
		if( wrapIn != null ){
			try {
				okrTaskHandled = okrTaskHandledService.save( wrapIn );
				if( okrTaskHandled != null ){
					result.setUserMessage( okrTaskHandled.getId() );
				}else{
					result.error( new Exception( "系统在保存信息时发生异常!" ) );
					result.setUserMessage( "系统在保存信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存信息时发生异常!" );
				logger.error( "OkrTaskHandledService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrTaskHandled对象.", response = WrapOutOkrTaskHandled.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrTaskHandled> result = new ActionResult<>();
		WrapOutOkrTaskHandled wrap = null;
		OkrTaskHandled okrTaskHandled = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to get okrTaskHandled{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrTaskHandled = okrTaskHandledService.get( id );
			if( okrTaskHandled != null ){
				wrap = wrapout_copier.copy( okrTaskHandled );
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
