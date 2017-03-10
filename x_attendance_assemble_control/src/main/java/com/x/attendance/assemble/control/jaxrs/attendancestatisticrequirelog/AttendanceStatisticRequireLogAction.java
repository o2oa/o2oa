package com.x.attendance.assemble.control.jaxrs.attendancestatisticrequirelog;

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
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceStatisticRequireLogFactory;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;


@Path("attendancestatisticrequirelog")
public class AttendanceStatisticRequireLogAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceStatisticRequireLogAction.class );
	private BeanCopyTools<WrapInAttendanceStatisticRequireLog, AttendanceStatisticRequireLog> wrapin_copier = BeanCopyToolsBuilder.create( WrapInAttendanceStatisticRequireLog.class, AttendanceStatisticRequireLog.class, null, WrapInAttendanceStatisticRequireLog.Excludes );
	private BeanCopyTools<AttendanceStatisticRequireLog, WrapOutAttendanceStatisticRequireLog> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceStatisticRequireLog.class, WrapOutAttendanceStatisticRequireLog.class, null, WrapOutAttendanceStatisticRequireLog.Excludes);
	
	@HttpMethodDescribe(value = "获取所有AttendanceStatisticRequireLog列表", response = WrapOutAttendanceStatisticRequireLog.class)
	@GET
	@Path("list/all")
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listAllAttendanceStatisticRequireLog(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAttendanceStatisticRequireLog>> result = new ActionResult<>();
		List<WrapOutAttendanceStatisticRequireLog> wraps = null;
		List<AttendanceStatisticRequireLog> attendanceStatisticRequireLogList = null;
		Business business = null;
		AttendanceStatisticRequireLogFactory attendanceStatisticRequireLogFactory = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			business = new Business(emc);					
			attendanceStatisticRequireLogFactory  = business.getAttendanceStatisticRequireLogFactory();					
			//查询ID IN ids 的所有应用信息列表
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogFactory.listAll();	
			//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = wrapout_copier.copy( attendanceStatisticRequireLogList );
			//对查询的列表进行排序				
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取AttendanceStatisticRequireLog对象.", response = WrapOutAttendanceStatisticRequireLog.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutAttendanceStatisticRequireLog> result = new ActionResult<>();
		WrapOutAttendanceStatisticRequireLog wrap = null;
		AttendanceStatisticRequireLog attendanceStatisticRequireLog = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			attendanceStatisticRequireLog = emc.find( id, AttendanceStatisticRequireLog.class );
			if( attendanceStatisticRequireLog != null ){
				//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wrap = wrapout_copier.copy(attendanceStatisticRequireLog);				
				//对查询的列表进行排序				
				result.setData(wrap);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceStatisticRequireLog对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInAttendanceStatisticRequireLog wrapIn = null;
		
		//获取到当前用户信息
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceStatisticRequireLog.class );
		} catch (Exception e ) {
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		
		if( wrapIn != null){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				AttendanceStatisticRequireLog _attendanceStatisticRequireLog = null;
				AttendanceStatisticRequireLog attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				if( wrapIn.getId() !=null && wrapIn.getId().length() > 10 ){
					//根据ID查询信息是否存在，如果存在就update，如果不存在就create
					_attendanceStatisticRequireLog = emc.find( wrapIn.getId(), AttendanceStatisticRequireLog.class );
					if( _attendanceStatisticRequireLog != null ){
						//更新
						emc.beginTransaction( AttendanceStatisticRequireLog.class );
						wrapin_copier.copy( wrapIn, _attendanceStatisticRequireLog );
						emc.check( _attendanceStatisticRequireLog, CheckPersistType.all);	
						emc.commit();
					}else{
						emc.beginTransaction( AttendanceStatisticRequireLog.class );
						wrapin_copier.copy( wrapIn, attendanceStatisticRequireLog );
						attendanceStatisticRequireLog.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
						emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
						emc.commit();
					}
				}else{
					//没有传入指定的ID
					emc.beginTransaction( AttendanceStatisticRequireLog.class );
					wrapin_copier.copy( wrapIn, attendanceStatisticRequireLog );
					emc.persist( attendanceStatisticRequireLog, CheckPersistType.all);	
					emc.commit();
				}
				result.setData( new WrapOutId(attendanceStatisticRequireLog.getId()) );
			} catch ( Exception e ) {
				Exception exception = new AttendanceStatisticRequireSaveException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
//		else{
//			wrapOutMessage.setStatus( "ERROR");
//			wrapOutMessage.setMessage( "请求传入的参数wrapIn为空，无法继续保存AttendanceStatisticRequireLog!" );
//		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceStatisticRequireLogAttendanceStatisticRequireLog对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			AttendanceStatisticRequireLog attendanceStatisticRequireLog = emc.find(id, AttendanceStatisticRequireLog.class);
			if ( null == attendanceStatisticRequireLog ) {
				Exception exception = new AttendanceStatisticRequireNotExistsException( id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}else{
				emc.beginTransaction( AttendanceStatisticRequireLog.class );
				emc.remove( attendanceStatisticRequireLog, CheckRemoveType.all );
				emc.commit();
				result.setData( new WrapOutId( id ) );
			}			
		} catch ( Exception e ) {
			Exception exception = new AttendanceStatisticRequireDeleteException( e, id );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}