package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.element.Application;

public class WrapOutApplication extends Application {

	private static final long serialVersionUID = -4862564047240738097L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private List<WrapOutProcess> processList;

	/* 当前用户是否可以编辑此应用的列表界面 */
	private Boolean allowControl;

	public Boolean getAllowControl() {
		return allowControl;
	}

	public void setAllowControl(Boolean allowControl) {
		this.allowControl = allowControl;
	}

	public List<WrapOutProcess> getProcessList() {
		return processList;
	}

	public void setProcessList(List<WrapOutProcess> processList) {
		this.processList = processList;
	}

}
