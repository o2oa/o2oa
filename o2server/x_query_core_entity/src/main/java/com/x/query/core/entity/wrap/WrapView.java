package com.x.query.core.entity.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.query.core.entity.View;

public class WrapView extends View {

	private static final long serialVersionUID = 6575393842412659085L;

	public static WrapCopier<View, WrapView> outCopier = WrapCopierFactory.wo(View.class, WrapView.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapView, View> inCopier = WrapCopierFactory.wi(WrapView.class, View.class, null,
			JpaObject.FieldsUnmodifyExcludeId);
}