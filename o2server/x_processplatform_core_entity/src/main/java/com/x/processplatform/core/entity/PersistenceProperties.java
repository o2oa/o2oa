package com.x.processplatform.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Content {

		public static class TaskCompleted {
			public static final String table = "PP_C_TASKCOMPLETED";
			// public static final String table = "pp_c_taskCompleted";
		}

		public static class Hint {
			public static final String table = "PP_C_HINT";
			// public static final String table = "pp_c_hint";
		}

		public static class Task {
			public static final String table = "PP_C_TASK";
			// public static final String table = "pp_c_task";
		}

		public static class Read {
			public static final String table = "PP_C_READ";
		}

		public static class Review {
			public static final String table = "PP_C_REVIEW";
		}

		public static class ReadCompleted {
			public static final String table = "PP_C_READCOMPLETED";
		}

		public static class WorkCompleted {
			public static final String table = "PP_C_WORKCOMPLETED";
		}

		public static class WorkLog {
			public static final String table = "PP_C_WORKLOG";
		}

		public static class Work {
			public static final String table = "PP_C_WORK";
		}

		public static class Attachment {
			public static final String table = "PP_C_ATTACHMENT";
		}

		public static class DataItem {
			public static final String table = "PP_C_DATAITEM";
		}

		public static class DataLobItem {
			public static final String table = "PP_C_DATALOBITEM";
		}

		public static class SerialNumber {
			public static final String table = "PP_E_SERIALNUMBER";
		}
	}

	public static class Element {

		public static class Application {
			public static final String table = "PP_E_APPLICATION";
		}

		public static class Agent {
			public static final String table = "PP_E_AGENT";
		}

		public static class Begin {
			public static final String table = "PP_E_BEGIN";
		}

		public static class Cancel {
			public static final String table = "PP_E_CANCEL";
		}

		public static class Choice {
			public static final String table = "PP_E_CHOICE";
		}

		public static class Delay {
			public static final String table = "PP_E_DELAY";
		}

		public static class Embed {
			public static final String table = "PP_E_EMBED";
		}

		public static class End {
			public static final String table = "PP_E_END";
		}

		public static class Parallel {
			public static final String table = "PP_E_PARALLEL";
		}

		public static class Invoke {
			public static final String table = "PP_E_INVOKE";
		}

		public static class Manual {
			public static final String table = "PP_E_MANUAL";
		}

		public static class Merge {
			public static final String table = "PP_E_MERGE";
		}

		public static class Message {
			public static final String table = "PP_E_MESSAGE";
		}

		public static class Route {
			public static final String table = "PP_E_ROUTE";
		}

		public static class Script {
			public static final String table = "PP_E_SCRIPT";
		}

		public static class ScriptVersion {
			public static final String table = "PP_E_SCRIPTVERSION";
		}

		public static class Service {
			public static final String table = "PP_E_SERVICE";
		}

		public static class Split {
			public static final String table = "PP_E_SPLIT";
		}

		public static class Form {
			public static final String table = "PP_E_FORM";
		}

		public static class FormField {
			public static final String table = "PP_E_FORMFIELD";
		}

		public static class FormVersion {
			public static final String table = "PP_E_FORMVERSION";
		}

		public static class File {
			public static final String table = "PP_E_FILE";
		}

		public static class TemplateForm {
			public static final String table = "PP_E_TEMPLATEFORM";
		}

		public static class Process {
			public static final String table = "PP_E_PROCESS";
		}

		public static class ProcessVersion {
			public static final String table = "PP_E_PROCESSVERSION";
		}

		public static class ApplicationDict {
			public static final String table = "PP_E_APPLICATIONDICT";
		}

		public static class ApplicationDictItem {
			public static final String table = "PP_E_APPLICATIONDICTITEM";
		}

		public static class ApplicationDictLobItem {
			public static final String table = "PP_E_APPLICATIONDICTLOBITEM";
		}

		public static class QueryView {
			public static final String table = "PP_E_QUERYVIEW";
		}

		public static class QueryStat {
			public static final String table = "PP_E_QUERYSTAT";
		}

		public static class QueryStatTimed {
			public static final String table = "PP_E_QUERYSTATTIMED";
		}

		public static class Projection {
			public static final String table = "PP_E_PROJECTION";
		}

		public static class Mapping {
			public static final String table = "PP_E_MAPPING";
		}
	}

	public static class Log {
		public static class ProcessingError {
			public static final String table = "PP_L_PROCESSINGERROR";
		}
	}

	public static class Temporary {
		public static class TriggerWorkRecord {
			public static final String table = "PP_T_TRIGGERWORKRECORD";
		}

		public static class ServiceValue {
			public static final String table = "PP_T_SERVICEVALUE";
		}
	}
}