package com.x.program.init;

import java.util.Objects;
import java.util.stream.Stream;

public class Missions {

	private Missions() {
		// nothing
	}

	public static void execute() {
		Stream.<Mission>of(ThisApplication.getMissionUpgradeH2(), ThisApplication.getMissionRestore(),
				ThisApplication.getMissionSetSecret()).filter(Objects::nonNull).forEach(Mission::execute);
	}

}