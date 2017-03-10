package com.x.file.assemble.control.jaxrs.file;

import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.wrapout.WrapOutFile;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.ReferenceType;

class ActionListWithReferenceTypeWithReference extends ActionBase {
	ActionResult<List<WrapOutFile>> execute(EffectivePerson effectivePerson, String referenceTypeString,
			String reference) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutFile>> result = new ActionResult<>();
			Business business = new Business(emc);
			ReferenceType referenceType = EnumUtils.getEnum(ReferenceType.class, referenceTypeString);
			if (null == referenceType) {
				throw new InvalidReferenceTypeException(referenceTypeString);
			}
			if (StringUtils.isEmpty(reference)) {
				throw new EmptyReferenceException(reference);
			}
			List<String> ids = business.file().listWithReferenceTypeWithReference(referenceType, reference);
			List<WrapOutFile> wraps = copier.copy(emc.list(File.class, ids));
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}
}
