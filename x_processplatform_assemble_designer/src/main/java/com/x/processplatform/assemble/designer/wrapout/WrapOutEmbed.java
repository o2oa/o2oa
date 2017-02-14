package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Embed;

@Wrap(Embed.class)
public class WrapOutEmbed extends Embed {

	private static final long serialVersionUID = 8427413538568458981L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
}
