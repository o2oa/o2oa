package com.x.meeting.assemble.control.jaxrs.building;

import java.util.ArrayList;
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

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.DateTools;
import com.x.base.core.utils.SortTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapin.WrapInBuilding;
import com.x.meeting.assemble.control.wrapout.WrapOutBuilding;
import com.x.meeting.assemble.control.wrapout.WrapOutRoom;
import com.x.meeting.core.entity.Building;

@Path("building")
public class BuildingAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "获取Building,同时获取Building下的Room 和 Room 下的将来Meeting.", response = WrapOutBuilding.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutBuilding> result = new ActionResult<>();
		WrapOutBuilding wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Building building = emc.find(id, Building.class, ExceptionWhen.not_found);
			wrap = WrapTools.buildingOutCopier.copy(building);
			/* 添加Room信息 */
			WrapTools.setRoom(business, wrap);
			/* 添加将来的Meeting信息 */
			WrapTools.setFutureMeeting(business, wrap.getRoomList(), true);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取所有Building 同时获取Building下的Room 和 Room 下的将来Meeting.", response = WrapOutBuilding.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutBuilding>> result = new ActionResult<>();
		List<WrapOutBuilding> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = business.building().list();
			wraps = WrapTools.buildingOutCopier.copy(emc.list(Building.class, ids));
			WrapTools.setRoom(business, wraps);
			for (WrapOutBuilding wrap : wraps) {
				WrapTools.setFutureMeeting(business, wrap.getRoomList(), true);
			}
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示在所有building和下属的room，和room下面将来的会议，同时判断在指定时间内room是否空闲", response = WrapOutBuilding.class)
	@GET
	@Path("list/start/{start}/completed/{completed}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCheckRoomIde(@Context HttpServletRequest request, @PathParam("start") String start,
			@PathParam("completed") String completed) {
		ActionResult<List<WrapOutBuilding>> result = new ActionResult<>();
		List<WrapOutBuilding> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Date startTime = DateTools.parse(start, DateTools.format_yyyyMMdd + " " + DateTools.format_HHmm);
			Date completedTime = DateTools.parse(completed, DateTools.format_yyyyMMdd + " " + DateTools.format_HHmm);
			List<String> ids = business.building().list();
			wraps = WrapTools.buildingOutCopier.copy(emc.list(Building.class, ids));
			WrapTools.setRoom(business, wraps);
			for (WrapOutBuilding wrap : wraps) {
				WrapTools.setFutureMeeting(business, wrap.getRoomList(), true);
				for (WrapOutRoom room : wrap.getRoomList()) {
					WrapTools.checkRoomIdle(business, room, startTime, completedTime);
				}
			}
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建Building.", request = WrapInBuilding.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInBuilding wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			if (!business.buildingEditAvailable(effectivePerson)) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
			}
			Building o = new Building();
			emc.beginTransaction(Building.class);
			WrapTools.buildingInCopier.copy(wrapIn, o);
			emc.persist(o, CheckPersistType.all);
			emc.commit();
			wrap = new WrapOutId(o.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新Building.", request = WrapInBuilding.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInBuilding wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			if (!business.buildingEditAvailable(effectivePerson)) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
			}
			Building o = emc.find(id, Building.class, ExceptionWhen.not_found);
			emc.beginTransaction(Building.class);
			WrapTools.buildingInCopier.copy(wrapIn, o);
			emc.check(o, CheckPersistType.all);
			emc.commit();
			wrap = new WrapOutId(o.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除Building.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			business.buildingEditAvailable(effectivePerson, ExceptionWhen.not_allow);
			Building o = emc.find(id, Building.class, ExceptionWhen.not_found);
			emc.beginTransaction(Building.class);
			emc.remove(o, CheckRemoveType.all);
			emc.commit();
			wrap = new WrapOutId(o.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取拼音首字母开始的Building.", response = WrapOutBuilding.class)
	@GET
	@Path("list/pinyininitial/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPinyinInitial(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutBuilding>> result = new ActionResult<>();
		List<WrapOutBuilding> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = business.building().listPinyinInitial(key);
			wraps = WrapTools.buildingOutCopier.copy(emc.list(Building.class, ids));
			WrapTools.setRoom(business, wraps);
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据名称进行模糊查询.", response = WrapOutBuilding.class)
	@GET
	@Path("list/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLike(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutBuilding>> result = new ActionResult<>();
		List<WrapOutBuilding> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = business.building().listLike(key);
			wraps = WrapTools.buildingOutCopier.copy(emc.list(Building.class, ids));
			WrapTools.setRoom(business, wraps);
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据拼音或者首字母进行模糊查询.", response = WrapOutBuilding.class)
	@GET
	@Path("/list/like/pinyin/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikePinyin(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutBuilding>> result = new ActionResult<>();
		List<WrapOutBuilding> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = business.building().listLikePinyin(key);
			wraps = WrapTools.buildingOutCopier.copy(emc.list(Building.class, ids));
			WrapTools.setRoom(business, wraps);
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}