package com.x.base.core.project.jaxrs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapIdList extends GsonPropertyObject {

	public WrapIdList() {
	}

	public WrapIdList(Collection<String> collection) {
		this.idList = new ArrayList<String>(collection);
	}

	@FieldDescribe("标识符.")
	private List<String> idList = new ArrayList<>();

	public List<String> add(String value, Boolean unique) {
		if (this.idList == null) {
			this.idList = new ArrayList<>();
		}
		if (unique) {
			if (!this.idList.contains(value)) {
				this.idList.add(value);
			}
		} else {
			this.idList.add(value);
		}
		return this.idList;
	}

	public List<String> getIdList() {
		return idList;
	}

	public void setIdList(List<String> idList) {
		this.idList = idList;
	}

}