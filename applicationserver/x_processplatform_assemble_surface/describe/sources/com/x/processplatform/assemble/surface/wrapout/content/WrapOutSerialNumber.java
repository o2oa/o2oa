package com.x.processplatform.assemble.surface.wrapout.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.content.SerialNumber;

public class WrapOutSerialNumber extends SerialNumber {

	private static final long serialVersionUID = 6915255077548190559L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private String processName;

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

}