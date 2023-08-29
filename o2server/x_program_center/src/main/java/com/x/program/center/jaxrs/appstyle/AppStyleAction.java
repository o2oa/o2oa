package com.x.program.center.jaxrs.appstyle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("appstyle")
@JaxrsDescribe("移动客户端样式")
public class AppStyleAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(AppStyleAction.class);

	@JaxrsMethodDescribe(value = "获取当前生效的移动客户端样式.", action = ActionCurrentStyle.class)
	@GET
	@Path("current/style")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void currentStyle(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionCurrentStyle.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCurrentStyle().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取当前生效的移动客户端样式最后修改时间,如果默认那么返回2018-01-01 00:00:00.", action = ActionCurrentUpdate.class)
	@GET
	@Path("current/update")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void currentUpdate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionCurrentUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCurrentUpdate().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定的移动客户端样式.", action = ActionEdit.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void edit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionEdit.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionEdit().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "设置图片:首页底部菜单中间主页的按钮(不选中),114x114", action = ActionImageMenuLogoBlur.class)
	@PUT
	@Path("image/menu/logo/blur")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void imageMenuLogoBlur(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("图片名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("图片文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionImageMenuLogoBlur.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageMenuLogoBlur().execute(effectivePerson, bytes, disposition, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置图片:首页底部菜单中间主页的按钮(选中),114x114", action = ActionImageMenuLogoFocus.class)
	@PUT
	@Path("image/menu/logo/focus")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void imageMenuLogoFocus(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("图片名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("图片文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionImageMenuLogoFocus.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageMenuLogoFocus().execute(effectivePerson, bytes, disposition, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置图片:启动页的logo,195x195.", action = ActionImageLaunchLogo.class)
	@PUT
	@Path("image/launch/logo")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void imageLaunchLogo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("图片名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("图片文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionImageLaunchLogo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageLaunchLogo().execute(effectivePerson, bytes, disposition, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置图片:登陆页面默认头像图片,225x225.", action = ActionImageLoginAvatar.class)
	@PUT
	@Path("image/login/avatar")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void imageLoginAvatar(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("图片名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("图片文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionImageLoginAvatar.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageLoginAvatar().execute(effectivePerson, bytes, disposition, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置图片:流程、应用的默认图片,90x90.", action = ActionImageProcessDefault.class)
	@PUT
	@Path("image/process/default")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void imageProcessDefault(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("图片名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("图片文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionImageProcessDefault.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageProcessDefault().execute(effectivePerson, bytes, disposition, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置图片:设置页面“关于”项的图标,66x66.", action = ActionImageSetupAboutLogo.class)
	@PUT
	@Path("image/setup/about/logo")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void imageSetupAboutLogo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("图片名称") @FormDataParam(FILENAME_FIELD) String fileName,
			@JaxrsParameterDescribe("图片文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionImageSetupAboutLogo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageSetupAboutLogo().execute(effectivePerson, bytes, disposition, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置图片:应用页面顶部图片,730x390.", action = ActionImageApplicationTop.class)
	@PUT
	@Path("image/application/top")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void imageApplicationTop(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									@FormDataParam(FILE_FIELD) final byte[] bytes,
									@JaxrsParameterDescribe("图片名称") @FormDataParam(FILENAME_FIELD) String fileName,
									@JaxrsParameterDescribe("图片文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionImageApplicationTop.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageApplicationTop().execute(effectivePerson, bytes, disposition, fileName);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除图片:首页底部菜单中间主页的按钮(不选中).", action = ActionImageMenuLogoBlurErase.class)
	@GET
	@Path("image/menu/logo/blur/erase")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void imageMenuLogoBlurErase(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ActionImageMenuLogoBlurErase.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageMenuLogoBlurErase().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除图片:首页底部菜单中间主页的按钮(选中)..", action = ActionImageMenuLogoFocusErase.class)
	@GET
	@Path("image/menu/logo/focus/erase")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void imageMenuLogoFocusErase(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ActionImageMenuLogoFocusErase.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageMenuLogoFocusErase().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置图片:启动页的logo.", action = ActionImageLaunchLogoErase.class)
	@GET
	@Path("image/launch/logo/erase")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void imageLaunchLogoErase(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ActionImageLaunchLogoErase.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageLaunchLogoErase().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置图片:登陆页面默认头像图片.", action = ActionImageLoginAvatarErase.class)
	@GET
	@Path("image/login/avatar/erase")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void imageLoginAvatarErase(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ActionImageLoginAvatarErase.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageLoginAvatarErase().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置图片:流程、应用的默认图片.", action = ActionImageProcessDefaultErase.class)
	@GET
	@Path("image/process/default/erase")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void imageProcessDefaultErase(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ActionImageProcessDefaultErase.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageProcessDefaultErase().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置图片:设置页面“关于”项的图标.", action = ActionImageSetupAboutLogoErase.class)
	@GET
	@Path("image/setup/about/logo/erase")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void imageSetupAboutLogoErase(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ActionImageSetupAboutLogoErase.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageSetupAboutLogoErase().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "设置图片:应用页面顶部图片,730x390", action = ActionImageApplicationTopErase.class)
	@GET
	@Path("image/application/top/erase")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void imageApplicationTopErase(@Suspended final AsyncResponse asyncResponse,
										 @Context HttpServletRequest request) {
		ActionResult<ActionImageApplicationTopErase.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionImageApplicationTopErase().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取移动端首页门户.", action = ActionGetIndexPortal.class)
	@GET
	@Path("index/portal")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getIndexPortal(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionGetIndexPortal.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetIndexPortal().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
