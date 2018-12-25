package com.x.portal.core.entity.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.portal.core.entity.Widget;

public class WrapWidget extends Widget {

	private static final long serialVersionUID = 6325001527932834623L;

	public static WrapCopier<Widget, WrapWidget> outCopier = WrapCopierFactory.wo(Widget.class, WrapWidget.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapWidget, Widget> inCopier = WrapCopierFactory.wi(WrapWidget.class, Widget.class, null,
			JpaObject.FieldsUnmodifyExcludeId);

}
