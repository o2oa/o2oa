package com.x.base.core.project.jaxrs;

import java.net.URI;
import java.util.Objects;
import java.util.zip.CRC32;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpHeader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.exception.CallbackPromptException;
import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.StringTools;

/**
 * @author sword
 */
public class ResponseFactory {

	private static CacheControl defaultCacheControl = CacheControlFactory.getDefault();

	public static final String Content_Disposition = "Content-Disposition";
	public static final String Accept_Ranges = "Accept-Ranges";
	public static final String Content_Type = "Content-Type";
	public static final String Content_Length = "Content-Length";
	public static final String Accept_Language = "Accept-Language";
	private static final int TAG_MAX_FILE_SIZE = 1024 * 1024 * 5;

	private static final Gson COMPACT_GSON = XGsonBuilder.compactInstance();

	public static <T> Response getDefaultActionResultResponse(ActionResult<T> result) {
		if (result.getType().equals(ActionResult.Type.error)) {
			if ((result.throwable instanceof CallbackPromptException)) {
				return Response.ok(callbackError(result)).cacheControl(defaultCacheControl).build();
			} else {
				return Response.serverError().entity(result.toJson()).cacheControl(defaultCacheControl).build();
			}
		} else {
			if ((null != result.getData()) && (result.getData() instanceof WoFile)) {
				WoFile wo = (WoFile) result.getData();
				return Response.ok(wo.getBytes()).header(Content_Disposition, wo.getContentDisposition())
						.header(Content_Type, wo.getContentType()).header(Content_Length, wo.getBytes().length)
						.header(Accept_Ranges, "bytes").build();
			} else if ((null != result.getData()) && (result.getData() instanceof WoText)) {
				WoText wo = (WoText) result.getData();
				return Response.ok(wo.getText()).cacheControl(defaultCacheControl).type(HttpMediaType.TEXT_PLAIN_UTF_8)
						.build();
			} else if ((null != result.getData()) && (result.getData() instanceof WoContentType)) {
				WoContentType wo = (WoContentType) result.getData();
				return Response.ok(wo.getBody()).type(wo.getContentType()).build();
			} else if ((null != result.getData()) && (result.getData() instanceof WoCallback)) {
				return Response.ok(callback((WoCallback) result.getData())).cacheControl(defaultCacheControl).build();
			} else if ((null != result.getData()) && (result.getData() instanceof WoSeeOther)) {
				WoSeeOther wo = (WoSeeOther) result.getData();
				try {
					return Response.seeOther(new URI(wo.getUrl())).build();
				} catch (Exception e) {
					return Response.serverError().entity(Objects.toString(wo.getUrl(), ""))
							.cacheControl(defaultCacheControl).build();
				}
			} else if ((null != result.getData()) && (result.getData() instanceof WoTemporaryRedirect)) {
				WoTemporaryRedirect wo = (WoTemporaryRedirect) result.getData();
				try {
					return Response.temporaryRedirect(new URI(wo.getUrl())).build();
				} catch (Exception e) {
					return Response.serverError().entity(Objects.toString(wo.getUrl(), ""))
							.cacheControl(defaultCacheControl).build();
				}
			} else {
				return Response.ok(result.toJson()).cacheControl(defaultCacheControl).build();
			}
		}
	}

	public static <T> Response getEntityTagActionResultResponse(HttpServletRequest request, ActionResult<T> result,
			JsonElement jsonElement) {
		request.setAttribute(HttpToken.X_REQUESTBODY, COMPACT_GSON.toJson(jsonElement));
		return getEntityTagActionResultResponse(request, result);
	}

	public static <T> Response getEntityTagActionResultResponse(HttpServletRequest request, ActionResult<T> result) {
		if (result.getType().equals(ActionResult.Type.error)) {
			if ((result.throwable instanceof LanguagePromptException)) {
				LanguagePromptException e = (LanguagePromptException) result.throwable;
				String message = e.getFormatMessage(result.getPrompt(), request.getHeader(Accept_Language));
				if (StringUtils.isNotBlank(message)) {
					result.setMessage(message);
				}
			}
			if ((result.throwable instanceof CallbackPromptException)) {
				return Response.ok(callbackError(result)).build();
			} else {
				return Response.serverError().entity(result.toJson()).build();
			}
		} else {
			if ((null != result.getData()) && (result.getData() instanceof WoFile)) {
				// 附件,二进制流文件
				WoFile wo = (WoFile) result.getData();
				Integer maxAge = maxAgeDefault(wo);
				if (null != maxAge) {
					CacheControl cacheControl = new CacheControl();
					cacheControl.setMaxAge(maxAge);
					if (wo.getStreamingOutput() != null){
						return Response.ok(wo.getStreamingOutput()).header(Content_Disposition, wo.getContentDisposition())
								.header(Content_Type, wo.getContentType()).header(Content_Length, wo.getContentLength())
								.header(Accept_Ranges, "bytes").cacheControl(cacheControl).lastModified(wo.getLastModified()).build();
					}else {
						return Response.ok(wo.getBytes()).header(Content_Disposition, wo.getContentDisposition())
								.header(Content_Type, wo.getContentType()).header(Content_Length, wo.getBytes().length)
								.header(Accept_Ranges, "bytes").cacheControl(cacheControl).lastModified(wo.getLastModified()).build();
					}
				} else {
					EntityTag tag = new EntityTag(etagWoFile(wo));
					if (notModified(request, tag)) {
						return Response.notModified().tag(tag).build();
					}
					if (wo.getStreamingOutput() != null) {
						return Response.ok(wo.getStreamingOutput()).header(Content_Disposition, wo.getContentDisposition())
								.header(Content_Type, wo.getContentType()).header(Content_Length, wo.getContentLength())
								.header(Accept_Ranges, "bytes").tag(tag).build();
					} else {
						return Response.ok(wo.getBytes()).header(Content_Disposition, wo.getContentDisposition())
								.header(Content_Type, wo.getContentType()).header(Content_Length, wo.getBytes().length)
								.header(Accept_Ranges, "bytes").tag(tag).build();
					}
				}
			} else if ((null != result.getData()) && (result.getData() instanceof WoText)) {
				// 纯文本text
				WoText wo = (WoText) result.getData();
				EntityTag tag = new EntityTag(etagWoText(wo));
				if (notModified(request, tag)) {
					return Response.notModified().tag(tag).build();
				}
				return Response.ok(wo.getText()).type(wo.getContentType()).tag(tag).build();
			} else if ((null != result.getData()) && (result.getData() instanceof WoContentType)) {
				WoContentType wo = (WoContentType) result.getData();
				EntityTag tag = new EntityTag(etagWoContentType(wo));
				if (notModified(request, tag)) {
					return Response.notModified().tag(tag).build();
				}
				return Response.ok(wo.getBody()).type(wo.getContentType()).tag(tag).build();
			} else if ((null != result.getData()) && (result.getData() instanceof WoCallback)) {
				// jsonp callback
				return Response.ok(callback((WoCallback) result.getData())).build();
			} else if ((null != result.getData()) && (result.getData() instanceof WoSeeOther)) {
				// 303
				WoSeeOther wo = (WoSeeOther) result.getData();
				try {
					return Response.seeOther(new URI(wo.getUrl())).build();
				} catch (Exception e) {
					return Response.serverError().entity(Objects.toString(wo.getUrl(), "")).build();
				}
			} else if ((null != result.getData()) && (result.getData() instanceof WoTemporaryRedirect)) {
				// 304
				WoTemporaryRedirect wo = (WoTemporaryRedirect) result.getData();
				try {
					return Response.temporaryRedirect(new URI(wo.getUrl())).build();
				} catch (Exception e) {
					return Response.serverError().entity(Objects.toString(wo.getUrl(), "")).build();
				}
			} else {
				// default
				Integer maxAge = maxAgeDefault(result.getData());
				if (null != maxAge) {
					CacheControl cacheControl = new CacheControl();
					cacheControl.setMaxAge(maxAge);
					return Response.ok(result.toJson()).cacheControl(cacheControl).build();
				} else {
					EntityTag tag = new EntityTag(etagDefault(result.getData()));
					if (notModified(request, tag)) {
						return Response.notModified().tag(tag).build();
					}
					return Response.ok(result.toJson()).tag(tag).build();
				}
			}
		}
	}

	private static boolean notModified(HttpServletRequest request, EntityTag tag) {
		String ifNoneMatch = request.getHeader(HttpHeader.IF_NONE_MATCH.toString());
		return (StringUtils.isNotEmpty(ifNoneMatch) && StringUtils.equals(ifNoneMatch, "\"" + tag.getValue() + "\""));
	}

	private static String etagWoFile(WoFile wo) {
		CRC32 crc = new CRC32();
		if (StringUtils.isNotEmpty(wo.getFastETag())) {
			crc.update(wo.getFastETag().getBytes(DefaultCharset.charset_utf_8));
		} else if(wo.getBytes()!=null && wo.getBytes().length < TAG_MAX_FILE_SIZE){
			crc.update(wo.getBytes());
		} else {
			crc.update(StringTools.uniqueToken().getBytes(DefaultCharset.charset_utf_8));
		}
		return crc.getValue() + "";
	}

	private static String etagWoContentType(WoContentType wo) {
		CRC32 crc = new CRC32();
		crc.update((wo.getBody().toString() + wo.getContentType()).getBytes());
		return crc.getValue() + "";
	}

	private static String etagWoText(WoText wo) {
		CRC32 crc = new CRC32();
		if (StringUtils.isNotEmpty(wo.getFastETag())) {
			crc.update(wo.getFastETag().getBytes(DefaultCharset.charset_utf_8));
		} else {
			crc.update(wo.getText().getBytes(DefaultCharset.charset_utf_8));
		}
		return crc.getValue() + "";
	}

	private static String etagDefault(Object o) {
		CRC32 crc = new CRC32();
		if (o instanceof WoMaxAgeFastETag) {
			WoMaxAgeFastETag fast = ((WoMaxAgeFastETag) o);
			if (StringUtils.isNotEmpty(fast.getFastETag())) {
				crc.update(fast.getFastETag().getBytes(DefaultCharset.charset_utf_8));
			} else {
				crc.update(XGsonBuilder.toJson(o).getBytes(DefaultCharset.charset_utf_8));
			}
		} else {
			crc.update(XGsonBuilder.toJson(o).getBytes(DefaultCharset.charset_utf_8));
		}
		return crc.getValue() + "";
	}

	private static Integer maxAgeDefault(Object o) {
		if (o instanceof WoMaxAgeFastETag) {
			return ((WoMaxAgeFastETag) o).getMaxAge();
		}
		return null;
	}

	private static String callback(WoCallback woCallback) {
		ActionResult<Object> result = new ActionResult<>();
		result.setData(woCallback.getObject());
		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><meta http-equiv=\"charset\" content=\"" + DefaultCharset.name + "\" /><script>");
		sb.append(woCallback.getCallbackName() + "('" + result.toCompactJson() + "')");
		sb.append("</script></head></html>");
		return sb.toString();
	}

	private static String callbackError(ActionResult result) {
		StringBuffer sb = new StringBuffer();
		CallbackPromptException ex = (CallbackPromptException) result.throwable;
		sb.append("<html><head><meta http-equiv=\"charset\" content=\"" + DefaultCharset.name + "\" /><script>");
		sb.append(ex.getCallbackName() + "('" + result.toCompactJson() + "')");
		sb.append("</script></head></html>");
		return sb.toString();
	}
}
