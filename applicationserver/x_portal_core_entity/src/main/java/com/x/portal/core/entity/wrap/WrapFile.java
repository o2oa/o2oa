package com.x.portal.core.entity.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.portal.core.entity.File;

public class WrapFile extends File {

	private static final long serialVersionUID = 7652835125230680770L;

	public static WrapCopier<File, WrapFile> outCopier = WrapCopierFactory.wo(File.class, WrapFile.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapFile, File> inCopier = WrapCopierFactory.wi(WrapFile.class, File.class, null,
			JpaObject.FieldsUnmodifyExcludeId);

}
