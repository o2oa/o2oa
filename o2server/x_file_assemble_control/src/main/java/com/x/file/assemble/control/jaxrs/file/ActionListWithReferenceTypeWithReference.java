package com.x.file.assemble.control.jaxrs.file;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.ReferenceType;

class ActionListWithReferenceTypeWithReference extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String referenceTypeString, String reference)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			ReferenceType referenceType = EnumUtils.getEnum(ReferenceType.class, referenceTypeString);
			if (null == referenceType) {
				throw new ExceptionInvalidReferenceType(referenceTypeString);
			}
			if (StringUtils.isEmpty(reference)) {
				throw new ExceptionEmptyReference(reference);
			}
			List<String> ids = business.file().listWithReferenceTypeWithReference(referenceType, reference);
			List<Wo> wos = Wo.copier.copy(emc.list(File.class, ids));
			wos = wos.stream().sorted(Comparator.comparing(File::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends File {

		private static final long serialVersionUID = -125007357898871894L;

		@FieldDescribe("排序号")
		private Long rank;

		static WrapCopier<File, Wo> copier = WrapCopierFactory.wo(File.class, Wo.class,
				JpaObject.singularAttributeField(File.class, true, true), null);

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}
