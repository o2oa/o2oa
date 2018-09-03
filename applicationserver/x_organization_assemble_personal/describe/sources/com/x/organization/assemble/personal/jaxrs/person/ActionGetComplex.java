//package com.x.organization.assemble.personal.jaxrs.person;
//
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.x.base.core.DefaultCharset;
//import com.x.base.core.bean.WrapCopier;
//import com.x.base.core.bean.WrapCopierFactory;
//import com.x.base.core.cache.ApplicationCache;
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.entity.JpaObject;
//import com.x.base.core.http.ActionResult;
//import com.x.base.core.http.EffectivePerson;
//import com.x.base.core.http.WrapOutOnline;
//import com.x.base.core.project.x_collaboration_assemble_websocket;
//import com.x.base.core.project.organization.Identity;
//import com.x.base.core.project.organization.Person;
//import com.x.base.core.project.server.Config;
//import com.x.organization.assemble.personal.Business;
//import com.x.organization.assemble.personal.ThisApplication;
//import com.x.organization.core.express.Organization;
//
//import net.sf.ehcache.Element;
//
//class ActionGetComplex extends BaseAction {
//
//	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
//		ActionResult<Wo> result = new ActionResult<>();
//		Wo wo = null;
//		if (Config.token().isInitialManager(effectivePerson.getDistinguishedName())) {
//			wo = new Wo();
//			Config.token().initialManagerInstance().copyTo(wo);
//		} else {
//			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(),
//					effectivePerson.getDistinguishedName());
//			Element element = cache.get(cacheKey);
//			if ((null != element) && (null != element.getObjectValue())) {
//				wo = (Wo) element.getObjectValue();
//			} else {
//				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//					Business business = new Business(emc);
//					Person person = business.organization().person().getObject(effectivePerson.getDistinguishedName());
//					if (null != person) {
//						wo = Wo.copier.copy(person);
//						// if (StringUtils.isEmpty(wrap.getIcon())) {
//						// if (Objects.equals(wrap.getGenderType(),
//						// GenderType.f)) {
//						// wrap.setIcon(Person.ICON_FEMALE);
//						// } else if (Objects.equals(wrap.getGenderType(),
//						// GenderType.m)) {
//						// wrap.setIcon(Person.ICON_MALE);
//						// } else {
//						// wrap.setIcon(Person.ICON_UNKOWN);
//						// }
//						// }
//						wo.setIdentityList(this.listIdentity(business.organization(), wo.getDistinguishedName()));
//						// wo.setOnlineStatus(this.getOnlineStatus(business,
//						// wrap.getName()));
//						cache.put(new Element(cacheKey, wo));
//					}
//				}
//			}
//		}
//		result.setData(wo);
//		return result;
//	}
//
//	public static class Wo extends Person {
//
//		static WrapCopier<Person, Wo> copier = WrapCopierFactory.create(Person.class, Wo.class, null,
//				JpaObject.FieldsInvisible);
//
//		private List<WoIdentity> identityList = new ArrayList<>();
//
//		private String onlineStatus;
//
//		public String getOnlineStatus() {
//			return onlineStatus;
//		}
//
//		public void setOnlineStatus(String onlineStatus) {
//			this.onlineStatus = onlineStatus;
//		}
//
//		public List<WoIdentity> getIdentityList() {
//			return identityList;
//		}
//
//		public void setIdentityList(List<WoIdentity> identityList) {
//			this.identityList = identityList;
//		}
//
//	}
//
//	public static class WoIdentity extends Identity {
//
//		static WrapCopier<Identity, WoIdentity> copier = WrapCopierFactory.create(Identity.class,
//				WoIdentity.class, null, JpaObject.FieldsInvisible);
//
//		private List<String> unitDutyList = new ArrayList<>();
//
//		public List<String> getUnitDutyList() {
//			return unitDutyList;
//		}
//
//		public void setUnitDutyList(List<String> unitDutyList) {
//			this.unitDutyList = unitDutyList;
//		}
//
//	}
//
//	private List<WoIdentity> listIdentity(Organization organization, String personName) throws Exception {
//		List<WoIdentity> list = new ArrayList<>();
//		List<Identity> os = organization.identity().listObject(organization.identity().listWithPerson(personName));
//		for (Identity o : os) {
//			WoIdentity wo = WoIdentity.copier.copy(o);
//			wo.getUnitDutyList().addAll(organization.unitDuty().listNameWithIdentity(o.getDistinguishedName()));
//			list.add(wo);
//		}
//		return list;
//	}
//
//	private String getOnlineStatus(Business business, String person) throws Exception {
//		WrapOutOnline online = ThisApplication.context().applications()
//				.getQuery(x_collaboration_assemble_websocket.class,
//						"online/person/" + URLEncoder.encode(person, DefaultCharset.name))
//				.getData(WrapOutOnline.class);
//		return online.getOnlineStatus();
//	}
//}
