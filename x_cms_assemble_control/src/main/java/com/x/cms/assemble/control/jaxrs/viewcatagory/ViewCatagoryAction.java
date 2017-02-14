package com.x.cms.assemble.control.jaxrs.viewcatagory;

import java.util.Collections;
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
import com.x.cms.assemble.control.factory.ViewCatagoryFactory;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.element.ViewCatagory;


@Path("viewcatagory")
public class ViewCatagoryAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( ViewCatagoryAction.class );
	private LogService logService = new LogService();
	private BeanCopyTools<ViewCatagory, WrapOutViewCatagory> copier = BeanCopyToolsBuilder.create( ViewCatagory.class, WrapOutViewCatagory.class, null, WrapOutViewCatagory.Excludes);
	
	@HttpMethodDescribe(value = "获取全部的视图分类关联信息列表", response = WrapOutViewCatagory.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllViewCatagory(@Context HttpServletRequest request ) {		
		ActionResult<List<WrapOutViewCatagory>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutViewCatagory> wraps = null;
		logger.debug("user["+ currentPerson.getName() +"] try to get all viewCatagory......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);			
			//如判断用户是否有查看所有视图分类关联信息的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有查询全部视图分类关联信息配置的权限！");
			}			
			//如果有权限，继续操作
			ViewCatagoryFactory viewCatagoryFactory  = business.getViewCatagoryFactory();
			List<String> ids = viewCatagoryFactory.listAll();//获取所有视图分类关联信息列表
			List<ViewCatagory> viewCatagoryList = viewCatagoryFactory.list( ids );//查询ID IN ids 的所有视图分类关联信息信息列表
			wraps = copier.copy( viewCatagoryList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort( wraps );//对查询的列表进行排序		
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取指定分类的全部视图分类关联信息信息列表", response = WrapOutViewCatagory.class)
	@GET
	@Path("list/catagory/{catagoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByCatagoryId(@Context HttpServletRequest request, @PathParam("catagoryId")String catagoryId ) {		
		ActionResult<List<WrapOutViewCatagory>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutViewCatagory> wraps = null;
		logger.debug("user["+currentPerson.getName()+"] try to get all viewCatagory in catagoryInfo{'id':'"+catagoryId+"'}......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);			
			//如判断用户是否有查看所有视图分类关联信息的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有查询全部视图分类关联信息的权限！");
			}			
			//如果有权限，继续操作
			ViewCatagoryFactory viewCatagoryFactory  = business.getViewCatagoryFactory();
			List<String> ids = viewCatagoryFactory.listByCatagoryId( catagoryId );//获取指定应用的所有视图分类关联信息列表
			List<ViewCatagory> viewList = viewCatagoryFactory.list( ids );//查询ID IN ids 的所有视图分类关联信息信息列表
			wraps = copier.copy( viewList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort( wraps );//对查询的列表进行排序		
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取指定视图的全部视图分类关联信息信息列表", response = WrapOutViewCatagory.class)
	@GET
	@Path("list/view/{viewId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByViewId(@Context HttpServletRequest request, @PathParam("viewId")String viewId ) {		
		ActionResult<List<WrapOutViewCatagory>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutViewCatagory> wraps = null;
		logger.debug("user["+currentPerson.getName()+"] try to get all viewCatagory in view{'id':'"+viewId+"'}......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);			
			//如判断用户是否有查看所有视图分类关联信息的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有查询全部视图分类关联信息的权限！");
			}			
			//如果有权限，继续操作
			ViewCatagoryFactory viewCatagoryFactory  = business.getViewCatagoryFactory();
			List<String> ids = viewCatagoryFactory.listByViewId( viewId );//获取指定应用的所有视图分类关联信息列表
			List<ViewCatagory> viewList = viewCatagoryFactory.list( ids );//查询ID IN ids 的所有视图分类关联信息信息列表
			wraps = copier.copy( viewList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort( wraps );//对查询的列表进行排序		
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取view对象.", response = WrapOutViewCatagory.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutViewCatagory> result = new ActionResult<>();
		WrapOutViewCatagory wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);		
		logger.debug("user[" + currentPerson.getName() + "] try to get view{'id':'"+id+"'}......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ViewCatagory view = business.getViewCatagoryFactory().get(id);
			if ( null == view ) {
				throw new Exception("view{id:" + id + "} 信息不存在.");
			}
			//如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
			BeanCopyTools<ViewCatagory, WrapOutViewCatagory> copier = BeanCopyToolsBuilder.create( ViewCatagory.class, WrapOutViewCatagory.class, null, WrapOutViewCatagory.Excludes);
			wrap = new WrapOutViewCatagory();
			copier.copy(view, wrap);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	
	@HttpMethodDescribe(value = "创建View应用信息对象.", request = WrapInViewCatagory.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInViewCatagory wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//获取到当前用户信息
			EffectivePerson currentPerson = this.effectivePerson(request);			
			Business business = new Business(emc);
			//看看用户是否有权限进行应用信息新增操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有内容管理视图分类关联信息信息操作的权限！");
			}
			//用户有权限进行信息操作
			BeanCopyTools<WrapInViewCatagory, ViewCatagory> copier = BeanCopyToolsBuilder.create( WrapInViewCatagory.class, ViewCatagory.class, null, WrapInViewCatagory.Excludes );
			ViewCatagory viewCatagory = new ViewCatagory();
			copier.copy( wrapIn, viewCatagory );
			//如果JSON给过来的ID不为空，那么使用用户传入的ID
			if( wrapIn.getId().length() == viewCatagory.getId().length() ){
				if( business.getViewCatagoryFactory().get( wrapIn.getId() ) == null ){
					logger.debug("Used id in data, id=" + wrapIn.getId() );
					viewCatagory.setId( wrapIn.getId() );	
				}
			}
			logger.debug("System trying to beginTransaction to save view......" );
			emc.beginTransaction( ViewCatagory.class );
			emc.persist( viewCatagory, CheckPersistType.all );
			emc.commit();
			logger.debug("System save view success......" );
			//成功上传一个视图分类关联信息信息
			logger.debug("System trying to save log......" );
			emc.beginTransaction( Log.class );
			logService.log( emc,  currentPerson.getName(), "成功新增一个视图分类关联信息信息", "", "", "", viewCatagory.getId(), "VIEWCATAGORY", "新增" );
			emc.commit();
			wrap = new WrapOutId( viewCatagory.getId() );
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新View信息对象.", request = WrapInViewCatagory.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInViewCatagory wrapIn) {
		logger.debug("method put has been called, try to update view......" );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			ViewCatagory viewCatagory = business.getViewCatagoryFactory().get(id);
			if ( null == viewCatagory ) {
				throw new Exception("view{id:" + id + "} not existed.");
			}
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if ( !business.viewEditAvailable( request, currentPerson ) ) {
				throw new Exception("person{name:" + currentPerson.getName() + "} has sufficient permissions");
			}			
			//进行数据库持久化操作
			BeanCopyTools<WrapInViewCatagory, ViewCatagory> copier = BeanCopyToolsBuilder.create( WrapInViewCatagory.class, ViewCatagory.class, null, WrapInViewCatagory.Excludes);
			logger.debug("System trying to beginTransaction to update appInfo......" );
			emc.beginTransaction( ViewCatagory.class);
			copier.copy( wrapIn, viewCatagory );
			emc.check( viewCatagory, CheckPersistType.all);			
			emc.commit();
			logger.debug("System update appInfo success......" );
			//成功更新一个应用信息
			logger.debug("System trying to save log......" );
			emc.beginTransaction( Log.class);
			logService.log( emc,  currentPerson.getName(), "成功更新一个视图分类关联信息信息", "", "", "", viewCatagory.getId(), "VIEWCATAGORY", "更新" );
			emc.commit();
			wrap = new WrapOutId( viewCatagory.getId());
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
			ViewCatagory viewCatagory = business.getViewCatagoryFactory().get(id);
			if (null == viewCatagory) {
				throw new Exception("view{id:" + id + "} 应用信息不存在.");
			}
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("view{name:" + currentPerson.getName() + "} 用户没有内容管理应用信息操作的权限！");
			}
			
			//进行数据库持久化操作
			logger.debug("System trying to beginTransaction to delete view......" );
			emc.beginTransaction( ViewCatagory.class );
			emc.remove( viewCatagory, CheckRemoveType.all );
			emc.commit();
			logger.debug("System delete view success......" );
			//成功删除一个视图分类关联信息信息
			logger.debug("System trying to save log......" );
			emc.beginTransaction( Log.class );
			logService.log( emc,  currentPerson.getName(), "成功删除一个视图分类关联信息信息", "", "", "", viewCatagory.getId(), "VIEWCATAGORY", "删除" );
			emc.commit();
			wrap = new WrapOutId( viewCatagory.getId() );
			result.setData( wrap );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}