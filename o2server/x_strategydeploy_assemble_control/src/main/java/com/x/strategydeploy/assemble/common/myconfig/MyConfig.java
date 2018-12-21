package com.x.strategydeploy.assemble.common.myconfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.stream.JsonReader;

public class MyConfig {

	//private Logger logger = LoggerFactory.getLogger(MyConfig.class);
	
	private String configPath = "/config/";

	public void getconfig() {
		InputStream is = this.getClass().getResourceAsStream(configPath + "exceptiondefault.json");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		JsonReader jsonReader = null;
		jsonReader = new JsonReader(br); 
		try {
			jsonReader.beginObject();
			//logger.info(jsonReader.toString());
			System.out.println(jsonReader.toString());
			jsonReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
