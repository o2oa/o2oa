package o2.collect.assemble.jaxrs.collect.umeng.android;

import com.x.base.core.project.gson.GsonPropertyObject;

public class UmengAndroidPushMessage extends GsonPropertyObject {

	public UmengAndroidPushMessage() {
		this.filter = new Filter();
		this.payload = new Payload();
		this.payload.body = new Body();
		this.payload.body.play_lights = "true";
		this.payload.body.play_sound = "true";
		this.payload.body.play_vibrate = "true";
		this.payload.extra = new Extra();
		this.policy = new Policy();
		this.appKey = "56f231e867e58e64190012f4";
		this.type = "unicast";
		this.payload.display_type = "notification";
		// this.payload.display_type = "notification";
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
		private String display_type;
		private Body body;
		private Extra extra;

		public String getDisplay_type() {
			return display_type;
		}

		public void setDisplay_type(String display_type) {
			this.display_type = display_type;
		}

		public Body getBody() {
			return body;
		}

		public void setBody(Body body) {
			this.body = body;
		}

		public Extra getExtra() {
			return extra;
		}

		public void setExtra(Extra extra) {
			this.extra = extra;
		}
	}

	public class Body extends GsonPropertyObject {
		private String ticker;
		private String title;
		private String text;
		private String icon;
		private String largeIcon;
		private String img;
		private String sound;
		private String builder_id;
		private String play_vibrate;
		private String play_lights;
		private String play_sound;
		private String after_open;
		private String url;
		private String activity;
		private Object custom;

		public String getTicker() {
			return ticker;
		}

		public void setTicker(String ticker) {
			this.ticker = ticker;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getLargeIcon() {
			return largeIcon;
		}

		public void setLargeIcon(String largeIcon) {
			this.largeIcon = largeIcon;
		}

		public String getImg() {
			return img;
		}

		public void setImg(String img) {
			this.img = img;
		}

		public String getSound() {
			return sound;
		}

		public void setSound(String sound) {
			this.sound = sound;
		}

		public String getBuilder_id() {
			return builder_id;
		}

		public void setBuilder_id(String builder_id) {
			this.builder_id = builder_id;
		}

		public String getPlay_vibrate() {
			return play_vibrate;
		}

		public void setPlay_vibrate(String play_vibrate) {
			this.play_vibrate = play_vibrate;
		}

		public String getPlay_lights() {
			return play_lights;
		}

		public void setPlay_lights(String play_lights) {
			this.play_lights = play_lights;
		}

		public String getPlay_sound() {
			return play_sound;
		}

		public void setPlay_sound(String play_sound) {
			this.play_sound = play_sound;
		}

		public String getAfter_open() {
			return after_open;
		}

		public void setAfter_open(String after_open) {
			this.after_open = after_open;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public Object getCustom() {
			return custom;
		}

		public void setCustom(Object custom) {
			this.custom = custom;
		}
	}

	public class Custom extends GsonPropertyObject {
	}

	public class Extra extends GsonPropertyObject {
		private String key1;
		private String key2;
		private String key3;
		private String key4;

		public String getKey1() {
			return key1;
		}

		public void setKey1(String key1) {
			this.key1 = key1;
		}

		public String getKey2() {
			return key2;
		}

		public void setKey2(String key2) {
			this.key2 = key2;
		}

		public String getKey3() {
			return key3;
		}

		public void setKey3(String key3) {
			this.key3 = key3;
		}

		public String getKey4() {
			return key4;
		}

		public void setKey4(String key4) {
			this.key4 = key4;
		}
	}

	public class Policy extends GsonPropertyObject {
		private String start_time;
		private String expire_time;
		private String max_send_num;
		private String out_biz_no;

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

		public String getOut_biz_no() {
			return out_biz_no;
		}

		public void setOut_biz_no(String out_biz_no) {
			this.out_biz_no = out_biz_no;
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