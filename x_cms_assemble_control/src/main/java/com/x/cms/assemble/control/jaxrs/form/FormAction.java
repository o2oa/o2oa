package com.x.cms.assemble.control.jaxrs.form;

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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
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
import com.x.cms.assemble.control.factory.FormFactory;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.element.Form;

@Path("form")
public class FormAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( FormAction.class );
	private LogService logService = new LogService();
	private BeanCopyTools<Form, WrapOutForm> copier = BeanCopyToolsBuilder.create(Form.class, WrapOutForm.class, null,
			WrapOutForm.Excludes);

	@HttpMethodDescribe(value = "获取全部的表单模板列表", response = WrapOutForm.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllForm(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutForm>> result = new ActionResult<>();
		List<WrapOutForm> wraps = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			FormFactory formFactory = business.getFormFactory();
			List<String> ids = formFactory.listAll();// 获取所有表单模板列表
			List<Form> formList = formFactory.list(ids);// 查询ID IN ids 的所有表单模板信息列表
			wraps = copier.copy(formList);// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort(wraps);// 对查询的列表进行排序
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定应用的全部表单模板信息列表", response = WrapOutForm.class)
	@GET
	@Path("list/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listFormByAppId(@Context HttpServletRequest request, @PathParam("appId") String appId) {
		ActionResult<List<WrapOutForm>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutForm> wraps = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (!business.formEditAvailable( request, currentPerson )) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有查询全部表单模板的权限！");
			}
			FormFactory formFactory = business.getFormFactory();
			List<String> ids = formFactory.listByAppId(appId);// 获取指定应用的所有表单模板列表
			List<Form> formList = formFactory.list(ids);
			wraps = copier.copy(formList);// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort(wraps);// 对查询的列表进行排序
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取form对象.", response = WrapOutForm.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutForm> result = new ActionResult<>();
		WrapOutForm wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Form form = business.getFormFactory().get(id);
			if (null == form) {
				throw new Exception("form{id:" + id + "} 信息不存在.");
			}
			BeanCopyTools<Form, WrapOutForm> copier = BeanCopyToolsBuilder.create( Form.class, WrapOutForm.class, null, WrapOutForm.Excludes );
			wrap = new WrapOutForm();
			copier.copy(form, wrap);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建Form应用信息对象.", request = WrapInForm.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInForm wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 获取到当前用户信息
			EffectivePerson currentPerson = this.effectivePerson(request);
			Business business = new Business(emc);
			// 看看用户是否有权限进行应用信息新增操作
			if (!business.formEditAvailable(request, currentPerson)) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有内容管理表单模板信息操作的权限！");
			}
			// 用户有权限进行信息操作
			BeanCopyTools<WrapInForm, Form> copier = BeanCopyToolsBuilder.create(WrapInForm.class, Form.class, null,
					WrapInForm.Excludes);
			Form form = new Form();
			copier.copy(wrapIn, form);

			if (wrapIn.getId() != null && wrapIn.getId().length() > 10) {
				form.setId(wrapIn.getId());
			}

			logger.debug("System trying to beginTransaction to save form......");
			emc.beginTransaction(Form.class);
			emc.persist(form, CheckPersistType.all);
			emc.commit();
			logger.debug("System save form success......");
			// 成功上传一个表单模板信息
			logger.debug("System trying to save log......");
			emc.beginTransaction(Log.class);
			logService.log( emc, currentPerson.getName(), "成功新增一个表单模板信息", form.getAppId(), "", "", form.getId(), "FORM", "新增");
			emc.commit();
			wrap = new WrapOutId(form.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新Form信息对象.", request = WrapInForm.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInForm wrapIn) {
		logger.debug("method put has been called, try to update form......");
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			// 先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			Form form = business.getFormFactory().get(id);
			if (null == form) {
				throw new Exception("form{id:" + id + "} not existed.");
			}
			// 如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if (!business.formEditAvailable(request, currentPerson)) {
				throw new Exception("person{name:" + currentPerson.getName() + "} has sufficient permissions");
			}
			// 进行数据库持久化操作
			BeanCopyTools<WrapInForm, Form> copier = BeanCopyToolsBuilder.create(WrapInForm.class, Form.class, null,
					WrapInForm.Excludes);
			logger.debug("System trying to beginTransaction to update appInfo......");
			emc.beginTransaction(Form.class);
			copier.copy(wrapIn, form);
			emc.check(form, CheckPersistType.all);
			emc.commit();
			logger.debug("System update appInfo success......");
			// 成功更新一个应用信息
			logger.debug("System trying to save log......");
			emc.beginTransaction(Log.class);
			logService.log( emc, currentPerson.getName(), "成功更新一个表单模板信息", "", "", "", form.getId(), "FORM", "更新");
			emc.commit();
			wrap = new WrapOutId(form.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除Form应用信息对象.", response = WrapOutId.class)
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

			// 先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			Form form = business.getFormFactory().get(id);
			if (null == form) {
				throw new Exception("form{id:" + id + "} 应用信息不存在.");
			}
			// 如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if (!business.formEditAvailable(request, currentPerson)) {
				throw new Exception("form{name:" + currentPerson.getName() + "} 用户没有内容管理应用信息操作的权限！");
			}

			// 进行数据库持久化操作
			logger.debug("System trying to beginTransaction to delete form......");
			emc.beginTransaction(Form.class);
			emc.remove(form, CheckRemoveType.all);
			emc.commit();
			logger.debug("System delete form success......");
			// 成功删除一个表单模板信息
			logger.debug("System trying to save log......");
			emc.beginTransaction(Log.class);
			logService.log( emc, currentPerson.getName(), "成功删除一个表单模板信息", form.getAppId(), "", "", form.getId(), "FORM", "删除");
			emc.commit();
			wrap = new WrapOutId(form.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Form,下一页.", response = WrapOutForm.class, request = WrapInFilter.class)
	@POST
	@Path("filter/list/{id}/next/{count}/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("appId") Integer appId, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutForm>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();
		logger.debug("user[" + currentPerson.getName() + "] try to get appinfo for nextpage, last docuId=" + id);
		try {
			// equals = new ListOrderedMap<>();
			equals.put("appId", appId);
			if ((null != wrapIn.getCatagoryIdList()) && (!wrapIn.getCatagoryIdList().isEmpty())) {
				equals.put("catagoryId", wrapIn.getCatagoryIdList().get(0).getValue());
			}
			if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
				equals.put("creatorUid", wrapIn.getCreatorList().get(0).getValue());
			}
			if ((null != wrapIn.getStatusList()) && (!wrapIn.getStatusList().isEmpty())) {
				equals.put("docStatus", wrapIn.getStatusList().get(0).getValue());
			}
			if (StringUtils.isNotEmpty(wrapIn.getKey())) {
				String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					// likes = new ListOrderedMap<>();
					likes.put("title", key);
				}
			}
			logger.debug("call method [standardListNext] to get resultList[]");
			result = this.standardListNext(copier, id, count, "sequence", equals, null, likes, null, null, null, null,
					true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Form,上一页.", response = WrapOutForm.class, request = WrapInFilter.class)
	@POST
	@Path("filter/list/{id}/prev/{count}/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("appId") Integer appId, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutForm>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();
		logger.debug("user[" + currentPerson.getName() + "] try to get document for prevpage, last docuId=" + id);
		try {
			// equals = new ListOrderedMap<>();
			equals.put("appId", appId);
			if ((null != wrapIn.getCatagoryIdList()) && (!wrapIn.getCatagoryIdList().isEmpty())) {
				equals.put("catagoryId", wrapIn.getCatagoryIdList().get(0).getValue());
			}
			if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
				equals.put("creatorUid", wrapIn.getCreatorList().get(0).getValue());
			}
			if ((null != wrapIn.getStatusList()) && (!wrapIn.getStatusList().isEmpty())) {
				equals.put("docStatus", wrapIn.getStatusList().get(0).getValue());
			}
			if (StringUtils.isNotEmpty(wrapIn.getKey())) {
				String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					likes.put("title", key);
				}
			}
			logger.debug("call method [standardListPrev] to get resultList[]");
			result = this.standardListPrev(copier, id, count, "sequence", equals, null, likes, null, null, null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}