package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JpaObject;

public enum ActivityType {
	agent, begin, cancel, choice, delay, end, embed, invoke, manual, merge, message, parallel, service, split;

	public static final int length = JpaObject.length_16B;

	public static Class<? extends Activity> getClassOfActivityType(ActivityType activityType) {
		switch (activityType) {
		case agent:
			return Agent.class;
		case begin:
			return Begin.class;
		case cancel:
			return Cancel.class;
		case choice:
			return Choice.class;
		case delay:
			return Delay.class;
		case end:
			return End.class;
		case embed:
			return Embed.class;
		case invoke:
			return Invoke.class;
		case manual:
			return Manual.class;
		case merge:
			return Merge.class;
		case parallel:
			return Parallel.class;
		case message:
			return Message.class;
		case service:
			return Service.class;
		case split:
			return Split.class;
		default:
			return null;
		}
	}
}
