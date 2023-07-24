package com.x.base.core.project.instrument;

public class Instrument {

	private CodeFactory code;

	public CodeFactory code() {
		if (null == this.code) {
			this.code = new CodeFactory();
		}
		return code;
	}

	private CaptchaFactory captcha;

	public CaptchaFactory captcha() {
		if (null == this.captcha) {
			this.captcha = new CaptchaFactory();
		}
		return captcha;
	}

	private CollectFactory collect;

	public CollectFactory collect() {
		if (null == this.collect) {
			this.collect = new CollectFactory();
		}
		return collect;
	}
}