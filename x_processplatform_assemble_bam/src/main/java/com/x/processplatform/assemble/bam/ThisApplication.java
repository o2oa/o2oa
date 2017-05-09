package com.x.processplatform.assemble.bam;

import com.x.base.core.project.Context;
import com.x.processplatform.assemble.bam.timer.PeriodTimer;
import com.x.processplatform.assemble.bam.timer.StateTimer;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			context().timer(new PeriodTimer(context()), 180, 60 * 30);
			/* state 运行统计需要读取组织库,如果开始运行的时候组织应用还没启动那么会为空 */
			context().timer(new StateTimer(context()), 180, 60 * 60 * 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static State state = new State();
	public static Period period = new Period();

}
