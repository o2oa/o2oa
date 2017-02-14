package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Condition;

@Wrap(Condition.class)
public class WrapOutCondition extends Condition {

	private static final long serialVersionUID = -5592538114504882979L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}