package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Publish;

public class WrapPublish extends Publish {

	private static final long serialVersionUID = 6181704815573246882L;

	public static final WrapCopier<Publish, WrapPublish> outCopier = WrapCopierFactory.wo(Publish.class, WrapPublish.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapPublish, Publish> inCopier = WrapCopierFactory.wi(WrapPublish.class, Publish.class, null,
			JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);

}
