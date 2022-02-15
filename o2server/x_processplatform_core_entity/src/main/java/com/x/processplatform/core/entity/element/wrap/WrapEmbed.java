package com.x.processplatform.core.entity.element.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.processplatform.core.entity.element.Embed;

public class WrapEmbed extends Embed {

	private static final long serialVersionUID = 8427413538568458981L;

	public static final WrapCopier<Embed, WrapEmbed> outCopier = WrapCopierFactory.wo(Embed.class, WrapEmbed.class,
			null, JpaObject.FieldsInvisible);

	public static final WrapCopier<WrapEmbed, Embed> inCopier = WrapCopierFactory.wi(WrapEmbed.class, Embed.class, null,
			JpaObject.FieldsUnmodifyIncludePorpertiesExcludeId, false);
}
