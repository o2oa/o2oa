package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInApplication;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplication;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplicationSummary;
import com.x.processplatform.assemble.designer.wrapout.WrapOutForm;
import com.x.processplatform.assemble.designer.wrapout.WrapOutProcess;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Process;

abstract class ActionBase extends StandardJaxrsAction {
	static BeanCopyTools<Application, WrapOutApplication> outCopier = BeanCopyToolsBuilder.create(Application.class,
			WrapOutApplication.class, null, WrapOutApplication.Excludes);

	static BeanCopyTools<Application, WrapOutApplicationSummary> summaryOutCopier = BeanCopyToolsBuilder
			.create(Application.class, WrapOutApplicationSummary.class, null, WrapOutApplicationSummary.Excludes);

	static BeanCopyTools<WrapInApplication, Application> inCopier = BeanCopyToolsBuilder.create(WrapInApplication.class,
			Application.class, null, WrapInApplication.Excludes);

	/* 用于拼装applicationSummary下的Process信息 */
	List<WrapOutProcess> wrapOutProcessWithApplication(Business business, String application) throws Exception {
		List<WrapOutProcess> list = new ArrayList<>();
		List<String> ids = business.process().listWithApplication(application);
		for (Process o : business.entityManagerContainer().fetchAttribute(ids, Process.class, "name", "updateTime")) {
			WrapOutProcess wrap = new WrapOutProcess();
			o.copyTo(wrap);
			list.add(wrap);
		}
		this.sortWrapOutProcess(list);
		return list;
	}

	/* 用于拼装applicationSummary下的Form信息 */
	List<WrapOutForm> wrapOutFormWithApplication(Business business, String application) throws Exception {
		List<WrapOutForm> list = new ArrayList<>();
		List<String> ids = business.form().listWithApplication(application);
		for (Form o : business.entityManagerContainer().fetchAttribute(ids, Form.class, "name", "updateTime")) {
			WrapOutForm wrap = new WrapOutForm();
			o.copyTo(wrap);
			list.add(wrap);
		}
		this.sortWrapOutForm(list);
		return list;
	}

	void sortWrapOutApplicationSummary(List<WrapOutApplicationSummary> list) {
		Collections.sort(list, new Comparator<WrapOutApplicationSummary>() {
			public int compare(WrapOutApplicationSummary o1, WrapOutApplicationSummary o2) {
				/* ASC */
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}

	void sortWrapOutProcess(List<WrapOutProcess> list) {
		Collections.sort(list, new Comparator<WrapOutProcess>() {
			public int compare(WrapOutProcess o1, WrapOutProcess o2) {
				/* ASC */
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}

	void sortWrapOutForm(List<WrapOutForm> list) {
		Collections.sort(list, new Comparator<WrapOutForm>() {
			public int compare(WrapOutForm o1, WrapOutForm o2) {
				/* ASC */
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}
}
