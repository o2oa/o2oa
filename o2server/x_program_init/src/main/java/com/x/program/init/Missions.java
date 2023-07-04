package com.x.program.init;

import java.util.Objects;
import java.util.stream.Stream;

public class Missions {

	private Missions() {
		// nothing
	}

	public static void execute() {
		Stream.<Mission>of(ThisApplication.getMissionH2Upgrade(), ThisApplication.getMissionRestore(),
				ThisApplication.getMissionSetSecret()).filter(Objects::nonNull).forEach(Mission::execute);
	}

	public interface Mission {

		public void execute();

	}

}
