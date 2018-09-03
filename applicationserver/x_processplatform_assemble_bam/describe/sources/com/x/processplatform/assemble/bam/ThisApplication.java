package com.x.processplatform.assemble.bam;

import com.x.base.core.project.Context;
import com.x.processplatform.assemble.bam.jaxrs.period.Period;
import com.x.processplatform.assemble.bam.jaxrs.state.State;
import com.x.processplatform.assemble.bam.schedule.PeriodTimer;
import com.x.processplatform.assemble.bam.schedule.StateTimer;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			context().scheduleLocal(PeriodTimer.class, 180, 60 * 30);
			/* state 运行统计需要读取组织库,如果开始运行的时候组织应用还没启动那么会为空 */
			context().scheduleLocal(StateTimer.class, 180, 60 * 60 * 1);
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
