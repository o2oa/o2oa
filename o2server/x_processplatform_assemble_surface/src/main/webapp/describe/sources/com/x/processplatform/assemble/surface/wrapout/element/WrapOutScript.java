package com.x.processplatform.assemble.surface.wrapout.element;

import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapOutScript extends GsonPropertyObject {

	private String text;

	private List<String> importedList;

	public List<String> getImportedList() {
		return importedList;
	}

	public void setImportedList(List<String> importedList) {
		this.importedList = importedList;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
