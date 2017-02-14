package com.x.common.core.application.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.XGsonBuilder;

public class x_meeting_assemble_junction extends Assemble {

	public static List<String> containerEntities = new ArrayList<>();

	protected void custom(File dir, String repositoryPath) throws Exception {
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_meeting_assemble_junction o = new x_meeting_assemble_junction();
			o.pack(arg.getDistPath(), arg.getRepositoryPath(), arg.getCenterHost(), arg.getCenterPort(),
					arg.getCenterCipher(), arg.getConfigApplicationServer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
