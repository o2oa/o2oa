package com.x.processplatform.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Content {

		public static class TaskCompleted {
			public static final String table = "pp_c_taskCompleted";
		}

		public static class Task {
			public static final String table = "pp_c_task";
		}

		public static class Read {
			public static final String table = "pp_c_read";
		}

		public static class Review {
			public static final String table = "pp_c_review";
		}

		public static class ReadCompleted {
			public static final String table = "pp_c_readCompleted";
		}

		public static class WorkCompleted {
			public static final String table = "pp_c_workCompleted";
		}

		public static class WorkLog {
			public static final String table = "pp_c_workLog";
		}

		public static class Work {
			public static final String table = "pp_c_work";
		}

		public static class Attachment {
			public static final String table = "pp_c_attachment";
		}

		public static class DataItem {
			public static final String table = "pp_c_dataItem";
		}
		
		public static class DataLobItem {
			public static final String table = "pp_c_dataLobItem";
		}

		public static class SerialNumber {
			public static final String table = "pp_e_serialNumber";
		}
	}

	public static class Element {

		public static class Application {
			public static final String table = "pp_e_application";
		}

		public static class Agent {
			public static final String table = "pp_e_agent";
		}

		public static class Begin {
			public static final String table = "pp_e_begin";
		}

		public static class Cancel {
			public static final String table = "pp_e_cancel";
		}

		public static class Choice {
			public static final String table = "pp_e_choice";
		}

		public static class Condition {
			public static final String table = "pp_e_condition";
		}

		public static class Delay {
			public static final String table = "pp_e_delay";
		}

		public static class Embed {
			public static final String table = "pp_e_embed";
		}

		public static class End {
			public static final String table = "pp_e_end";
		}

		public static class Parallel {
			public static final String table = "pp_e_parallel";
		}

		public static class Invoke {
			public static final String table = "pp_e_invoke";
		}

		public static class Manual {
			public static final String table = "pp_e_manual";
		}

		public static class Merge {
			public static final String table = "pp_e_merge";
		}

		public static class Message {
			public static final String table = "pp_e_message";
		}

		public static class Route {
			public static final String table = "pp_e_route";
		}

		public static class Script {
			public static final String table = "pp_e_script";
		}

		public static class Service {
			public static final String table = "pp_e_service";
		}

		public static class Split {
			public static final String table = "pp_e_split";
		}

		public static class Form {
			public static final String table = "pp_e_form";
		}

		public static class FormField {
			public static final String table = "pp_e_formField";
		}

		public static class TemplateForm {
			public static final String table = "pp_e_templateForm";
		}

		public static class Process {
			public static final String table = "pp_e_process";
		}

		public static class ApplicationDict {
			public static final String table = "pp_e_applicationDict";
		}

		public static class ApplicationDictItem {
			public static final String table = "pp_e_applicationDictItem";
		}
		
		public static class ApplicationDictLobItem {
			public static final String table = "pp_e_applicationDictLobItem";
		}

		public static class QueryView {
			public static final String table = "pp_e_queryview";
		}

		public static class QueryStat {
			public static final String table = "pp_e_querystat";
		}

		public static class QueryStatTimed {
			public static final String table = "pp_e_querystattimed";
		}
	}

	public static class Log {
		public static class ProcessingError {
			public static final String table = "pp_l_processingError";
		}
	}

	public static class Temporary {
		public static class TriggerWorkRecord {
			public static final String table = "pp_t_triggerWorkRecord";
		}

		public static class ServiceValue {
			public static final String table = "pp_t_serviceValue";
		}
	}
}