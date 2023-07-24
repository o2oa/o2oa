package com.x.component.assemble.control.jaxrs.status;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.component.assemble.control.Business;
import com.x.component.core.entity.Component;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionList.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> ids = business.component().listVisiable();
			emc.list(Component.class, ids).stream().sorted(
					Comparator.comparing(Component::getOrderNumber, Comparator.nullsLast(Comparator.naturalOrder())))
					.forEach(o -> {
						if (this.allow(o, effectivePerson.getDistinguishedName())) {
							wo.getAllowList().add(WoComponent.copier.copy(o));
						} else {
							wo.getDenyList().add(WoComponent.copier.copy(o));
						}
					});
		}
		result.setData(wo);
		return result;
	}

	private boolean allow(Component component, String person) {
		if ((component.getAllowList().isEmpty()) && (component.getDenyList().isEmpty())) {
			return true;
		}
		if (component.getAllowList().contains(person)) {
			return true;
		}
		if (component.getDenyList().contains(person)) {
			return false;
		}
		return false;
	}

	@Schema(name = "com.x.component.assemble.control.jaxrs.status.ActionList$Wo")
	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 5129436264737035805L;

		private List<WoComponent> allowList = new ArrayList<>();
		private List<WoComponent> denyList = new ArrayList<>();

		public List<WoComponent> getAllowList() {
			return allowList;
		}

		public void setAllowList(List<WoComponent> allowList) {
			this.allowList = allowList;
		}

		public List<WoComponent> getDenyList() {
			return denyList;
		}

		public void setDenyList(List<WoComponent> denyList) {
			this.denyList = denyList;
		}

	}

	@Schema(name = "com.x.component.assemble.control.jaxrs.status.ActionList$WoComponent")
	public static class WoComponent extends Component {

		private static final long serialVersionUID = -4751880472257349437L;

		static WrapCopier<Component, WoComponent> copier = WrapCopierFactory.wo(Component.class, WoComponent.class,
				null, JpaObject.FieldsInvisible);

	}

}
