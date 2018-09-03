package o2.collect.assemble.jaxrs.unexpectederrorlog;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.core.entity.log.UnexpectedErrorLog;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			UnexpectedErrorLog o = emc.find(id, UnexpectedErrorLog.class);
			if (null == o) {
				throw new ExceptionUnexpectedErrorLogNotExist(id);
			}
			Wo wo = Wo.copier.copy(o);
			result.setData(wo);
		}
		return result;
	}

	public static class Wo extends UnexpectedErrorLog {

		private static final long serialVersionUID = -4855784604415258419L;

		static WrapCopier<UnexpectedErrorLog, Wo> copier = WrapCopierFactory.wo(UnexpectedErrorLog.class, Wo.class,
				null, JpaObject.FieldsInvisible);

	}
}
