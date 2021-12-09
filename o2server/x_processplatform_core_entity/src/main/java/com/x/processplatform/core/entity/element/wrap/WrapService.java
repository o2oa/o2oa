package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Service;

public class WrapService extends Service {

	private static final long serialVersionUID = 6862383643551191712L;

	public static final WrapCopier<Service, WrapService> outCopier = WrapCopierFactory.wo(Service.class,
			WrapService.class, null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapService, Service> inCopier = WrapCopierFactory.wi(WrapService.class,
			Service.class, null, JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);
}
