package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.End;

@Wrap(End.class)
public class WrapOutEnd extends End {

	private static final long serialVersionUID = -8770201790121656238L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}