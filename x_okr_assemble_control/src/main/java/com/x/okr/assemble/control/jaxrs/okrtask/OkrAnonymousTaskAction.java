package com.x.okr.assemble.control.jaxrs.okrtask;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.organization.core.express.wrap.WrapPerson;


@Path( "task" )
public class OkrAnonymousTaskAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrAnonymousTaskAction.class );
	private OkrTaskService okrTaskService = new OkrTaskService();
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	
	@HttpMethodDescribe(value = "查询指定用户的待办数量.", response = WrapOutOkrTaskCollect.class )
	@GET
	@Path( "count/{flag}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showTaskCollect(@Context HttpServletRequest request, @PathParam( "flag" ) String flag ) {
		ActionResult<WrapOutOkrTaskCollect> result = new ActionResult<>();
		List<String> taskTypeList = new ArrayList<String>();
		WrapPerson person = null;
		Long taskCount = 0L;
		boolean check = true;
		
		if( check ){
			try {
				person = okrUserManagerService.getUserByUserNumber( flag );
			} catch ( Exception e ) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据根据人员唯一标识查询人员信息时发生异常。" );
				logger.error( "system get person by user flag got an exception.", e );
			}
		}
		
		if( check ){
			if( person != null ){
				taskTypeList.add( "中心工作" );
				taskTypeList.add( "工作汇报汇总" );
				//taskTypeList.add( "工作汇报拟稿" );
				try{
					taskCount = okrTaskService.getTaskCountByUserName( taskTypeList, person.getName() );
				}catch(Exception e){
					check = false;
					result.error( e );
					result.setUserMessage( "系统在根据用户姓名获取待办总数时发生异常。" );
					logger.error( "system get task count by user name got an exception.", e );
				}
			}else{
				check = false;
				result.error( new Exception("person{'flag':'"+flag+"'} is not exists.") );
				result.setUserMessage( "用户帐号'"+ flag +"'不存在。" );
				logger.error( "person{'flag':'"+flag+"'} is not exists." );
			}
		}
		result.setCount( taskCount );
		result.setUserMessage( result + "" );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
