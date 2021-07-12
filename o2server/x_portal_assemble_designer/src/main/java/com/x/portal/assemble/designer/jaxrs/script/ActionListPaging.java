package com.x.portal.assemble.designer.jaxrs.script;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;

class ActionListPaging extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (!business.editable(effectivePerson, null)) {
				throw new PortalInvisibleException(effectivePerson.getDistinguishedName(), "all", "all");
			}
			EntityManager em = emc.get(Script.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			Predicate p = cb.conjunction();
			List<Wo> wos = emc.fetchDescPaging(Script.class, Wo.copier, p, page, size, Script.sequence_FIELDNAME);
			wos.stream().forEach(wo -> {
				try {
					Portal portal = emc.find(wo.getPortal(), Portal.class);
					if (portal != null) {
						wo.setPortalName(portal.getName());
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
			result.setData(wos);
			result.setCount(emc.count(Script.class, p));
			return result;
		}
	}

	public static class Wo extends Script {

		private static final long serialVersionUID = 1211311928431174895L;

		static WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Script.dependScriptList_FIELDNAME, Script.text_FIELDNAME));

		@FieldDescribe("门户应用名称.")
		private String portalName;

		public String getPortalName() {
			return portalName;
		}

		public void setPortalName(String portalName) {
			this.portalName = portalName;
		}
	}
}
