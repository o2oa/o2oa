package com.x.cms.assemble.control.jaxrs.script;

import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.element.Script;

@Wrap(Script.class)
public class WrapInScriptNested extends GsonPropertyObject {

	private List<String> importedList;

	public List<String> getImportedList() {
		return importedList;
	}

	public void setImportedList(List<String> importedList) {
		this.importedList = importedList;
	}

}
