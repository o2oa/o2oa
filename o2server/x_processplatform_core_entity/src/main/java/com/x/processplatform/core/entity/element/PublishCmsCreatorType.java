package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JpaObject;

public enum PublishCmsCreatorType {
	creator, identity, lastIdentity, script;
	public static final int length = JpaObject.length_16B;
}
