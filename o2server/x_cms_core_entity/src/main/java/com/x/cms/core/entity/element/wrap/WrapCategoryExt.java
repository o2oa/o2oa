package com.x.cms.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.cms.core.entity.CategoryExt;

public class WrapCategoryExt extends CategoryExt {

	private static final long serialVersionUID = -7244816539971035609L;

	public static WrapCopier<CategoryExt, WrapCategoryExt> outCopier = WrapCopierFactory.wo(CategoryExt.class, WrapCategoryExt.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapCategoryExt, CategoryExt> inCopier = WrapCopierFactory.wi(WrapCategoryExt.class, CategoryExt.class, null,
			JpaObject.FieldsUnmodifyExcludeId);

}
