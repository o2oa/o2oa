package o2.collect.assemble.jaxrs.warnlog;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.core.entity.log.WarnLog;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WarnLog o = emc.find(id, WarnLog.class);
			if (null == o) {
				throw new ExceptionWarnLogNotExist(id);
			}
			Wo wo = Wo.copier.copy(o);
			result.setData(wo);
		}
		return result;
	}

	public static class Wo extends WarnLog {

		private static final long serialVersionUID = -4855784604415258419L;

		static WrapCopier<WarnLog, Wo> copier = WrapCopierFactory.wo(WarnLog.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
