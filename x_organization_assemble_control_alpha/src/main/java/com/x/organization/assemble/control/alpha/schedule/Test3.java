package com.x.organization.assemble.control.alpha.schedule;

import com.x.base.core.project.Context;
import com.x.base.core.project.clock.ClockScheduleTask;
import com.x.base.core.utils.DateTools;

public class Test3 extends ClockScheduleTask {

	public Test3(Context context) {
		super(context);
	}

	public void execute() throws Exception {
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + this.getClass());
		System.out.println(DateTools.now());
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + this.getClass());

	}

}
