package com.x.organization.core.express.trustlog;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.organization.TrustLog;

public class TrustLogFactory {

	public TrustLogFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 创建委托记录 */
	public boolean log(TrustLog trustLog) throws Exception {
		return ActionLog.execute(context, trustLog);
	}

}
