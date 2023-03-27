package com.x.base.core.entity.dataitem;

import com.x.base.core.entity.JpaObject;

public enum ItemCategory {
	pp, cms, bbs, pp_dict, portal_dict, service_dict;
	public static final int length = JpaObject.length_16B;
}
