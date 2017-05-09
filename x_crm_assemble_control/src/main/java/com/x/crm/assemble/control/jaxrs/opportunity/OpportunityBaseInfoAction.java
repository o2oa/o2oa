package com.x.crm.assemble.control.jaxrs.opportunity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.crm.assemble.control.Business;
import com.x.crm.assemble.control.WrapCrmTools;
import com.x.crm.assemble.control.wrapout.WrapOutOpportunity;
import com.x.crm.core.entity.Opportunity;

import testdata.CreateTestData;

@Path("opportunity/baseinfo")
public class OpportunityBaseInfoAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger(OpportunityBaseInfoAction.class);

	@HttpMethodDescribe(value = "测试", response = WrapOutString.class)
	@GET
	@Path("iswork")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response iswork() {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = null;
		wrap = new WrapOutString();
		wrap.setValue("opportunity/baseinfo/iswork  is work!!");
		result.setData(wrap);
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列出所有商机", response = WrapOutString.class)
	@GET
	@Path("listall")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ListAllOpportunity() {
		ActionResult<List<WrapOutOpportunity>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<WrapOutOpportunity> wraps = new ArrayList<>();
			List<String> ids = business.opportunityFactory().listAll();
			wraps = WrapCrmTools.OpportunityOutCopier.copy(emc.list(Opportunity.class, ids));
			logger.warn(wraps.toString());
			result.setData(wraps);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建数据表，创建数据测试", response = WrapOutString.class)
	@GET
	@Path("create")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create() {
		ActionResult<WrapOutString> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Opportunity opportunity = new Opportunity();
			CreateTestData createtestdata = new CreateTestData();
			createtestdata.creatDataOpportunity(opportunity);

			//模拟创建，随机和客户产生关联
			Business business = new Business(emc);
			List<String> customerids = business.customerBaseInfoFactory().listAll();
			Random rand = new Random();
			int _tmpIndex = rand.nextInt(customerids.size());
			String _tmpId = customerids.get(_tmpIndex);
			//模拟创建，随机和客户产生关联

			opportunity.setCustomerid(_tmpId);

			emc.beginTransaction(Opportunity.class);
			emc.persist(opportunity);
			emc.commit();
			WrapOutString wrap = null;
			wrap = new WrapOutString();
			wrap.setValue(opportunity.getId());
			result.setData(wrap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据id删除商机", response = WrapOutString.class)
	@GET
	@Path("delete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.error("delete:" + id);
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

	@HttpMethodDescribe(value = "根据id查找商机", response = WrapOutString.class)
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

	@HttpMethodDescribe(value = "根据客户id列出所有符合条件的商机", response = WrapOutString.class)
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

}
