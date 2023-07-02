package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionEnable extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = emc.find(id, Process.class);
			if (null == process) {
				throw new ExceptionProcessNotExisted(id);
			}
			Application application = emc.find(process.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(process.getApplication());
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			emc.beginTransaction(Process.class);
			if(StringUtils.isEmpty(process.getEdition())){
				process.setLastUpdateTime(new Date());
				process.setEdition(process.getId());
				process.setEditionEnable(true);
				process.setEditionNumber(1.0);
				process.setEditionName(process.getName() + "_V" + process.getEditionNumber());
				this.updateCreatePersonLastUpdatePerson(effectivePerson, business, process);
			}else{
				if(!BooleanUtils.isTrue(process.getEditionEnable())){
					process.setLastUpdateTime(new Date());
					process.setEditionEnable(true);
					this.updateCreatePersonLastUpdatePerson(effectivePerson, business, process);
				}
				for (Process p : business.entityManagerContainer().listEqualAndEqual(Process.class, Process.application_FIELDNAME,
						process.getApplication(), Process.edition_FIELDNAME, process.getEdition())) {
					if (!process.getId().equals(p.getId()) && BooleanUtils.isTrue(p.getEditionEnable())) {
						p.setLastUpdateTime(new Date());
						p.setEditionEnable(false);
						this.updateCreatePersonLastUpdatePerson(effectivePerson, business, p);
					}
				}
			}
			emc.commit();
			cacheNotify();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 4191812620073853271L;

	}

	private void updateCreatePersonLastUpdatePerson(EffectivePerson effectivePerson, Business business, Process process)
			throws Exception {
		process.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		String name = business.organization().person().get(process.getCreatorPerson());
		if (StringUtils.isEmpty(name)) {
			process.setCreatorPerson(effectivePerson.getDistinguishedName());
		} else {
			process.setCreatorPerson(name);
		}
	}

}