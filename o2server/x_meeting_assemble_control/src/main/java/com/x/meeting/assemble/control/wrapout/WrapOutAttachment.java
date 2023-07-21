package com.x.meeting.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.meeting.core.entity.Attachment;

public class WrapOutAttachment extends Attachment {

	private static final long serialVersionUID = -9014091321503987347L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private Long rank;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

}
