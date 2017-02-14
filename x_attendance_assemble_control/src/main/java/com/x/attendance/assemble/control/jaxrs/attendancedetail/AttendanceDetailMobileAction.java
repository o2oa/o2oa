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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.jaxrs.WrapOutMessage;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.SortTools;


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
		WrapOutAttendanceDetailMobile wrap = null;
		AttendanceDetailMobile attendanceDetailMobile = null;
		Boolean check = true;
		
		if( check ){
			if( id == null ){
				check = false;
				result.error( new Exception("需要查询的打卡详细记录ID为空，无法进行数据查询。") );
				result.setUserMessage( "需要查询的打卡详细记录ID为空，无法进行数据查询。" );
			}
		}		
		if( check ){
			try {
				attendanceDetailMobile = attendanceDetailServiceAdv.getMobile( id );
			} catch (Exception e) {
				check = false;
				result.error( new Exception("系统在根据用户传入的ID查询打卡详细信息记录时发生异常。") );
				result.setUserMessage( "系统在根据用户传入的ID查询打卡详细信息记录时发生异常。" );
				logger.error( "system get attendance detail Mobile info with id:"+ id +" got an exception.", e );
			}
		}		
		if( check ){
			if( attendanceDetailMobile == null ){
				check = false;
				result.error( new Exception("系统在根据用户传入的ID未能查询到任何打卡详细信息记录常。" ) );
				result.setUserMessage( "系统在根据用户传入的ID未能查询到任何打卡详细信息记录常。" );
			}
		}		
		if( check ){
			try {
				wrap = wrapout_copier.copy( attendanceDetailMobile );
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				result.error( new Exception("系统在转换数据库对象attendanceDetailMobile为输出对象时发生异常。") );
				result.setUserMessage( "系统在转换数据库对象为输出对象时发生异常。" );
				logger.error( "system copy attendanceDetailMobile to wrap got an exception.", e );
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
		List<WrapOutAttendanceDetailMobile> wraps = new ArrayList<>();
		List<WrapOutAttendanceDetailMobile> allResultWrap = null;
		List<AttendanceDetailMobile> attendanceDetailMobileList = null;
		Date datetime = null;
		DateOperation dateOperation = new DateOperation();
		Long total = 0L;
		Integer selectTotal = 0;
		Boolean check = true;
		Boolean queryConditionIsNull = true;
		
		if( wrapIn == null ){
			check = false;
			result.error( new Exception("系统未获取到需要保存的数据！") );
			result.setUserMessage( "系统未获取到需要保存的数据！" );
		}
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
				result.error( new Exception("员工号，员工姓名和查询日期不能全部为空！") );
				result.setUserMessage( "员工号，员工姓名和查询日期不能全部为空！" );
			}
		}
		if( check ){
			if( wrapIn.getEndDate() != null && !wrapIn.getEndDate().isEmpty() ){
				try{
					datetime = dateOperation.getDateFromString( wrapIn.getEndDate() );
					wrapIn.setEndDate( dateOperation.getDateStringFromDate( datetime, "YYYY-MM-DD") ); //结束日期
				}catch( Exception e ){
					check = false;
					result.error( new Exception("结束日期格式异常，日期：" + wrapIn.getEndDate() ) );
					result.setUserMessage( "结束日期格式异常，日期：" + wrapIn.getEndDate() );
					logger.error("end date string error:" + wrapIn.getEndDate(), e);
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
					result.error( new Exception("开始日期格式异常，日期：" + wrapIn.getStartDate() ) );
					result.setUserMessage( "开始日期格式异常，日期：" + wrapIn.getStartDate() );
					logger.error("start date string error:" + wrapIn.getStartDate(), e);
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
					result.error( e );
					result.setUserMessage( "根据ID信息查询版块信息时发生异常！" );
					logger.error( "system query all top subject info with section info got an exceptin.", e );
				}
			}
		}
		if( check ){
			if( selectTotal > 0 && total > 0 ){
				try{
					attendanceDetailMobileList = attendanceDetailServiceAdv.listAttendanceDetailMobileForPage( wrapIn.getEmpNo(), wrapIn.getEmpName(), wrapIn.getSignDescription(), wrapIn.getStartDate(), wrapIn.getEndDate(), selectTotal );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "根据ID信息查询版块信息时发生异常！" );
					logger.error( "system query all top subject info with section info got an exceptin.", e );
				}
			}
		}
		if( check ){
			if( attendanceDetailMobileList != null && !attendanceDetailMobileList.isEmpty() ){
				try {
					allResultWrap = wrapout_copier.copy( attendanceDetailMobileList );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统将列表转换为输出格式时发生异常！" );
					logger.error( "system copy list to wraps got an exceptin.", e );
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
		AttendanceDetailMobile attendanceDetailMobile = new AttendanceDetailMobile();
		Boolean check = true;
		
		if( wrapIn == null ){
			check = false;
			result.error( new Exception("系统未获取到需要保存的数据！") );
			result.setUserMessage( "系统未获取到需要保存的数据！" );
		}
		if( check ){
			if( wrapIn.getRecordAddress() == null || wrapIn.getRecordAddress().isEmpty() ){
				check = false;
				result.error( new Exception("打卡信息中打卡地址描述 不能为空！") );
				result.setUserMessage( "打卡信息中打卡地址描述 不能为空！" );
			}else{
				attendanceDetailMobile.setRecordAddress( wrapIn.getRecordAddress() );
			}
		}
		if( check ){
			if( wrapIn.getLatitude() == null || wrapIn.getLatitude().isEmpty() ){
				check = false;
				result.error( new Exception("打卡信息中打卡地址纬度信息不能为空！") );
				result.setUserMessage( "打卡信息中打卡地址纬度信息不能为空！" );
			}else{
				attendanceDetailMobile.setLatitude( wrapIn.getLatitude() );
			}
		}
		if( check ){
			if( wrapIn.getLongitude() == null || wrapIn.getLongitude().isEmpty() ){
				check = false;
				result.error( new Exception("打卡信息中打卡地址经度信息不能为空！") );
				result.setUserMessage( "打卡信息中打卡地址经度信息不能为空！" );
			}else{
				attendanceDetailMobile.setLongitude( wrapIn.getLongitude() );
			}
		}
		if( check ){
			if( wrapIn.getRecordDateString() == null || wrapIn.getRecordDateString().isEmpty() ){
				check = false;
				result.error( new Exception("打卡信息中打卡日期不能为空，格式: yyyy-mm-dd！") );
				result.setUserMessage( "打卡信息中打卡日期不能为空，格式: yyyy-mm-dd！" );
			}else{
				attendanceDetailMobile.setRecordDateString( wrapIn.getRecordDateString() );
			}
		}
		if( check ){
			if( wrapIn.getEmpName() == null || wrapIn.getEmpName().isEmpty() ){
				check = false;
				result.error( new Exception("打卡信息中打卡员工姓名不能为空！") );
				result.setUserMessage( "打卡信息中打卡员工姓名不能为空！" );
			}else{
				attendanceDetailMobile.setEmpName( wrapIn.getEmpName() );
			}
		}
		if( check ){
			try{
				datetime = dateOperation.getDateFromString( wrapIn.getRecordDateString() );
				attendanceDetailMobile.setRecordDate( datetime );
				attendanceDetailMobile.setRecordDateString( dateOperation.getDateStringFromDate( datetime, "YYYY-MM-DD") );
			}catch( Exception e ){
				check = false;
				result.error( new Exception("打卡日期格式异常，时间：" + wrapIn.getRecordDateString() ) );
				result.setUserMessage( "打卡日期格式异常，时间：" + wrapIn.getRecordDateString() );
				logger.error("record date string error:" + wrapIn.getRecordDateString(), e);
			}
		}
		if( check ){
			if( wrapIn.getSignTime() != null && wrapIn.getSignTime().trim().length() > 0 ){
				try{
					datetime = dateOperation.getDateFromString( wrapIn.getSignTime() );
					attendanceDetailMobile.setSignTime( dateOperation.getDateStringFromDate( datetime, "HH:mm:ss") ); //打卡时间
				}catch( Exception e ){
					check = false;
					result.error( new Exception("打卡时间格式异常，时间：" + wrapIn.getSignTime() ) );
					result.setUserMessage( "打卡时间格式异常，时间：" + wrapIn.getSignTime() );
					logger.error("sign time string error:" + wrapIn.getSignTime(), e);
				}
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
				result.error( new Exception("系统在保存打卡数据信息时发生异常。" ) );
				result.setUserMessage( "系统在保存打卡数据信息时发生异常。" );
				logger.error("system save attendanceDetailMobile got an exception.", e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceDetailMobile数据对象.", response = WrapOutMessage.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		WrapOutMessage wrapOutMessage = new WrapOutMessage();
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			AttendanceDetailMobile attendanceDetailMobile = emc.find(id, AttendanceDetailMobile.class);
			if (null == attendanceDetailMobile) {
				wrapOutMessage.setStatus("ERROR");
				wrapOutMessage.setMessage( "需要删除的打卡数据信息不存在。id=" + id );
			}else{
				//进行数据库持久化操作				
				emc.beginTransaction( AttendanceDetailMobile.class );
				emc.remove( attendanceDetailMobile, CheckRemoveType.all );
				emc.commit();			
				wrapOutMessage.setStatus("SUCCESS");
				wrapOutMessage.setMessage( "成功删除打卡数据信息。id=" + id );
			}			
		} catch ( Exception e ) {
			e.printStackTrace();
			wrapOutMessage.setStatus("ERROR");
			wrapOutMessage.setMessage( "删除打卡数据过程中发生异常。" );
			wrapOutMessage.setExceptionMessage( e.getMessage() );
		}
		result.setData( wrapOutMessage );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}