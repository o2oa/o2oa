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

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.service.AttendanceSettingServiceAdv;
import com.x.attendance.entity.AttendanceSetting;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;


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
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceSetting> wraps = null;
		List<AttendanceSetting> attendanceSettingList = null;
		Boolean check = true;
		
		if( check ){
			try {
				attendanceSettingList = attendanceSettingServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceSettingListAllException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wraps = wrapout_copier.copy( attendanceSettingList );
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceSettingWrapOutException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
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
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutAttendanceSetting wrap = null;
		AttendanceSetting attendanceSetting = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new AttendanceSettingIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceSetting = attendanceSettingServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new GetAttendanceSettingByIdException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wrap = wrapout_copier.copy( attendanceSetting );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceSettingWrapOutException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
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
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutAttendanceSetting wrap = null;
		AttendanceSetting attendanceSetting = null;
		Boolean check = true;
		
		if( check ){
			if( code == null || code.isEmpty() ){
				check = false;
				Exception exception = new AttendanceSettingCodeEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceSetting = attendanceSettingServiceAdv.listIdsByCode( code );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceSettingListByCodeException( e, code );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wrap = wrapout_copier.copy( attendanceSetting );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceSettingWrapOutException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceSetting系统设置对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInAttendanceSetting wrapIn = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutId wrapOutId = null;
		AttendanceSetting attendanceSetting = new AttendanceSetting();
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceSetting.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			if( wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty() ){
				check = false;
				Exception exception = new AttendanceSettingCodeEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getConfigName() == null || wrapIn.getConfigName().isEmpty() ){
				check = false;
				Exception exception = new AttendanceSettingNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
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
				Exception exception = new AttendanceSettingWrapInException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceSetting = attendanceSettingServiceAdv.save( attendanceSetting );
				wrapOutId = new WrapOutId( attendanceSetting.getId() );
				result.setData( wrapOutId );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceSettingSaveException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceSetting系统设置对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutId wrapOutId = null;
		AttendanceSetting attendanceSetting = null;
		Boolean check = true;
		
		if( check ){
			if( id != null && !id.isEmpty() ){
				check = false;
				Exception exception = new AttendanceSettingIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceSetting = attendanceSettingServiceAdv.get( id );
				if( attendanceSetting == null ){			
					check = false;
					Exception exception = new AttendanceSettingNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new GetAttendanceSettingByIdException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceSettingServiceAdv.delete( id );
				wrapOutId = new WrapOutId( id );
				result.setData( wrapOutId );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceSettingDeleteException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}