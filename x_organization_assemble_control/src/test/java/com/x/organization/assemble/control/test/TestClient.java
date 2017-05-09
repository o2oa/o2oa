package com.x.organization.assemble.control.test;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestClient {
	@Test
	public void test() {
		BufferedReader br = null;
		try {
			// String name;
			// br = new BufferedReader(new FileReader("names.txt"));
			// int i = 0;
			// while ((name = br.readLine()) != null) {
			// name = name + i;
			// String body = "{name:\"" + name + "\",password:\"1\",employee:\""
			// + (name + "测试" + i) + "\"}";
			// Request.Post("http://xa01.zoneland.net:9080/x_organization_assemble_control/jaxrs/person").bodyString(body,
			// ContentType.APPLICATION_JSON).execute().returnContent()
			// .asString();
			// System.out.println(name);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Test
	public void test1() throws Exception {
		String str = "(person.getEmplyee())";
		Pattern pattern = Pattern.compile(com.x.base.core.project.server.Person.RegularExpression_Script);
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			String eval = matcher.group(1);
			System.out.println(eval);
		}

	}
}
