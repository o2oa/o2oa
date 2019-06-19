package com.x.program.center.jaxrs.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.core.entity.validation.Meta;

class ActionMeta extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			emc.beginTransaction(Meta.class);
			for (Meta o : emc.listAll(Meta.class)) {
				emc.remove(o);
			}
			emc.commit();
			emc.beginTransaction(Meta.class);
			Meta meta = new Meta();
			meta.setStringValue(StringTools.uniqueToken());
			meta.setBooleanValue(true);
			meta.setDateTimeValue(new Date());
			meta.setDateValue(new Date());
			meta.setTimeValue(new Date());
			meta.setIntegerValue(123456789);
			meta.setLongValue(123456789L);
			meta.setDoubleValue(1233456789.987654321d);
			meta.setListValueList(new ArrayList<>());
			meta.getListValueList().add("AAA");
			meta.getListValueList().add("BBB");
			meta.getListValueList().add("CCC");
			meta.setMapValueMap(new LinkedHashMap<String, String>());
			meta.getMapValueMap().put("key1", "value1");
			meta.getMapValueMap().put("key2", "true");
			meta.getMapValueMap().put("key3", "12.34");
			StringBuffer largeString = new StringBuffer();
			for (int i = 0; i < 100; i++) {
				largeString.append(i + ":测试测试测试,");
			}
			meta.setStringLobValue(largeString.toString());
			emc.persist(meta, CheckPersistType.all);
			emc.commit();
			emc.flush();
			meta = emc.find(meta.getId(), Meta.class);
			result.setData(Wo.copier.copy(meta));
			return result;
		}
	}

	public static class Wo extends Meta {

		private static final long serialVersionUID = -4468460971064420534L;

		static WrapCopier<Meta, Wo> copier = WrapCopierFactory.wo(Meta.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
