package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.control.jaxrs.WrapOutMessage;
import com.x.attendance.assemble.control.service.AttendanceImportFileInfoServiceAdv;
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;


@Path("attendanceimportfileinfo")
public class AttendanceImportFileInfoAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceImportFileInfoAction.class );
	private BeanCopyTools<AttendanceImportFileInfo, WrapOutAttendanceImportFileInfo> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceImportFileInfo.class, WrapOutAttendanceImportFileInfo.class, null, WrapOutAttendanceImportFileInfo.Excludes);
	private AttendanceImportFileInfoServiceAdv attendanceImportFileInfoServiceAdv = new AttendanceImportFileInfoServiceAdv();
	
	@HttpMethodDescribe(value = "获取所有已经上传成功的文件列表", response = WrapOutAttendanceImportFileInfo.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAttendanceImportFileInfo(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAttendanceImportFileInfo>> result = new ActionResult<>();
		List<WrapOutAttendanceImportFileInfo> wraps = null;
		List<AttendanceImportFileInfo> attendanceSettingList = null;
		Boolean check = true;
		
		if( check ){
			try {
				attendanceSettingList = attendanceImportFileInfoServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统查询所有导入文件信息列表时发生异常。");
				logger.error( "system query all import file info list got an exception.", e);
			}
		}
		if( check && attendanceSettingList != null ){
			try {
				wraps = wrapout_copier.copy( attendanceSettingList );
				result.setData(wraps);
				result.setUserMessage( "查询成功！" );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常。");
				logger.error( "system copy import file info list to wrap got an exception.", e);
			}
			
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取AttendanceImportFileInfo对象.", response = WrapOutAttendanceImportFileInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutAttendanceImportFileInfo> result = new ActionResult<>();
		WrapOutAttendanceImportFileInfo wrap = null;
		AttendanceImportFileInfo attendanceImportFileInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("系统传入的ID为空。") );
				result.setUserMessage("系统传入的ID为空。");
			}
		}
		if( check ){
			try {
				attendanceImportFileInfo = attendanceImportFileInfoServiceAdv.get(id);
				if( attendanceImportFileInfo == null ){
					check = false;
					result.error( new Exception("指定的文件导入信息不存在。") );
					result.setUserMessage("指定的文件导入信息不存在。");
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据ID查询导入文件信息列表时发生异常。");
				logger.error( "system query import file info with id got an exception.id:" + id, e);
			}
		}
		if( check && attendanceImportFileInfo != null ){
			try {
				wrap = wrapout_copier.copy(attendanceImportFileInfo);
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常。");
				logger.error( "system copy import file info to wrap got an exception.", e);
			}	
		}	
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除已经上传成功的文件以及文件信息.", response = WrapOutMessage.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrapOutId = null;
		AttendanceImportFileInfo attendanceImportFileInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("系统传入的ID为空。") );
				result.setUserMessage("系统传入的ID为空。");
			}
		}
		if( check ){
			try {
				attendanceImportFileInfo = attendanceImportFileInfoServiceAdv.get(id);
				if( attendanceImportFileInfo == null ){
					check = false;
					result.error( new Exception("指定的文件导入信息不存在。") );
					result.setUserMessage("指定的文件导入信息不存在。");
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据ID查询导入文件信息列表时发生异常。");
				logger.error( "system query import file info with id got an exception.id:" + id, e);
			}
		}
		if( check ){
			try {
				attendanceImportFileInfoServiceAdv.delete(id);
				wrapOutId = new WrapOutId( id );
				result.setData( wrapOutId );
				result.setUserMessage( "数据删除成功！" );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据ID删除导入文件信息列表时发生异常。");
				logger.error( "system delete import file info with id got an exception.id:" + id, e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}