package o2.collect.assemble.sms;

public class SmsMessage {

	private String mobile;

	private String message;

	private SmsMessageType smsMessageType;

	private String reference;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public SmsMessageType getSmsMessageType() {
		return smsMessageType;
	}

	public void setSmsMessageType(SmsMessageType smsMessageType) {
		this.smsMessageType = smsMessageType;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
