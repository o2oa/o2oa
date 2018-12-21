package com.x.processplatform.assemble.surface.wrapout.content;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.processplatform.core.entity.content.Attachment;

public class WrapOutAttachment extends Attachment {

	private static final long serialVersionUID = 1954637399762611493L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private Long rank;

	private Long referencedCount;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public Long getReferencedCount() {
		return referencedCount;
	}

	public void setReferencedCount(Long referencedCount) {
		this.referencedCount = referencedCount;
	}

}
