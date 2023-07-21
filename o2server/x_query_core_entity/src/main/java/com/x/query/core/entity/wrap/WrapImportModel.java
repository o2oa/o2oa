package com.x.query.core.entity.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.query.core.entity.ImportModel;

public class WrapImportModel extends ImportModel {

	private static final long serialVersionUID = 6575393842412659085L;

	public static WrapCopier<ImportModel, WrapImportModel> outCopier = WrapCopierFactory.wo(ImportModel.class, WrapImportModel.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapImportModel, ImportModel> inCopier = WrapCopierFactory.wi(WrapImportModel.class, ImportModel.class, null,
			JpaObject.FieldsUnmodifyExcludeId);
}
