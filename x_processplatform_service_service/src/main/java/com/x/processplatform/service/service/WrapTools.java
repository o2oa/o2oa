package com.x.processplatform.service.service;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.service.service.wrapout.WrapOutWork;

public class WrapTools {

	public static BeanCopyTools<Work, WrapOutWork> workOutCopier = BeanCopyToolsBuilder.create(Work.class,
			WrapOutWork.class, null, WrapOutWork.Excludes);

	// public static BeanCopyTools<WrapInBuilding, Building> buildingInCopier =
	// BeanCopyToolsBuilder
	// .create(WrapInBuilding.class, Building.class, null,
	// WrapInBuilding.Excludes);

}