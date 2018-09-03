package o2.collect.assemble.sms;

import java.util.concurrent.LinkedBlockingQueue;

import o2.collect.assemble.sms.code.SmsCodeSender;
import o2.collect.assemble.sms.code.SmsCodeSenderTencent;

public class SmsSender {

	private volatile static SmsSender INSTANCE;

	private LinkedBlockingQueue<SmsMessage> queue;

	private SmsCodeSender codeSender;

	private SmsSender() {
		this.queue = new LinkedBlockingQueue<SmsMessage>();
		this.codeSender = new SmsCodeSenderTencent();
		SendThread sendThread = new SendThread();
		sendThread.start();
	}

	public static void send(SmsMessage o) throws Exception {
		INSTANCE.queue.put(o);
	}

	public static void start() {
		if (INSTANCE == null) {
			synchronized (SmsSender.class) {
				if (INSTANCE == null) {
					INSTANCE = new SmsSender();
				}
			}
		}
	}

	public static void stop() {
		try {
			if (INSTANCE != null) {
				INSTANCE.queue.put(INSTANCE.new StopThreadSignal());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class SendThread extends Thread {
		public void run() {
			while (true) {
				try {
					SmsMessage o = queue.take();
					if (o instanceof StopThreadSignal) {
						break;
					} else {
						switch (o.getSmsMessageType()) {
						case code:
							codeSender.send(o);
							break;
						default:
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class StopThreadSignal extends SmsMessage {

	}
}
