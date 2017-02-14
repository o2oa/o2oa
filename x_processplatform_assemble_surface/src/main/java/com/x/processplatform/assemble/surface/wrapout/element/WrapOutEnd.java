package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.End;
@Wrap(End.class)
public class WrapOutEnd extends End {
	private static final long serialVersionUID = 7675857316009459984L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
}