package com.x.organization.core.entity;

import java.util.regex.Pattern;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static final String distinguishNameSplit = "@";

	public static class Custom {
		public static final String table = "ORG_CUSTOM";
	}

	public static class Definition {
		public static final String table = "ORG_DEFINTION";
	}

	public static class Group {
		public static final String table = "ORG_GROUP";
		public static final String distinguishNameCharacter = "G";
		public static final Pattern distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@G$");

	}

	public static class Identity {
		public static final String table = "ORG_IDENTITY";
		public static final String distinguishNameCharacter = "I";
		public static final Pattern distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@I$");
	}

	public static class Person {
		public static final String table = "ORG_PERSON";
		public static final String distinguishNameCharacter = "P";
		public static final Pattern distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@P$");
	}

	public static class PersonCard {
		public static final String table = "ORG_PERSONCARD";
		public static final String distinguishNameCharacter = "P";
		public static final Pattern distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@P$");
	}

	public static class PermissionSetting {
		public static final String table = "ORG_PERMISSIONSETTING";
		public static final String distinguishNameCharacter = "P";
		public static final Pattern distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@P$");
	}

	public static class PersonAttribute {
		public static final String table = "ORG_PERSONATTRIBUTE";
		public static final String distinguishNameCharacter = "PA";
		public static final Pattern distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@PA$");
	}

	public static class Role {
		public static final String table = "ORG_ROLE";
		public static final String distinguishNameCharacter = "R";
		public static final Pattern distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@R$");

	}

	public static class Bind {
		public static final String table = "ORG_BIND";
	}

	public static class Unit {
		public static final String table = "ORG_UNIT";
		public static final String distinguishNameCharacter = "U";
		public static final Pattern distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@U$");
		public static final String levelNameSplit = "/";
	}

	public static class UnitAttribute {
		public static final String table = "ORG_UNITATTRIBUTE";
		public static final String distinguishNameCharacter = "UA";
		public static final Pattern distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@UA$");
	}

	public static class UnitDuty {
		public static final String table = "ORG_UNITDUTY";
		public static final String distinguishNameCharacter = "UD";
		public static final Pattern distinguishedName_pattern = Pattern.compile("^(\\S+)\\@(\\S+)\\@UD$");
	}

	public static class OauthCode {
		public static final String table = "ORG_OAUTHCODE";
	}

	public static class Accredit {

		public static class Empower {
			public static final String table = "ORG_A_EMPOWER";
		}

		public static class EmpowerLog {
			public static final String table = "ORG_A_EMPOWERLOG";
		}
	}

	public static class PersonExtend {
		public static final String TABLE = "ORG_PERSONEXTEND";
	}

	public static class Log {

		private Log() {
			// nothing
		}

		public static class TokenThreshold {

			private TokenThreshold() {
				// nothing
			}

			public static final String table = "ORG_L_TOKENTHRESHOLD";
		}

	}

}