package com.x.cms.assemble.control.jaxrs.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.google.gson.JsonElement;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.script.exception.WrapInConvertException;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.element.Script;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("script")
public class ScriptAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( ScriptAction.class );
	private LogService logService = new LogService();
	private Ehcache cache = ApplicationCache.instance().getCache( Script.class );
	
	@HttpMethodDescribe( value = "获取指定的脚本信息.", response = WrapOutScript.class )
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutScript> result = new ActionResult<>();
		WrapOutScript wrap = null;
		
		String cacheKey = "script.get." + id;
		Element element = null;
		element = cache.get(cacheKey);	
		if( element != null ){
			wrap = (WrapOutScript) element.getObjectValue();
			result.setData( wrap );
		}else{
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Script script = emc.find(id, Script.class);
				if (null == script) {
					throw new Exception("[get]script{id:" + id + "} not existed.");
				}
				wrap = new WrapOutScript();
				WrapTools.script_wrapout_copier.copy(script, wrap);
				result.setData(wrap);
				
				//将查询结果放进缓存里
				cache.put( new Element( cacheKey, wrap ) );
				
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建脚本.", response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInScript wrapIn = null;
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInScript.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				AppInfo appInfo = emc.find(wrapIn.getAppId(), AppInfo.class);
				if (null == appInfo) {
					throw new Exception("[post]appinfo{id:" + wrapIn.getAppId() + "} not existed.");
				}
				logger.debug("[post]system try to save script.");
				emc.beginTransaction(Script.class);
				Script script = new Script();
				wrapIn.copyTo(script);
				script.setCreatorPerson( currentPerson.getName() );
				script.setLastUpdatePerson( currentPerson.getName() );
				script.setLastUpdateTime(new Date());
				emc.persist(script, CheckPersistType.all);
				emc.commit();
				logger.debug("[post]script{'id':'"+script.getId()+"'} has saved.");
				
				logger.debug("[post]System try to remove all Script cache......" );
				//清除所有的Script缓存
				ApplicationCache.notify( Script.class );
				
				//记录日志
				emc.beginTransaction( Log.class );
				logService.log( emc,  currentPerson.getName(), script.getName(), script.getAppId(), "", "", script.getId(), "SCRIPT", "新增" );
				emc.commit();
				wrap = new WrapOutId(script.getId());
				wrap.copyTo(script);
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新脚本.", response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapInScript wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInScript.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}

		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Script script = emc.find(id, Script.class);
				if (null == script) {
					throw new Exception("script{id:" + id + "} not existed.");
				}
				emc.beginTransaction(Script.class);
				wrapIn.copyTo(script);
				script.setLastUpdatePerson( currentPerson.getName() );
				script.setLastUpdateTime(new Date());
				emc.commit();
				//清除所有的Script缓存
				ApplicationCache.notify( Script.class );
				
				//记录日志
				emc.beginTransaction( Log.class );
				logService.log( emc,  currentPerson.getName(), script.getName(),script.getAppId(), "", "", script.getId(), "SCRIPT", "更新" );
				emc.commit();
				wrap = new WrapOutId(script.getId());
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除脚本.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("[delete]user[" + currentPerson.getName() + "] try to delete script{'id':'"+id+"'}.");
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Script script = emc.find(id, Script.class);
			if (null == script) {
				throw new Exception("script{id:" + id + "} not existed.");
			}
			emc.beginTransaction(Script.class);
			emc.remove(script, CheckRemoveType.all);
			emc.commit();
			//清除所有的Script缓存
			ApplicationCache.notify( Script.class );
			
			//记录日志
			emc.beginTransaction( Log.class );
			logService.log( emc,  currentPerson.getName(), script.getName(), script.getAppId(), "", "", script.getId(), "SCRIPT", "删除" );
			emc.commit();
			result.setData(new WrapOutId(script.getId()));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "列示应用所有脚本.", response = WrapOutScript.class)
	@GET
	@Path("list/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithApp(@Context HttpServletRequest request, @PathParam("appId") String appId) {
		ActionResult<List<WrapOutScript>> result = new ActionResult<>();
		List<WrapOutScript> wraps = new ArrayList<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("[listWithApp]user[" + currentPerson.getName() + "] try to list script with appInfo{'id':'"+appId+"'}.");
		
		String cacheKey = "script.listWithApp.appId." + appId;
		Element element = null;
		element = cache.get(cacheKey);		
		if( element != null ){
			logger.debug("[listWithApp]get scriptlist from cache. cacheKey="+cacheKey );
			wraps = ( List<WrapOutScript> ) element.getObjectValue();
			result.setData( wraps );
		}else{
			logger.debug("[listWithApp]no scriptlist cache exists for appinfo{'id':'"+appId+"'}. cacheKey=" + cacheKey );
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				AppInfo appInfo = emc.find( appId, AppInfo.class);
				if (null == appInfo) {
					throw new Exception("[listWithApp]appInfo{id:" + appId + "} not existed.");
				}
				List<String> ids = business.getScriptFactory().listWithApp(appInfo.getId());
				
				for (Script o : emc.list(Script.class, ids)) {
					WrapOutScript wrap = new WrapOutScript();
					WrapTools.script_wrapout_copier.copy(o, wrap);
					wraps.add(wrap);
				}
				Collections.sort(wraps, new Comparator<WrapOutScript>() {
					public int compare(WrapOutScript o1, WrapOutScript o2) {
						return ObjectUtils.compare(o1.getName(), o2.getName(), true);
					}
				});
				result.setData(wraps);
				
				//将查询结果放进缓存里
				logger.debug("[listWithApp]push scriptlist to cache. appinfo{'id':'"+appId+"'}, cacheKey="+cacheKey );
				cache.put( new Element( cacheKey, wraps ) );
				
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据应用ID和脚本名称获取脚本.", response = WrapOutScript.class)
	@GET
	@Path("list/app/{appId}/name/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppWithName(@Context HttpServletRequest request, @PathParam("appId") String appId, @PathParam("name") String name) {
		ActionResult<WrapOutScript> result = new ActionResult<>();
		WrapOutScript wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("[getWithAppWithName]user[" + currentPerson.getName() + "] try to get script with script{'appId':'"+appId+"','name':'"+name+"']}.");
		
		String cacheKey = "script.getWithAppWithName.appId." + appId+".name."+name;
		Element element = null;
		element = cache.get(cacheKey);	
		if( element != null ){
			logger.debug("[getWithAppWithName]get script from cache. cacheKey="+cacheKey );
			wrap = ( WrapOutScript ) element.getObjectValue();
			result.setData( wrap );
		}else{
			logger.debug("[getWithAppWithName]no script cache exists for script{'appId':'"+appId+"','name':'"+name+"']}. cacheKey=" + cacheKey );
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				AppInfo appInfo = emc.find( appId, AppInfo.class);
				if (null == appInfo) {
					throw new Exception("[getWithAppWithName]appInfo{id:" + appId + "} not existed.");
				}
				String id = business.getScriptFactory().getWithAppWithName( appInfo.getId(), name);
				if (StringUtils.isNotEmpty(id)) {
					Script script = emc.find(id, Script.class);
					wrap = new WrapOutScript();
					wrap.copyTo( script );
				} else {
					throw new Exception("[getWithAppWithName]script not existed with name or alias : " + name + ".");
				}
				result.setData(wrap);
				
				//将查询结果放进缓存里
				logger.debug("[getWithAppWithName]push script{'appId':'"+appId+"','name':'"+name+"']} to cache. cacheKey="+cacheKey );
				cache.put( new Element( cacheKey, wrap ) );
				
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示Script对象,下一页.", response = WrapOutId.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListNext(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count) {
		ActionResult<List<WrapOutScript>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("[standardListNext]user[" + currentPerson.getName() + "] try to list script nextpage {'id':'"+id+"','count':'"+count+"'}.");
		try {
			result = this.standardListNext( WrapTools.script_wrapout_copier, id, count, "sequence", null, null, null, null, null, null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示Script对象,上一页.", response = WrapOutId.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListPrev(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count) {
		ActionResult<List<WrapOutScript>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("[standardListNext]user[" + currentPerson.getName() + "] try to list script prevpage {'id':'"+id+"','count':'"+count+"'}.");
		try {
			result = this.standardListPrev( WrapTools.script_wrapout_copier, id, count, "sequence", null, null, null, null, null, null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取Script以及依赖脚本内容。", response = WrapOutScriptNested.class)
	@POST
	@Path("{uniqueName}/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getScriptNested(@Context HttpServletRequest request, @PathParam("uniqueName") String uniqueName, @PathParam("appId") String appId, JsonElement jsonElement) {
		ActionResult<WrapOutScriptNested> result = new ActionResult<>();
		WrapOutScriptNested wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapInScriptNested wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInScriptNested.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Business business = new Business(emc);
				AppInfo appInfo = business.getAppInfoFactory().get( appId );
				if ( null == appInfo ) {
					throw new Exception("appInfo{id:" + appId + "} not existed.");
				}
				List<Script> list = new ArrayList<>();
				for (Script o : business.getScriptFactory().listScriptNestedWithAppInfoWithUniqueName( appInfo.getId(), uniqueName)) {
					if ((!wrapIn.getImportedList().contains(o.getAlias())) && (!wrapIn.getImportedList().contains(o.getName())) && (!wrapIn.getImportedList().contains(o.getId()))) {
						list.add(o);
					}
				}
				StringBuffer buffer = new StringBuffer();
				List<String> imported = new ArrayList<>();
				for (Script o : list) {
					buffer.append(o.getText());
					buffer.append(SystemUtils.LINE_SEPARATOR);
					imported.add(o.getId());
					imported.add(o.getName());
					imported.add(o.getAlias());
				}
				wrap = new WrapOutScriptNested();
				wrap.setImportedList(imported);
				wrap.setText(buffer.toString());
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}