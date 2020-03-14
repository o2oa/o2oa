package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.*;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
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

@Path("user/hotpic")
@JaxrsDescribe("热点信息管理")
public class HotPictureInfoAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(HotPictureInfoAction.class);
	private HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();
	private WrapCopier<WrapInHotPictureInfo, HotPictureInfo> wrapin_copier = WrapCopierFactory.wi(WrapInHotPictureInfo.class, HotPictureInfo.class, null, WrapInHotPictureInfo.Excludes);
	private WrapCopier<HotPictureInfo, WrapOutHotPictureInfo> wrapout_copier = WrapCopierFactory.wo(HotPictureInfo.class, WrapOutHotPictureInfo.class, null, WrapOutHotPictureInfo.Excludes);
	private Ehcache cache = ApplicationCache.instance().getCache(HotPictureInfo.class);

	// @HttpMethodDescribe(value = "检查所有的热点新闻还在不在.", response =
	// WrapOutString.class)
	@JaxrsMethodDescribe(value = "检查所有的热点新闻还在不在", action= StandardJaxrsAction.class )
	@GET
	@Path("exists/check")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void existsCheck(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			hotPictureInfoService.documentExistsCheck();
		} catch (Exception e) {
			result.error(e);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	// @HttpMethodDescribe(value = "查询指定的图片的base64编码.", response =
	// WrapOutString.class)
	@JaxrsMethodDescribe(value = "查询指定的图片的base64编码", action= StandardJaxrsAction.class )
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
	// @HttpMethodDescribe( value = "根据应用类型以及信息ID查询热图信息.", response =
	// WrapOutHotPictureInfo.class )
	@JaxrsMethodDescribe(value = "根据应用类型以及信息ID查询热图信息", action= StandardJaxrsAction.class )
	@GET
	@Path("{application}/{infoId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByApplicationAndInfoId(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @PathParam("application") String application,
			@PathParam("infoId") String infoId) {
		ActionResult<List<WrapOutHotPictureInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutHotPictureInfo> wraps = null;
		List<HotPictureInfo> hotPictureInfos = null;
		Boolean check = true;

		if (check) {
			if (application == null || application.isEmpty() || "(0)".equals(application)) {
				check = false;
				Exception exception = new InfoApplicationEmptyException();
				result.error(exception);
				// logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (infoId == null || infoId.isEmpty() || "(0)".equals(infoId)) {
				check = false;
				Exception exception = new InfoIdEmptyException();
				result.error(exception);
				// logger.error( e, effectivePerson, request, null);
			}
		}

		String cacheKey = "list#" + application + "#" + infoId;
		Element element = cache.get(cacheKey);

		if (check) {
			if (null != element) {
				wraps = (List<WrapOutHotPictureInfo>) element.getObjectValue();
				result.setData(wraps);
			} else {
				if (check) {
					try {
						hotPictureInfos = hotPictureInfoService.listByApplicationInfoId(application, infoId);
					} catch (Exception e) {
						check = false;
						Exception exception = new InfoListByApplicationException(e, application, infoId);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
				if (check) {
					if (hotPictureInfos != null && !hotPictureInfos.isEmpty()) {
						try {
							wraps = wrapout_copier.copy(hotPictureInfos);
							cache.put(new Element(cacheKey, wraps));
							result.setData(wraps);
						} catch (Exception e) {
							check = false;
							Exception exception = new InfoWrapOutException(e);
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
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

	/**
	 * 保存热图信息，登录用户访问
	 * 
	 * @param request
	 * @return
	 */
	// @HttpMethodDescribe(value = "创建新的热图信息或者更新热图信息.", request =
	// JsonElement.class, response = WrapOutId.class)
	@JaxrsMethodDescribe(value = "创建新的热图信息或者更新热图信息", action= StandardJaxrsAction.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInHotPictureInfo wrapIn = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutId wrap = null;
		Boolean check = true;
		HotPictureInfo hotPictureInfo = null;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WrapInHotPictureInfo.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new WrapInConvertException(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			if (wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty()) {
				check = false;
				Exception exception = new InfoTitleEmptyException();
				result.error(exception);
				// logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (wrapIn.getUrl() == null || wrapIn.getUrl().isEmpty()) {
				check = false;
				Exception exception = new InfoUrlEmptyException();
				result.error(exception);
				// logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				hotPictureInfo = wrapin_copier.copy(wrapIn);
			} catch (Exception e) {
				check = false;
				Exception exception = new InfoWrapInException(e);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				hotPictureInfo = hotPictureInfoService.save(hotPictureInfo);
				wrap = new WrapOutId(hotPictureInfo.getId());
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new InfoSaveException(e);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
			try {
				ApplicationCache.notify(HotPictureInfo.class);
			} catch (Exception e) {
				logger.warn("system notify application cache got an exception!");
				logger.error(e);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/**
	 * 修改已经存在的热点图片的标题信息
	 * 
	 * @param request
	 * @return
	 */
	@JaxrsMethodDescribe(value = "修改已经存在的热点图片的标题信息", action= StandardJaxrsAction.class )
	@Path("changeTitle")
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void changeTitle(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInHotPictureInfo wrapIn = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutId wrap = null;
		Boolean check = true;
		List<HotPictureInfo> hotPictureInfos = null;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WrapInHotPictureInfo.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new WrapInConvertException(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (wrapIn.getApplication() == null || wrapIn.getApplication().isEmpty()
					|| "(0)".equals(wrapIn.getApplication())) {
				check = false;
				Exception exception = new InfoApplicationEmptyException();
				result.error(exception);
			}
		}
		if (check) {
			if (wrapIn.getInfoId() == null || wrapIn.getInfoId().isEmpty() || "(0)".equals(wrapIn.getInfoId())) {
				check = false;
				Exception exception = new InfoIdEmptyException();
				result.error(exception);
			}
		}

		if (check) {
			if (wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty()) {
				check = false;
				Exception exception = new InfoTitleEmptyException();
				result.error(exception);
			}
		}

		if (check) {
			try {
				hotPictureInfos = hotPictureInfoService.listByApplicationInfoId(wrapIn.getApplication(),
						wrapIn.getInfoId());
			} catch (Exception e) {
				check = false;
				Exception exception = new InfoListByApplicationException(e, wrapIn.getApplication(),
						wrapIn.getInfoId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (ListTools.isNotEmpty(hotPictureInfos)) {
				for (HotPictureInfo hotPictureInfo : hotPictureInfos) {
					try {
						hotPictureInfoService.changeTitle(hotPictureInfo.getId(), wrapIn.getTitle());
						wrap = new WrapOutId(hotPictureInfo.getId());
					} catch (Exception e) {
						check = false;
						Exception exception = new InfoSaveException(e);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
			try {
				ApplicationCache.notify(HotPictureInfo.class);
			} catch (Exception e) {
				logger.warn("system notify application cache got an exception!");
				logger.error(e);
			}
			result.setData(wrap);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	// @HttpMethodDescribe( value = "根据ID删除指定的热图信息.", response = WrapOutId.class
	// )
	@JaxrsMethodDescribe(value = "根据ID删除指定的热图信息", action= StandardJaxrsAction.class )
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutId wrap = null;
		HotPictureInfo hotPictureInfo = null;
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
				hotPictureInfo = hotPictureInfoService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new InfoQueryByIdException(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && hotPictureInfo != null) {
			try {
				hotPictureInfoService.delete(id);
				wrap = new WrapOutId(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new InfoDeleteException(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
			try {
				ApplicationCache.notify(HotPictureInfo.class);
			} catch (Exception e) {
				logger.warn("system notify application cache got an exception!");
				logger.error(e);
			}
		}
		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	// @HttpMethodDescribe( value = "根据应用类型以及信息ID删除热图信息.", response =
	// WrapOutId.class )
	@JaxrsMethodDescribe(value = "根据应用类型以及信息ID删除热图信息", action= StandardJaxrsAction.class )
	@DELETE
	@Path("{application}/{infoId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("application") String application, @PathParam("infoId") String infoId) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutId wrap = null;
		List<HotPictureInfo> hotPictureInfos = null;
		Boolean check = true;

		if (check) {
			if (application == null || application.isEmpty() || "(0)".equals(application)) {
				check = false;
				Exception exception = new InfoApplicationEmptyException();
				result.error(exception);
			}
		}
		if (check) {
			if (infoId == null || infoId.isEmpty() || "(0)".equals(infoId)) {
				check = false;
				Exception exception = new InfoIdEmptyException();
				result.error(exception);
			}
		}
		if (check) {
			try {
				hotPictureInfos = hotPictureInfoService.listByApplicationInfoId(application, infoId);
			} catch (Exception e) {
				check = false;
				Exception exception = new InfoListByApplicationException(e, application, infoId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (ListTools.isNotEmpty(hotPictureInfos)) {
				for (HotPictureInfo hotPictureInfo : hotPictureInfos) {
					try {
						hotPictureInfoService.deleteWithInfoId(hotPictureInfo.getInfoId());
						wrap = new WrapOutId(hotPictureInfo.getInfoId());
						result.setData(wrap);
					} catch (Exception e) {
						check = false;
						Exception exception = new InfoDeleteException(e, application, infoId);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
				try {
					ApplicationCache.notify(HotPictureInfo.class);
				} catch (Exception e) {
					logger.warn("system notify application cache got an exception!");
				}
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}