package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
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

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;


@Path("attendancedetail/mobile")
public class AttendanceDetailMobileAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceDetailMobileAction.class );
	private AttendanceDetailServiceAdv attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
	private BeanCopyTools<AttendanceDetailMobile, WrapOutAttendanceDetailMobile> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceDetailMobile.class, WrapOutAttendanceDetailMobile.class, null, WrapOutAttendanceDetailMobile.Excludes);

	@HttpMethodDescribe(value = "根据ID获取AttendanceDetail对象.", response = WrapOutAttendanceDetailMobile.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutAttendanceDetailMobile> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapOutAttendanceDetailMobile wrap = null;
		AttendanceDetailMobile attendanceDetailMobile = null;
		Boolean check = true;
		
		if( check ){
			if( id == null ){
				check = false;
				Exception exception = new AttendanceDetailMobileIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){
			try {
				attendanceDetailMobile = attendanceDetailServiceAdv.getMobile( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailMobileQueryByIdException( e, id);
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){
			if( attendanceDetailMobile == null ){
				check = false;
				Exception exception = new AttendanceDetaillMobileNotExistsException( id);
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){
			try {
				wrap = wrapout_copier.copy( attendanceDetailMobile );
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailMobileWrapCopyException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "手机打卡信息明细查询.", request = WrapInAttendanceDetailMobileQuery.class, response = AttendanceDetailMobile.class)
	@Path("filter/list/page/{page}/count/{count}")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDataForMobile(@Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, WrapInAttendanceDetailMobileQuery wrapIn ) {
		ActionResult<List<WrapOutAttendanceDetailMobile>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutAttendanceDetailMobile> wraps = new ArrayList<>();
		List<WrapOutAttendanceDetailMobile> allResultWrap = null;
		List<AttendanceDetailMobile> attendanceDetailMobileList = null;
		Date datetime = null;
		DateOperation dateOperation = new DateOperation();
		Long total = 0L;
		Integer selectTotal = 0;
		Boolean check = true;
		Boolean queryConditionIsNull = true;
		
//		if( wrapIn == null ){
//			check = false;
//			result.error( new Exception("系统未获取到需要保存的数据！") );
//			result.setUserMessage( "系统未获取到需要保存的数据！" );
//		}
		if( check ){
			if( page == null ){
				page = 1;
			}
		}
		if( check ){
			if( count == null ){
				count = 20;
			}
		}
		if( page <= 0 ){
			page = 1;
		}
		if( count <= 0 ){
			count = 20;
		}
		if( check ){
			if( wrapIn.getEmpNo() != null && !wrapIn.getEmpNo().isEmpty() ){
				queryConditionIsNull = false;
			}
			if( wrapIn.getEmpName() != null && !wrapIn.getEmpName().isEmpty() ){
				queryConditionIsNull = false;
			}
			if( wrapIn.getStartDate() != null && !wrapIn.getStartDate().isEmpty() ){
				queryConditionIsNull = false;
				if( wrapIn.getEndDate() == null || wrapIn.getEndDate().isEmpty() ){
					wrapIn.setEndDate( wrapIn.getStartDate() );
				}
			}
			if( queryConditionIsNull ){
				check = false;
				Exception exception = new AttendanceDetailMobileQueryParameterEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getEndDate() != null && !wrapIn.getEndDate().isEmpty() ){
				try{
					datetime = dateOperation.getDateFromString( wrapIn.getEndDate() );
					wrapIn.setEndDate( dateOperation.getDateStringFromDate( datetime, "YYYY-MM-DD") ); //结束日期
				}catch( Exception e ){
					check = false;
					Exception exception = new AttendanceDetailMobileEndDateFormatException( e, wrapIn.getEndDate() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
				if( wrapIn.getEndDate() == null || wrapIn.getEndDate().isEmpty() ){
					wrapIn.setEndDate( wrapIn.getStartDate() );
				}
			}
			if( wrapIn.getStartDate() != null && !wrapIn.getStartDate().isEmpty() ){
				try{
					datetime = dateOperation.getDateFromString( wrapIn.getStartDate() );
					wrapIn.setStartDate( dateOperation.getDateStringFromDate( datetime, "YYYY-MM-DD") ); //开始日期
				}catch( Exception e ){
					check = false;
					Exception exception = new AttendanceDetailMobileStartDateFormatException( e, wrapIn.getEndDate() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		//查询的最大条目数
		selectTotal = page * count;
		if( check ){
			if( selectTotal > 0 ){
				try{
					total = attendanceDetailServiceAdv.countAttendanceDetailMobileForPage( wrapIn.getEmpNo(), wrapIn.getEmpName(), wrapIn.getSignDescription(), wrapIn.getStartDate(), wrapIn.getEndDate() );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailMobileCountException( e, wrapIn.getEmpNo(), wrapIn.getEmpName(), wrapIn.getSignDescription(), wrapIn.getStartDate(), wrapIn.getEndDate() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check ){
			if( selectTotal > 0 && total > 0 ){
				try{
					attendanceDetailMobileList = attendanceDetailServiceAdv.listAttendanceDetailMobileForPage( wrapIn.getEmpNo(), wrapIn.getEmpName(), wrapIn.getSignDescription(), wrapIn.getStartDate(), wrapIn.getEndDate(), selectTotal );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailMobileListByParameterException( e, wrapIn.getEmpNo(), wrapIn.getEmpName(), wrapIn.getSignDescription(), wrapIn.getStartDate(), wrapIn.getEndDate() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check ){
			if( attendanceDetailMobileList != null && !attendanceDetailMobileList.isEmpty() ){
				try {
					allResultWrap = wrapout_copier.copy( attendanceDetailMobileList );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailMobileWrapCopyException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check ){
			int startIndex = ( page - 1 ) * count;
			int endIndex = page * count;
			for( int i=0; allResultWrap != null && i< allResultWrap.size(); i++ ){
				if( i >= startIndex && i < endIndex ){
					wraps.add( allResultWrap.get( i ) );
				}
			}
		}
		result.setCount( total );
		result.setData( wraps );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 打卡信息接入，移动端特点，会多次接入，部分接入，一次接入的信息不完整
	 * 接入后只保存 ，由定时代理定期进行数据整合，入库并且 分析
	 * 1-员工姓名 EmployeeName	
	   2-员工号   EmployeeNo
	   3-日期	RecordDateString
	   4-打卡时间 SignTime
	   6-打卡位置 
	   7-打卡坐标
	 * @author liyi_
	 */
	@HttpMethodDescribe(value = "打卡信息接入，移动端特点，会多次接入，部分接入，一次接入的信息不完整，接入完成后不直接进行分析.", request = WrapInAttendanceDetailMobile.class, response = WrapOutId.class)
	@Path("recive")
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reciveForMobile(@Context HttpServletRequest request, WrapInAttendanceDetailMobile wrapIn ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrapOutId = null;
		Date datetime = null;
		DateOperation dateOperation = new DateOperation();
		EffectivePerson currentPerson = this.effectivePerson( request );
		AttendanceDetailMobile attendanceDetailMobile = new AttendanceDetailMobile();
		Boolean check = true;
		
//		if( wrapIn == null ){
//			check = false;
//			result.error( new Exception("系统未获取到需要保存的数据！") );
//			result.setUserMessage( "系统未获取到需要保存的数据！" );
//		}
		if( check ){
			if( wrapIn.getRecordAddress() == null || wrapIn.getRecordAddress().isEmpty() ){
				check = false;
				Exception exception = new AttendanceDetailMobileRecordAddressEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}else{
				attendanceDetailMobile.setRecordAddress( wrapIn.getRecordAddress() );
			}
		}
		if( check ){
			if( wrapIn.getLatitude() == null || wrapIn.getLatitude().isEmpty() ){
				check = false;
				Exception exception = new AttendanceDetailMobileLatitudeEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}else{
				attendanceDetailMobile.setLatitude( wrapIn.getLatitude() );
			}
		}
		if( check ){
			if( wrapIn.getLongitude() == null || wrapIn.getLongitude().isEmpty() ){
				check = false;
				Exception exception = new AttendanceDetailMobileLongitudeEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}else{
				attendanceDetailMobile.setLongitude( wrapIn.getLongitude() );
			}
		}
		if( check ){
			attendanceDetailMobile.setEmpName( currentPerson.getName() );
		}
		if( check ){
			if( wrapIn.getSignTime() != null && wrapIn.getSignTime().trim().length() > 0 ){
				try{
					datetime = dateOperation.getDateFromString( wrapIn.getSignTime() );
					attendanceDetailMobile.setSignTime( dateOperation.getDateStringFromDate( datetime, "HH:mm:ss") ); //打卡时间
				}catch( Exception e ){
					check = false;
					Exception exception = new AttendanceDetailMobileSignTimeFormatException( e, wrapIn.getSignTime() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}else{//打卡时间没有填写就填写为当前时间
				attendanceDetailMobile.setSignTime( dateOperation.getNowTime() ); //打卡时间
			}
		}
		if( check ){
			if( wrapIn.getRecordDateString() != null && !wrapIn.getRecordDateString().isEmpty() ){
				try{
					datetime = dateOperation.getDateFromString( wrapIn.getRecordDateString() );
					attendanceDetailMobile.setRecordDateString( dateOperation.getDateStringFromDate( datetime, "yyyy-MM-dd") ); //打卡时间
				}catch( Exception e ){
					check = false;
					Exception exception = new AttendanceDetailMobileRecordDateFormatException( e, wrapIn.getRecordDateString() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}				
			}else{
				attendanceDetailMobile.setRecordDateString( dateOperation.getNowDate() ); //打卡日期
			}
		}
		if( check ){
			if( wrapIn.getId() != null && !wrapIn.getId().isEmpty()){
				attendanceDetailMobile.setId( wrapIn.getId() );
			}
			attendanceDetailMobile.setSignDescription( wrapIn.getSignDescription() );
			try {
				attendanceDetailMobile = attendanceDetailServiceAdv.save( attendanceDetailMobile );
				wrapOutId = new WrapOutId( attendanceDetailMobile.getId() );
				result.setData( wrapOutId );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailMobileSaveException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceDetailMobile数据对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson( request );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			AttendanceDetailMobile attendanceDetailMobile = emc.find(id, AttendanceDetailMobile.class);
			if ( null == attendanceDetailMobile ) {
				Exception exception = new AttendanceDetaillMobileNotExistsException( id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}else{
				//进行数据库持久化操作				
				emc.beginTransaction( AttendanceDetailMobile.class );
				emc.remove( attendanceDetailMobile, CheckRemoveType.all );
				emc.commit();
				result.setData( new WrapOutId(id) );
				logger.info( "成功删除打卡数据信息。id=" + id );
			}			
		} catch ( Exception e ) {
			Exception exception = new AttendanceDetaillMobileNotExistsException( id );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}