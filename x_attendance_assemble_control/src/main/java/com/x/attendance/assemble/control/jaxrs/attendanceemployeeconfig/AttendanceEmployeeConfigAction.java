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

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.attendance.entity.AttendanceEmployeeConfig;
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
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceEmployeeConfig> wraps = null;
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigList = null;
		Boolean check = true;
		if( check ){
			try {
				attendanceEmployeeConfigList = attendanceEmployeeConfigServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error(e);
				Exception exception = new AttendanceEmployeeConfigListAllException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check && attendanceEmployeeConfigList != null ){
			//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			try {
				wraps = wrapout_copier.copy( attendanceEmployeeConfigList );
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceEmployeeConfigWrapOutException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
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
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutAttendanceEmployeeConfig wrap = null;
		AttendanceEmployeeConfig attendanceEmployeeConfig = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new AttendanceEmployeeConfigIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}		
		if( check ){
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.get( id );
				if( attendanceEmployeeConfig == null ){
					check = false;
					Exception exception = new AttendanceEmployeeConfigNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceEmployeeConfigQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wrap = wrapout_copier.copy(attendanceEmployeeConfig);
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceEmployeeConfigWrapOutException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceEmployeeConfig对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInAttendanceEmployeeConfig wrapIn = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutId wrapOutId = null;
		AttendanceEmployeeConfig attendanceEmployeeConfig = new AttendanceEmployeeConfig();
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceEmployeeConfig.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			try {
				wrapin_copier.copy( wrapIn, attendanceEmployeeConfig );
				if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
					attendanceEmployeeConfig.setId( wrapIn.getId() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceEmployeeConfigWrapInException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.save( attendanceEmployeeConfig );
				result.setData( new WrapOutId( attendanceEmployeeConfig.getId() ) );
				logger.info( "人员考勤配置数据保存成功！" );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceEmployeeConfigSaveException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceEmployeeConfigAttendanceEmployeeConfig对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		AttendanceEmployeeConfig attendanceEmployeeConfig = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new AttendanceEmployeeConfigIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}		
		if( check ){
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.get( id );
				if( attendanceEmployeeConfig == null ){
					check = false;
					Exception exception = new AttendanceEmployeeConfigNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceEmployeeConfigQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceEmployeeConfigServiceAdv.delete( id );
				result.setData( new WrapOutId( id ) );
				logger.info( "人员考勤配置数据保存成功！" );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceEmployeeConfigDeleteException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
			
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}