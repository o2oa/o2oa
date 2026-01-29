package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.pan.assemble.control.entities.WopiRequestHeader;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author sword
 */
@Path("wopi")
@JaxrsDescribe("officeOnline基于WOPI在线编辑文件")
public class AttachmentWopiAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AttachmentWopiAction.class);

	@JaxrsMethodDescribe(value = "获取附件内容", action = ActionDownloadWopi.class)
	@GET
	@Path("/files/{fileId}/contents")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@JaxrsParameterDescribe("附件标识") @PathParam("fileId") String fileId,
			@JaxrsParameterDescribe("附件标识") @QueryParam("access_token") String accessToken) {
		ActionResult<ActionDownloadWopi.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		if(StringUtils.isNotBlank(accessToken)){
			try {
				HttpToken httpToken = new HttpToken();
				effectivePerson = httpToken.who(accessToken, Config.token().getCipher(), HttpToken.remoteAddress(request));
				if(effectivePerson.isAnonymous()){
					logger.warn("bad accessToken:{}",accessToken);
				}
			} catch (Exception e) {
				logger.warn("bad accessToken:{},error:{}",accessToken,e.getMessage());
			}
		}
		try {
			logger.info("downloadStream当前用户：{}，附件ID:{}",effectivePerson.getDistinguishedName(), fileId);
			result = new ActionDownloadWopi().execute(response, effectivePerson, fileId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "officeOnline在线编辑回调获取文件信息", action = ActionEditFileInfo.class)
	@GET
	@Path("files/{fileId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void fileInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
										@JaxrsParameterDescribe("附件标识") @PathParam("fileId") String fileId,
						 @JaxrsParameterDescribe("操作模式：view(只读，默认)|write(编辑)") @QueryParam("mode") String mode,
						 @JaxrsParameterDescribe("附件标识") @QueryParam("access_token") String accessToken) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		if(StringUtils.isNotBlank(accessToken)){
			try {
				HttpToken httpToken = new HttpToken();
				effectivePerson = httpToken.who(accessToken, Config.token().getCipher(), HttpToken.remoteAddress(request));
				if(effectivePerson.isAnonymous()){
					logger.warn("bad accessToken:{}",accessToken);
				}
			} catch (Exception e) {
				logger.warn("bad accessToken:{},error:{}",accessToken,e.getMessage());
			}
		}
		Response response = Response.ok().build();
		try {
			logger.info("fileInfo当前用户：{}, mode:{}, fileId:{}",effectivePerson.getDistinguishedName(), mode, fileId);
			response = new ActionEditFileInfoWopi().execute(effectivePerson, fileId, mode);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(response);
	}

	@JaxrsMethodDescribe(value = "officeOnline在线编辑回调获取文件锁定信息", action = ActionEditFileInfo.class)
	@POST
	@Path("files/{fileId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void handleLock(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						 @JaxrsParameterDescribe("附件标识") @PathParam("fileId") String fileId,
						 @JaxrsParameterDescribe("附件标识") @QueryParam("access_token") String accessToken) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		if(StringUtils.isNotBlank(accessToken)){
			try {
				HttpToken httpToken = new HttpToken();
				effectivePerson = httpToken.who(accessToken, Config.token().getCipher(), HttpToken.remoteAddress(request));
				if(effectivePerson.isAnonymous()){
					logger.warn("bad accessToken:{}",accessToken);
				}
			} catch (Exception e) {
				logger.warn("bad accessToken:{},error:{}",accessToken,e.getMessage());
			}
		}
		Response response = Response.ok().build();
		try {
			String wopiOverride = request.getHeader(WopiRequestHeader.OVERRIDE);
			String requestLock = request.getHeader(WopiRequestHeader.LOCK);
			String oldLock = request.getHeader(WopiRequestHeader.OLD_LOCK);
			response = new ActionHandleLockWopi().execute(request, effectivePerson, fileId);
			logger.info("handleLock当前用户：{}, fileId:{}, wopiOverride:{}, requestLock:{}, oldLock:{}, response:{}",
					effectivePerson.getDistinguishedName(), fileId, wopiOverride, requestLock, oldLock, response);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(response);
	}

	@JaxrsMethodDescribe(value = "officeOnline在线编辑回调保存文件", action = ActionSaveByWopi.class)
	@POST
	@Path("files/{fileId}/contents")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public void saveFile(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									 @JaxrsParameterDescribe("附件标识") @PathParam("fileId") String fileId,
						 @JaxrsParameterDescribe("附件标识") @QueryParam("access_token") String accessToken,
						 byte[] bytes) {
		String result = "{'result':10001, 'msg':'服务异常'}";
		EffectivePerson effectivePerson = this.effectivePerson(request);
		if(StringUtils.isNotBlank(accessToken)){
			try {
				HttpToken httpToken = new HttpToken();
				effectivePerson = httpToken.who(accessToken, Config.token().getCipher(), HttpToken.remoteAddress(request));
				if(effectivePerson.isAnonymous()){
					logger.warn("bad accessToken:{}",accessToken);
				}
			} catch (Exception e) {
				logger.warn("bad accessToken:{},error:{}",accessToken,e.getMessage());
			}
		}
		try {
			result = new ActionSaveByWopi().execute(effectivePerson, fileId, bytes);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(Response.ok(result).build());
	}

}
