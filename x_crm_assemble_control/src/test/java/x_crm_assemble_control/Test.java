package x_crm_assemble_control;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Test {
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();

		list.add("java");
		list.add("world");
		list.add("android");
		ListIterator lit = list.listIterator();
		while (lit.hasNext()) {
			String s = (String) lit.next();
			if ("android".equals(s)) {
				lit.add("javaee");
			}
		}
		System.out.println(list);
	}
}