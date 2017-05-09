package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceWorkDayConfigFactory;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;


@Path("attendanceworkdayconfig")
public class AttendanceWorkDayConfigAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceWorkDayConfigAction.class );
	private BeanCopyTools<WrapInAttendanceWorkDayConfig, AttendanceWorkDayConfig> wrapin_copier = BeanCopyToolsBuilder.create( WrapInAttendanceWorkDayConfig.class, AttendanceWorkDayConfig.class, null, WrapInAttendanceWorkDayConfig.Excludes );
	private BeanCopyTools<AttendanceWorkDayConfig, WrapOutAttendanceWorkDayConfig> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceWorkDayConfig.class, WrapOutAttendanceWorkDayConfig.class, null, WrapOutAttendanceWorkDayConfig.Excludes);

	@HttpMethodDescribe(value = "获取所有节假日配置列表", response = WrapOutAttendanceWorkDayConfig.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAttendanceWorkDayConfig(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAttendanceWorkDayConfig>> result = new ActionResult<>();
		List<WrapOutAttendanceWorkDayConfig> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("[listAllAttendanceWorkDayConfig]user[" + currentPerson.getName() + "] try to get all attendanceWorkDayConfig......" );
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Business business = null;
		AttendanceWorkDayConfigFactory attendanceWorkDayConfigFactory = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			business = new Business(emc);					
			attendanceWorkDayConfigFactory  = business.getAttendanceWorkDayConfigFactory();			
			//查询ID IN ids 的所有应用信息列表
			attendanceWorkDayConfigList = attendanceWorkDayConfigFactory.listAll();	
			//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = wrapout_copier.copy( attendanceWorkDayConfigList );
			
			//对查询的列表进行排序				
			result.setData(wraps);			
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据条件获取节假日配置列表", response = WrapOutAttendanceWorkDayConfig.class, request = JsonElement.class)
	@PUT
	@Path("filter")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterWorkDayConfig(@Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<List<WrapOutAttendanceWorkDayConfig>> result = new ActionResult<>();
		List<WrapOutAttendanceWorkDayConfig> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<String> ids = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Business business = null;
		AttendanceWorkDayConfigFactory attendanceWorkDayConfigFactory = null;
		String q_Name = null;
		String q_Year = null;
		String q_Month = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if(check ){
			q_Name = wrapIn.getQ_Name();
			q_Year = wrapIn.getQ_Year();
			q_Month = wrapIn.getQ_Month();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
				business = new Business(emc);					
				attendanceWorkDayConfigFactory  = business.getAttendanceWorkDayConfigFactory();			
				//获取所有应用列表
				if( q_Year != null && !q_Year.isEmpty() ){
					if( q_Month != null && !q_Month.isEmpty() ){
						//根据年份月份获取所有节假日配置列表
						logger.debug( "根据年份月份获取所有节假日配置列表" );
						ids = attendanceWorkDayConfigFactory.listByYearAndMonth( q_Year, q_Month );	
					}
					if( q_Name != null && !q_Name.isEmpty() ){
						//根据年份名称获取所有节假日配置列表
						logger.debug( "根据年份名称获取所有节假日配置列表" );
						ids = attendanceWorkDayConfigFactory.listByYearAndName( q_Year, q_Name );	
					}
				}else{
					if( q_Name != null && !q_Name.isEmpty() ){
						//根据名称获取所有节假日配置列表
						logger.debug( "根据名称获取所有节假日配置列表" );
						ids = attendanceWorkDayConfigFactory.listByName( q_Name );	
					}
				}
				//查询ID IN ids 的所有应用信息列表
				attendanceWorkDayConfigList = attendanceWorkDayConfigFactory.list( ids );	
				//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier.copy( attendanceWorkDayConfigList );
				//对查询的列表进行排序				
				result.setData(wraps);			
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取AttendanceWorkDayConfig对象.", response = WrapOutAttendanceWorkDayConfig.class, request = WrapInFilter.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutAttendanceWorkDayConfig> result = new ActionResult<>();
		WrapOutAttendanceWorkDayConfig wrap = null;
		AttendanceWorkDayConfig attendanceWorkDayConfig = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("[get]user[" + currentPerson.getName() + "] try to get attendanceWorkDayConfig{'id':'"+id+"'}......" );
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			
			attendanceWorkDayConfig = emc.find( id, AttendanceWorkDayConfig.class );
			
			if( attendanceWorkDayConfig != null ){
				//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wrap = wrapout_copier.copy(attendanceWorkDayConfig);				
				//对查询的列表进行排序				
				result.setData(wrap);
			}
			
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceWorkDayConfig节假日配置对象.", request = AttendanceWorkDayConfig.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInAttendanceWorkDayConfig wrapIn = null;
		//获取到当前用户信息
		EffectivePerson currentPerson = this.effectivePerson(request);
		DateOperation dateOperation = new DateOperation();
		Boolean check = true;
		Date date = null;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceWorkDayConfig.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				AttendanceWorkDayConfig _attendanceWorkDayConfig = null;
				AttendanceWorkDayConfig attendanceWorkDayConfig = new AttendanceWorkDayConfig();
				if( wrapIn != null && wrapIn.getId() !=null && wrapIn.getId().length() > 10 ){
					if( wrapIn.getId() !=null && wrapIn.getId().length() > 10 ){
						//根据ID查询信息是否存在，如果存在就update，如果不存在就create
						_attendanceWorkDayConfig = emc.find( wrapIn.getId(), AttendanceWorkDayConfig.class );
						if( _attendanceWorkDayConfig != null ){
							//更新
							emc.beginTransaction( AttendanceWorkDayConfig.class );
							wrapin_copier.copy( wrapIn, _attendanceWorkDayConfig );
							try{
								date = dateOperation.getDateFromString(_attendanceWorkDayConfig.getConfigDate());
								_attendanceWorkDayConfig.setConfigYear( dateOperation.getYear(date) );
								_attendanceWorkDayConfig.setConfigMonth( dateOperation.getMonth(date) );
								emc.check( _attendanceWorkDayConfig, CheckPersistType.all);	
								emc.commit();
								result.setData( new WrapOutId( _attendanceWorkDayConfig.getId() ) );
							}catch(Exception e){
								Exception exception = new AttendanceWorkDayFormatException( e, _attendanceWorkDayConfig.getConfigDate() );
								result.error( exception );
								logger.error( e, currentPerson, request, null);
							}
						}else{
							emc.beginTransaction( AttendanceWorkDayConfig.class );
							wrapin_copier.copy( wrapIn, attendanceWorkDayConfig );
							attendanceWorkDayConfig.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
							try{
								date = dateOperation.getDateFromString( attendanceWorkDayConfig.getConfigDate());
								attendanceWorkDayConfig.setConfigYear( dateOperation.getYear(date) );
								attendanceWorkDayConfig.setConfigMonth( dateOperation.getMonth(date) );
								emc.persist( attendanceWorkDayConfig, CheckPersistType.all);	
								emc.commit();
								result.setData( new WrapOutId( attendanceWorkDayConfig.getId() ) );
							}catch(Exception e){
								Exception exception = new AttendanceWorkDayFormatException( e, attendanceWorkDayConfig.getConfigDate() );
								result.error( exception );
								logger.error( e, currentPerson, request, null);
							}
						}
					}else{
						//没有传入指定的ID
						emc.beginTransaction( AttendanceWorkDayConfig.class );
						wrapin_copier.copy( wrapIn, attendanceWorkDayConfig );
						try{
							date = dateOperation.getDateFromString(attendanceWorkDayConfig.getConfigDate());
							attendanceWorkDayConfig.setConfigYear( dateOperation.getYear(date) );
							attendanceWorkDayConfig.setConfigMonth( dateOperation.getMonth(date) );
							emc.persist( attendanceWorkDayConfig, CheckPersistType.all);	
							emc.commit();
							result.setData( new WrapOutId( attendanceWorkDayConfig.getId() ) );
						}catch(Exception e){
							Exception exception = new AttendanceWorkDayFormatException( e, attendanceWorkDayConfig.getConfigDate() );
							result.error( exception );
							logger.error( e, currentPerson, request, null);
						}
					}
				}
			} catch ( Exception e ) {
				Exception exception = new AttendanceWorkDayConfigSaveException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceWorkDayConfig节假日配置对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			AttendanceWorkDayConfig attendanceWorkDayConfig = emc.find(id, AttendanceWorkDayConfig.class);
			if (null == attendanceWorkDayConfig) {
				Exception exception = new AttendanceWorkDayConfigNotExistsException( id );
				result.error( exception );
				//logger.error( e, currentPerson, request, null);
			}else{
				//进行数据库持久化操作
				emc.beginTransaction( AttendanceWorkDayConfig.class );
				emc.remove( attendanceWorkDayConfig, CheckRemoveType.all );
				emc.commit();
				result.setData( new WrapOutId( id ) );
			}			
		} catch ( Exception e ) {
			Exception exception = new AttendanceWorkDayConfigDeleteException( e, id );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}