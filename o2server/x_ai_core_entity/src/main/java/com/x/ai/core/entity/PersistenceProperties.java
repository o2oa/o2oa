package com.x.ai.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class File {
		public static final String table = "AI_FILE";
	}

	public static class Clue {
		public static final String table = "AI_CLUE";
	}

	public static class Completion {
		public static final String table = "AI_COMPLETION";
	}

	public static class AiModel {
		public static final String table = "AI_MODEL";
	}
}
