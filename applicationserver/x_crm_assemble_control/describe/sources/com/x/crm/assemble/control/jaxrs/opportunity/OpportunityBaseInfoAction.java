package com.x.crm.assemble.control.jaxrs.opportunity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.crm.assemble.control.Business;
import com.x.crm.assemble.control.WrapCrmTools;
import com.x.crm.assemble.control.wrapin.WrapInFilterOpportunity;
import com.x.crm.assemble.control.wrapin.WrapInOpportunity;
import com.x.crm.assemble.control.wrapout.WrapOutOpportunity;
import com.x.crm.core.entity.Opportunity;

import testdata.CreateTestData;

@Path("opportunity/baseinfo")
public class OpportunityBaseInfoAction extends StandardJaxrsAction {
	private Logger logger = LoggerFactory.getLogger(OpportunityBaseInfoAction.class);

	// @HttpMethodDescribe(value = "测试", response = WrapOutString.class)
	@GET
	@Path("iswork")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void iswork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = null;
		wrap = new WrapOutString();
		wrap.setValue("opportunity/baseinfo/iswork  is work!!");
		result.setData(wrap);
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	// @HttpMethodDescribe(value = "列出所有商机", response = WrapOutString.class)
	@GET
	@Path("listallopportunity")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListAllOpportunity(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<WrapOutOpportunity>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<WrapOutOpportunity> wraps = new ArrayList<>();
			List<String> ids = business.opportunityFactory().listAll();
			wraps = WrapCrmTools.OpportunityOutCopier.copy(emc.list(Opportunity.class, ids));
			result.setData(wraps);
		} catch (Exception e) {
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	// @HttpMethodDescribe(value = "列出所有商机id", response = WrapOutString.class)
	@GET
	@Path("listallids")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListAllOpportunityIds(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<List<String>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = business.opportunityFactory().listAll();
			result.setData(ids);
		} catch (Exception e) {
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	// @HttpMethodDescribe(value = "创建数据表，创建数据测试", response =
	// WrapOutString.class)
	@POST
	@Path("create")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			EffectivePerson effectivePerson = this.effectivePerson(request);
			String _loginPersonName = effectivePerson.getDistinguishedName();
			Business business = new Business(emc);
			Opportunity opportunity = new Opportunity();
			boolean isPassTest = true;

			WrapInOpportunity WrapInOpportunity = new WrapInOpportunity();
			WrapInOpportunity = this.convertToWrapIn(jsonElement, WrapInOpportunity.class);

			CreateTestData createtestdata = new CreateTestData();
			createtestdata.creatDataOpportunity(opportunity);

			// 模拟创建，随机和客户产生关联
			List<String> customerids = business.customerBaseInfoFactory().listAll();
			Random rand = new Random();
			int _tmpIndex = rand.nextInt(customerids.size());
			String _tmpId = customerids.get(_tmpIndex);
			// 模拟创建，随机和客户产生关联

			if (isPassTest) {
				opportunity = WrapCrmTools.OpportunityInCopier.copy(WrapInOpportunity);
				opportunity.setCustomerid(_tmpId);
				opportunity.setBelongerid(_loginPersonName);
				emc.beginTransaction(Opportunity.class);
				emc.persist(opportunity);
				emc.commit();
			}

			WrapOutString wrap = null;
			wrap = new WrapOutString();
			wrap.setValue(opportunity.getId());
			result.setData(wrap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	// @HttpMethodDescribe(value = "根据id删除商机", response = WrapOutString.class)
	@GET
	@Path("delete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.error(new Exception("delete:" + id));
			Opportunity opportunity = emc.find(id, Opportunity.class, ExceptionWhen.not_found);
			emc.beginTransaction(Opportunity.class);
			emc.remove(opportunity);
			emc.commit();
			wrap = new WrapOutId(opportunity.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			// TODO Auto-generated catch block
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	// @HttpMethodDescribe(value = "根据id查找商机", response = WrapOutString.class)
	@GET
	@Path("getbyid/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response FindOpportunityById(@PathParam("id") String id) {
		ActionResult<Opportunity> result = new ActionResult<>();
		WrapOutOpportunity wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Opportunity opportunity = emc.find(id, Opportunity.class, ExceptionWhen.not_found);
			wrap = WrapCrmTools.OpportunityOutCopier.copy(opportunity);
			result.setData(wrap);
		} catch (Throwable th) {
			// TODO Auto-generated catch block
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	// @HttpMethodDescribe(value = "根据客户id列出所有符合条件的商机", response =
	// WrapOutString.class)
	@GET
	@Path("listbycustomerid/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ListAllOpportunityByCustomerId(@PathParam("id") String _customerid) {
		ActionResult<List<WrapOutOpportunity>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<WrapOutOpportunity> wraps = new ArrayList<>();
			List<String> ids = business.opportunityFactory().ListOpportunityByCustomerId(_customerid);
			wraps = WrapCrmTools.OpportunityOutCopier.copy(emc.list(Opportunity.class, ids));
			logger.warn(wraps.toString());
			result.setData(wraps);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	// 客户列表Next
	// @HttpMethodDescribe(value = "列出所有商机,默认每页count条", response =
	// WrapOutOpportunity.class)
	@PUT
	@Path("listall/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListOpportunityNextPage(@Suspended final AsyncResponse asyncResponse, @PathParam("id") String id,
			@PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutOpportunity>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapInFilterOpportunity wrapInFilter = null;
			try {
				wrapInFilter = this.convertToWrapIn(jsonElement, WrapInFilterOpportunity.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			WrapCopier<Opportunity, WrapOutOpportunity> wrapout_copier = WrapCopierFactory.wo(Opportunity.class,
					WrapOutOpportunity.class, null, WrapOutOpportunity.Excludes);
			String sequenceField = wrapInFilter.getSequenceField();
			EqualsTerms equals = new EqualsTerms();
			NotEqualsTerms notEquals = new NotEqualsTerms();
			LikeTerms likes = new LikeTerms();
			InTerms ins = new InTerms();
			NotInTerms notIns = new NotInTerms();
			MemberTerms members = new MemberTerms();
			NotMemberTerms notMembers = new NotMemberTerms();
			Boolean andJoin = false;
			String order = wrapInFilter.getOrder();

			// 多字段模糊差选
			if (null != wrapInFilter && null != wrapInFilter.getFuzzySearchKey()
					&& !wrapInFilter.getFuzzySearchKey().isEmpty()) {
				likes.put("opportunityname", wrapInFilter.getFuzzySearchKey());
				likes.put("udssel1", wrapInFilter.getFuzzySearchKey());
				likes.put("udssel2", wrapInFilter.getFuzzySearchKey());
				likes.put("udssel3", wrapInFilter.getFuzzySearchKey());
			}
			result = this.standardListNext(wrapout_copier, id, count, sequenceField, equals, notEquals, likes, ins,
					notIns, members, notMembers, andJoin, order);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	// 客户列表Prev
	// @HttpMethodDescribe(value = "列出所有商机,默认每页count条", response =
	// WrapOutOpportunity.class)
	@PUT
	@Path("listall/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListOpportunityPrevPage(@Suspended final AsyncResponse asyncResponse, @PathParam("id") String id,
			@PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutOpportunity>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapInFilterOpportunity wrapInFilter = null;
			try {
				wrapInFilter = this.convertToWrapIn(jsonElement, WrapInFilterOpportunity.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			WrapCopier<Opportunity, WrapOutOpportunity> wrapout_copier = WrapCopierFactory.wo(Opportunity.class,
					WrapOutOpportunity.class, null, WrapOutOpportunity.Excludes);
			String sequenceField = wrapInFilter.getSequenceField();
			EqualsTerms equals = new EqualsTerms();
			NotEqualsTerms notEquals = new NotEqualsTerms();
			LikeTerms likes = new LikeTerms();
			InTerms ins = new InTerms();
			NotInTerms notIns = new NotInTerms();
			MemberTerms members = new MemberTerms();
			NotMemberTerms notMembers = new NotMemberTerms();
			Boolean andJoin = false;
			String order = wrapInFilter.getOrder();

			// 多字段模糊差选
			if (null != wrapInFilter && null != wrapInFilter.getFuzzySearchKey()
					&& !wrapInFilter.getFuzzySearchKey().isEmpty()) {
				likes.put("opportunityname", wrapInFilter.getFuzzySearchKey());
				likes.put("udssel1", wrapInFilter.getFuzzySearchKey());
				likes.put("udssel2", wrapInFilter.getFuzzySearchKey());
				likes.put("udssel3", wrapInFilter.getFuzzySearchKey());
			}
			result = this.standardListPrev(wrapout_copier, id, count, sequenceField, equals, notEquals, likes, ins,
					notIns, members, notMembers, andJoin, order);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	// @HttpMethodDescribe(value = "根据id,判断商机是否存在", response =
	// WrapOutOpportunity.class)
	@GET
	@Path("isexist/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void IsExist(@Suspended final AsyncResponse asyncResponse, @PathParam("id") String id) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			boolean _IsExist = business.opportunityFactory().IsExistById(id);
			WrapOutBoolean wrap = new WrapOutBoolean();
			wrap.setValue(_IsExist);
			result.setData(wrap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}
