package com.x.cms.assemble.control.jaxrs.appdict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;

@Path("appdict")
public class AppDictAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( AppDictAction.class );
	private LogService logService = new LogService();
	
	@HttpMethodDescribe(value = "获取Application的数据字典列表.", response = WrapOutAppDict.class)
	@GET
	@Path("list/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithAppId(@Context HttpServletRequest request, @PathParam("appId") String appId) {
		ActionResult<List<WrapOutAppDict>> result = new ActionResult<>();
		List<WrapOutAppDict> wraps = new ArrayList<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);			
			AppInfo appInfo = emc.find(appId, AppInfo.class);
			if (null == appInfo) {
				throw new Exception("appInfo{id:" + appId + "} not existed.");
			}
			List<String> ids = business.getAppDictFactory().listWithAppInfo(appId);
			for (AppDict o : emc.list(AppDict.class, ids)) {
				wraps.add(new WrapOutAppDict(o));
			}
			Collections.sort(wraps, new Comparator<WrapOutAppDict>() {
				public int compare(WrapOutAppDict o1, WrapOutAppDict o2) {
					return ObjectUtils.compare(o1.getName(), o2.getName(), true);
				}
			});
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取单个数据字典以及数据字典数据.", response = WrapOutAppDict.class)
	@GET
	@Path("{appDictId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId) {
		ActionResult<WrapOutAppDict> result = new ActionResult<>();
		WrapOutAppDict wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			AppDict appDict = emc.find( appDictId, AppDict.class );
			if (null == appDict) {
				throw new Exception("appDict{id:" + appDictId + "} not existed.");
			}
			AppInfo appInfo = emc.find( appDict.getAppId(), AppInfo.class);
			if (null == appInfo) {
				throw new Exception("appInfo{id:" + appDict.getAppId() + "} not existed with appDictId{id:" + appDictId + "}");
			}
			wrap = new WrapOutAppDict( appDict );
			List<AppDictItem> items = business.getAppDictItemFactory().listEntityWithAppDict(appDictId);
			/* 由于需要排序重新生成可排序List */
			items = new ArrayList<>(items);
			ItemConverter<AppDictItem> converter = new ItemConverter<>(AppDictItem.class);
			JsonElement json = converter.assemble(items);
			wrap.setData(json);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建数据字典以及数据.", request = WrapInAppDict.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInAppDict wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			AppInfo appInfo = emc.find(wrapIn.getAppId(), AppInfo.class);
			if (null == appInfo) {
				throw new Exception("application{id:" + wrapIn.getAppId() + "} not existed.");
			}
			logger.debug("[post]system try to save new appdict......");
			emc.beginTransaction(AppDict.class);
			emc.beginTransaction(AppDictItem.class);
			AppDict appDict = new AppDict();
			wrapIn.copyTo(appDict, JpaObject.ID_DISTRIBUTEFACTOR);
			emc.persist(appDict, CheckPersistType.all);
			ItemConverter<AppDictItem> converter = new ItemConverter<>(AppDictItem.class);
			List<AppDictItem> list = converter.disassemble(wrapIn.getData());
			for (AppDictItem o : list) {
				o.setAppDictId(appDict.getId());
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			//记录日志
			emc.beginTransaction( Log.class );
			logService.log( emc, currentPerson.getName(), "用户["+currentPerson.getName()+"]成功新增一个数据字典信息", appDict.getAppId(), "", "", appDict.getId(), "DICT", "新增" );
			emc.commit();
			wrap = new WrapOutId(appDict.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新数据字典以及数据.", request = WrapInAppDict.class, response = WrapOutId.class)
	@PUT
	@Path("{appDictId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId, WrapInAppDict wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			AppDict dict = emc.find( appDictId, AppDict.class);
			if (null == dict) {
				throw new Exception("appDict{id:" + appDictId + "} not existed.");
			}
			AppInfo appInfo = emc.find(wrapIn.getAppId(), AppInfo.class);
			if (null == appInfo) {
				throw new Exception("appInfo{id:" + wrapIn.getAppId() + "} not existed.");
			}
			emc.beginTransaction(AppDict.class);
			emc.beginTransaction(AppDictItem.class);
			wrapIn.copyTo(dict, JpaObject.ID, JpaObject.DISTRIBUTEFACTOR, "application");
			emc.check(dict, CheckPersistType.all);
			ItemConverter<AppDictItem> converter = new ItemConverter<>(AppDictItem.class);
			List<AppDictItem> exists = business.getAppDictItemFactory().listEntityWithAppDict( appDictId );
			List<AppDictItem> currents = converter.disassemble(wrapIn.getData());
			List<AppDictItem> removes = converter.subtract(exists, currents);
			List<AppDictItem> adds = converter.subtract(currents, exists);
			for (AppDictItem o : removes) {
				emc.remove(o);
			}
			for (AppDictItem o : adds) {
				o.setAppDictId( dict.getId() );
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			//记录日志
			emc.beginTransaction( Log.class );
			logService.log( emc, currentPerson.getName(), "用户["+currentPerson.getName()+"]成功更新一个数据字典信息", dict.getAppId(), "", "", dict.getId(), "DICT", "更新" );
			emc.commit();
			wrap = new WrapOutId(dict.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除指定的数据字典以及数据字典数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			AppDict dict = emc.find( appDictId, AppDict.class);
			if (null == dict) {
				throw new Exception("appDict{id:" + appDictId + "} not existed.");
			}
			AppInfo appInfo = emc.find(dict.getAppId(), AppInfo.class);
			if (null == appInfo) {
				throw new Exception("appInfo{id:" + dict.getAppId() + "} not existed.");
			}
			emc.beginTransaction(AppDict.class);
			emc.beginTransaction(AppDictItem.class);
			List<String> ids = business.getAppDictItemFactory().listWithAppDict( appDictId );
			emc.delete( AppDictItem.class, ids );
			emc.remove( dict, CheckRemoveType.all );
			emc.commit();
			//记录日志
			emc.beginTransaction( Log.class );
			logService.log( emc, currentPerson.getName(), "用户["+currentPerson.getName()+"]成功删除一个数据字典信息", dict.getAppId(), "", "", dict.getId(), "DICT", "删除" );
			emc.commit();
			wrap = new WrapOutId(dict.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}