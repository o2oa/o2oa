package com.x.processplatform.service.processing.jaxrs.read;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.service.processing.Business;

public class ActionReset extends ActionBase {

	protected WrapOutId execute(Business business, String id, WrapInRead wrapIn) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Read read = emc.find(id, Read.class, ExceptionWhen.not_found);
		emc.beginTransaction(Read.class);
		emc.beginTransaction(ReadCompleted.class);
		ReadCompleted readCompleted = this.createReadCompleted(business, read, ProcessingType.reset);
		emc.persist(readCompleted, CheckPersistType.all);
		for (String str : wrapIn.getIdentityList()) {
			Read o = new Read();
			read.copyTo(o, JpaObject.ID, JpaObject.DISTRIBUTEFACTOR, "opinion");
			o.setIdentity(business.organization().identity().getWithName(str).getName());
			o.setPerson(business.organization().person().getWithIdentity(o.getIdentity()).getName());
			o.setDepartment(business.organization().department().getWithIdentity(o.getIdentity()).getName());
			o.setCompany(business.organization().company().getWithIdentity(o.getIdentity()).getName());
			emc.persist(o, CheckPersistType.all);
		}
		List<String> ids = new ArrayList<>();
		if (read.getCompleted()) {
			ids = business.read().listWithPersonWithWorkCompleted(read.getPerson(), read.getWorkCompleted());
		} else {
			ids = business.read().listWithPersonWithWork(read.getPerson(), read.getWork());
		}
		emc.delete(Read.class, ids);
		emc.commit();
		return new WrapOutId(id);
	}

}
