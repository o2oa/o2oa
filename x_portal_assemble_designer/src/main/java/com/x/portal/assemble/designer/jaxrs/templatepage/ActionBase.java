package com.x.portal.assemble.designer.jaxrs.templatepage;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapin.WrapInTemplatePage;
import com.x.portal.assemble.designer.wrapout.WrapOutTemplatePage;
import com.x.portal.core.entity.TemplatePage;

abstract class ActionBase extends AbstractJaxrsAction {

	static BeanCopyTools<TemplatePage, WrapOutTemplatePage> outCopier = BeanCopyToolsBuilder.create(TemplatePage.class,
			WrapOutTemplatePage.class, null, WrapOutTemplatePage.Excludes);

	static BeanCopyTools<WrapInTemplatePage, TemplatePage> inCopier = BeanCopyToolsBuilder
			.create(WrapInTemplatePage.class, TemplatePage.class, null, WrapInTemplatePage.Excludes);

//	static BeanCopyTools<WrapInTemplatePage, TemplatePage> updateCopier = BeanCopyToolsBuilder
//			.create(WrapInTemplatePage.class, TemplatePage.class, null, JpaObject.FieldsUnmodifies);

	void checkName(Business business, TemplatePage o) throws Exception {
		if (StringUtils.isEmpty(o.getName())) {
			throw new NameEmptyException();
		}
		String id = business.templatePage().getWithName(o.getName());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(o.getId(), id))) {
			throw new NameDuplicateException(o.getName());
		}
		id = business.templatePage().getWithAlias(o.getName());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(o.getId(), id))) {
			throw new NameDuplicateWithAliasException(o.getName());
		}
	}

	void checkAlias(Business business, TemplatePage o) throws Exception {
		if (StringUtils.isEmpty(o.getAlias())) {
			return;
		}
		String id = business.templatePage().getWithAlias(o.getAlias());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(o.getId(), id))) {
			throw new AliasDuplicateException(o.getAlias());
		}
		id = business.templatePage().getWithName(o.getAlias());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(o.getId(), id))) {
			throw new AliasDuplicateWithNameException(o.getAlias());
		}
	}
}