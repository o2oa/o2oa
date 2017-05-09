package com.x.organization.assemble.control.alpha;

import com.x.base.core.project.Context;
import com.x.organization.assemble.control.alpha.schedule.Test1;
import com.x.organization.assemble.control.alpha.schedule.Test2;
import com.x.organization.assemble.control.alpha.schedule.Test3;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static void init() {
		try {
			context().schedule(Test1.class, "0 */1 * * * ?");
			context().schedule(Test2.class, "0 */2 * * * ?");
			context().schedule(Test3.class, "0 */3 * * * ?");
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

}
