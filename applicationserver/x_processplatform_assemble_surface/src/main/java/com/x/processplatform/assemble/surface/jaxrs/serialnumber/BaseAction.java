package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapin.content.WrapInSerialNumber;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutSerialNumber;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.element.Process;

abstract class BaseAction extends StandardJaxrsAction {

	static WrapCopier<SerialNumber, WrapOutSerialNumber> outCopier = WrapCopierFactory.wo(SerialNumber.class,
			WrapOutSerialNumber.class, null, WrapOutSerialNumber.Excludes);

	static WrapCopier<WrapInSerialNumber, SerialNumber> inCopier = WrapCopierFactory.wi(WrapInSerialNumber.class,
			SerialNumber.class, null, JpaObject.FieldsUnmodify);

	void fillProcessName(Business business, WrapOutSerialNumber wrap) throws Exception {
		Process process = business.process().pick(wrap.getProcess());
		if (null != process) {
			wrap.setProcessName(process.getName());
		}
	}

	void fillProcessName(Business business, List<WrapOutSerialNumber> wraps) throws Exception {
		for (WrapOutSerialNumber o : wraps) {
			this.fillProcessName(business, o);
		}
	}

}
