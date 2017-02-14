package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import java.util.ArrayList;
import java.util.Collections;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.ViewFieldConfigFactory;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewFieldConfig;


@Path("viewfieldconfig")
public class ViewFieldConfigAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( ViewFieldConfigAction.class );
	private LogService logService = new LogService();
	private BeanCopyTools<ViewFieldConfig, WrapOutViewFieldConfig> copier = BeanCopyToolsBuilder.create( ViewFieldConfig.class, WrapOutViewFieldConfig.class, null, WrapOutViewFieldConfig.Excludes);
	
	@HttpMethodDescribe(value = "获取全部的展示列配置信息列表", response = WrapOutViewFieldConfig.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllView(@Context HttpServletRequest request ) {		
		ActionResult<List<WrapOutViewFieldConfig>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutViewFieldConfig> wraps = null;
		logger.debug("user["+ currentPerson.getName() +"] try to get all viewFieldConfig......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);			
			//如判断用户是否有查看所有展示列配置信息的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有查询全部展示列配置信息配置的权限！");
			}			
			//如果有权限，继续操作
			ViewFieldConfigFactory viewFieldConfigFactory  = business.getViewFieldConfigFactory();
			List<String> ids = viewFieldConfigFactory.listAll();//获取所有展示列配置信息列表
			List<ViewFieldConfig> viewFieldConfigList = viewFieldConfigFactory.list( ids );//查询ID IN ids 的所有展示列配置信息信息列表
			wraps = copier.copy( viewFieldConfigList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort( wraps );//对查询的列表进行排序		
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取指定视图的全部展示列配置信息列表", response = WrapOutViewFieldConfig.class)
	@GET
	@Path("list/view/{viewId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listViewFieldConfigByViewId(@Context HttpServletRequest request, @PathParam("viewId")String viewId ) {		
		ActionResult<List<WrapOutViewFieldConfig>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutViewFieldConfig> wraps = null;
		logger.debug("user["+currentPerson.getName()+"] try to get all viewFieldConfig in view{'id':'"+viewId+"'}......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);			
			//如判断用户是否有查看所有展示列配置信息的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有查询全部展示列配置信息的权限！");
			}			
			//如果有权限，继续操作
			ViewFieldConfigFactory viewFieldConfigFactory = business.getViewFieldConfigFactory();
			List<String> ids = viewFieldConfigFactory.listByViewId( viewId );//获取指定应用的所有展示列配置信息列表
			List<ViewFieldConfig> viewFieldConfigList = viewFieldConfigFactory.list( ids );//查询ID IN ids 的所有展示列配置信息信息列表
			wraps = copier.copy( viewFieldConfigList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort( wraps );//对查询的列表进行排序		
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取viewFieldConfig对象.", response = WrapOutViewFieldConfig.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutViewFieldConfig> result = new ActionResult<>();
		WrapOutViewFieldConfig wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);		
		logger.debug("user[" + currentPerson.getName() + "] try to get viewFieldConfig{'id':'"+id+"'}......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ViewFieldConfig viewFieldConfig = business.getViewFieldConfigFactory().get(id);
			if ( null == viewFieldConfig ) {
				throw new Exception("viewFieldConfig{id:" + id + "} 信息不存在.");
			}
			//如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
			BeanCopyTools<ViewFieldConfig, WrapOutViewFieldConfig> copier = BeanCopyToolsBuilder.create(ViewFieldConfig.class, WrapOutViewFieldConfig.class, null, WrapOutViewFieldConfig.Excludes);
			wrap = new WrapOutViewFieldConfig();
			copier.copy(viewFieldConfig, wrap);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	
	@HttpMethodDescribe(value = "创建View应用信息对象.", request = WrapInViewFieldConfig.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInViewFieldConfig wrapIn) {
		logger.debug("[POST]尝试保存展示列信息，Used id in data, id=" + wrapIn.getId() );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//获取到当前用户信息
			EffectivePerson currentPerson = this.effectivePerson(request);			
			Business business = new Business(emc);
			//看看用户是否有权限进行应用信息新增操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("[POST]person{name:" + currentPerson.getName() + "} 用户没有内容管理展示列配置信息信息操作的权限！");
			}			
			//用户有权限进行信息操作
			BeanCopyTools<WrapInViewFieldConfig, ViewFieldConfig> copier = BeanCopyToolsBuilder.create( WrapInViewFieldConfig.class, ViewFieldConfig.class, null, WrapInViewFieldConfig.Excludes );
			ViewFieldConfig viewFieldConfig = new ViewFieldConfig();
			copier.copy( wrapIn, viewFieldConfig );
			//如果JSON给过来的ID不为空，那么使用用户传入的ID
			if( wrapIn.getId().length() == viewFieldConfig.getId().length() ){
				if( business.getViewFieldConfigFactory().get( wrapIn.getId() ) == null ){
					logger.debug("[POST]Used id in data, id=" + wrapIn.getId() );
					viewFieldConfig.setId( wrapIn.getId() );
				}
			}
			//查询视图信息
			View view = business.getViewFactory().get( viewFieldConfig.getViewId() );
			if( view == null ){
				logger.debug("[POST]view{'id':'"+viewFieldConfig.getViewId()+"'}不存在, 尝试新增一个视图信息！");
				//新增一个新的视图
				view = new View();
				view.setId( viewFieldConfig.getViewId() );
				view.setAppId("");
				view.setContent("");
				view.setCreateTime( new Date());
				view.setDescription("系统自动创建");
				view.setEditor( currentPerson.getName() );
				view.setFormId( "" );
				view.setName("无标题");
				view.setOrderField("");
				view.setOrderFieldType("CREATETIME");
				view.setOrderType("DESC");
				view.setPageSize(10);
				view.setSequence("");
				view.setUpdateTime(new Date());
				emc.beginTransaction( View.class);
				emc.persist( view );
				emc.commit();
				logger.debug("[POST]view{'id':'"+viewFieldConfig.getViewId()+"'}成功！");
			}
			logger.debug("[POST]System trying to beginTransaction to save viewFieldConfig......" );
			emc.beginTransaction( ViewFieldConfig.class );
			emc.beginTransaction( View.class);
			emc.persist( viewFieldConfig, CheckPersistType.all );
			
			addFieldConfigIdToFieldConfigList( view, viewFieldConfig.getId() );
			
			emc.commit();
			logger.debug("[POST]System save viewFieldConfig success......" );
			//成功上传一个展示列配置信息信息
			logger.debug("[POST]System trying to save log......" );
			emc.beginTransaction( Log.class );
			logService.log( emc,  currentPerson.getName(), "成功新增一个展示列配置信息信息", "", "", "", viewFieldConfig.getId(), "VIEWFIELDCONFIG", "新增" );
			emc.commit();
			wrap = new WrapOutId( viewFieldConfig.getId() );
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新View信息对象.", request = WrapInViewFieldConfig.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInViewFieldConfig wrapIn) {
		logger.debug("[PUT]method put has been called, try to update viewFieldConfig......" );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			ViewFieldConfig viewFieldConfig = business.getViewFieldConfigFactory().get(id);
			
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if ( !business.viewEditAvailable( request, currentPerson ) ) {
				throw new Exception("[PUT]person{name:" + currentPerson.getName() + "} has sufficient permissions");
			}
			
			//判断配置项是否存在，如果存在则更新，如果不存在则新增一个			
			//查询视图信息
			View view = business.getViewFactory().get( wrapIn.getViewId() );
			if( view == null ){
				throw new Exception("[PUT]view{'id':'"+viewFieldConfig.getViewId()+"'}不存在, 无法继续进行查询操作！");
			}
			//进行数据库持久化操作
			BeanCopyTools<WrapInViewFieldConfig, ViewFieldConfig> copier = BeanCopyToolsBuilder.create( WrapInViewFieldConfig.class, ViewFieldConfig.class, null, WrapInViewFieldConfig.Excludes);
			logger.debug("[PUT]System trying to beginTransaction to update appInfo......" );
			emc.beginTransaction( ViewFieldConfig.class);
			emc.beginTransaction( View.class);
			copier.copy( wrapIn, viewFieldConfig );
			if ( viewFieldConfig == null ) {
				logger.debug("[PUT]viewFieldConfig{id:" + id + "} 应用信息不存在，立即进行新增操作.");
				emc.persist( viewFieldConfig, CheckPersistType.all );
			}else{
				emc.check( viewFieldConfig, CheckPersistType.all);	
			}
			//检查视图和展示列配置的关联关系，如果没有则添加新的关联
			addFieldConfigIdToFieldConfigList( view, viewFieldConfig.getId() );
			
			emc.commit();
			logger.debug("[PUT]System update appInfo success......" );
			//成功更新一个应用信息
			logger.debug("[PUT]System trying to save log......" );
			emc.beginTransaction( Log.class);
			logService.log( emc,  currentPerson.getName(), "成功更新一个展示列配置信息信息", "", "", "", viewFieldConfig.getId(), "VIEWFIELDCONFIG", "更新" );
			emc.commit();
			wrap = new WrapOutId( viewFieldConfig.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除View应用信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson currentPerson = this.effectivePerson(request);
			Business business = new Business(emc);
			
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			ViewFieldConfig viewFieldConfig = business.getViewFieldConfigFactory().get(id);
			
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("viewFieldConfig{name:" + currentPerson.getName() + "} 用户没有内容管理应用信息操作的权限！");
			}
			
			//查询视图信息
			View view = business.getViewFactory().get( viewFieldConfig.getViewId() );
			if( view == null ){
				throw new Exception("view{'id':'"+viewFieldConfig.getViewId()+"'}不存在, 无法继续进行查询操作！");
			}
			//进行数据库持久化操作
			logger.debug("System trying to beginTransaction to delete viewFieldConfig......" );
			emc.beginTransaction( ViewFieldConfig.class );
			emc.beginTransaction( View.class);
			if ( null == viewFieldConfig ) {
				logger.debug("viewFieldConfig{id:" + id + "} 应用信息不存在.");
			}else{
				emc.remove( viewFieldConfig, CheckRemoveType.all );
			}
			deleteFieldConfigIdFromFieldConfigList( view, id );
			
			emc.commit();
			logger.debug("System delete viewFieldConfig success......" );
			//成功删除一个展示列配置信息信息
			logger.debug("System trying to save log......" );
			emc.beginTransaction( Log.class );
			logService.log( emc,  currentPerson.getName(), "成功删除一个展示列配置信息信息", "", "", "", viewFieldConfig.getId(), "VIEWFIELDCONFIG", "删除" );
			emc.commit();
			wrap = new WrapOutId( viewFieldConfig.getId() );
			result.setData( wrap );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	private void addFieldConfigIdToFieldConfigList( View view, String viewFieldConfigId){
		if( view != null ){
			if( view.getFieldConfigList() == null ){
				logger.debug("view.FieldConfigList is null, init to an arraylist" );
				view.setFieldConfigList(new ArrayList<String>());
			}
			//看看是否已经包含配置ID
			if( !view.getFieldConfigList().contains( viewFieldConfigId )){
				logger.debug("view.FieldConfigList add new viewFieldConfig{'id':'"+ viewFieldConfigId +"'}." );
				view.getFieldConfigList().add( viewFieldConfigId );
			}else{
				logger.debug("view.FieldConfigList is contains viewFieldConfig{'id':'"+ viewFieldConfigId +"'}." );
			}
		}else{
			logger.debug("view is null, can not save FieldConfigList info." );
		}
	}
	
	private void deleteFieldConfigIdFromFieldConfigList( View view, String viewFieldConfigId){
		if( view != null ){
			if( view.getFieldConfigList() == null ){
				logger.debug("view.FieldConfigList is null, init to an arraylist" );
				view.setFieldConfigList(new ArrayList<String>());
			}
			//看看是否已经包含分类ID
			if( !view.getFieldConfigList().contains( viewFieldConfigId )){
				logger.debug("view.FieldConfigList delete viewFieldConfig{'id':'"+ viewFieldConfigId +"'}." );
				view.getFieldConfigList().remove( viewFieldConfigId );
			}else{
				logger.debug("view.FieldConfigList is contains viewFieldConfig{'id':'"+ viewFieldConfigId +"'}." );
			}
		}else{
			logger.debug("view is null, can not save FieldConfigList info." );
		}
	}
}