package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.entity.HotPictureInfo;

@Path("cipher/hotpic")
@JaxrsDescribe("热点信息管理（服务器间调用）")
public class HotPictureInfoCipherAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(HotPictureInfoCipherAction.class);
	private HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();
	private WrapCopier<WrapInHotPictureInfo, HotPictureInfo> wrapin_copier = WrapCopierFactory
			.wi(WrapInHotPictureInfo.class, HotPictureInfo.class, null, WrapInHotPictureInfo.Excludes);
	private WrapCopier<HotPictureInfo, WrapOutHotPictureInfo> wrapout_copier = WrapCopierFactory
			.wo(HotPictureInfo.class, WrapOutHotPictureInfo.class, null, WrapOutHotPictureInfo.Excludes);

	private CacheCategory cacheCategory = new CacheCategory(HotPictureInfo.class);

	@JaxrsMethodDescribe(value = "根据CMS文档ID删除热点信息", action = StandardJaxrsAction.class)
	@DELETE
	@Path("cms/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteCms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("id") String id) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutString wrap = null;
		List<HotPictureInfo> hotPictureInfos = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty() || "(0)".equals(id)) {
				check = false;
				Exception exception = new InfoIdEmptyException();
				result.error(exception);
			}
		}
		if (check) {
			try {
				hotPictureInfos = hotPictureInfoService.listByApplicationInfoId(HotPictureInfo.APPLICATION_CMS, id);
				if (ListTools.isNotEmpty(hotPictureInfos)) {
					for (HotPictureInfo hotPictureInfo : hotPictureInfos) {
						hotPictureInfoService.delete(hotPictureInfo.getId());
					}
					CacheManager.notify(HotPictureInfo.class);
				}
			} catch (Exception e) {
				Exception exception = new InfoQueryByIdException(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		WrapOutString wrapOutString = new WrapOutString();
		wrapOutString.setValue(id);
		result.setData(wrapOutString);
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据BBS主贴ID删除热点信息", action = StandardJaxrsAction.class)
	@DELETE
	@Path("bbs/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteBBS(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("id") String id) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutString wrap = null;
		List<HotPictureInfo> hotPictureInfos = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty() || "(0)".equals(id)) {
				check = false;
				Exception exception = new InfoIdEmptyException();
				result.error(exception);
			}
		}
		if (check) {
			try {
				hotPictureInfos = hotPictureInfoService.listByApplicationInfoId(HotPictureInfo.APPLICATION_BBS, id);
				if (ListTools.isNotEmpty(hotPictureInfos)) {
					for (HotPictureInfo hotPictureInfo : hotPictureInfos) {
						hotPictureInfoService.delete(hotPictureInfo.getId());
					}
					CacheManager.notify(HotPictureInfo.class);
				}
			} catch (Exception e) {
				Exception exception = new InfoQueryByIdException(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		WrapOutString wrapOutString = new WrapOutString();
		wrapOutString.setValue(id);
		result.setData(wrapOutString);
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

//	@JaxrsMethodDescribe(value = "根据ID获取单个热图信息", action = StandardJaxrsAction.class)
//	@GET
//	@Path("{id}")
//	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
//			@PathParam("id") String id) {
//		ActionResult<WrapOutString> result = new ActionResult<>();
//		EffectivePerson effectivePerson = this.effectivePerson(request);
//		WrapOutString wrap = null;
//		HotPictureInfo hotPictureInfo = null;
//		Boolean check = true;
//
//		if (check) {
//			if (id == null || id.isEmpty() || "(0)".equals(id)) {
//				check = false;
//				Exception exception = new InfoIdEmptyException();
//				result.error(exception);
//				// logger.error( e, effectivePerson, request, null);
//			}
//		}
//		// String cacheKey = "base64#" + id;
//		CacheKey cacheKey = new CacheKey("base64#", id);
//		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
//		if (check) {
//			if (optional.isPresent()) {
//				wrap = (WrapOutString) optional.get();
//				result.setData(wrap);
//			} else {
//				try {
//					hotPictureInfo = hotPictureInfoService.get(id);
//					if (hotPictureInfo == null) {
//						Exception exception = new InfoNotExistsException(id);
//						result.error(exception);
//						// logger.error( e, effectivePerson, request, null);
//					} else {
//						wrap = new WrapOutString();
//						cache.put(new Element(cacheKey, wrap));
//						result.setData(wrap);
//					}
//				} catch (Exception e) {
//					check = false;
//					Exception exception = new InfoQueryByIdException(e, id);
//					result.error(exception);
//					logger.error(e, effectivePerson, request, null);
//				}
//			}
//		}
//		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
//	}

	@SuppressWarnings("unchecked")
	// @HttpMethodDescribe(value = "列示根据过滤条件的HotPictureInfo,下一页.", response =
	// WrapOutHotPictureInfo.class, request = JsonElement.class )
	@JaxrsMethodDescribe(value = "列示根据过滤条件的HotPictureInfo,下一页", action = StandardJaxrsAction.class)
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
			if (wrapIn == null) {
				wrapIn = new WrapInFilter();
			}
			if (page == null) {
				page = 1;
			}
			if (page <= 0) {
				page = 1;
			}
			if (count == null) {
				count = 20;
			}
			if (count <= 0) {
				count = 20;
			}
			selectTotal = page * count;

			CacheKey cacheKey1 = new CacheKey("filter#", page, count, wrapIn.getApplication(), wrapIn.getInfoId(),
					wrapIn.getTitle());
			CacheKey cacheKey2 = new CacheKey("total#", page, count, wrapIn.getApplication(), wrapIn.getInfoId(),
					wrapIn.getTitle());
			Optional<?> optional1 = CacheManager.get(cacheCategory, cacheKey1);
			Optional<?> optional2 = CacheManager.get(cacheCategory, cacheKey2);
			if (check) {
				if (optional1.isPresent() && optional2.isPresent()) {
					wraps = (List<WrapOutHotPictureInfo>) optional1.get();
					result.setCount(Long.parseLong(optional2.get().toString()));
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
						CacheManager.put(cacheCategory, cacheKey1, wraps);
						CacheManager.put(cacheCategory, cacheKey2, total.toString());
						result.setData(wraps);
						result.setCount(total);
					}
				}
			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}