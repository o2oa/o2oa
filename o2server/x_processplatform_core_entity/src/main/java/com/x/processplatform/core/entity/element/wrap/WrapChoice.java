package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Choice;

public class WrapChoice extends Choice {

	private static final long serialVersionUID = 1985192289895811979L;

	public static final WrapCopier<Choice, WrapChoice> outCopier = WrapCopierFactory.wo(Choice.class, WrapChoice.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapChoice, Choice> inCopier = WrapCopierFactory.wi(WrapChoice.class, Choice.class,
			null, JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);
}
