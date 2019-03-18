package com.x.server.console.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.x.server.console.CommandFactory;

public class TestClient {
	
	@Test
	public void test1() throws Exception {
		FileUtils.writeByteArrayToFile(new File("d:/111.png"),Base64.decodeBase64(CommandFactory.DEFAULT_STARTIMAGE));
	}

}
