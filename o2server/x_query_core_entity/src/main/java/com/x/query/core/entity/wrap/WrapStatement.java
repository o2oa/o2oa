package com.x.query.core.entity.wrap;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.query.core.entity.schema.Statement;

public class WrapStatement extends Statement {

	private static final long serialVersionUID = 3933371970432187836L;

	public static WrapCopier<Statement, WrapStatement> outCopier = WrapCopierFactory.wo(Statement.class, WrapStatement.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapStatement, Statement> inCopier = WrapCopierFactory.wi(WrapStatement.class, Statement.class, null,
			JpaObject.FieldsUnmodifyExcludeId);
}