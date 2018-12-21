package com.x.query.core.entity.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.query.core.entity.Reveal;

public class WrapReveal extends Reveal  {

	private static final long serialVersionUID = 6575393842412659085L;

	public static WrapCopier<Reveal , WrapReveal> outCopier = WrapCopierFactory.wo(Reveal .class, WrapReveal.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapReveal, Reveal > inCopier = WrapCopierFactory.wi(WrapReveal.class, Reveal .class, null,
			JpaObject.FieldsUnmodifyExcludeId);
}