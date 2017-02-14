package com.x.base.core.project.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.x.base.core.DefaultCharset;
import com.x.base.core.project.x_organization_assemble_authentication;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.server.ApplicationServer;
import com.x.base.core.project.server.ApplicationServer.NameWeightPair;
import com.x.base.core.project.server.DataServer;
import com.x.base.core.project.server.Node;
import com.x.base.core.project.server.StorageServer;
import com.x.base.core.project.server.StorageServer.Account;
import com.x.base.core.project.server.WebServer;

public class TestClient {
	@Test
	public void test() throws Exception {
		Node config = new Node();
		config.setEnable(true);
		config.setIsPrimaryCenter(true);
		config.setApplication(new ApplicationServer());
		config.getApplication().setEnable(true);
		config.setData(new DataServer());
		config.getData().setEnable(true);
		config.setWeb(new WebServer());
		config.getWeb().setEnable(true);
		config.setStorage(new StorageServer());
		config.getStorage().setEnable(true);
		config.getData().setEnable(true);
		config.getData().setPassword("1");
		config.getData().setTcpPort(20050);
		config.getData().setWebPort(20051);
		Account account = config.getStorage().new Account();
		// account.setName("xs01processplatform");
		account.setPassword("1");
		account.setUsername("processPlatform");
		account.setWeight(100);
		config.getStorage().getAccounts().add(account);
		account = config.getStorage().new Account();
		// account.setName("xs01processplatform");
		account.setPassword("1");
		account.setUsername("processPlatform");
		account.setWeight(100);
		config.getStorage().getAccounts().add(account);
		account = config.getStorage().new Account();
		// account.setName("xs01file");
		account.setPassword("1");
		account.setUsername("file");
		account.setWeight(100);
		config.getStorage().getAccounts().add(account);
		account = config.getStorage().new Account();
		// account.setName("xs01meeting");
		account.setPassword("1");
		account.setUsername("meeting");
		account.setWeight(100);
		config.getStorage().getAccounts().add(account);
		account = config.getStorage().new Account();
		// account.setName("xs01okr");
		account.setPassword("1");
		account.setUsername("okr");
		account.setWeight(100);
		config.getStorage().getAccounts().add(account);
		account = config.getStorage().new Account();
		// account.setName("xs01cms");
		account.setPassword("1");
		account.setUsername("cms");
		account.setWeight(100);
		config.getStorage().getAccounts().add(account);
		NameWeightPair nameWeightPair = config.getApplication().new NameWeightPair();
		nameWeightPair.setName(x_organization_assemble_authentication.class.getSimpleName());
		nameWeightPair.setWeight(100);
		config.getApplication().getProjects().add(nameWeightPair);
		nameWeightPair = config.getApplication().new NameWeightPair();
		nameWeightPair.setName(x_organization_assemble_control.class.getSimpleName());
		nameWeightPair.setWeight(100);
		config.getApplication().getProjects().add(nameWeightPair);
		config.getApplication().setForceRedeploy(true);
		File file = new File("e:/newo2server/config/nodeConfig_127.0.0.1.json");
		FileUtils.write(file, config.toString(), DefaultCharset.charset);
	}

	@Test
	public void test3() {
		List<String> l = new ArrayList<>();
		l.add(null);
		l.add("b");
		l.add("a");
		l.add("b");
		l.add(null);
		TreeList<String> tree = new TreeList<>(l);
		Collections.sort(tree);
		for (String o : tree) {
			System.out.println(o);
		}
	}

}
