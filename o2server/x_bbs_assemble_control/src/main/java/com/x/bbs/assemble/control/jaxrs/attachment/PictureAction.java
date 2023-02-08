package com.x.bbs.assemble.control.jaxrs.attachment;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("picture")
@JaxrsDescribe("图片操作")
public class PictureAction extends StandardJaxrsAction {
    private static Logger logger = LoggerFactory.getLogger(PictureAction.class);

    @JaxrsMethodDescribe(value = "将图片转为base64编码，并且进行尺寸转换.", action = ActionUploadAndEncode.class)
    @POST
    @Path("encode/base64/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void pictureEncode(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("尺寸大小") @PathParam("size") Integer size,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<WrapOutString> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUploadAndEncode().execute(request, effectivePerson, size, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "更新版块图标.", action = ActionSectionIconUpdate.class)
    @POST
    @Path("section/{id}/icon")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void sectionIconUpdate(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("版块ID") @PathParam("id") String id,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<WrapOutId> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionSectionIconUpdate().execute(request, effectivePerson, id, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
