package com.x.portal.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.portal.core.entity.Source;

@Wrap(Source.class)
public class WrapOutSource extends Source {

	private static final long serialVersionUID = -7592184343034018992L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
