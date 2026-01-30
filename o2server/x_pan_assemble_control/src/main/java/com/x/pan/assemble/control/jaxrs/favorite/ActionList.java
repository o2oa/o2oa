package com.x.pan.assemble.control.jaxrs.favorite;

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
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Favorite;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class ActionList extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger( ActionList.class );

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			final Business business = new Business(emc);
			List<Wo> wos = emc.fetchEqual(Favorite.class, Wo.copier, Favorite.person_FIELDNAME, effectivePerson.getDistinguishedName());

			final boolean isManager = business.controlAble(effectivePerson);
			wos.stream().forEach(wo -> {
				try {
					boolean isAdmin;
					boolean isEditor;
					if (isManager) {
						isAdmin = true;
						isEditor = true;
					} else {
						isAdmin = business.folder3().isZoneAdmin(wo.getZoneId(), effectivePerson.getDistinguishedName());
						if (isAdmin) {
							isEditor = true;
						} else {
							isEditor = business.folder3().isZoneEditor(wo.getZoneId(), effectivePerson.getDistinguishedName());
						}
					}
					wo.setIsAdmin(isAdmin);
					wo.setIsEditor(isEditor);
				} catch (Exception e) {
					logger.debug(e.getMessage());
				}
			});

			wos = wos.stream().sorted(Comparator.comparing(Favorite::getOrderNumber, Comparator.nullsLast(Integer::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Favorite {

		protected static WrapCopier<Favorite, Wo> copier = WrapCopierFactory.wo(Favorite.class, Wo.class,
				JpaObject.singularAttributeField(Favorite.class, true, true), null);

		@FieldDescribe("是否管理员")
		private Boolean isAdmin = false;
		@FieldDescribe("是否编辑着")
		private Boolean isEditor = false;

		public Boolean getIsAdmin() {
			return isAdmin;
		}

		public void setIsAdmin(Boolean isAdmin) {
			this.isAdmin = isAdmin;
		}

		public Boolean getIsEditor() {
			return isEditor;
		}

		public void setIsEditor(Boolean isEditor) {
			this.isEditor = isEditor;
		}
	}

}
