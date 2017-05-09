package com.x.meeting.assemble.control.jaxrs.room;

import java.util.ArrayList;
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

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.server.StorageMapping;
import com.x.base.core.utils.SortTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.ThisApplication;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapin.WrapInRoom;
import com.x.meeting.assemble.control.wrapout.WrapOutRoom;
import com.x.meeting.core.entity.Attachment;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.Room;
import com.x.organization.core.express.wrap.WrapPerson;

@Path("room")
public class RoomAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "列示所有的会议室.", response = WrapOutRoom.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutRoom>> result = new ActionResult<>();
		List<WrapOutRoom> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			business.isManager(effectivePerson, ExceptionWhen.not_allow);
			List<String> ids = business.room().list();
			wraps = WrapTools.roomOutCopier.copy(emc.list(Room.class, ids));
			SortTools.asc(wraps, false, "name");
			WrapTools.setFutureMeeting(business, wraps, true);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定的会议室.", response = WrapOutRoom.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutRoom> result = new ActionResult<>();
		WrapOutRoom wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Room room = emc.find(id, Room.class, ExceptionWhen.not_found);
			wrap = WrapTools.roomOutCopier.copy(room);
			WrapTools.setFutureMeeting(business, wrap, true);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建Room.", request = WrapInRoom.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInRoom wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			business.buildingEditAvailable(effectivePerson, ExceptionWhen.not_allow);
			Room room = new Room();
			emc.beginTransaction(Room.class);
			WrapTools.roomInCopier.copy(wrapIn, room);
			this.checkAuditor(business, room);
			emc.persist(room, CheckPersistType.all);
			emc.commit();
			wrap = new WrapOutId(room.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新Room.", request = WrapInRoom.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInRoom wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			Room room = emc.find(id, Room.class, ExceptionWhen.not_found);
			business.roomEditAvailable(effectivePerson, room, ExceptionWhen.not_allow);
			emc.beginTransaction(Room.class);
			WrapTools.roomInCopier.copy(wrapIn, room);
			/* 检查审核人 */
			this.checkAuditor(business, room);
			emc.check(room, CheckPersistType.all);
			emc.commit();
			wrap = new WrapOutId(room.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除Room.", response = WrapOutId.class)
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
			Room room = emc.find(id, Room.class, ExceptionWhen.not_found);
			business.roomEditAvailable(effectivePerson, room, ExceptionWhen.not_allow);
			emc.beginTransaction(Room.class);
			emc.beginTransaction(Meeting.class);
			emc.beginTransaction(Attachment.class);
			for (Meeting meeting : emc.list(Meeting.class, business.meeting().listWithRoom(room.getId()))) {
				for (Attachment attachment : emc.list(Attachment.class,
						business.attachment().listWithMeeting(meeting.getId()))) {
					StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
							attachment.getStorage());
					attachment.deleteContent(mapping);
					emc.remove(attachment);
				}
				emc.remove(meeting);
			}
			emc.remove(room);
			emc.commit();
			wrap = new WrapOutId(room.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取拼音首字母开始的.", response = WrapOutRoom.class)
	@GET
	@Path("list/pinyininitial/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPinyinInitial(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutRoom>> result = new ActionResult<>();
		List<WrapOutRoom> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = business.room().listPinyinInitial(key);
			wraps = WrapTools.roomOutCopier.copy(emc.list(Room.class, ids));
			WrapTools.setFutureMeeting(business, wraps, true);
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据名称进行模糊查询.", response = WrapOutRoom.class)
	@GET
	@Path("list/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLike(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutRoom>> result = new ActionResult<>();
		List<WrapOutRoom> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = business.room().listLike(key);
			wraps = WrapTools.roomOutCopier.copy(emc.list(Room.class, ids));
			WrapTools.setFutureMeeting(business, wraps, true);
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据拼音或者首字母进行模糊查询.", response = WrapOutRoom.class)
	@GET
	@Path("/list/like/pinyin/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikePinyin(@Context HttpServletRequest request, @PathParam("key") String key) {
		ActionResult<List<WrapOutRoom>> result = new ActionResult<>();
		List<WrapOutRoom> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = business.room().listLikePinyin(key);
			wraps = WrapTools.roomOutCopier.copy(emc.list(Room.class, ids));
			WrapTools.setFutureMeeting(business, wraps, true);
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private void checkAuditor(Business business, Room room) throws Exception {
		if (StringUtils.isNotEmpty(room.getAuditor())) {
			WrapPerson person = business.organization().person().getWithName(room.getAuditor());
			if (null != person) {
				room.setAuditor(person.getName());
			} else {
				room.setAuditor("");
			}
		}
	}

}