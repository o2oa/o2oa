package com.x.query.core.entity.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.query.core.entity.schema.Table;

public class WrapTable extends Table {

	private static final long serialVersionUID = 7447932372252151469L;

	public static WrapCopier<Table, WrapTable> outCopier = WrapCopierFactory.wo(Table.class, WrapTable.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapTable, Table> inCopier = WrapCopierFactory.wi(WrapTable.class, Table.class, null,
			JpaObject.FieldsUnmodifyExcludeId);
}