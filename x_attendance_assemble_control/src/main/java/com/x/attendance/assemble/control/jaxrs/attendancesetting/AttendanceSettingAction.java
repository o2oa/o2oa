package com.x.attendance.assemble.control.jaxrs.attendancesetting;

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
import com.x.attendance.assemble.control.service.AttendanceSettingServiceAdv;
import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;


@Path("attendancesetting")
public class AttendanceSettingAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceSettingAction.class );
	private AttendanceSettingServiceAdv attendanceSettingServiceAdv = new AttendanceSettingServiceAdv();
	private BeanCopyTools<WrapInAttendanceSetting, AttendanceSetting> wrapin_copier = BeanCopyToolsBuilder.create( WrapInAttendanceSetting.class, AttendanceSetting.class, null, WrapInAttendanceSetting.Excludes );
	private BeanCopyTools<AttendanceSetting, WrapOutAttendanceSetting> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceSetting.class, WrapOutAttendanceSetting.class, null, WrapOutAttendanceSetting.Excludes);
	
	@HttpMethodDescribe(value = "获取所有系统配置列表", response = WrapOutAttendanceSetting.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAttendanceSetting(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAttendanceSetting>> result = new ActionResult<>();
		List<WrapOutAttendanceSetting> wraps = null;
		List<AttendanceSetting> attendanceSettingList = null;
		Boolean check = true;
		
		if( check ){
			try {
				attendanceSettingList = attendanceSettingServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				logger.error( "system query all attendance setting got an exception.", e );
				result.error(e);
				result.setUserMessage( "系统在查询全部考勤系统配置时发生异常！" );
			}
		}
		if( check ){
			try {
				wraps = wrapout_copier.copy( attendanceSettingList );
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常。");
				logger.error( "system copy attendance setting list to wrap got an exception.", e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取AttendanceSetting对象.", response = WrapOutAttendanceSetting.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutAttendanceSetting> result = new ActionResult<>();
		WrapOutAttendanceSetting wrap = null;
		AttendanceSetting attendanceSetting = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到需要查询的数据ID，参数id为空。" ));
				result.setUserMessage( "系统未获取到需要查询的数据ID，参数id为空。" );
			}
		}
		if( check ){
			try {
				attendanceSetting = attendanceSettingServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				logger.error( "system get attendance setting with id got an exception.id:" + id, e );
				result.error(e);
				result.setUserMessage( "系统在根据ID查询考勤系统配置时发生异常！" );
			}
		}
		if( check ){
			try {
				wrap = wrapout_copier.copy( attendanceSetting );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常。");
				logger.error( "system copy attendance setting to wrap got an exception.", e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据Code获取AttendanceSetting对象.", response = WrapOutAttendanceSetting.class)
	@GET
	@Path("code/{code}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithCode(@Context HttpServletRequest request, @PathParam("code") String code) {
		ActionResult<WrapOutAttendanceSetting> result = new ActionResult<>();
		WrapOutAttendanceSetting wrap = null;
		AttendanceSetting attendanceSetting = null;
		Boolean check = true;
		
		if( check ){
			if( code == null || code.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到需要查询的数据code，参数code为空。" ));
				result.setUserMessage( "系统未获取到需要查询的数据code，参数code为空。" );
			}
		}
		if( check ){
			try {
				attendanceSetting = attendanceSettingServiceAdv.listIdsByCode( code );
			} catch (Exception e) {
				check = false;
				logger.error( "system get attendance setting with code got an exception.code:" + code, e );
				result.error(e);
				result.setUserMessage( "系统在根据ID查询考勤系统配置时发生异常！" );
			}
		}
		if( check ){
			try {
				wrap = wrapout_copier.copy( attendanceSetting );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常。");
				logger.error( "system copy attendance setting to wrap got an exception.", e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceSetting系统设置对象.", request = WrapInAttendanceSetting.class, response = WrapOutMessage.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request, WrapInAttendanceSetting wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrapOutId = null;
		AttendanceSetting attendanceSetting = new AttendanceSetting();
		Boolean check = true;
		
		if( wrapIn == null ){
			check = false;
			result.error( new Exception( "系统未获取到需要保存的数据对象。" ) );
			result.setUserMessage( "系统未获取到需要保存的数据对象。" );
		}
		if( check ){
			if( wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty() ){
				check = false;
				result.error( new Exception( "系统配置编码不允许为空，请检查您的输入。" ) );
				result.setUserMessage( "系统配置编码不允许为空，请检查您的输入。" );
			}
		}
		if( check ){
			if( wrapIn.getConfigName() == null || wrapIn.getConfigName().isEmpty() ){
				check = false;
				result.error( new Exception( "系统配置名称不允许为空，请检查您的输入。" ) );
				result.setUserMessage( "系统配置名称不允许为空，请检查您的输入。" );
			}
		}
		if( check ){
			try {
				wrapin_copier.copy( wrapIn, attendanceSetting );
				if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
					attendanceSetting.setId( wrapIn.getId() );
				}
			} catch (Exception e) {
				check = false;
				logger.error( "system copy wrap in data to attendance setting object got an exception.", e );
				result.error( e );
				result.setUserMessage( "系统在将传入的数据格式化为系统配置对象时发生异常。" );
			}
		}
		if( check ){
			try {
				attendanceSetting = attendanceSettingServiceAdv.save( attendanceSetting );
				wrapOutId = new WrapOutId( attendanceSetting.getId() );
				result.setData( wrapOutId );
			} catch (Exception e) {
				check = false;
				logger.error( "system save attendance setting got an exception.", e );
				result.error( e );
				result.setUserMessage( "系统在保存系统配置到数据库时发生异常。" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceSetting系统设置对象.", response = WrapOutMessage.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrapOutId = null;
		AttendanceSetting attendanceSetting = null;
		Boolean check = true;
		
		if( check ){
			if( id != null && !id.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到需要删除的数据id，参数id为空。" ));
				result.setUserMessage( "系统未获取到需要删除的数据id，参数id为空。" );
			}
		}
		if( check ){
			try {
				attendanceSetting = attendanceSettingServiceAdv.get( id );
				if( attendanceSetting == null ){			
					check = false;
					result.error( new Exception("需要删除的数据不存在！") );
					result.setUserMessage( "需要删除的数据不存在！" );
				}
			} catch (Exception e) {
				check = false;
				logger.error( "system get attendance setting with id got an exception.id:" + id, e );
				result.error(e);
				result.setUserMessage( "系统在根据ID查询考勤系统配置时发生异常！" );
			}
		}
		if( check ){
			try {
				attendanceSettingServiceAdv.delete( id );
				wrapOutId = new WrapOutId( id );
				result.setData( wrapOutId );
			} catch (Exception e) {
				check = false;
				logger.error( "system delete attendance setting with id got an exception.id:" + id, e );
				result.error(e);
				result.setUserMessage( "系统在根据ID删除考勤系统配置时发生异常！" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}