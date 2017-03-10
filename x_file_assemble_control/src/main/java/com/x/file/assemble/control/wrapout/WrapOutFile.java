package com.x.file.assemble.control.wrapout;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.file.core.entity.open.File;

@Wrap(File.class)
public class WrapOutFile extends File {

	private static final long serialVersionUID = 100904116457932549L;

	public static List<String> Excludes = JpaObject.FieldsInvisible;

	private Long rank;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}
}
