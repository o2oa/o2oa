package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.control.jaxrs.WrapOutMessage;
import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;


@Path("attendanceemployeeconfig")
public class AttendanceEmployeeConfigAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceEmployeeConfigAction.class );
	private AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
	private BeanCopyTools<WrapInAttendanceEmployeeConfig, AttendanceEmployeeConfig> wrapin_copier = BeanCopyToolsBuilder.create( WrapInAttendanceEmployeeConfig.class, AttendanceEmployeeConfig.class, null, WrapInAttendanceEmployeeConfig.Excludes );
	private BeanCopyTools<AttendanceEmployeeConfig, WrapOutAttendanceEmployeeConfig> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceEmployeeConfig.class, WrapOutAttendanceEmployeeConfig.class, null, WrapOutAttendanceEmployeeConfig.Excludes);
	
	@HttpMethodDescribe(value = "获取所有AttendanceEmployeeConfig列表", response = WrapOutAttendanceEmployeeConfig.class)
	@GET
	@Path("list/all")
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listAllAttendanceEmployeeConfig( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAttendanceEmployeeConfig>> result = new ActionResult<>();
		List<WrapOutAttendanceEmployeeConfig> wraps = null;
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigList = null;
		Boolean check = true;
		if( check ){
			try {
				attendanceEmployeeConfigList = attendanceEmployeeConfigServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统查询所有人员考勤配置时发生异常。");
				logger.error( "system query all employee config list got an exception.", e);
			}
		}
		if( check && attendanceEmployeeConfigList != null ){
			//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			try {
				wraps = wrapout_copier.copy( attendanceEmployeeConfigList );
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常。");
				logger.error( "system copy employee config list to wrap got an exception.", e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取AttendanceEmployeeConfig对象.", response = WrapOutAttendanceEmployeeConfig.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutAttendanceEmployeeConfig> result = new ActionResult<>();
		WrapOutAttendanceEmployeeConfig wrap = null;
		AttendanceEmployeeConfig attendanceEmployeeConfig = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("查询操作传入的参数ID为空，无法进行查询操作。") );
				result.setUserMessage("查询操作传入的参数ID为空，无法进行查询操作。。");
			}
		}		
		if( check ){
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.get( id );
				if( attendanceEmployeeConfig == null ){
					check = false;
					result.error(new Exception("指定的人员考勤数据不存在！"));
					result.setUserMessage("指定的人员考勤数据不存在！");
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据ID查询指定的人员考勤配置信息时发生异常。");
				logger.error( "system query employee config with id got an exception.id:" + id, e);
			}
		}
		if( check ){
			try {
				wrap = wrapout_copier.copy(attendanceEmployeeConfig);
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常。");
				logger.error( "system copy employee config to wrap got an exception.", e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceEmployeeConfig对象.", request = WrapInAttendanceEmployeeConfig.class, response = WrapOutMessage.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInAttendanceEmployeeConfig wrapIn ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrapOutId = null;
		AttendanceEmployeeConfig attendanceEmployeeConfig = new AttendanceEmployeeConfig();
		Boolean check = true;
		
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error(new Exception("请求传入的参数为空，无法继续保存人员考勤数据！"));
				result.setUserMessage("请求传入的参数为空，无法继续保存人员考勤数据！");
			}
		}
		if( check ){
			try {
				wrapin_copier.copy( wrapIn, attendanceEmployeeConfig );
				if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
					attendanceEmployeeConfig.setId( wrapIn.getId() );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage("系统将传入的参数COPY到对象里时发生异常！");
				logger.error( "system copy wrap in to attendanceEmployeeConfig object got an exception.", e );
			}
		}
		if( check ){
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.save( attendanceEmployeeConfig );
				wrapOutId = new WrapOutId( attendanceEmployeeConfig.getId() );
				result.setData( wrapOutId );
				result.setUserMessage( "人员考勤配置数据保存成功！" );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage("系统将传入的参数COPY到对象里时发生异常！");
				logger.error( "system copy wrap in to attendanceEmployeeConfig object got an exception.", e );
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceEmployeeConfigAttendanceEmployeeConfig对象.", response = WrapOutMessage.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrapOutId = null;
		AttendanceEmployeeConfig attendanceEmployeeConfig = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数ID为空，无法进行删除操作。") );
				result.setUserMessage("传入的参数ID为空，无法进行删除操作。。");
			}
		}		
		if( check ){
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.get( id );
				if( attendanceEmployeeConfig == null ){
					check = false;
					result.error(new Exception("需要删除的人员考勤数据不存在！"));
					result.setUserMessage("需要删除的人员考勤数据不存在！");
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据ID查询指定的人员考勤配置信息时发生异常。");
				logger.error( "system query employee config with id got an exception.id:" + id, e);
			}
		}
		if( check ){
			try {
				attendanceEmployeeConfigServiceAdv.delete( id );
				result.setUserMessage( "人员考勤配置数据保存成功！" );
				wrapOutId = new WrapOutId( id );
				result.setData( wrapOutId );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage("系统根据ID删除指定的人员考勤配置信息时发生异常。");
				logger.error( "system delete employee config with id got an exception.id:" + id, e);
			}
			
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}