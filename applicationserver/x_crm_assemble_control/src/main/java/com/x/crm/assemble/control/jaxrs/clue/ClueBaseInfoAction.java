package com.x.crm.assemble.control.jaxrs.clue;

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

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.HttpMediaType;
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
import com.x.crm.assemble.control.Business;
import com.x.crm.assemble.control.WrapCrmTools;
import com.x.crm.assemble.control.wrapin.WrapInFilterClue;
import com.x.crm.assemble.control.wrapout.WrapOutClue;
import com.x.crm.core.entity.Clue;

import testdata.CreateTestData;

@Path("clue/baseinfo")
public class ClueBaseInfoAction extends StandardJaxrsAction {
	private Logger logger = LoggerFactory.getLogger(ClueBaseInfoAction.class);

	//@HttpMethodDescribe(value = "测试", response = WrapOutString.class)
	@GET
	@Path("iswork")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response iswork() {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = null;
		wrap = new WrapOutString();
		wrap.setValue("clue/baseinfo/iswork  is work!!");
		result.setData(wrap);
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	//@HttpMethodDescribe(value = "创建数据表，创建数据测试", response = WrapOutString.class)
	@GET
	@Path("create")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create() {
		ActionResult<WrapOutString> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Clue clue = new Clue();
			CreateTestData createtestdata = new CreateTestData();
			createtestdata.CreatDataClue(clue);
			// //模拟创建，随机和客户产生关联
			// Business business = new Business(emc);
			// List<String> customerids =
			// business.customerBaseInfoFactory().listAll();
			// Random rand = new Random();
			// int _tmpIndex = rand.nextInt(customerids.size());
			// String _tmpId = customerids.get(_tmpIndex);
			// clue.setCustomerid(_tmpId);
			// //模拟创建，随机和客户产生关联
			emc.beginTransaction(Clue.class);
			emc.persist(clue);
			emc.commit();
			WrapOutString wrap = null;
			wrap = new WrapOutString();
			wrap.setValue(clue.getId());
			result.setData(wrap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	//@HttpMethodDescribe(value = "列出所有线索", response = WrapOutString.class)
	@GET
	@Path("listall")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ListAllClue() {
		ActionResult<List<WrapOutClue>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<WrapOutClue> wraps = new ArrayList<>();
			List<String> ids = business.clueFactory().listAllids();
			wraps = WrapCrmTools.ClueOutCopier.copy(emc.list(Clue.class, ids));
			result.setData(wraps);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	//@HttpMethodDescribe(value = "列出所有线索ids", response = String.class)
	@GET
	@Path("listallids")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListAllids(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<String>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = business.clueFactory().listAllids();
			result.setData(ids);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 线索列表Next
	//@HttpMethodDescribe(value = "列出所有线索,默认每页count条", response = WrapOutClue.class)
	@PUT
	@Path("listall/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListClueNextPage(@Suspended final AsyncResponse asyncResponse, @PathParam("id") String id,
			@PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutClue>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapInFilterClue wrapInFilter = null;
			try {
				wrapInFilter = this.convertToWrapIn(jsonElement, WrapInFilterClue.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			WrapCopier<Clue, WrapOutClue> wrapout_copier = WrapCopierFactory.wo(Clue.class, WrapOutClue.class, null,
					WrapOutClue.Excludes);
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
				likes.put("salescluename", wrapInFilter.getFuzzySearchKey());
				likes.put("source", wrapInFilter.getFuzzySearchKey());
				likes.put("marketingeventid", wrapInFilter.getFuzzySearchKey());
				likes.put("address", wrapInFilter.getFuzzySearchKey());
			}
			result = this.standardListNext(wrapout_copier, id, count, sequenceField, equals, notEquals, likes, ins,
					notIns, members, notMembers, andJoin, order);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	// 线索列表Prev
	//@HttpMethodDescribe(value = "列出所有线索,默认每页count条", response = WrapOutClue.class)
	@PUT
	@Path("listall/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void ListCluePrevPage(@Suspended final AsyncResponse asyncResponse, @PathParam("id") String id,
			@PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutClue>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WrapInFilterClue wrapInFilter = null;
			try {
				wrapInFilter = this.convertToWrapIn(jsonElement, WrapInFilterClue.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			WrapCopier<Clue, WrapOutClue> wrapout_copier = WrapCopierFactory.wo(Clue.class, WrapOutClue.class, null,
					WrapOutClue.Excludes);
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
				likes.put("salescluename", wrapInFilter.getFuzzySearchKey());
				likes.put("source", wrapInFilter.getFuzzySearchKey());
				likes.put("marketingeventid", wrapInFilter.getFuzzySearchKey());
				likes.put("address", wrapInFilter.getFuzzySearchKey());
			}
			result = this.standardListPrev(wrapout_copier, id, count, sequenceField, equals, notEquals, likes, ins,
					notIns, members, notMembers, andJoin, order);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	// @HttpMethodDescribe(value = "更新 ", request = WrapInRoom.class, response =
	// WrapOutId.class)
	// @PUT
	// @Path("{id}")
	// @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	// @Consumes(MediaType.APPLICATION_JSON)
	// public Response put(@Context HttpServletRequest request, @PathParam("id")
	// String id, WrapInRoom wrapIn) {
	// ActionResult<WrapOutId> result = new ActionResult<>();
	// WrapOutId wrap = null;
	// try (EntityManagerContainer emc =
	// EntityManagerContainerFactory.instance().create()) {
	// EffectivePerson effectivePerson = this.effectivePerson(request);
	// Business business = new Business(emc);
	// Room room = emc.find(id, Room.class, ExceptionWhen.not_found);
	// business.roomEditAvailable(effectivePerson, room,
	// ExceptionWhen.not_allow);
	// emc.beginTransaction(Room.class);
	// WrapTools.roomInCopier.copy(wrapIn, room);
	// /* 检查审核人 */
	// this.checkAuditor(business, room);
	// emc.check(room, CheckPersistType.all);
	// emc.commit();
	// wrap = new WrapOutId(room.getId());
	// result.setData(wrap);
	// } catch (Throwable th) {
	// th.printStackTrace();
	// result.error(th);
	// }
	// return ResponseFactory.getDefaultActionResultResponse(result);
	// }

	/*
	 * @HttpMethodDescribe(value = "更新 ", request = WrapInClue.class, response =
	 * WrapOutId.class)
	 * 
	 * @GET
	 * 
	 * @Path("update/{id}")
	 * 
	 * @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON) public Response update(@Context
	 * HttpServletRequest request, @PathParam("id") String id, WrapInClue
	 * wrapIn) { ActionResult<WrapOutId> result = new ActionResult<>();
	 * WrapOutId wrap = null; try (EntityManagerContainer emc =
	 * EntityManagerContainerFactory.instance().create()) { Business business =
	 * new Business(emc); Clue clue = emc.find(id, Clue.class,
	 * ExceptionWhen.not_found); emc.beginTransaction(Clue.class);
	 * //WrapCrmTools..copy(wrapIn, clue); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } return null; }
	 */
}
