package o2.collect.assemble.message;

import com.x.base.core.entity.JpaObject;

public enum MessageCategory {
	dialog, notification, operation;
	public static final int length = JpaObject.length_16B;
}
