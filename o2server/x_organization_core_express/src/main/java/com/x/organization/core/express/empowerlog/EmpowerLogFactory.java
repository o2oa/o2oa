package com.x.organization.core.express.empowerlog;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.organization.EmpowerLog;

public class EmpowerLogFactory {

	public EmpowerLogFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 创建委托记录 */
	public boolean log(EmpowerLog empowerLog) throws Exception {
		return ActionLog.execute(context, empowerLog);
	}

}
