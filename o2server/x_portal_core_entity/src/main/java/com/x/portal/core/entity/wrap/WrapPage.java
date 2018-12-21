package com.x.portal.core.entity.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.portal.core.entity.Page;

public class WrapPage extends Page {

	private static final long serialVersionUID = 811616877741009141L;

	public static WrapCopier<Page, WrapPage> outCopier = WrapCopierFactory.wo(Page.class, WrapPage.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapPage, Page> inCopier = WrapCopierFactory.wi(WrapPage.class, Page.class, null,
			JpaObject.FieldsUnmodifyExcludeId);

}
