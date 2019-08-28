package com.x.processplatform.assemble.surface.copier;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.core.entity.element.Manual;

public class TestClient {
	@Test
	public void test1() throws Exception {
		String aaa = "aaa";
		System.out.println(FilenameUtils.getName(aaa));
		System.out.println(FilenameUtils.getBaseName(aaa));
		System.out.println(FilenameUtils.getExtension(aaa));

	}

	@Test
	public void test3() throws Exception {
		Manual manual = new Manual();
	//	manual.setForm("");
		String formId = PropertyTools.getOrElse(manual, Manual.form_FIELDNAME, String.class, "a");
		System.out.println(formId);
	}

}
