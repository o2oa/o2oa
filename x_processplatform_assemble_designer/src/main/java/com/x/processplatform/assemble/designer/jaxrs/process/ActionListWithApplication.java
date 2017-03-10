package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutProcess;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionListWithApplication extends ActionBase {

	ActionResult<List<WrapOutProcess>> execute(EffectivePerson effectivePerson, String applicationId)
			throws Exception {
		ActionResult<List<WrapOutProcess>> result = new ActionResult<>();
		List<WrapOutProcess> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class, ExceptionWhen.not_found);
			if (null == application) {
				throw new Exception("application{id:" + applicationId + "} not existed.");
			}
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			List<String> ids = business.process().listWithApplication(applicationId);
			for (Process o : emc.list(Process.class, ids)) {
				wraps.add(processOutCopier.copy(o));
			}
			Collections.sort(wraps, new Comparator<WrapOutProcess>() {
				public int compare(WrapOutProcess o1, WrapOutProcess o2) {
					return ObjectUtils.compare(o1.getName(), o2.getName(), true);
				}
			});
			result.setData(wraps);
			return result;
		}
	}

}