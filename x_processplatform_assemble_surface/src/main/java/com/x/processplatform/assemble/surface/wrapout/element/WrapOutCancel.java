package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Cancel;

@Wrap(Cancel.class)
public class WrapOutCancel extends Cancel {

	private static final long serialVersionUID = 813182162888838666L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
}
