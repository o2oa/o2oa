package com.x.portal.assemble.surface.wrapin;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.portal.core.entity.Script;
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
