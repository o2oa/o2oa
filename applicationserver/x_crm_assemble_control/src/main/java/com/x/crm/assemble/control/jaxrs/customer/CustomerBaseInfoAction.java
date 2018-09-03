package com.x.crm.assemble.control.jaxrs.customer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.jaxrs.MemberTerms;
import com.x.base.core.project.jaxrs.NotEqualsTerms;
import com.x.base.core.project.jaxrs.NotInTerms;
import com.x.base.core.project.jaxrs.NotMemberTerms;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoId;
import com.x.crm.assemble.control.Business;
import com.x.crm.assemble.control.WrapCrmTools;
import com.x.crm.assemble.control.wrapin.WrapInFilterCustomerBaseInfo;
import com.x.crm.assemble.control.wrapout.WrapOutCustomerBaseInfo;
import com.x.crm.core.entity.CustomerBaseInfo;

@Path("customer/baseinfo")
@JaxrsDescribe("客户增删改查服务")
public class CustomerBaseInfoAction extends StandardJaxrsAction {
	private Logger logger = LoggerFactory.getLogger(CustomerBaseInfoAction.class);

	/*
	 * //测试
	 * 
	 * @HttpMethodDescribe(value = "测试", response = WrapOutString.class)
	 * 
	 * @GET
	 * 
	 * @Path("iswork")
	 * 
	 * @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON) public void iswork(@Suspended final
	 * AsyncResponse asyncResponse, @Context HttpServletRequest request) {
	 * ActionResult<WrapOutString> result = new ActionResult<>(); WrapOutString
	 * wrap = new WrapOutString(); EffectivePerson effectivePerson =
	 * this.effectivePerson(request);
	 * wrap.setValue("customer/baseinfo/iswork  is work!! Token:" +
	 * effectivePerson.getToken() + " Name:" +
	 * effectivePerson.getDistinguishedName() + " TokenType:" +
	 * effectivePerson.getTokenType()); result.setData(wrap);
	 * asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(
	 * result)); }
	 * 
	 * @HttpMethodDescribe(value = "第二个测试", response = WrapOutString.class)
	 * 
	 * @GET
	 * 
	 * @Path("test")
	 * 
	 * @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON) public void test(@Suspended final
	 * AsyncResponse asyncResponse, @Context HttpServletRequest request) {
	 * ActionResult<WrapOutString> result = new ActionResult<>(); WrapOutString
	 * wrap = new WrapOutString(); try (EntityManagerContainer emc =
	 * EntityManagerContainerFactory.instance().create()) { EffectivePerson
	 * effectivePerson = this.effectivePerson(request); String _loginPersonName
	 * = effectivePerson.getDistinguishedName(); Business business = new
	 * Business(emc); CustomerBaseInfo customer = new CustomerBaseInfo(); Long
	 * _count = business.customerBaseInfoFactory().count(); LocalDate today =
	 * LocalDate.now(); DateTimeFormatter formatter =
	 * DateTimeFormatter.ofPattern("yyyyMMdd"); String _tmptoday =
	 * today.format(formatter); String _string = String.format("%06d", _count);
	 * wrap.setValue(_string + "_" + _tmptoday); result.setData(wrap); } catch
	 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(
	 * result)); }
	 */

	// 创建
	@JaxrsMethodDescribe(value = "创建客户", action = ActionCreate.class)
	@POST
	@Path("create")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("客户信息") JsonElement jsonElement) {
		ActionResult<WoId> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			result = new ActionCreate().execute(request, jsonElement);
		} catch (Exception e) {
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据id查找客户名称", action = ActionGetCustomerNameById.class)
	@GET
	@Path("getnamebyid/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void FindCustomerNameById(@Suspended final AsyncResponse asyncResponse,
			@JaxrsParameterDescribe("客户id") @PathParam("id") String id) {
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

	// 根据id查找客户
	@JaxrsMethodDescribe(value = "根据id查找客户", action = StandardJaxrsAction.class)
	@GET
	@Path("getbyid/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getCustomerById(@Suspended final AsyncResponse asyncResponse,
			@JaxrsParameterDescribe("客户id") @PathParam("id") String id) {
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

	// 根据客户名称判断，客户是否存在
	@JaxrsMethodDescribe(value = "根据客户名称判断，客户是否存在", action = StandardJaxrsAction.class)
	@GET
	@Path("checkexistbyname/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void IsExistByName(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("客户名称") @PathParam("name") String _name) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		boolean _IsExist = false;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			String _loginPersonName = effectivePerson.getDistinguishedName();
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

	// 根据id删除客户
	@JaxrsMethodDescribe(value = "根据id删除客户", action = StandardJaxrsAction.class)
	@GET
	@Path("delete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse,
			@JaxrsParameterDescribe("客户id") @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.error(new Exception("delete:" + id));
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

	// 客户列表下一页
	@JaxrsMethodDescribe(value = "列出所有客户,下一页", action = StandardJaxrsAction.class)
	@PUT
	@Path("listall/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListAllCustomerNextPage(@Suspended final AsyncResponse asyncResponse,
			@JaxrsParameterDescribe("上一页最后一个id") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页多少条") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutCustomerBaseInfo>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapInFilterCustomerBaseInfo wrapIn = null;
			try {
				wrapIn = this.convertToWrapIn(jsonElement, WrapInFilterCustomerBaseInfo.class);
				logger.error(new Exception("wrapIn:" + wrapIn.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			logger.debug(wrapIn.getFuzzySearchKey());

			WrapCopier<CustomerBaseInfo, WrapOutCustomerBaseInfo> wrapout_copier = WrapCopierFactory
					.wo(CustomerBaseInfo.class, WrapOutCustomerBaseInfo.class, null, WrapOutCustomerBaseInfo.Excludes);
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

			// 多字段模糊差选
			if (null != wrapIn && null != wrapIn.getFuzzySearchKey() && !wrapIn.getFuzzySearchKey().isEmpty()) {
				likes.put("customername", wrapIn.getFuzzySearchKey());
				likes.put("province", wrapIn.getFuzzySearchKey());
				likes.put("city", wrapIn.getFuzzySearchKey());
				likes.put("telno", wrapIn.getFuzzySearchKey());
				likes.put("houseno", wrapIn.getFuzzySearchKey());
				likes.put("industryfirst", wrapIn.getFuzzySearchKey());
				likes.put("industrysecond", wrapIn.getFuzzySearchKey());
			}

			result = this.standardListNext(wrapout_copier, id, count, sequenceField, equals, notEquals, likes, ins,
					notIns, members, notMembers, andJoin, order);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	// 客户列表下一页
	@JaxrsMethodDescribe(value = "列出所有客户,上一页", action = StandardJaxrsAction.class)
	@PUT
	@Path("listall/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListAllCustomerPrevPage(@Suspended final AsyncResponse asyncResponse,
			@JaxrsParameterDescribe("上一页最后一个id") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页多少条") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutCustomerBaseInfo>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapInFilterCustomerBaseInfo wrapIn = null;
			try {
				wrapIn = this.convertToWrapIn(jsonElement, WrapInFilterCustomerBaseInfo.class);
				logger.error(new Exception("wrapIn:" + wrapIn.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			logger.debug(wrapIn.getFuzzySearchKey());

			WrapCopier<CustomerBaseInfo, WrapOutCustomerBaseInfo> wrapout_copier = WrapCopierFactory
					.wo(CustomerBaseInfo.class, WrapOutCustomerBaseInfo.class, null, WrapOutCustomerBaseInfo.Excludes);
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

			// 多字段模糊差选
			if (null != wrapIn && null != wrapIn.getFuzzySearchKey() && !wrapIn.getFuzzySearchKey().isEmpty()) {
				likes.put("customername", wrapIn.getFuzzySearchKey());
				likes.put("province", wrapIn.getFuzzySearchKey());
				likes.put("city", wrapIn.getFuzzySearchKey());
				likes.put("telno", wrapIn.getFuzzySearchKey());
				likes.put("houseno", wrapIn.getFuzzySearchKey());
				likes.put("industryfirst", wrapIn.getFuzzySearchKey());
				likes.put("industrysecond", wrapIn.getFuzzySearchKey());
			}

			result = this.standardListPrev(wrapout_copier, id, count, sequenceField, equals, notEquals, likes, ins,
					notIns, members, notMembers, andJoin, order);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	// 根据页码和每页条数，获得一页之内的结果
	@JaxrsMethodDescribe(value = "根据页码和每页条数，获得一页之内的结果", action = StandardJaxrsAction.class)
	@PUT
	@Path("page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListObjectbyPageAndCount(@Suspended final AsyncResponse asyncResponse,
			@JaxrsParameterDescribe("第几页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页多少条") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutCustomerBaseInfo>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			boolean isPassTest = true;
			Business business = new Business(emc);
			WrapInFilterCustomerBaseInfo wrapIn = null;
			try {
				wrapIn = this.convertToWrapIn(jsonElement, WrapInFilterCustomerBaseInfo.class);
				logger.error(new Exception("wrapIn:" + wrapIn.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (page < 1 || count < 1) {
				Exception exception = new CustomerPageCountException();
				result.error(exception);
				isPassTest = false;
			}

			if (isPassTest) {
				logger.debug(wrapIn.getFuzzySearchKey());

				WrapCopier<CustomerBaseInfo, WrapOutCustomerBaseInfo> wrapout_copier = WrapCopierFactory.wo(
						CustomerBaseInfo.class, WrapOutCustomerBaseInfo.class, null, WrapOutCustomerBaseInfo.Excludes);
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

				// 多字段模糊差选
				if (null != wrapIn && null != wrapIn.getFuzzySearchKey() && !wrapIn.getFuzzySearchKey().isEmpty()) {
					likes.put("customername", wrapIn.getFuzzySearchKey());
					likes.put("province", wrapIn.getFuzzySearchKey());
					likes.put("city", wrapIn.getFuzzySearchKey());
					likes.put("telno", wrapIn.getFuzzySearchKey());
					likes.put("houseno", wrapIn.getFuzzySearchKey());
					likes.put("industryfirst", wrapIn.getFuzzySearchKey());
					likes.put("industrysecond", wrapIn.getFuzzySearchKey());
				}

				if (null == sequenceField || StringUtils.isBlank(sequenceField)) {
					sequenceField = "sequence";
				}
				if (null == order || StringUtils.isBlank(order)) {
					order = "DESC";
				}

				// result =
				// business.customerBaseInfoFactory().listbyPageAndCount(wrapout_copier,
				// "(0)", page, sequenceField, equals, notEquals, likes, ins,
				// notIns, members, notMembers, andJoin, order);
				result = business.customerBaseInfoFactory().listbyPageAndCount(wrapout_copier, "(0)", page, count,
						sequenceField, equals, notEquals, likes, ins, notIns, members, notMembers, andJoin, order);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}
