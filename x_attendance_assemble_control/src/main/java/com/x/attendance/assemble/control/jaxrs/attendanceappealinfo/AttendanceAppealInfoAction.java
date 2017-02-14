package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.WrapOutMessage;
import com.x.attendance.assemble.control.service.AttendanceAppealInfoServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceNoticeService;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpAttribute;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapPerson;

@Path("attendanceappealInfo")
public class AttendanceAppealInfoAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( AttendanceAppealInfoAction.class );
	private BeanCopyTools<AttendanceAppealInfo, WrapOutAttendanceAppealInfo> wrapout_copier = BeanCopyToolsBuilder.create(AttendanceAppealInfo.class, WrapOutAttendanceAppealInfo.class, null, WrapOutAttendanceAppealInfo.Excludes);
	private AttendanceAppealInfoServiceAdv attendanceAppealInfoServiceAdv = new AttendanceAppealInfoServiceAdv();
	private AttendanceDetailServiceAdv attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
	private UserManagerService userManagerService = new UserManagerService();
	private AttendanceNoticeService attendanceNoticeService = new AttendanceNoticeService();
	
	@HttpMethodDescribe(value = "根据ID获取AttendanceAppealInfo对象.", response = WrapOutAttendanceAppealInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutAttendanceAppealInfo> result = new ActionResult<>();
		WrapOutAttendanceAppealInfo wrap = null;
		AttendanceAppealInfo attendanceAppealInfo = null;
        Boolean check = true;	
        if( check ){
        	if( id == null || id.isEmpty() || "(0)".equals( id )){
        		check = false;
        		result.error( new Exception("传入的id为空，或者不合法，无法查询数据。") );
        	}
        }
        if( check ){
        	try {
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据Id查询申诉信息时发生异常！" );
				logger.error( "system get appeal info by id got an exception.", e );
			}
        }
        if( check ){
        	if( attendanceAppealInfo != null ){
        		try {
					wrap = wrapout_copier.copy( attendanceAppealInfo );
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统转换申诉对象为输出格式时发生异常！" );
					logger.error( "system copy appeal info to wrap out got an exception.", e );
				}
        	}
        }
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除AttendanceAppealInfo申诉信息对象.", response = WrapOutMessage.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		Boolean check = true;
		
        if( check ){
        	if( id == null || id.isEmpty() || "(0)".equals( id )){
        		check = false;
        		result.error( new Exception("传入的id为空，或者不合法，无法查询数据。") );
        	}
        }		
		if( check ){
			try {
				attendanceAppealInfoServiceAdv.delete( id );
				result.setUserMessage( "成功删除申诉信息信息。id=" + id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "删除申诉信息过程中发生异常！" );
				logger.error( "system delete appeal info by id got an exception.", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	/**
	 * 对某条打卡记录进行申诉
	 * 
	 * @param request
	 * @param id
	 * @param wrapIn
	 * @return
	 */
	@HttpMethodDescribe(value = "对某条打卡记录进行申诉", request = WrapInAttendanceAppealInfo.class, response = WrapOutMessage.class)
	@PUT
	@Path("appeal/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response appealAttendanceDetail(@Context HttpServletRequest request, @PathParam("id") String id, WrapInAttendanceAppealInfo wrapIn) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		WrapOutMessage wrapOutMessage = new WrapOutMessage();
		DateOperation dateOperation = new DateOperation();
		AttendanceAppealInfo attendanceAppealInfo = null;
		AttendanceDetail attendanceDetail = null;
		WrapDepartment department = null;
		WrapPerson person = null;
		Boolean check = true;
		
		if( check ){
			try {
				attendanceDetail = attendanceDetailServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				logger.error("system get attendance detail with id got an exception.", e );
				result.error( e );
				result.setUserMessage( "系统根据ID查询打卡明细时发生异常。" );
			}
		}		
		if( check ){
			if (attendanceDetail != null) {
				//利用打卡记录中的信息，创建一个申诉信息记录
				//申诉状态:0-未申诉，1-申诉中，-1-申诉未通过，9-申诉通过!
				attendanceAppealInfo = new AttendanceAppealInfo();
				attendanceAppealInfo.setId(id);
				attendanceAppealInfo.setDetailId(id);
				attendanceAppealInfo.setRecordDate( attendanceDetail.getRecordDate() );
				attendanceAppealInfo.setRecordDateString( attendanceDetail.getRecordDateString() );
				attendanceAppealInfo.setYearString( attendanceDetail.getCycleYear() );
				attendanceAppealInfo.setMonthString( attendanceDetail.getCycleMonth() );
				attendanceAppealInfo.setEmpName( attendanceDetail.getEmpName() );
				attendanceAppealInfo.setDepartmentName( attendanceDetail.getDepartmentName() );
				attendanceAppealInfo.setCompanyName( attendanceDetail.getCompanyName() );
				attendanceAppealInfo.setAppealDescription( wrapIn.getAppealDescription() );
				attendanceAppealInfo.setAppealReason( wrapIn.getAppealReason() );
				attendanceAppealInfo.setReason( wrapIn.getReason() );
				attendanceAppealInfo.setAddress( wrapIn.getAddress() );
				attendanceAppealInfo.setSelfHolidayType( wrapIn.getSelfHolidayType() );
				attendanceAppealInfo.setAppealDateString( dateOperation.getNowDateTime() );
				attendanceAppealInfo.setProcessPerson1( wrapIn.getProcessPerson1() );
				attendanceAppealInfo.setProcessPerson2( wrapIn.getProcessPerson2() );
				attendanceAppealInfo.setStartTime( wrapIn.getStartTime() );
				attendanceAppealInfo.setEndTime( wrapIn.getEndTime() );	
				// 将第一个处理人设置为当前处理人
				attendanceAppealInfo.setCurrentProcessor( attendanceAppealInfo.getProcessPerson1() );
			}else{// 打卡记录不存在
				check = false;
				result.error( new Exception("打卡信息不存在，无法继续进行申诉") );
				result.setUserMessage( "打卡信息不存在，无法继续进行申诉！" );
				logger.error( "attendanceDetailInfo{'id':'" + id + "'} not exists, system can not appeal attendance detail." );
			}
		}
		if( check ){
			if( attendanceAppealInfo != null ){
				//wrapIn.getProcessPerson1() 有可能是姓名，有可能是身份，如果是身份，那么直接根据身份来确定部门
				//如果是人员姓名，那么需要取根据姓名查询到的第一个部门的信息
				if (wrapIn.getProcessPerson1() != null && !wrapIn.getProcessPerson1().isEmpty()) {
					//先根据人员姓名获取一次，如果传入的是身份，则无法获取到部门信息，也可能发生异常
					try {
						department = userManagerService.getDepartmentByEmployeeName( wrapIn.getProcessPerson1() );
						if( department != null ){
							attendanceAppealInfo.setProcessPersonDepartment1( department.getName() );
							attendanceAppealInfo.setProcessPersonCompany1( department.getCompany() );
						}
					} catch (Exception e) {
						department = null;
					}
					if( department == null ){//再根据人员身份获取一次
						try {
							department = userManagerService.getDepartmentByIdentity( wrapIn.getProcessPerson1() );
							if( department != null ){
								person = userManagerService.getPersonByIdentity( wrapIn.getProcessPerson1() );
								if( person != null ){
									attendanceAppealInfo.setProcessPerson1( person.getName() );
									attendanceAppealInfo.setProcessPersonDepartment1( department.getName() );
									attendanceAppealInfo.setProcessPersonCompany1( department.getCompany() );
								}else{
									check = false;
									result.error( new Exception("系统无法根据员工身份查询到审核人信息!身份：" + wrapIn.getProcessPerson1() ) );
									result.setUserMessage( "系统无法根据员工身份查询到审核人信息!" );
									logger.error("system can not find any person by employee identity for processor1:" + wrapIn.getProcessPerson1() );
								}
							}else{
								check = false;
								result.error( new Exception("系统无法根据员工姓名以及身份查询到部门信息，请检查该审核人的部门信息!KEY："+ wrapIn.getProcessPerson1() ) );
								result.setUserMessage( "系统无法根据员工姓名以及身份查询到部门信息，请检查该审核人的部门信息!" );
								logger.error("system can not find any department by employee name for processor1:" + wrapIn.getProcessPerson1() );
							}
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "系统根据员工姓名查询部门信息时发生异常!" );
							logger.error("system query department by employee name got an exception", e );
						}
					}
				}
			}
		}
		if( check ){
			if( attendanceAppealInfo != null ){
				//wrapIn.getProcessPerson2() 有可能是姓名，有可能是身份，如果是身份，那么直接根据身份来确定部门
				//如果是人员姓名，那么需要取根据姓名查询到的第一个部门的信息
				if ( wrapIn.getProcessPerson2() != null && !wrapIn.getProcessPerson2().isEmpty() ) {
					//先根据人员姓名获取一次，如果传入的是身份，则无法获取到部门信息，也可能发生异常
					try {
						department = userManagerService.getDepartmentByEmployeeName( wrapIn.getProcessPerson2() );
						if( department != null ){
							attendanceAppealInfo.setProcessPersonDepartment2( department.getName() );
							attendanceAppealInfo.setProcessPersonCompany2( department.getCompany() );
						}
					} catch (Exception e) {
						department = null;
					}
					if( department == null ){//再根据人员身份获取一次
						try {
							department = userManagerService.getDepartmentByIdentity( wrapIn.getProcessPerson2() );
							if( department != null ){
								person = userManagerService.getPersonByIdentity( wrapIn.getProcessPerson2() );
								if( person != null ){
									attendanceAppealInfo.setProcessPerson2( person.getName() );
									attendanceAppealInfo.setProcessPersonDepartment2( department.getName() );
									attendanceAppealInfo.setProcessPersonCompany2( department.getCompany() );
								}else{
									check = false;
									result.error( new Exception("系统无法根据员工身份查询到人员信息!" ) );
									result.setUserMessage( "系统无法根据员工身份查询到人员信息!" );
									logger.error("system can not find any department by employee identity for processor2:" + wrapIn.getProcessPerson2() );
								}
							}else{
								check = false;
								result.error( new Exception("系统无法根据员工姓名查询到部门信息，请检查该员工的部门信息!" ) );
								result.setUserMessage( "系统无法根据员工姓名查询到部门信息，请检查该员工的部门信息!" );
								logger.error("system can not find any department by employee name for processor2:" + wrapIn.getProcessPerson2() );
							}
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "系统根据员工姓名查询部门信息时发生异常!" );
							logger.error("system query department by employee name got an exception", e );
						}
					}
				}	
			}
		}
		if( check ){
			try {
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.saveNewAppeal( attendanceAppealInfo );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在保存申诉信息时发生异常!" );
				logger.error("system save appeal info got an exception", e );
			}
		}
		if( check ){
			//填充申诉信息内容 - 申诉信息成功生成.尝试向当前处理人[" + wrapIn.getProcessPerson1() + "]发送消息通知......
			try {
				attendanceNoticeService.notifyAttendanceAppealProcessness1Message( attendanceAppealInfo);
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "申诉信息提交成功，向申诉当前处理人发送通知消息发生异常!" );
				logger.error("system send notice to current processor got an exception.name:" + wrapIn.getProcessPerson1(), e );
			}
		}
		result.setData( wrapOutMessage );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "审核人处理申诉记录", request = WrapInAttendanceAppealInfo.class, response = WrapOutMessage.class)
	@PUT
	@Path("process/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response firstProcessAttendanceAppeal( @Context HttpServletRequest request, @PathParam("id") String id,
			WrapInAttendanceAppealInfo wrapIn ) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson( request );
		WrapDepartment department = null;
		AttendanceAppealInfo attendanceAppealInfo = null;
		String departmentName = null, companyName = null;
		Boolean check = true;
		
		if( check ){
			try{
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.get( id );
				if( attendanceAppealInfo == null ){
					check = false;
					result.error( new Exception( "申诉信息不存在，申诉处理不成功!" ) );
					result.setUserMessage( "申诉信息不存在，申诉处理不成功!" );
					logger.error("attendanceAppealInfo{'id':'" + id + "'} not exists, system can not process appeal info." );
				}
			} catch ( Exception e ) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询申诉信息对象时发生异常!" );
				logger.error("system get appeal info with id got an exception.id:" + id, e );
			}
		}
		if( check ){
			try{
				department = userManagerService.getDepartmentByEmployeeName( currentPerson.getName() );
				if (department != null) {
					departmentName = department.getName();
					companyName = department.getCompany();
				}else{
					check = false;
					result.error( new Exception( "抱歉，未能在系统中查询到您所在的部门信息，申诉信息暂时无法处理，请联系管理员。") );
					result.setUserMessage( "抱歉，未能在系统中查询到您所在的部门信息，申诉信息暂时无法处理，请联系管理员。" );
				}
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据登录用户姓名查询用户所在的部门时发生异常!" );
				logger.error("system get department with user name got an exception.id:" + id, e );
			}
		}
		if( check ){
			try{
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.firstProcessAttendanceAppeal(
						id, departmentName, companyName, currentPerson.getName(), //processorName
						new Date(), //processTime
						wrapIn.getOpinion1(),  //opinion
						wrapIn.getStatus() //status审批状态:0-待处理，1-审批通过，-1-审批不能过，2-需要下一次审批
				);
				result.setUserMessage( "申诉信息审核处理成功!" );
			} catch ( Exception e ) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在更新申诉处理信息对象时发生异常!" );
				logger.error("system update appeal first process info got an exception.id:" + id, e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "复核人处理申诉记录", request = WrapInAttendanceAppealInfo.class, response = WrapOutMessage.class)
	@PUT
	@Path("process2/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response secondProcessAttendanceAppeal( @Context HttpServletRequest request, @PathParam("id") String id,
			WrapInAttendanceAppealInfo wrapIn) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapDepartment department = null;
		AttendanceAppealInfo attendanceAppealInfo = null;
		String departmentName = null, companyName = null;
		Boolean check = true;
		
		if( check ){
			try{
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.get( id );
				if( attendanceAppealInfo == null ){
					check = false;
					result.error( new Exception( "申诉信息不存在，申诉处理不成功!" ) );
					result.setUserMessage( "申诉信息不存在，申诉处理不成功!" );
					logger.error("attendanceAppealInfo{'id':'" + id + "'} not exists, system can not process appeal info." );
				}
			} catch ( Exception e ) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询申诉信息对象时发生异常!" );
				logger.error("system get appeal info with id got an exception.id:" + id, e );
			}
		}
		if( check ){
			try{
				department = userManagerService.getDepartmentByEmployeeName( currentPerson.getName() );
				if (department != null) {
					departmentName = department.getName();
					companyName = department.getCompany();
				}else{
					check = false;
					result.error( new Exception( "抱歉，未能在系统中查询到您所在的部门信息，申诉信息暂时无法处理，请联系管理员。") );
					result.setUserMessage( "抱歉，未能在系统中查询到您所在的部门信息，申诉信息暂时无法处理，请联系管理员。" );
				}
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据登录用户姓名查询用户所在的部门时发生异常!" );
				logger.error("system get department with user name got an exception.id:" + id, e );
			}
		}
		if( check ){
			try{
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.secondProcessAttendanceAppeal(
						id, departmentName, companyName, currentPerson.getName(), //processorName
						new Date(), //processTime
						wrapIn.getOpinion2(),  //opinion
						wrapIn.getStatus() //status
				);
				result.setUserMessage( "申诉信息复核处理成功!" );
			} catch ( Exception e ) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在更新申诉处理信息对象时发生异常!" );
				logger.error("system update appeal first process info got an exception.id:" + id, e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的AttendanceAppealInfo,下一页.", response = WrapOutAttendanceAppealInfo.class, request = WrapInFilterAppeal.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, WrapInFilterAppeal wrapIn) {
		ActionResult<List<WrapOutAttendanceAppealInfo>> result = new ActionResult<>();
		List<WrapOutAttendanceAppealInfo> wraps = null;
		Long total = 0L;
		List<AttendanceAppealInfo> detailList = null;
		
		try {
			EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
			Business business = new Business(emc);

			// 查询出ID对应的记录的sequence
			Object sequence = null;
			if (id == null || "(0)".equals(id) || id.isEmpty()) {
				logger.debug("第一页查询，没有id传入");
			} else {
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty( emc.find(id, AttendanceAppealInfo.class, ExceptionWhen.not_found), "sequence");
				}
			}
			// 从数据库中查询符合条件的一页数据对象
			detailList = business.getAttendanceAppealInfoFactory().listIdsNextWithFilter(id, count, sequence, wrapIn);
			// 从数据库中查询符合条件的对象总数
			total = business.getAttendanceAppealInfoFactory().getCountWithFilter(wrapIn);
			// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = wrapout_copier.copy(detailList);

			// 对查询的列表进行排序
			result.setCount(total);
			result.setData(wraps);

		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的AttendanceAppealInfo,上一页.", response = WrapOutAttendanceAppealInfo.class, request = WrapInFilterAppeal.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, WrapInFilterAppeal wrapIn) {
		ActionResult<List<WrapOutAttendanceAppealInfo>> result = new ActionResult<>();
		List<WrapOutAttendanceAppealInfo> wraps = null;
		Long total = 0L;
		List<AttendanceAppealInfo> detailList = null;
		try {
			EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
			Business business = new Business(emc);

			// 查询出ID对应的记录的sequence
			Object sequence = null;
			logger.debug("传入的ID=" + id);
			if (id == null || "(0)".equals(id) || id.isEmpty()) {
				logger.debug("第一页查询，没有id传入");
			} else {
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils
							.getProperty(emc.find(id, AttendanceAppealInfo.class, ExceptionWhen.not_found), "sequence");
				}
			}
			// 从数据库中查询符合条件的一页数据对象
			detailList = business.getAttendanceAppealInfoFactory().listIdsPrevWithFilter(id, count, sequence, wrapIn);
			// 从数据库中查询符合条件的对象总数
			total = business.getAttendanceAppealInfoFactory().getCountWithFilter(wrapIn);
			// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = wrapout_copier.copy(detailList);
			result.setCount(total);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "将指定的申诉信息记录归档", response = WrapOutMessage.class)
	@GET
	@Path("archive/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response archiveAttendanceAppeal(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		
		if ( id != null && !id.isEmpty() ) { //归档指定的考勤申诉记录
			try{
				attendanceAppealInfoServiceAdv.archive( id );
				result.setUserMessage( "对指定申诉信息进行归档操作成功完成！" );
			}catch( Exception e ){
				result.error( e );
				result.setUserMessage( "系统在根据ID对申诉信息进行归档操作时发生异常!" );
				logger.error("system archive appeal info with id{'"+ id +"'} got an exception.id:" + id, e );
			}
		}else{ //归档所有的考勤申诉记录
			try{
				attendanceAppealInfoServiceAdv.archiveAll();
				result.setUserMessage( "对所有申诉信息进行归档操作成功完成！" );
			}catch( Exception e ){
				result.error( e );
				result.setUserMessage( "系统在对所有申诉信息进行归档操作时发生异常!" );
				logger.error("system archive all appeal info got an exception.id:" + id, e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}