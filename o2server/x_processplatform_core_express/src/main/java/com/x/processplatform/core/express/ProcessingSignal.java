package com.x.processplatform.core.express;

import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;

public class ProcessingSignal extends GsonPropertyObject {

	public ProcessingSignal() {

	}

	public static final String TYPE_SPLIT = "split";

	private String type = "";

	public String getType() {
		return type;
	}

	private Split split = new Split();

	public Split getSplit() {
		if (null == split) {
			this.split = new Split();
		}
		return this.split;
	}

	public static class Split {

		List<String> splitValueList;

		public List<String> getSplitValueList() {
			return splitValueList;
		}

		public void setSplitValueList(List<String> splitValueList) {
			this.splitValueList = splitValueList;
		}
	}

	public static ProcessingSignal splitSignal(List<String> splitValues) {
		ProcessingSignal p = new ProcessingSignal();
		p.type = TYPE_SPLIT;
		Split split = new Split();
		split.setSplitValueList(splitValues);
		p.split = split;
		return p;
	}

}
