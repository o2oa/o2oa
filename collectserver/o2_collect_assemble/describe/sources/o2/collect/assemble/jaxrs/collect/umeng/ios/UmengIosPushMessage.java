package o2.collect.assemble.jaxrs.collect.umeng.ios;

import com.x.base.core.project.gson.GsonPropertyObject;

public class UmengIosPushMessage extends GsonPropertyObject {

	public UmengIosPushMessage() {
		this.filter = new Filter();
		this.payload = new Payload();
		this.policy = new Policy();
		this.appKey = "57f88c7ce0f55a657200241d";
		this.type = "unicast";
	}

	private String appKey;
	private String timestamp;
	private String type;
	private String device_tokens;
	private String alias_type;
	private String alias;
	private String file_id;
	private Filter filter;
	private Payload payload;
	private Policy policy;
	private String production_mode;
	private String description;
	private String thridparty_id;

	public class Filter extends GsonPropertyObject {
	}

	public class Payload extends GsonPropertyObject {

		private Aps aps = new Aps();

		public Aps getAps() {
			return aps;
		}

		public void setAps(Aps aps) {
			this.aps = aps;
		}

	}

	public class Aps extends GsonPropertyObject {
		private Alert alert = new Alert();

		public Alert getAlert() {
			return alert;
		}

		public void setAlert(Alert alert) {
			this.alert = alert;
		}

	}

	public class Alert extends GsonPropertyObject {
		private String title = "";
		private String subtitle = "";
		private String body = "";

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getSubtitle() {
			return subtitle;
		}

		public void setSubtitle(String subtitle) {
			this.subtitle = subtitle;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

	}

	public class Policy extends GsonPropertyObject {
		private String start_time;
		private String expire_time;
		private String max_send_num;

		public String getStart_time() {
			return start_time;
		}

		public void setStart_time(String start_time) {
			this.start_time = start_time;
		}

		public String getExpire_time() {
			return expire_time;
		}

		public void setExpire_time(String expire_time) {
			this.expire_time = expire_time;
		}

		public String getMax_send_num() {
			return max_send_num;
		}

		public void setMax_send_num(String max_send_num) {
			this.max_send_num = max_send_num;
		}

	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDevice_tokens() {
		return device_tokens;
	}

	public void setDevice_tokens(String device_tokens) {
		this.device_tokens = device_tokens;
	}

	public String getAlias_type() {
		return alias_type;
	}

	public void setAlias_type(String alias_type) {
		this.alias_type = alias_type;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Payload getPayload() {
		return payload;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	public String getProduction_mode() {
		return production_mode;
	}

	public void setProduction_mode(String production_mode) {
		this.production_mode = production_mode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getThridparty_id() {
		return thridparty_id;
	}

	public void setThridparty_id(String thridparty_id) {
		this.thridparty_id = thridparty_id;
	}
}