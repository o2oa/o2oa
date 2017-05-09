package com.x.file.assemble.control.jaxrs.file;

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

import com.x.base.core.bean.NameValueCountPair;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.file.assemble.control.wrapout.WrapOutFile;
import com.x.file.core.entity.open.ReferenceType;

@Path("file")
public class FileAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(FileAction.class);

	@HttpMethodDescribe(value = "获取指定File.", response = WrapOutFile.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutFile> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示当前用户下的所有File,下一页.", response = WrapOutFile.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNext(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutFile>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListNext().execute(effectivePerson, id, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示当前用户下的所有File,上一页.", response = WrapOutFile.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrev(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutFile>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPrev().execute(effectivePerson, id, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示当前用户指定referenceType下的所有File,下一页.", response = WrapOutFile.class)
	@GET
	@Path("list/{id}/next/{count}/referencetype/{referenceType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithReferenceType(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("referenceType") ReferenceType referenceType) {
		ActionResult<List<WrapOutFile>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListNextWithReferenceType().execute(effectivePerson, id, count, referenceType);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示当前用户指定referenceType下的所有File,上一页.", response = WrapOutFile.class)
	@GET
	@Path("list/{id}/prev/{count}/referencetype/{referenceType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithReferenceType(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("referenceType") ReferenceType referenceType) {
		ActionResult<List<WrapOutFile>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPrevWithReferenceType().execute(effectivePerson, id, count, referenceType);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示所有File,下一页.", response = WrapOutFile.class)
	@GET
	@Path("list/{id}/next/{count}/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextAll(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutFile>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListNextAll().execute(effectivePerson, id, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示有File,上一页.", response = WrapOutFile.class)
	@GET
	@Path("list/{id}/prev/{count}/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevAll(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutFile>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPrevAll().execute(effectivePerson, id, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示当前用户的ReferenceType.", response = WrapOutFile.class)
	@GET
	@Path("list/referencetype")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listReference(@Context HttpServletRequest request) {
		ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListReferenceType().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据referenceType和reference删除File.", response = WrapOutBoolean.class)
	@DELETE
	@Path("referencetype/{referenceType}/reference/{reference}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("referenceType") String referenceType,
			@PathParam("reference") String reference) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(effectivePerson, referenceType, reference);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "将Attachment的附件复制到File中,关联到referenceType和reference.", response = WrapOutId.class)
	@GET
	@Path("copy/attachment/{attachmentId}/referencetype/{referenceType}/reference/{reference}/scale/{scale}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response copy(@Context HttpServletRequest request, @PathParam("attachmentId") String attachmentId,
			@PathParam("referenceType") String referenceType, @PathParam("reference") String reference,
			@PathParam("scale") Integer scale) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCopy().execute(effectivePerson, attachmentId, referenceType, reference, scale);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据指定的referenceType和reference列示所有的File.", response = WrapOutFile.class)
	@GET
	@Path("list/referencetype/{referenceType}/reference/{reference}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithReferenceTypeWithReference(@Context HttpServletRequest request,
			@PathParam("referenceType") String referenceType, @PathParam("reference") String reference) {
		ActionResult<List<WrapOutFile>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithReferenceTypeWithReference().execute(effectivePerson, referenceType, reference);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}