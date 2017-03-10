package com.x.file.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.file.core.entity.personal.Attachment;

@Wrap(Attachment.class)
public class WrapOutAttachment extends Attachment {

	private static final long serialVersionUID = -531053101150157872L;
	public static List<String> Excludes = new ArrayList<>();

	static {
		Excludes.add(JpaObject.DISTRIBUTEFACTOR);
	}

	private String contentType;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
