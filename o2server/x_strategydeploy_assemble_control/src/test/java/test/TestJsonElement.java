package test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TestJsonElement {
	public static void main(String[] args) {
		Gson gson = new Gson();
		JsonParser jsonParser = new JsonParser();
		String jsonStr = "{'year':'2018'}";
		JsonElement jsonelement =  jsonParser.parse(jsonStr); 
		
		JsonObject jsonObject= jsonelement.getAsJsonObject();
		//jsonObject.get(memberName);
	}
}
