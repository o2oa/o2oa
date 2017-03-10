package com.x.attendance.assemble.control.jaxrs.workplace;

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
import com.x.attendance.assemble.control.service.AttendanceWorkPlaceServiceAdv;
import com.x.attendance.entity.AttendanceWorkPlace;
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


@Path("workplace")
public class AttendanceWorkPlaceAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceWorkPlaceAction.class );
	private BeanCopyTools<WrapInAttendanceWorkPlace, AttendanceWorkPlace> wrapin_copier = BeanCopyToolsBuilder.create( WrapInAttendanceWorkPlace.class, AttendanceWorkPlace.class, null, WrapInAttendanceWorkPlace.Excludes );
	private BeanCopyTools<AttendanceWorkPlace, WrapOutAttendanceWorkPlace> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceWorkPlace.class, WrapOutAttendanceWorkPlace.class, null, WrapOutAttendanceWorkPlace.Excludes);
	private AttendanceWorkPlaceServiceAdv attendanceWorkPlaceServiceAdv = new AttendanceWorkPlaceServiceAdv();
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceWorkPlace对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		AttendanceWorkPlace attendanceWorkPlace = null;
		WrapInAttendanceWorkPlace wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceWorkPlace.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		if( check ){
			if( wrapIn.getErrorRange() == null || wrapIn.getPlaceName().isEmpty() ){
				wrapIn.setErrorRange( 200 );
			}
		}
		if( check ){
			if( wrapIn.getPlaceName() == null || wrapIn.getPlaceName().isEmpty() ){
				check = false;
				Exception exception = new AttendanceWorkPlaceNameEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getLatitude() == null || wrapIn.getLatitude().isEmpty() ){
				check = false;
				Exception exception = new AttendanceWorkPlaceLatitudeEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getLongitude() == null || wrapIn.getLongitude().isEmpty() ){
				check = false;
				Exception exception = new AttendanceWorkPlaceLongitudeEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getPlaceAlias() == null || wrapIn.getPlaceAlias().isEmpty() ){
				wrapIn.setPlaceAlias( wrapIn.getPlaceName() );
			}
		}
		if( check ){
			try {
				attendanceWorkPlace = new AttendanceWorkPlace();
				wrapin_copier.copy( wrapIn, attendanceWorkPlace );
				attendanceWorkPlace.setCreator( currentPerson.getName() );
				if(  wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
					attendanceWorkPlace.setId( wrapIn.getId() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceWorkPlaceWrapInException(e);
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceWorkPlace = attendanceWorkPlaceServiceAdv.save( attendanceWorkPlace );
				result.setData( new WrapOutId(attendanceWorkPlace.getId()) );
			} catch (Exception e) {
				Exception exception = new AttendanceWorkPlaceSaveException(e);
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取所有AttendanceWorkPlace列表", response = WrapOutAttendanceWorkPlace.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAttendanceWorkPlace( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAttendanceWorkPlace>> result = new ActionResult<>();
		List<WrapOutAttendanceWorkPlace> wraps = null;
		List<AttendanceWorkPlace> attendanceWorkPlaceList = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			try {
				attendanceWorkPlaceList = attendanceWorkPlaceServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceWorkPlaceListAllException(e);
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( attendanceWorkPlaceList != null && !attendanceWorkPlaceList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( attendanceWorkPlaceList );
					result.setData(wraps);
				} catch (Exception e) {
					Exception exception = new AttendanceWorkPlaceWrapOutException(e);
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取指定的AttendanceWorkPlace对象.", response = WrapOutAttendanceWorkPlace.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutAttendanceWorkPlace> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapOutAttendanceWorkPlace wrap = null;
		AttendanceWorkPlace attendanceWorkPlace = null;
		Boolean check = true;
		if( check ){
			try {
				attendanceWorkPlace = attendanceWorkPlaceServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceWorkPlaceyQueryByIdException(e, id);
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if (attendanceWorkPlace != null) {
				try {
					wrap = wrapout_copier.copy( attendanceWorkPlace );
					result.setData(wrap);
				} catch (Exception e) {
					Exception exception = new AttendanceWorkPlaceWrapOutException(e);
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
				
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceWorkPlaceAttendanceWorkPlace对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response delete( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
        if( check ){
        	if( id == null || id.isEmpty() || "(0)".equals( id )){
        		check = false;
        		Exception exception = new AttendanceWorkPlaceIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
        	}
        }
        if( check ){
        	try {
        		attendanceWorkPlaceServiceAdv.delete( id );
        		result.setData( new WrapOutId(id) );
    		} catch (Exception e) {
    			Exception exception = new AttendanceWorkPlaceDeleteException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
    		}
        }
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}