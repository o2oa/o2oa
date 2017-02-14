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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceStatisticalCycleFactory;
import com.x.attendance.assemble.control.jaxrs.WrapOutMessage;
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
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.core.express.wrap.WrapIdentity;


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
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("[listAllAttendanceStatisticalCycle]user[" + currentPerson.getName() + "] try to get all attendanceStatisticalCycle......" );
		
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("[get]user[" + currentPerson.getName() + "] try to get attendanceStatisticalCycle{'id':'"+id+"'}......" );
		
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		AttendanceStatisticalCycleServiceAdv attendanceStatisticalCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
		String companyName = null, departmentName = null;
		Boolean check = true;
		
		if( check ){
			if( year == null || year.isEmpty() ){
				check = false;
				result.error( new Exception("系统未获取到传入的参数year。") );
				result.setUserMessage( "系统未获取到传入的参数year。" );
			}
		}
		if( check ){
			if( month == null || month.isEmpty() ){
				check = false;
				result.error( new Exception("系统未获取到传入的参数year。") );
				result.setUserMessage( "系统未获取到传入的参数year。" );
			}
		}		
		if( check ){
			try {
				companyName = userManagerService.getCompanyNameByEmployeeName( currentPerson.getName() );
				if( companyName == null || companyName.isEmpty() ){
					check = false;
					result.error( new Exception("根据人员未获取到公司信息。姓名：" + currentPerson.getName() ) );
					result.setUserMessage( "根据人员未获取到公司信息。" );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据用户姓名获取公司名称时发生异常。" );
				logger.error( "system get company name with employee name got an exception.name:" + currentPerson.getName(), e );
			}
		}
		if( check ){
			try {
				departmentName = userManagerService.getDepartmentNameByEmployeeName( currentPerson.getName() );
				if( companyName == null || companyName.isEmpty() ){
					check = false;
					result.error( new Exception("根据人员未获取到部门信息。姓名：" + currentPerson.getName() ) );
					result.setUserMessage( "根据人员未获取到部门信息。" );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据用户姓名获取部门名称时发生异常。" );
				logger.error( "system get department name with employee name got an exception.name:" + currentPerson.getName(), e );
			}
		}
		if( check ){
			try {
				allCycleMap = attendanceStatisticalCycleServiceAdv.getCycleMapFormAllCycles();
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统获取所有的统计周期信息时发生异常。" );
				logger.error( "system get all cycle map form all cycles got an exception.name:" + currentPerson.getName(), e );
			}
		}
		if( check ){
			try {
				attendanceStatisticalCycle = attendanceStatisticalCycleServiceAdv.getAttendanceDetailStatisticCycle( companyName, departmentName, year, month, allCycleMap );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统获取指定公司、部门在指定年份月份的统计周期信息时发生异常。" );
				logger.error( "system get all cycle map form all cycles got an exception.companyName:"+companyName+", departmentName:"+departmentName+", year:"+year+", month:" + month, e );
			}
		}
		if( check ){
			if( attendanceStatisticalCycle != null ){
				try{
					wrap = wrapout_copier.copy( attendanceStatisticalCycle );
					result.setData(wrap);
				}catch(Exception e){
					logger.error( "系统在COPY实体对象到输出对象时发生异常！", e);
				}
			}else{
				check = false;
				result.error( new Exception("系统未查询到任何统计周期信息。" ) );
				result.setUserMessage( "系统未查询到任何统计周期信息.companyName:"+companyName+", departmentName:"+departmentName+", year:"+year+", month:" + month );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceStatisticalCycle对象.", request = WrapInAttendanceStatisticalCycle.class, response = WrapOutMessage.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInAttendanceStatisticalCycle wrapIn) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		WrapOutMessage wrapOutMessage = new WrapOutMessage();
		//获取到当前用户信息
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<String> ids = null;
		
		if( wrapIn != null){
			logger.debug("user " + currentPerson.getName() + "try to save AttendanceStatisticalCycle......" );			
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				AttendanceStatisticalCycle _attendanceStatisticalCycle = null;
				AttendanceStatisticalCycle attendanceStatisticalCycle = new AttendanceStatisticalCycle();
				Business business = new Business(emc);
				logger.debug("System trying to beginTransaction to create/update attendanceStatisticalCycle......" );
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
					logger.debug( "查询ID="+wrapIn.getId()+"的考勤统计周期配置项....." );
					_attendanceStatisticalCycle = emc.find( wrapIn.getId(), AttendanceStatisticalCycle.class );
					if( _attendanceStatisticalCycle != null ){
						wrapin_copier.copy( wrapIn, _attendanceStatisticalCycle );
						emc.check( _attendanceStatisticalCycle, CheckPersistType.all);	
						logger.debug("System update attendanceStatisticalCycle success！" );
					}
				}else{
					//新增就行了
					if( wrapIn.getId() !=null && wrapIn.getId().length() > 10 ){
						attendanceStatisticalCycle.setId( wrapIn.getId());
						logger.debug("System create attendanceStatisticalCycle used parameter id=" + wrapIn.getId() );
					}
					wrapin_copier.copy( wrapIn, attendanceStatisticalCycle );
					attendanceStatisticalCycle.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( attendanceStatisticalCycle, CheckPersistType.all);
					logger.debug("System create attendanceStatisticalCycle success！" );
				}
				emc.commit();
				wrapOutMessage.setStatus( "SUCCESS");
				wrapOutMessage.setMessage( attendanceStatisticalCycle.getId() );
			} catch ( Exception e ) {
				e.printStackTrace();
				wrapOutMessage.setStatus( "ERROR");
				wrapOutMessage.setMessage( "保存AttendanceStatisticalCycle过程中发生异常." );
				wrapOutMessage.setExceptionMessage( e.getMessage() );
			}
		}else{
			wrapOutMessage.setStatus( "ERROR");
			wrapOutMessage.setMessage( "请求传入的参数wrapIn为空，无法继续保存AttendanceStatisticalCycle!" );
		}
		
		result.setData( wrapOutMessage );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceStatisticalCycleAttendanceStatisticalCycle对象.", response = WrapOutMessage.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		logger.debug("method delete has been called, try to delete attendanceStatisticalCycle{'id':'"+id+"'}......" );
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		WrapOutMessage wrapOutMessage = new WrapOutMessage();
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("user " + currentPerson.getName() + "try to delete AttendanceStatisticalCycle......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			AttendanceStatisticalCycle attendanceStatisticalCycle = emc.find(id, AttendanceStatisticalCycle.class);
			if (null == attendanceStatisticalCycle) {
				wrapOutMessage.setStatus("ERROR");
				wrapOutMessage.setMessage( "需要删除的AttendanceStatisticalCycle信息不存在。id=" + id );
			}else{
				logger.debug("System trying to beginTransaction to delete attendanceStatisticalCycle......" );
				//进行数据库持久化操作
				emc.beginTransaction( AttendanceStatisticalCycle.class );
				emc.remove( attendanceStatisticalCycle, CheckRemoveType.all );
				emc.commit();
				logger.debug("System delete attendanceStatisticalCycle success......" );
				wrapOutMessage.setStatus("SUCCESS");
				wrapOutMessage.setMessage( "成功删除AttendanceStatisticalCycle信息。id=" + id );
			}			
		} catch ( Exception e ) {
			e.printStackTrace();
			wrapOutMessage.setStatus("ERROR");
			wrapOutMessage.setMessage( "删除AttendanceStatisticalCycle过程中发生异常。" );
			wrapOutMessage.setExceptionMessage( e.getMessage() );
		}
		result.setData( wrapOutMessage );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/* 查找在List<WrapIdentity>中是否有指定名称的身份。 */
	private WrapIdentity findIdentity(List<WrapIdentity> identities, String name) throws Exception {
		if( name != null && !name.isEmpty() ){
			for (WrapIdentity o : identities) {
				if (StringUtils.equals(o.getName(), name)) {
					return o;
				}
			}
		}else{
			return identities.get(0);
		}
		return null;
	}
}