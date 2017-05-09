package com.x.cms.assemble.control.jaxrs.templateform;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.core.entity.element.TemplateForm;

import net.sf.ehcache.Ehcache;


abstract class ActionBase extends StandardJaxrsAction {

	protected Ehcache cache = ApplicationCache.instance().getCache( TemplateForm.class);
	
	/** 设计端的id都是由前台提供的这里要忽略拷贝id */
	private static String[] copyInExcludes = new String[] { JpaObject.DISTRIBUTEFACTOR, "updateTime", "createTime",
			"sequence" };

	static BeanCopyTools<TemplateForm, WrapOutTemplateForm> outCopier = BeanCopyToolsBuilder.create(TemplateForm.class,
			WrapOutTemplateForm.class, null, WrapOutTemplateForm.Excludes);

	static BeanCopyTools<TemplateForm, WrapOutTemplateFormSimple> simpleOutCopier = BeanCopyToolsBuilder
			.create(TemplateForm.class, WrapOutTemplateFormSimple.class, null, WrapOutTemplateFormSimple.Excludes);

	static BeanCopyTools<WrapInTemplateForm, TemplateForm> inCopier = BeanCopyToolsBuilder
			.create(WrapInTemplateForm.class, TemplateForm.class, null, copyInExcludes);

}
