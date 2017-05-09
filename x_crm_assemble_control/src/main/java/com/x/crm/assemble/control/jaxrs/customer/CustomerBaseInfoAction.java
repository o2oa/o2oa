package com.x.crm.assemble.control.jaxrs.customer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.InTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.application.jaxrs.MemberTerms;
import com.x.base.core.application.jaxrs.NotEqualsTerms;
import com.x.base.core.application.jaxrs.NotInTerms;
import com.x.base.core.application.jaxrs.NotMemberTerms;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.crm.assemble.control.Business;
import com.x.crm.assemble.control.WrapCrmTools;
import com.x.crm.assemble.control.wrapin.WrapInCustomerBaseInfo;
import com.x.crm.assemble.control.wrapin.WrapInFilterCustomerBaseInfo;
import com.x.crm.assemble.control.wrapout.WrapOutCustomerBaseInfo;
import com.x.crm.core.entity.CustomerBaseInfo;

@Path("customer/baseinfo")
public class CustomerBaseInfoAction extends StandardJaxrsAction {
	private Logger logger = LoggerFactory.getLogger(CustomerBaseInfoAction.class);

	//测试
	@HttpMethodDescribe(value = "测试", response = WrapOutString.class)
	@GET
	@Path("iswork")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void iswork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		wrap.setValue("customer/baseinfo/iswork  is work!! Token:" + effectivePerson.getToken() + " Name:" + effectivePerson.getName() + " TokenType:" + effectivePerson.getTokenType());
		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "第二个测试", response = WrapOutString.class)
	@GET
	@Path("test")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void test(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			String _loginPersonName = effectivePerson.getName();
			Business business = new Business(emc);
			CustomerBaseInfo customer = new CustomerBaseInfo();
			Long _count = business.customerBaseInfoFactory().count();
			LocalDate today = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			String _tmptoday = today.format(formatter);
			String _string = String.format("%06d", _count);
			wrap.setValue(_string + "_" + _tmptoday);
			result.setData(wrap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	//创建
	@HttpMethodDescribe(value = "创建数据表，创建数据测试", response = WrapOutString.class)
	@PUT
	@Path("create")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			EffectivePerson effectivePerson = this.effectivePerson(request);
			String _loginPersonName = effectivePerson.getName();
			Business business = new Business(emc);
			CustomerBaseInfo customer = new CustomerBaseInfo();
			boolean isPassTest = true;

			WrapInCustomerBaseInfo WrapIncustomer = new WrapInCustomerBaseInfo();
			WrapIncustomer = this.convertToWrapIn(jsonElement, WrapInCustomerBaseInfo.class);

			//客户名称必须填写
			if (StringUtils.isEmpty(WrapIncustomer.getCustomername())) {
				Exception exception = new CustomernameNullException();
				result.error(exception);
				isPassTest = false;
			}

			//检查属于登陆人的客户名称是否唯一
			if (business.customerBaseInfoFactory().checkCustomerByCustomerName(WrapIncustomer.getCustomername(), _loginPersonName)) {
				Exception exception = new CustomerMustUniqueException();
				result.error(exception);
				isPassTest = false;
			}

			if (isPassTest) {
				logger.error("_creator:" + _loginPersonName);
				customer = WrapCrmTools.CustomerBaseInfoInCopier.copy(WrapIncustomer);
				customer.setCreatorname(_loginPersonName);
				String customersequence = business.customerBaseInfoFactory().defaultSequence();
				customer.setCustomersequence(customersequence);
				emc.beginTransaction(CustomerBaseInfo.class);
				emc.persist(customer);
				emc.commit();
				WrapOutId wrap = new WrapOutId(customer.getId());
				result.setData(wrap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	//客户列表【已废弃】
	@HttpMethodDescribe(value = "列出所有客户,默认每页5条", response = WrapOutString.class)
	@PUT
	@Path("listalltestback/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ListAllCustomerByPage(@PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutCustomerBaseInfo>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WrapInFilterCustomerBaseInfo wrapIn = null;
			try {
				//logger.error(jsonElement.getAsString());
				wrapIn = this.convertToWrapIn(jsonElement, WrapInFilterCustomerBaseInfo.class);
				logger.error(wrapIn.toString());
			} catch (Exception e) {
				e.printStackTrace();
				//check = false;
				//Exception exception = new WrapInConvertException( e, jsonElement );
				//result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}

			List<WrapOutCustomerBaseInfo> wraps = new ArrayList<>();
			List<CustomerBaseInfo> objList = business.customerBaseInfoFactory().listNextWithFilter(id, count, null, wrapIn);
			wraps = WrapCrmTools.CustomerBaseInfoOutCopier.copy(objList);
			//logger.warn(wraps.toString());
			result.setData(wraps);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	//根据id查找客户名称
	@HttpMethodDescribe(value = "根据id查找客户名称", response = WrapOutString.class)
	@GET
	@Path("getnamebyid/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void FindCustomerNameById(@Suspended final AsyncResponse asyncResponse, @PathParam("id") String id) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CustomerBaseInfo customerbaseinfo = new CustomerBaseInfo();
			customerbaseinfo = emc.find(id, CustomerBaseInfo.class);
			WrapOutString wrap = new WrapOutString();
			if (null == customerbaseinfo) {
				Exception exception = new CustomernameNullException();
				result.error(exception);
			} else {
				wrap.setValue(customerbaseinfo.getCustomername());
				result.setData(wrap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	//根据id查找客户
	@HttpMethodDescribe(value = "根据id查找客户", response = WrapOutString.class)
	@GET
	@Path("getbyid/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getCustomerById(@Suspended final AsyncResponse asyncResponse, @PathParam("id") String id) {
		ActionResult<CustomerBaseInfo> result = new ActionResult<>();
		WrapOutCustomerBaseInfo wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CustomerBaseInfo customerbaseinfo = emc.find(id, CustomerBaseInfo.class, ExceptionWhen.not_found);
			wrap = WrapCrmTools.CustomerBaseInfoOutCopier.copy(customerbaseinfo);
			result.setData(wrap);
		} catch (Throwable th) {
			// TODO Auto-generated catch block
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	//根据客户名称判断，客户是否存在
	@HttpMethodDescribe(value = "根据客户名称判断，客户是否存在", response = WrapOutString.class)
	@GET
	@Path("checkexistbyname/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void IsExistByName(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @PathParam("name") String _name) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		boolean _IsExist = false;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			String _loginPersonName = effectivePerson.getName();
			Business business = new Business(emc);

			_IsExist = business.customerBaseInfoFactory().checkCustomerByCustomerName(_name, _loginPersonName);
			WrapOutBoolean wrap = new WrapOutBoolean();
			wrap.setValue(_IsExist);
			result.setData(wrap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	//根据id删除客户
	@HttpMethodDescribe(value = "根据id删除客户", response = WrapOutString.class)
	@GET
	@Path("delete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.error("delete:" + id);
			CustomerBaseInfo customer = emc.find(id, CustomerBaseInfo.class, ExceptionWhen.not_found);
			emc.beginTransaction(CustomerBaseInfo.class);
			emc.remove(customer);
			emc.commit();
			wrap = new WrapOutId(customer.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			// TODO Auto-generated catch block
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	//客户列表
	@HttpMethodDescribe(value = "列出所有客户,默认每页5条", response = WrapOutString.class)
	@PUT
	@Path("listall/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListAllCustomerByPageTest(@Suspended final AsyncResponse asyncResponse, @PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutCustomerBaseInfo>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//Business business = new Business(emc);
			WrapInFilterCustomerBaseInfo wrapIn = null;
			try {
				//logger.error(jsonElement.getAsString());
				wrapIn = this.convertToWrapIn(jsonElement, WrapInFilterCustomerBaseInfo.class);
				logger.error("wrapIn:" + wrapIn.toString());
			} catch (Exception e) {
				e.printStackTrace();
				//check = false;
				//Exception exception = new WrapInConvertException( e, jsonElement );
				//result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}

			logger.debug(wrapIn.getFuzzySearchKey());

			//logger.error("jsonObject.get('equalFieldName').getAsString():" + jsonElement.getAsJsonObject().get("equalFieldName").getAsJsonObject().get("customername").getAsString());

			BeanCopyTools<CustomerBaseInfo, WrapOutCustomerBaseInfo> wrapout_copier = BeanCopyToolsBuilder.create(CustomerBaseInfo.class, WrapOutCustomerBaseInfo.class, null, WrapOutCustomerBaseInfo.Excludes);
			String sequenceField = wrapIn.getSequenceField();
			EqualsTerms equals = new EqualsTerms();
			NotEqualsTerms notEquals = new NotEqualsTerms();
			LikeTerms likes = new LikeTerms();
			InTerms ins = new InTerms();
			NotInTerms notIns = new NotInTerms();
			MemberTerms members = new MemberTerms();
			NotMemberTerms notMembers = new NotMemberTerms();
			Boolean andJoin = false;
			String order = wrapIn.getOrder();

			//多字段模糊差选
			if (null != wrapIn && null != wrapIn.getFuzzySearchKey() && !wrapIn.getFuzzySearchKey().isEmpty()) {
				likes.put("customername", wrapIn.getFuzzySearchKey());
				likes.put("province", wrapIn.getFuzzySearchKey());
				likes.put("city", wrapIn.getFuzzySearchKey());
				likes.put("telno", wrapIn.getFuzzySearchKey());
				likes.put("houseno", wrapIn.getFuzzySearchKey());
				likes.put("industryfirst", wrapIn.getFuzzySearchKey());
				likes.put("industrysecond", wrapIn.getFuzzySearchKey());
			}

			//			if (null != wrapIn && null != wrapIn.getEqualFieldName()) {
			//				logger.debug(wrapIn.getEqualFieldName().toString());
			//				ListOrderedMap<String, Object> _paraList = wrapIn.getEqualFieldName();
			//			}
			//likes.put("name", wrapIn.getFuzzySearchKey());

			//(BeanCopyTools<T, W> copier, String id, Integer count, String sequenceField, EqualsTerms equals, NotEqualsTerms notEquals, LikeTerms likes, InTerms ins, NotInTerms notIns, MemberTerms members,NotMemberTerms notMembers, boolean andJoin, String order)
			result = this.standardListNext(wrapout_copier, id, count, sequenceField, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin, order);
			List<WrapOutCustomerBaseInfo> wraps = new ArrayList<>();
			//wraps = result.getData();
			//List<CustomerBaseInfo> objList = business.customerBaseInfoFactory().listNextWithFilter(id, count, null, wrapIn);
			//wraps = WrapCrmTools.CustomerBaseInfoOutCopier.copy(objList);
			//logger.warn(wraps.toString());
			//result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}
