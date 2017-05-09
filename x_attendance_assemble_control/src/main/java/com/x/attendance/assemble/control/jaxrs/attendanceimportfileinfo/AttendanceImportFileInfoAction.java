package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.attendance.assemble.control.service.AttendanceImportFileInfoServiceAdv;
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;


@Path("attendanceimportfileinfo")
public class AttendanceImportFileInfoAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceImportFileInfoAction.class );
	private BeanCopyTools<AttendanceImportFileInfo, WrapOutAttendanceImportFileInfo> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceImportFileInfo.class, WrapOutAttendanceImportFileInfo.class, null, WrapOutAttendanceImportFileInfo.Excludes);
	private AttendanceImportFileInfoServiceAdv attendanceImportFileInfoServiceAdv = new AttendanceImportFileInfoServiceAdv();
	
	@HttpMethodDescribe(value = "获取所有已经上传成功的文件列表", response = WrapOutAttendanceImportFileInfo.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAttendanceImportFileInfo(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAttendanceImportFileInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceImportFileInfo> wraps = null;
		List<AttendanceImportFileInfo> attendanceSettingList = null;
		Boolean check = true;
		
		if( check ){
			try {
				attendanceSettingList = attendanceImportFileInfoServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceImportFileListAllException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check && attendanceSettingList != null ){
			try {
				wraps = wrapout_copier.copy( attendanceSettingList );
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceImportFileWrapOutException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取AttendanceImportFileInfo对象.", response = WrapOutAttendanceImportFileInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutAttendanceImportFileInfo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutAttendanceImportFileInfo wrap = null;
		AttendanceImportFileInfo attendanceImportFileInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new AttendanceImportFileIdEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceImportFileInfo = attendanceImportFileInfoServiceAdv.get(id);
				if( attendanceImportFileInfo == null ){
					check = false;
					Exception exception = new AttendanceImportFileNotExistsException( id );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceImportFileQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check && attendanceImportFileInfo != null ){
			try {
				wrap = wrapout_copier.copy( attendanceImportFileInfo );
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceImportFileWrapOutException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}	
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除已经上传成功的文件以及文件信息.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutId wrapOutId = null;
		AttendanceImportFileInfo attendanceImportFileInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new AttendanceImportFileIdEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceImportFileInfo = attendanceImportFileInfoServiceAdv.get(id);
				if( attendanceImportFileInfo == null ){
					check = false;
					Exception exception = new AttendanceImportFileNotExistsException( id );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceImportFileQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceImportFileInfoServiceAdv.delete(id);
				wrapOutId = new WrapOutId( id );
				result.setData( wrapOutId );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceImportFileDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}