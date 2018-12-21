package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Embed;

public class WrapOutEmbed extends Embed {

	private static final long serialVersionUID = 298602065524433660L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);


}
