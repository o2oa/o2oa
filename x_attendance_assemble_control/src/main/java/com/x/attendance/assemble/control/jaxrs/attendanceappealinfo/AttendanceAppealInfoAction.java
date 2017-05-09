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

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.service.AttendanceAppealInfoServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceNoticeService;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
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
		EffectivePerson effectivePerson = this.effectivePerson(request);
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
				Exception exception = new AttendanceAppealQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
        }
        if( check ){
        	if( attendanceAppealInfo != null ){
        		try {
					wrap = wrapout_copier.copy( attendanceAppealInfo );
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceAppealWrapCopyException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
        	}
        }
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除AttendanceAppealInfo申诉信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
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
				result.setData( new WrapOutId(id) );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceAppealDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
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
	@HttpMethodDescribe(value = "对某条打卡记录进行申诉", request = JsonElement.class, response = WrapOutId.class)
	@PUT
	@Path("appeal/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response appealAttendanceDetail(@Context HttpServletRequest request, @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		DateOperation dateOperation = new DateOperation();
		AttendanceAppealInfo attendanceAppealInfo = null;
		AttendanceDetail attendanceDetail = null;
		WrapDepartment department = null;
		WrapPerson person = null;
		WrapInAttendanceAppealInfo wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceAppealInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try {
				attendanceDetail = attendanceDetailServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
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
				Exception exception = new AttendanceDetailNotExistsException( id );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
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
									Exception exception = new PersonHasNoIdenitityException( wrapIn.getProcessPerson1() );
									result.error( exception );
								//	logger.error( e, effectivePerson, request, null);
								}
							}else{
								check = false;
								Exception exception = new PersonHasNoDepartmentException( wrapIn.getProcessPerson1() );
								result.error( exception );
							//	logger.error( e, effectivePerson, request, null);
							}
						} catch ( Exception e) {
							check = false;
							Exception exception = new QeuryDepartmentWithPersonException( e, wrapIn.getProcessPerson1() );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
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
									Exception exception = new PersonHasNoIdenitityException( wrapIn.getProcessPerson2() );
									result.error( exception );
								//	logger.error( e, effectivePerson, request, null);
								}
							}else{
								check = false;
								Exception exception = new PersonHasNoDepartmentException( wrapIn.getProcessPerson2() );
								result.error( exception );
								//logger.error( e, effectivePerson, request, null);
							}
						} catch (Exception e) {
							check = false;
							Exception exception = new QeuryDepartmentWithPersonException( e, wrapIn.getProcessPerson2() );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
				}	
			}
		}
		if( check ){
			try {
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.saveNewAppeal( attendanceAppealInfo );
				result.setData( new WrapOutId( id ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceAppealSaveException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			//填充申诉信息内容 - 申诉信息成功生成.尝试向当前处理人[" + wrapIn.getProcessPerson1() + "]发送消息通知......
			try {
				attendanceNoticeService.notifyAttendanceAppealProcessness1Message( attendanceAppealInfo);
			} catch (Exception e) {
				check = false;
				Exception exception = new NotifyAttendanceAppealException( e, attendanceAppealInfo.getProcessPerson1() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "审核人处理申诉记录", request = JsonElement.class, response = WrapOutId.class)
	@PUT
	@Path("process/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response firstProcessAttendanceAppeal( @Context HttpServletRequest request, @PathParam("id") String id,
			JsonElement jsonElement ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapDepartment department = null;
		AttendanceAppealInfo attendanceAppealInfo = null;
		String departmentName = null, companyName = null;
		WrapInAttendanceAppealInfo wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceAppealInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			try{
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.get( id );
				if( attendanceAppealInfo == null ){
					check = false;
					Exception exception = new AttendanceAppealNotExistsException( id );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new AttendanceAppealQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try{
				department = userManagerService.getDepartmentByEmployeeName( effectivePerson.getName() );
				if (department != null) {
					departmentName = department.getName();
					companyName = department.getCompany();
				}else{
					check = false;
					Exception exception = new PersonHasNoDepartmentException( effectivePerson.getName() );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new QeuryDepartmentWithPersonException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try{
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.firstProcessAttendanceAppeal(
						id, departmentName, companyName, effectivePerson.getName(), //processorName
						new Date(), //processTime
						wrapIn.getOpinion1(),  //opinion
						wrapIn.getStatus() //status审批状态:0-待处理，1-审批通过，-1-审批不能过，2-需要下一次审批
				);
				result.setData( new WrapOutId(id) );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new AttendanceAppealProcessException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "复核人处理申诉记录", request = JsonElement.class, response = WrapOutId.class)
	@PUT
	@Path("process2/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response secondProcessAttendanceAppeal( @Context HttpServletRequest request, @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapDepartment department = null;
		AttendanceAppealInfo attendanceAppealInfo = null;
		String departmentName = null, companyName = null;
		WrapInAttendanceAppealInfo wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceAppealInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		
		if( check ){
			try{
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.get( id );
				if( attendanceAppealInfo == null ){
					check = false;
					Exception exception = new AttendanceAppealNotExistsException( id );
					result.error( exception );
					//logger.error( e, currentPerson, request, null);
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new AttendanceAppealQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
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
					Exception exception = new PersonHasNoDepartmentException( currentPerson.getName() );
					result.error( exception );
					//logger.error( e, currentPerson, request, null);
				}
			}catch( Exception e ){
				check = false;
				result.error( e );
				Exception exception = new QeuryDepartmentWithPersonException( e, currentPerson.getName() );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
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
				result.setData( new WrapOutId(id) );
			} catch ( Exception e ) {
				check = false;
				result.error( e );
				Exception exception = new AttendanceAppealProcessException( e, id );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的AttendanceAppealInfo,下一页.", response = WrapOutAttendanceAppealInfo.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceAppealInfo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutAttendanceAppealInfo> wraps = null;
		Long total = 0L;
		List<AttendanceAppealInfo> detailList = null;
		WrapInFilterAppeal wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterAppeal.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try {
				EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Business business = new Business(emc);

				// 查询出ID对应的记录的sequence
				Object sequence = null;
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					logger.debug("第一页查询，没有id传入");
				} else {
					if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
						sequence = PropertyUtils.getProperty( emc.find(id, AttendanceAppealInfo.class ), "sequence");
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
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的AttendanceAppealInfo,上一页.", response = WrapOutAttendanceAppealInfo.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceAppealInfo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutAttendanceAppealInfo> wraps = null;
		Long total = 0L;
		List<AttendanceAppealInfo> detailList = null;
		WrapInFilterAppeal wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterAppeal.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try {
				EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Business business = new Business(emc);

				// 查询出ID对应的记录的sequence
				Object sequence = null;

				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					logger.debug("第一页查询，没有id传入");
				} else {
					if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
						sequence = PropertyUtils
								.getProperty(emc.find(id, AttendanceAppealInfo.class ), "sequence");
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
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "将指定的申诉信息记录归档", response = WrapOutId.class)
	@GET
	@Path("archive/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response archiveAttendanceAppeal(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		if ( id != null && !id.isEmpty() ) { //归档指定的考勤申诉记录
			try{
				attendanceAppealInfoServiceAdv.archive( id );
				result.setData( new WrapOutId(id) );
			}catch( Exception e ){
				result.error( e );
				Exception exception = new AttendanceAppealArchiveException( e, id );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}else{ //归档所有的考勤申诉记录
			try{
				attendanceAppealInfoServiceAdv.archiveAll();
			}catch( Exception e ){
				Exception exception = new AttendanceAppealArchiveException( e, null );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}