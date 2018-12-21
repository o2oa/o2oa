package com.x.processplatform.assemble.surface.wrapin.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.content.SerialNumber;

public class WrapInSerialNumber extends SerialNumber {

	private static final long serialVersionUID = -3037178659768770946L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodify);

	static {
		Excludes.add("name");
	}

}