package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.entity.HotPictureInfo;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("cipher/hotpic")
@JaxrsDescribe("热点信息管理（服务器间调用）")
public class HotPictureInfoCipherAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(HotPictureInfoCipherAction.class);
	private HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();
	private WrapCopier<WrapInHotPictureInfo, HotPictureInfo> wrapin_copier = WrapCopierFactory
			.wi(WrapInHotPictureInfo.class, HotPictureInfo.class, null, WrapInHotPictureInfo.Excludes);
	private WrapCopier<HotPictureInfo, WrapOutHotPictureInfo> wrapout_copier = WrapCopierFactory
			.wo(HotPictureInfo.class, WrapOutHotPictureInfo.class, null, WrapOutHotPictureInfo.Excludes);
	private Ehcache cache = ApplicationCache.instance().getCache(HotPictureInfo.class);

	@JaxrsMethodDescribe(value = "根据ID获取单个热图信息", action= StandardJaxrsAction.class )
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("id") String id) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutString wrap = null;
		HotPictureInfo hotPictureInfo = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty() || "(0)".equals(id)) {
				check = false;
				Exception exception = new InfoIdEmptyException();
				result.error(exception);
				// logger.error( e, effectivePerson, request, null);
			}
		}
		String cacheKey = "base64#" + id;
		Element element = cache.get(cacheKey);
		if (check) {
			if (null != element) {
				wrap = (WrapOutString) element.getObjectValue();
				result.setData(wrap);
			} else {
				try {
					hotPictureInfo = hotPictureInfoService.get(id);
					if (hotPictureInfo == null) {
						Exception exception = new InfoNotExistsException(id);
						result.error(exception);
						// logger.error( e, effectivePerson, request, null);
					} else {
						wrap = new WrapOutString();
						cache.put(new Element(cacheKey, wrap));
						result.setData(wrap);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new InfoQueryByIdException(e, id);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@SuppressWarnings("unchecked")
	// @HttpMethodDescribe(value = "列示根据过滤条件的HotPictureInfo,下一页.", response =
	// WrapOutHotPictureInfo.class, request = JsonElement.class )
	@JaxrsMethodDescribe(value = "列示根据过滤条件的HotPictureInfo,下一页", action= StandardJaxrsAction.class )
	@PUT
	@Path("filter/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listForPage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("page") Integer page, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutHotPictureInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutHotPictureInfo> wraps_out = new ArrayList<WrapOutHotPictureInfo>();
		List<WrapOutHotPictureInfo> wraps = new ArrayList<WrapOutHotPictureInfo>();
		List<HotPictureInfo> hotPictureInfoList = null;
		Integer selectTotal = 0;
		Long total = 0L;
		WrapInFilter wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WrapInFilter.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new WrapInConvertException(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (check) {
				if (wrapIn == null) {
					wrapIn = new WrapInFilter();
				}
			}
			if (check) {
				if (page == null) {
					page = 1;
				}
				if (page <= 0) {
					page = 1;
				}
			}
			if (check) {
				if (count == null) {
					count = 20;
				}
				if (count <= 0) {
					count = 20;
				}
			}
			selectTotal = page * count;

			String cacheKey1 = "filter#" + page + "#" + count + "#" + wrapIn.getApplication() + "#" + wrapIn.getInfoId()
					+ "#" + wrapIn.getTitle();
			Element element1 = cache.get(cacheKey1);
			String cacheKey2 = "total#" + page + "#" + count + "#" + wrapIn.getApplication() + "#" + wrapIn.getInfoId()
					+ "#" + wrapIn.getTitle();
			Element element2 = cache.get(cacheKey2);
			if (check) {
				if (null != element1 && null != element2) {
					wraps = (List<WrapOutHotPictureInfo>) element1.getObjectValue();
					result.setCount(Long.parseLong(element2.getObjectValue().toString()));
					result.setData(wraps);
				} else {
					if (check) {
						if (selectTotal > 0) {
							try {
								total = hotPictureInfoService.count(wrapIn.getApplication(), wrapIn.getInfoId(),
										wrapIn.getTitle());
							} catch (Exception e) {
								check = false;
								Exception exception = new InfoListByFilterException(e);
								result.error(exception);
								logger.error(e, effectivePerson, request, null);
							}
						}
					}
					if (check) {
						if (selectTotal > 0 && total > 0) {
							try {
								hotPictureInfoList = hotPictureInfoService.listForPage(wrapIn.getApplication(),
										wrapIn.getInfoId(), wrapIn.getTitle(), selectTotal);
								if (hotPictureInfoList != null) {
									try {
										wraps_out = wrapout_copier.copy(hotPictureInfoList);
										SortTools.desc(wraps_out, JpaObject.sequence_FIELDNAME);
									} catch (Exception e) {
										check = false;
										Exception exception = new InfoWrapOutException(e);
										result.error(exception);
										logger.error(e, effectivePerson, request, null);
									}
								}
							} catch (Exception e) {
								check = false;
								Exception exception = new InfoListByFilterException(e);
								result.error(exception);
								logger.error(e, effectivePerson, request, null);
							}
						}
					}
					if (check) {
						int startIndex = (page - 1) * count;
						int endIndex = page * count;
						int i = 0;
						for (i = 0; i < wraps_out.size(); i++) {
							if (i >= startIndex && i < endIndex) {
								wraps.add(wraps_out.get(i));
							}
						}
						cache.put(new Element(cacheKey1, wraps));
						cache.put(new Element(cacheKey2, total.toString()));
						result.setData(wraps);
						result.setCount(total);
					}
				}
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}