package com.x.file.assemble.control.jaxrs.attachment;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.file.core.entity.Attachment;

@Wrap(Attachment.class)
public class WrapInAttachment extends Attachment {

	private static final long serialVersionUID = -5317431633607552753L;
	public static List<String> Includes = new ArrayList<>();

	static {
		Includes.add("shareList");
		Includes.add("editorList");
		Includes.add("folder");
		Includes.add("name");
	}

}