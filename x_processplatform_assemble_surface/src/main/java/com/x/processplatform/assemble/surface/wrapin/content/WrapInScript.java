package com.x.processplatform.assemble.surface.wrapin.content;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Script;
@Wrap(Script.class)
public class WrapInScript extends GsonPropertyObject {

	private List<String> importedList;

	public List<String> getImportedList() {
		return importedList;
	}

	public void setImportedList(List<String> importedList) {
		this.importedList = importedList;
	}

}
