package com.x.cms.assemble.control.jaxrs.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.x.cms.assemble.control.factory.DocumentFactory;
import com.x.cms.assemble.control.factory.ViewFactory;
import com.x.cms.assemble.control.jaxrs.appinfo.WrapOutAppInfo;
import com.x.cms.assemble.control.jaxrs.document.WrapOutDocument;
import com.x.cms.assemble.control.jaxrs.document.WrapOutDocumentComplex;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.content.tools.DataHelper;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCatagory;
import com.x.cms.core.entity.element.ViewFieldConfig;



@Path("view")
public class ViewAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( ViewAction.class );
	private LogService logService = new LogService();
	private BeanCopyTools<View, WrapOutView> copier = BeanCopyToolsBuilder.create( View.class, WrapOutView.class, null, WrapOutView.Excludes);
	private BeanCopyTools<Document, WrapOutDocument> wrapout_copier_document = BeanCopyToolsBuilder.create(Document.class, WrapOutDocument.class, null, WrapOutDocument.Excludes);
	
	@HttpMethodDescribe(value = "获取全部的视图列表", response = WrapOutView.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllView(@Context HttpServletRequest request ) {		
		ActionResult<List<WrapOutView>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutView> wraps = null;
		logger.debug("user["+ currentPerson.getName() +"] try to get all view......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);			
			//如判断用户是否有查看所有视图的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有查询全部视图配置的权限！");
			}			
			//如果有权限，继续操作
			ViewFactory viewFactory  = business.getViewFactory();
			List<String> ids = viewFactory.listAll();//获取所有视图列表
			List<View> viewList = viewFactory.list( ids );//查询ID IN ids 的所有视图信息列表
			wraps = copier.copy( viewList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort( wraps );//对查询的列表进行排序		
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取指定应用ID的全部视图信息列表", response = WrapOutView.class)
	@GET
	@Path("list/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listViewByAppId(@Context HttpServletRequest request, @PathParam("appId")String appId ) {		
		ActionResult<List<WrapOutView>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutView> wraps = null;
		logger.debug("user["+currentPerson.getName()+"] try to get all view in appInfo{'id':'"+appId+"'}......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);			
			//如判断用户是否有查看所有视图的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有查询全部视图的权限！");
			}
			//如果有权限，继续操作
			ViewFactory viewFactory  = business.getViewFactory();
			List<String> ids = viewFactory.listByAppId( appId );//获取指定应用的所有视图列表
			List<View> viewList = viewFactory.list( ids );//查询ID IN ids 的所有视图信息列表
			wraps = copier.copy( viewList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort( wraps );//对查询的列表进行排序		
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取指定表单ID的全部视图信息列表", response = WrapOutView.class)
	@GET
	@Path("list/form/{formId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listViewByFormId(@Context HttpServletRequest request, @PathParam("formId")String formId ) {		
		ActionResult<List<WrapOutView>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutView> wraps = null;
		logger.debug("user["+currentPerson.getName()+"] try to get all view in form{'id':'"+formId+"'}......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);			
			//如判断用户是否有查看所有视图的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有查询全部视图的权限！");
			}
			//如果有权限，继续操作
			ViewFactory viewFactory  = business.getViewFactory();
			List<String> ids = viewFactory.listByFormId( formId );//获取指定表单ID的所有视图列表
			List<View> viewList = viewFactory.list( ids );//查询ID IN ids 的所有视图信息列表
			wraps = copier.copy( viewList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort( wraps );//对查询的列表进行排序		
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取指定分类的全部视图信息列表", response = WrapOutView.class)
	@GET
	@Path("list/catagory/{catagoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listViewByCatagoryId(@Context HttpServletRequest request, @PathParam("catagoryId")String catagoryId ) {		
		ActionResult<List<WrapOutView>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutView> wraps = null;
		logger.debug("user["+currentPerson.getName()+"] try to get all view in catagoryInfo{'id':'"+catagoryId+"'}......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);			
			//如判断用户是否有查看所有视图的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有查询全部视图的权限！");
			}
			//如果有权限，继续操作
			ViewFactory viewFactory  = business.getViewFactory();
			List<String> ids = viewFactory.listByCatagoryId( catagoryId );//获取指定应用的所有视图列表
			List<View> viewList = viewFactory.list( ids );//查询ID IN ids 的所有视图信息列表
			wraps = copier.copy( viewList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort( wraps );//对查询的列表进行排序		
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取view对象.", response = WrapOutView.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutView> result = new ActionResult<>();
		WrapOutView wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);		
		logger.debug("user[" + currentPerson.getName() + "] try to get view{'id':'"+id+"'}......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			View view = business.getViewFactory().get(id);
			if ( null == view ) {
				throw new Exception("view{id:" + id + "} 信息不存在.");
			}
			//如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
			BeanCopyTools<View, WrapOutView> copier = BeanCopyToolsBuilder.create(View.class, WrapOutView.class, null, WrapOutView.Excludes);
			wrap = new WrapOutView();
			copier.copy(view, wrap);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	
	@HttpMethodDescribe(value = "创建View应用信息对象.", request = WrapInView.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInView wrapIn) {
		logger.debug("[POST]开始尝试保存视图信息, id=" + wrapIn.getId() );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//获取到当前用户信息
			EffectivePerson currentPerson = this.effectivePerson(request);			
			Business business = new Business(emc);
			//看看用户是否有权限进行应用信息新增操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("[POST]person{name:" + currentPerson.getName() + "} 用户没有内容管理视图信息操作的权限！");
			}
			//用户有权限进行信息操作
			BeanCopyTools<WrapInView, View> copier = BeanCopyToolsBuilder.create( WrapInView.class, View.class, null, WrapInView.Excludes );
			View view = new View();
			copier.copy( wrapIn, view );
			if( wrapIn.getId() != null && wrapIn.getId().length() > 10 ){
				view.setId( wrapIn.getId() );
			}
			//如果JSON给过来的ID不为空，那么使用用户传入的ID
			if( wrapIn.getId().length() == view.getId().length() ){
				//要查询一下，该ID是否已经被使用
				if( business.getViewFactory().get( wrapIn.getId() ) == null ){
					logger.debug("[POST]Used id in data, id=" + wrapIn.getId() );
					view.setId( wrapIn.getId() );
				}
			}
			logger.debug("[POST]System trying to beginTransaction to save view......" );
			emc.beginTransaction( View.class );
			emc.persist( view, CheckPersistType.all );
			emc.commit();
			logger.debug("[POST]System save view success......" );
			//成功上传一个视图信息
			logger.debug("[POST]System trying to save log......" );
			emc.beginTransaction( Log.class );
			logService.log( emc,  currentPerson.getName(), "成功新增一个视图信息", "", "", "", view.getId(), "VIEW", "新增" );
			emc.commit();
			wrap = new WrapOutId( view.getId() );
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新View信息对象.", request = WrapInView.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInView wrapIn) {
		logger.debug("[PUT]method put has been called, try to update view......" );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			View view = business.getViewFactory().get(id);
			if ( null == view ) {
				throw new Exception("[PUT]view{id:" + id + "} not existed.");
			}
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if ( !business.viewEditAvailable( request, currentPerson ) ) {
				throw new Exception("[PUT]person{name:" + currentPerson.getName() + "} has sufficient permissions");
			}			
			//进行数据库持久化操作
			BeanCopyTools<WrapInView, View> copier = BeanCopyToolsBuilder.create( WrapInView.class, View.class, null, WrapInView.Excludes);
			logger.debug("[PUT]System trying to beginTransaction to update appInfo......" );
			emc.beginTransaction( View.class);
			copier.copy( wrapIn, view );
			emc.check( view, CheckPersistType.all);			
			emc.commit();
			logger.debug("[PUT]System update appInfo success......" );
			//成功更新一个应用信息
			logger.debug("[PUT]System trying to save log......" );
			emc.beginTransaction( Log.class);
			logService.log( emc,  currentPerson.getName(), "成功更新一个视图信息", "", "", "", view.getId(), "VIEW", "更新" );
			emc.commit();
			wrap = new WrapOutId( view.getId());
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
			View view = business.getViewFactory().get(id);
			//查询视图关联的所有列配置
			List<String> fieldConfigIds = business.getViewFieldConfigFactory().listByViewId(id);
			List<ViewFieldConfig> fieldConfigs = business.getViewFieldConfigFactory().list(fieldConfigIds);
			//查询视图关联的所有分类关联配置
			List<String> viewCatagoryIds = business.getViewCatagoryFactory().listByViewId(id);
			List<ViewCatagory> viewCatagorys = business.getViewCatagoryFactory().list( viewCatagoryIds );
			
			if (null == view) {
				throw new Exception("view{id:" + id + "} 应用信息不存在.");
			}
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if (!business.viewEditAvailable( request, currentPerson )) {
				throw new Exception("view{name:" + currentPerson.getName() + "} 用户没有内容管理应用信息操作的权限！");
			}			
			
			//进行数据库持久化操作
			logger.debug("System trying to beginTransaction to delete view......" );
			emc.beginTransaction( View.class );
			emc.beginTransaction( ViewFieldConfig.class );
			emc.beginTransaction( ViewCatagory.class );
			emc.remove( view, CheckRemoveType.all );
			//删除所有的viewFieldConfig
			if( fieldConfigs != null && fieldConfigs.size() > 0 ){
				for( ViewFieldConfig viewFieldConfig : fieldConfigs ){
					emc.remove( viewFieldConfig, CheckRemoveType.all );
					logger.debug("System trying to beginTransaction to delete viewFieldConfig{'id':'"+viewFieldConfig.getId()+"'}......" );
				}
			}
			if( viewCatagorys != null && viewCatagorys.size() > 0){
				for( ViewCatagory viewCatagory : viewCatagorys ){
					emc.remove( viewCatagory, CheckRemoveType.all );
					logger.debug("System trying to beginTransaction to delete viewCatagory{'id':'"+viewCatagory.getId()+"'}......" );
				}
			}
			
			emc.commit();
			logger.debug("System delete view success......" );
			//成功删除一个视图信息
			logger.debug("System trying to save log......" );
			emc.beginTransaction( Log.class );
			logService.log( emc,  currentPerson.getName(), "成功删除一个视图信息", "", "", "", view.getId(), "VIEW", "删除" );
			emc.commit();
			wrap = new WrapOutId( view.getId() );
			result.setData( wrap );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据条件的视图数据列表,下一页.", response = WrapOutAppInfo.class, request = WrapInFilter.class)
	@POST
	@Path("viewdata/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nextPageViewDataList(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutDocumentComplex>> result = new ActionResult<>();
		List<WrapOutDocumentComplex> wraps = new ArrayList<WrapOutDocumentComplex>();
		WrapOutDocumentComplex wrap = null;
		DataHelper dataHelper = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Map<String, Object> condition = new HashMap<String, Object>();
		logger.debug("[nextPageViewDataList]user[" + currentPerson.getName() + "] try to get viewData for next Page, id=" + id +" , and count=" + count );
		try {
			//1、进行输入的信息验证
			if( count <= 0){
				count = 12;
			}
			if( "(0)".equals(id) || "".equals(id) ){
				id = null;
			}
			if( wrapIn.getCatagoryId() != null ){
				condition.put( "catagoryId", wrapIn.getCatagoryId() );
			}
			if( wrapIn.getViewId() != null ){
				condition.put( "viewId", wrapIn.getViewId() );
			}
			if( wrapIn.getOrderField() != null ){
				condition.put( "orderField", wrapIn.getOrderField() );
			}
			if( wrapIn.getOrderType() != null ){
				condition.put( "orderType", wrapIn.getOrderType() );
			}
			if( wrapIn.getOrderType() != null ){
				condition.put( "searchDocStatus", wrapIn.getSearchDocStatus() );
			}
			
			EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
			Business business = new Business(emc);
			ViewFactory viewFactory = business.getViewFactory();
			DocumentFactory documentFactory = business.getDocumentFactory();
			if( viewFactory == null ){
				throw new Exception("[nextPageViewDataList]获取ViewFactory失败, 无法继续进行查询操作！");
			}
			if( documentFactory == null ){
				throw new Exception("[nextPageViewDataList]获取DocumentFactory失败, 无法继续进行查询操作！");
			}			
			Long documentCount = viewFactory.getDocIdsCount(condition);
			//分页工具类使用			
			logger.debug("[nextPageViewDataList]call method [getDocIdsForPagenation] to get idsList[]" );
			
			List<Document> documentList = viewFactory.nextPageDocuemntView( id, count, condition );
			logger.debug("[nextPageViewDataList]getResultList has " + documentList.size() + " elements" );
			
			if( documentList != null && documentList.size() > 0 ){
				for( Document document : documentList ){
					logger.debug("[nextPageViewDataList] get document's data for document{'id':'"+document.getId()+"'}." );
					//查询并且组织实际数据
					dataHelper = new DataHelper( business.entityManagerContainer(), document.getAppId(), document.getCatagoryId(), document.getId() );
					
					wrap = new WrapOutDocumentComplex();
					wrap.setDocument(wrapout_copier_document.copy(document));
					wrap.setData( dataHelper.get() );
					wraps.add( wrap );
				}
			}
			result.setData(wraps);
			result.setCount( documentCount );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}