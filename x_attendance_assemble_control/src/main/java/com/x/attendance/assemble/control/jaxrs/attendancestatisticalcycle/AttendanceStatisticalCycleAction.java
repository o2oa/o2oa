package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import java.util.List;
import java.util.Map;

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
import com.x.attendance.assemble.control.exception.PersonHasNoIdentityException;
import com.x.attendance.assemble.control.factory.AttendanceStatisticalCycleFactory;
import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceStatisticalCycle;
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


@Path("attendancestatisticalcycle")
public class AttendanceStatisticalCycleAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceStatisticalCycleAction.class );
	private BeanCopyTools<WrapInAttendanceStatisticalCycle, AttendanceStatisticalCycle> wrapin_copier = BeanCopyToolsBuilder.create( WrapInAttendanceStatisticalCycle.class, AttendanceStatisticalCycle.class, null, WrapInAttendanceStatisticalCycle.Excludes );
	private BeanCopyTools<AttendanceStatisticalCycle, WrapOutAttendanceStatisticalCycle> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceStatisticalCycle.class, WrapOutAttendanceStatisticalCycle.class, null, WrapOutAttendanceStatisticalCycle.Excludes);
	private UserManagerService userManagerService = new UserManagerService();
	
	@HttpMethodDescribe(value = "获取所有AttendanceStatisticalCycle列表", response = WrapOutAttendanceStatisticalCycle.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAttendanceStatisticalCycle(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAttendanceStatisticalCycle>> result = new ActionResult<>();
		List<WrapOutAttendanceStatisticalCycle> wraps = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		logger.debug("[listAllAttendanceStatisticalCycle]user[" + effectivePerson.getName() + "] try to get all attendanceStatisticalCycle......" );
		
		List<AttendanceStatisticalCycle> attendanceStatisticalCycleList = null;
		Business business = null;
		AttendanceStatisticalCycleFactory attendanceStatisticalCycleFactory = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			business = new Business(emc);					
			attendanceStatisticalCycleFactory  = business.getAttendanceStatisticalCycleFactory();			
			attendanceStatisticalCycleList = attendanceStatisticalCycleFactory.listAll();;	
			//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = wrapout_copier.copy( attendanceStatisticalCycleList );
			
			//对查询的列表进行排序				
			result.setData(wraps);
			
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取AttendanceStatisticalCycle对象.", response = WrapOutAttendanceStatisticalCycle.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutAttendanceStatisticalCycle> result = new ActionResult<>();
		WrapOutAttendanceStatisticalCycle wrap = null;
		AttendanceStatisticalCycle attendanceStatisticalCycle = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		logger.debug("[get]user[" + effectivePerson.getName() + "] try to get attendanceStatisticalCycle{'id':'"+id+"'}......" );
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			
			attendanceStatisticalCycle = emc.find( id, AttendanceStatisticalCycle.class );
			
			if( attendanceStatisticalCycle != null ){
				//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wrap = wrapout_copier.copy(attendanceStatisticalCycle);				
				//对查询的列表进行排序				
				result.setData(wrap);
			}
			
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据登录的用户查询合适的AttendanceStatisticalCycle对象.", response = WrapOutAttendanceStatisticalCycle.class)
	@GET
	@Path("cycleDetail/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response cycleDetail(@Context HttpServletRequest request, @PathParam("year") String year, @PathParam("month") String month ) {
		ActionResult<WrapOutAttendanceStatisticalCycle> result = new ActionResult<>();
		WrapOutAttendanceStatisticalCycle wrap = null;
		AttendanceStatisticalCycle attendanceStatisticalCycle = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>>  allCycleMap = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		AttendanceStatisticalCycleServiceAdv attendanceStatisticalCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
		String companyName = null, departmentName = null;
		Boolean check = true;
		
		if( check ){
			if( year == null || year.isEmpty() ){
				check = false;
				Exception exception = new QueryStatisticCycleYearEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( month == null || month.isEmpty() ){
				check = false;
				Exception exception = new QueryStatisticCycleMonthEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}		
		if( check ){
			try {
				companyName = userManagerService.getCompanyNameByEmployeeName( effectivePerson.getName() );
				if( companyName == null || companyName.isEmpty() ){
					check = false;
					Exception exception = new CanNotFindCompanyNameByPersonException( effectivePerson.getName() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}  catch ( PersonHasNoIdentityException e ) {
				check = false;
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			} catch (Exception e) {
				check = false;
				Exception exception = new GetCompanyNameByPersonException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				departmentName = userManagerService.getDepartmentNameByEmployeeName( effectivePerson.getName() );
				if( departmentName == null || departmentName.isEmpty() ){
					check = false;
					Exception exception = new CanNotFindDepartmentNameByPersonException( effectivePerson.getName() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch ( PersonHasNoIdentityException e ) {
				check = false;
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			} catch ( Exception e) {
				check = false;
				Exception exception = new GetDepartmentNameByPersonException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				allCycleMap = attendanceStatisticalCycleServiceAdv.getCycleMapFormAllCycles();
			} catch (Exception e) {
				check = false;
				Exception exception = new GetCycleMapFromAllCyclesException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceStatisticalCycle = attendanceStatisticalCycleServiceAdv.getAttendanceDetailStatisticCycle( companyName, departmentName, year, month, allCycleMap );
			} catch (Exception e) {
				check = false;
				Exception exception = new GetAttendanceDetailStatisticCycleException( e, companyName, departmentName, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( attendanceStatisticalCycle != null ){
				try{
					wrap = wrapout_copier.copy( attendanceStatisticalCycle );
					result.setData(wrap);
				}catch(Exception e){
					Exception exception = new AttendanceStatisticCycleWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceStatisticalCycle对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInAttendanceStatisticalCycle wrapIn = null;
		//获取到当前用户信息
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<String> ids = null;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceStatisticalCycle.class );
		} catch (Exception e ) {
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		if( wrapIn != null){		
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				AttendanceStatisticalCycle _attendanceStatisticalCycle = null;
				AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
				Business business = new Business(emc);
				if( wrapIn.getDepartmentName() == null  || wrapIn.getDepartmentName().isEmpty() ){
					wrapIn.setDepartmentName("*");
				}
				if( wrapIn.getCompanyName() == null  || wrapIn.getCompanyName().isEmpty() ){
					wrapIn.setCompanyName("*");
				}
				if( wrapIn.getCycleYear() == null  || wrapIn.getCycleYear().isEmpty() ){
					wrapIn.setCycleYear("*");
				}
				
				ids = business.getAttendanceStatisticalCycleFactory().listByParameter( wrapIn.getCompanyName(), wrapIn.getDepartmentName(), wrapIn.getCycleYear(), wrapIn.getCycleMonth());
				emc.beginTransaction( AttendanceStatisticalCycle.class );
				if( ids != null && ids.size() > 0 ){
					//说明有重复的
					_attendanceStatisticalCycle = emc.find( wrapIn.getId(), AttendanceStatisticalCycle.class );
					if( _attendanceStatisticalCycle != null ){
						wrapin_copier.copy( wrapIn, _attendanceStatisticalCycle );
						emc.check( _attendanceStatisticalCycle, CheckPersistType.all);	
					}
				}else{
					//新增就行了
					if( wrapIn.getId() !=null && wrapIn.getId().length() > 10 ){
						attendanceStatisticalCycle.setId( wrapIn.getId());
					}
					wrapin_copier.copy( wrapIn, attendanceStatisticalCycle );
					attendanceStatisticalCycle.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
				}
				emc.commit();
				result.setData( new WrapOutId( attendanceStatisticalCycle.getId() ) );
			} catch ( Exception e ) {
				e.printStackTrace();
				Exception exception = new AttendanceStatisticCycleSaveException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
//		else{
//			wrapOutMessage.setStatus( "ERROR");
//			wrapOutMessage.setMessage( "请求传入的参数wrapIn为空，无法继续保存AttendanceStatisticalCycle!" );
//		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceStatisticalCycleAttendanceStatisticalCycle对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			AttendanceStatisticalCycle attendanceStatisticalCycle = emc.find(id, AttendanceStatisticalCycle.class);
			if (null == attendanceStatisticalCycle) {
				Exception exception = new AttendanceStatisticCycleNotExistsException( id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}else{
				//进行数据库持久化操作
				emc.beginTransaction( AttendanceStatisticalCycle.class );
				emc.remove( attendanceStatisticalCycle, CheckRemoveType.all );
				emc.commit();
				result.setData( new WrapOutId(id) );
			}			
		} catch ( Exception e ) {
			Exception exception = new AttendanceStatisticCycleDeleteException( e, id );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}