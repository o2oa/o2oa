package com.x.processplatform.service.processing.processor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Empower;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.ManualProperties;
import com.x.processplatform.core.entity.ticket.Ticket;
import com.x.processplatform.core.entity.ticket.Tickets;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.processor.manual.TaskIdentity;

/**
 * 在Manual环节计算所有的待办人的Identity
 * 
 * @author Rui
 *
 */
public class TaskTickets {

	private TaskTickets() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskTickets.class);

	/* 计算manual节点中所有的待办，全部翻译成Identity */
	public static Tickets translate(AeiObjects aeiObjects, Manual manual) throws Exception {
		List<EmpowerableIdentity> identities = new ArrayList<>();
		List<String> units = new ArrayList<>();
		List<String> groups = new ArrayList<>();
		// 指定的身份
		if (ListTools.isNotEmpty(manual.getTaskIdentityList())) {
			identities.addAll(manual.getTaskIdentityList().stream().map(o -> new EmpowerableIdentity(false, o))
					.collect(Collectors.toList()));
		}
		// 流程角色
		if ((null != manual.getTaskParticipant()) && StringUtils.isNotBlank(manual.getTaskParticipant().getType())) {
			identities.addAll(participant(aeiObjects, manual).stream().map(o -> new EmpowerableIdentity(false, o))
					.collect(Collectors.toList()));
		}
		// 选择了职务
		identities.addAll(duty(aeiObjects, manual).stream().map(o -> new EmpowerableIdentity(false, o))
				.collect(Collectors.toList()));
		// 指定data数据路径值
		data(identities, units, groups, aeiObjects.getData(), manual);
		// 使用脚本计算
		script(identities, units, groups, aeiObjects, manual);
		// 指定处理组织
		if (ListTools.isNotEmpty(manual.getTaskUnitList())) {
			units.addAll(manual.getTaskUnitList());
		}
		// 指定处理群组
		if (ListTools.isNotEmpty(manual.getTaskGroupList())) {
			groups.addAll(manual.getTaskGroupList());
		}
		if (ListTools.isNotEmpty(units)) {
			identities.addAll(aeiObjects.business().organization().identity().listWithUnitSubDirect(units).stream()
					.map(o -> new EmpowerableIdentity(false, o)).collect(Collectors.toList()));
		}
		if (ListTools.isNotEmpty(groups)) {
			identities.addAll(aeiObjects.business().organization().identity().listWithGroup(groups).stream()
					.map(o -> new EmpowerableIdentity(false, o)).collect(Collectors.toList()));
		}
		return translateEmpowerableIdentity(aeiObjects, manual, identities);
	}

	public static Tickets translate(AeiObjects aeiObjects, Manual manual, List<String> distinguishedNames)
			throws Exception {
		List<EmpowerableIdentity> identities = distinguishedNames.stream().distinct()
				.map(o -> new EmpowerableIdentity(false, o)).collect(Collectors.toList());
		return translateEmpowerableIdentity(aeiObjects, manual, identities);

	}

	/**
	 * 转换成Tickets,进行授权和检查
	 * 
	 * @param aeiObjects
	 * @param manual
	 * @param identities
	 * @return
	 * @throws Exception
	 */
	private static Tickets translateEmpowerableIdentity(AeiObjects aeiObjects, Manual manual,
			List<EmpowerableIdentity> identities) throws Exception {

		identities = identities.stream().distinct().collect(Collectors.toList());

		if (StringUtils.equalsIgnoreCase(Work.WORKCREATETYPE_ASSIGN, aeiObjects.getWork().getWorkCreateType())
				|| BooleanUtils.isTrue(aeiObjects.getWork().getWorkThroughManual())) {
			empower(aeiObjects, identities);
		}

		List<String> checkIdentities = aeiObjects.business().organization().identity()
				.list(identities.stream().flatMap(o -> Stream.of(o.identity, o.toIdentity))
						.filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList()));

		identities = identities.stream().filter(o -> checkIdentities.contains(o.identity))
				.filter(o -> StringUtils.isEmpty(o.toIdentity) || checkIdentities.contains(o.toIdentity))
				.collect(Collectors.toList());

		return manual.toTickets(identities.stream().flatMap(o -> {
			if (StringUtils.isNotEmpty(o.toIdentity)) {
				Ticket ticket = new Ticket(o.toIdentity);
				ticket.fromDistinguishedName(o.identity);
				if (BooleanUtils.isTrue(o.keepEnable)) {
					Ticket keepTicket = new Ticket(o.identity);
					keepTicket.sibling(ticket);
					ticket.sibling(keepTicket);
					return Stream.of(ticket, keepTicket);
				} else {
					return Stream.of(ticket);
				}
			} else {
				return Stream.of(new Ticket(o.identity));
			}
		}).collect(Collectors.toList()));

	}

	/**
	 * 执行授权
	 * 
	 * @param aeiObjects
	 * @param identities
	 * @throws Exception
	 */
	private static void empower(AeiObjects aeiObjects, List<EmpowerableIdentity> identities) throws Exception {
		List<Empower> empowers = aeiObjects.business().organization().empower().listWithIdentityObject(
				aeiObjects.getWork().getApplication(), aeiObjects.getProcess().getEdition(),
				aeiObjects.getWork().getProcess(), aeiObjects.getWork().getId(),
				identities.stream().filter(o -> BooleanUtils.isFalse(o.skipEmpower)).map(o -> o.identity)
						.collect(Collectors.toList()));
		if (!empowers.isEmpty()) {
			Map<String, EmpowerableIdentity> nameEmpowerableIdentityMap = identities.stream().collect(
					Collectors.toMap(o -> o.identity, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));
			empowers.stream().forEach(o -> nameEmpowerableIdentityMap.computeIfPresent(o.getFromIdentity(), (k, v) -> {
				v.toIdentity = o.getToIdentity();
				v.keepEnable = BooleanUtils.isTrue(o.getKeepEnable());
				return v;
			}));
		}
	}

	/* 取到指定职务的identity */
	private static List<String> duty(AeiObjects aeiObjects, Manual manual) throws Exception {
		List<String> list = new ArrayList<>();
		if (StringUtils.isNotEmpty(manual.getTaskDuty())) {
			JsonArray array = XGsonBuilder.instance().fromJson(manual.getTaskDuty(), JsonArray.class);
			Iterator<JsonElement> iterator = array.iterator();
			while (iterator.hasNext()) {
				JsonObject o = iterator.next().getAsJsonObject();
				String name = o.get("name").getAsString();
				String code = o.get("code").getAsString();
				Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getActivity(),
						Business.EVENT_TASKDUTY, name, code);
				List<String> ds = GraalvmScriptingFactory.evalAsDistinguishedNames(source, aeiObjects.bindings());
				if (ListTools.isNotEmpty(ds)) {
					for (String str : ds) {
						List<String> os = aeiObjects.business().organization().unitDuty()
								.listIdentityWithUnitWithName(str, name);
						if (ListTools.isNotEmpty(os)) {
							list.addAll(os);
						}
					}
				}
			}
		}
		return ListTools.trim(list, true, true);
	}

	private static List<String> participant(AeiObjects aeiObjects, Manual manual) throws Exception {
		List<String> list = new ArrayList<>();
		switch (Objects.toString(manual.getTaskParticipant().getType(), "")) {
		case ManualProperties.Participant.TYPE_ACTIVITY:
			if ((null != manual.getTaskParticipant().getData())
					&& (manual.getTaskParticipant().getData().isJsonArray())) {
				final List<String> taskParticipantActivities = new ArrayList<>();
				manual.getTaskParticipant().getData().getAsJsonArray()
						.forEach(o -> taskParticipantActivities.add(o.getAsString()));
				aeiObjects.getTaskCompleteds().stream()
						.filter(o -> (!StringUtils.equalsIgnoreCase(o.getAct(), TaskCompleted.ACT_EMPOWER)))
						.filter(o -> taskParticipantActivities.contains(o.getActivity()))
						.map(TaskCompleted::getIdentity).filter(StringUtils::isNotBlank).forEach(list::add);
			}
			break;
		case ManualProperties.Participant.TYPE_MAINTENANCE:
			if (StringUtils.isNotBlank(aeiObjects.getProcess().getMaintenanceIdentity())) {
				list.add(aeiObjects.getProcess().getMaintenanceIdentity());
			}
			break;
		case ManualProperties.Participant.TYPE_CREATOR:
			list.add(aeiObjects.getWork().getCreatorIdentity());
			break;
		default:
			break;
		}
		return list;
	}

	/* 取到script指定的identity */
	private static List<String> script(List<EmpowerableIdentity> identities, List<String> units, List<String> groups,
			AeiObjects aeiObjects, Manual manual) throws Exception {
		List<String> list = new ArrayList<>();
		if ((StringUtils.isNotEmpty(manual.getTaskScript())) || (StringUtils.isNotEmpty(manual.getTaskScriptText()))) {
			Source source = aeiObjects.business().element().getCompiledScript(aeiObjects.getWork().getApplication(),
					manual, Business.EVENT_MANUALTASK);
			List<String> names = GraalvmScriptingFactory.evalAsDistinguishedNames(source, aeiObjects.bindings());
			addObjectToTaskIdentities(identities, units, groups, names);
		}
		return list;
	}

	/**
	 * 取得通过路径指定的组织专用标识,这里的值可能通过前台选择通过data传递进来,
	 * 在前台选择的值中可能包含ignoreEmpower,表示选择人员的时候强制忽略授权.
	 * 
	 * @param taskIdentities
	 * @param units
	 * @param groups
	 * @param data
	 * @param manual
	 * @throws Exception
	 */
	private static void data(List<EmpowerableIdentity> identities, List<String> units, List<String> groups, Data data,
			Manual manual) throws Exception {
		if (ListTools.isNotEmpty(manual.getTaskDataPathList())) {
			for (String str : ListTools.trim(manual.getTaskDataPathList(), true, true)) {
				Object o = data.find(str);
				if (null != o) {
					addObjectToTaskIdentities(identities, units, groups, o);
				}
			}
		}
	}

	private static void addObjectToTaskIdentities(List<EmpowerableIdentity> identities, List<String> units,
			List<String> groups, Object o) throws Exception {
		for (String d : asDistinguishedNames(o)) {
			if (OrganizationDefinition.isIdentityDistinguishedName(d)) {
				Boolean ignore = BooleanUtils.isTrue(BooleanUtils.toBooleanObject(Objects.toString(
						PropertyTools.getOrElse(o, TaskIdentity.FIELD_IGNOREEMPOWER, Boolean.class, Boolean.FALSE),
						"false")));
				identities.add(new EmpowerableIdentity(ignore, d));
			} else if (OrganizationDefinition.isUnitDistinguishedName(d)) {
				units.add(d);
			} else if (OrganizationDefinition.isGroupDistinguishedName(d)) {
				groups.add(d);
			}
		}
	}

	public static List<String> asDistinguishedNames(Object o)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<String> list = new ArrayList<>();
		if (null != o) {
			if (o instanceof CharSequence) {
				list.add(Objects.toString(o));
			} else if (o instanceof Iterable) {
				asIterable(o, list);
			} else {
				Object obj = PropertyUtils.getProperty(o, JpaObject.DISTINGUISHEDNAME);
				String str = Objects.toString(obj, "");
				if (StringUtils.isNotEmpty(str)) {
					list.add(str);
				}
			}
		}
		return list;
	}

	private static void asIterable(Object o, List<String> list)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		for (Object obj : (Iterable<?>) o) {
			if (null != obj) {
				if (obj instanceof CharSequence) {
					list.add(Objects.toString(obj));
				} else {
					Object d = PropertyUtils.getProperty(obj, JpaObject.DISTINGUISHEDNAME);
					if (null != d) {
						list.add(Objects.toString(d));
					}
				}
			}
		}
	}

	private static class EmpowerableIdentity {

		private Boolean skipEmpower;
		private String identity;
		private String toIdentity;
		private Boolean keepEnable;

		private EmpowerableIdentity(Boolean skipEmpower, String identity) {
			this.skipEmpower = skipEmpower;
			this.identity = identity;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			EmpowerableIdentity i = (EmpowerableIdentity) o;
			return StringUtils.equals(identity, i.identity);
		}

		@Override
		public int hashCode() {
			return Objects.hash(identity);
		}
	}

}