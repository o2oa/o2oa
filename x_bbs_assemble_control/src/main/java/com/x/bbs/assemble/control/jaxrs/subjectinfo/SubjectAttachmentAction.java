package com.x.bbs.assemble.control.jaxrs.subjectinfo;

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

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.WrapOutSectionInfo;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.AttachmentIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectIdEmptyException;

@Path("subjectattach")
public class SubjectAttachmentAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger(SubjectAttachmentAction.class);
	

	@HttpMethodDescribe(value = "根据指定ID获取附件信息.", response = WrapOutSectionInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectAttachment> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new AttachmentIdEmptyException();
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		if(check){
			try {
				result = new ExcuteAttachmentGet().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据指定ID获取版块信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除BBSSubjectAttachment数据对象.", response = WrapOutSubjectAttachment.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutSubjectAttachment> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		boolean check = true;
		if (id == null || id.isEmpty()) {
			check = false;
			Exception exception = new AttachmentIdEmptyException();
			result.error(exception);
			logger.error(exception, effectivePerson, request, null);
		}
		if(check){
			try {
				result = new ExcuteAttachmentDelete().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据指定ID获取版块信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据主题ID获取BBSSubjectAttachment列表.", response = WrapOutSubjectAttachment.class)
	@GET
	@Path("list/subject/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listBySubjectId(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<List<WrapOutSubjectAttachment>> result = new ActionResult<List<WrapOutSubjectAttachment>>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (id == null || id.isEmpty()) {
			check = false;
			Exception exception = new SubjectIdEmptyException();
			result.error(exception);
			logger.error(exception, effectivePerson, request, null);
		}
		if( check ){
			try {
				result = new ExcuteAttachmentListBySubjectId().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据主题ID获取BBSSubjectAttachment列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "将图片附件转为base64编码.", response = WrapOutString.class)
	@GET
	@Path("{id}/binary/base64/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response imageToBase64(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("size") String size) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new AttachmentIdEmptyException();
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		if(check){
			try {
				result = new ExcuteImageToBase64().execute( request, effectivePerson, id, size );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据指定ID获取版块信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}