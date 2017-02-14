package com.x.processplatform.assemble.surface.wrapout.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.core.entity.content.TaskCompleted;

@Wrap(TaskCompleted.class)
public class WrapOutTaskCompleted extends TaskCompleted {

	private static final long serialVersionUID = -7253999118308715077L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
	private Long rank;

	private Control control;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public Control getControl() {
		return control;
	}

	public void setControl(Control control) {
		this.control = control;
	}

}
