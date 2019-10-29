package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class Vfs extends ConfigObject {

	private Ftp ftp = new Ftp();
	private Ftps ftps = new Ftps();

	public Vfs() {
	}

	public Ftp getFtp() {
		return ((null != this.ftp) ? this.ftp : new Ftp());
	}

	public Ftps getFtps() {
		return ((null != this.ftps) ? this.ftps : new Ftps());
	}

	public static Vfs defaultInstance() {
		return new Vfs();
	}

	public static class Ftp extends ConfigObject {

		@FieldDescribe("是否启用被动方式传输,默认true")
		private Boolean passive = true;

		public Boolean getPassive() {
			return (!BooleanUtils.isFalse(this.passive));
		}

		public void setPassive(Boolean passive) {
			this.passive = passive;
		}

	}

	public static class Ftps extends ConfigObject {

		@FieldDescribe("是否启用被动方式传输,默认true")
		private Boolean passive = true;

		public Boolean getPassive() {
			return (!BooleanUtils.isFalse(this.passive));
		}

		public void setPassive(Boolean passive) {
			this.passive = passive;
		}

	}

	public void setFtp(Ftp ftp) {
		this.ftp = ftp;
	}
}
