package com.x.file.assemble.control.jaxrs.folder;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.file.core.entity.Folder;

@Wrap(Folder.class)
public class WrapInFolder extends Folder {

	private static final long serialVersionUID = 3965042303681243568L;

	public static List<String> Includes = new ArrayList<>();

	static {
		Includes.add("name");
		Includes.add("superior");
	}

}