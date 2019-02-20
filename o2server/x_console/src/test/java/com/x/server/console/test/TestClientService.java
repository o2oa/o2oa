package com.x.server.console.test;

public class TestClientService {

	public static void main(String[] args) {
		System.setProperty("wrapper.config", "D:/aaa.conf");
		WrappedService w = new WrappedService();
		w.init(); // read in configuration
		w.install(); // start the service
	}

}
