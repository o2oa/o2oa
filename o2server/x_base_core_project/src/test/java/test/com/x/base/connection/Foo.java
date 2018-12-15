package test.com.x.base.connection;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.tools.ListTools;

public class Foo {

	private List<String> list = new ArrayList<>(ListTools.toList("a", "b", "c", "d"));

	private String str = "s";

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

}
