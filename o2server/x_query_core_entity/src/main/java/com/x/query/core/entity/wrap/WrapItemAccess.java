package com.x.query.core.entity.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.query.core.entity.ItemAccess;

public class WrapItemAccess extends ItemAccess {

	private static final long serialVersionUID = -1346746447443966380L;

	public static WrapCopier<ItemAccess, WrapItemAccess> outCopier = WrapCopierFactory.wo(ItemAccess.class, WrapItemAccess.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapItemAccess, ItemAccess> inCopier = WrapCopierFactory.wi(WrapItemAccess.class, ItemAccess.class, null,
			JpaObject.FieldsUnmodifyExcludeId);
}
