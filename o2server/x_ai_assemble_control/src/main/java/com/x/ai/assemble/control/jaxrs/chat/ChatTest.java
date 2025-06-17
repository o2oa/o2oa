package com.x.ai.assemble.control.jaxrs.chat;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 * @author chengjian
 * @date 2025/05/29 14:24
 **/
public class ChatTest {

    private static final String END_SSE = "[DONE]";
    private static final String CHAT_SYSTEM_MESSAGE =
            "你是一个专业且友好的助手，擅长以清晰、有条理的方式回答问题。当前与你对话的用户的身份标识为：%s，请在处理用户请求时将此标识作为上下文参考。"
                    + "无论回答什么问题，请始终使用 Markdown 格式组织你的回复，遵循以下规则："
                    + "1.使用标题（## 或 ###）来组织主要内容或分段。"
                    + "2.使用项目符号（- 或 *）或编号（1. 2. 3.）列出列表项。"
                    + "3.使用 加粗 表示强调，斜体 表示次级强调。"
                    + "4.对于代码或技术内容，使用 ``` 包裹。"
                    + "5.如果需要引用，使用 > 符号。"
                    + "6.确保段落之间有空行，保持排版整洁。"
                    + "7.如果问题涉及表格，使用 Markdown 表格格式。"
                    + "你的目标是以简洁、结构化的 Markdown 格式提供准确且有用的回答。如果用户明确要求其他格式，优先遵循用户要求。";

    public static void main(String[] args) {
        o2Chat();
//        aliChat();
//        deepSeekChat();
    }
    private static void o2Chat(){
        System.out.println("o2 chat start");
        String url = "https://devp.o2oa.net/x_ai_assemble_control/jaxrs/chat/completion";
        String apiKey = "XKXL_d0PoMOuNH4UC6ENkTttmZxBXbY_3Zud7y4qM383LMUzpPSJ2TtC0yShKaaX9xbl5evq6HhnKKFIYXKyww";
        Gson gson = new Gson();
        Map<String, Object> wi = new HashMap<>();
        wi.put("generateType", "chat");
        wi.put("input", "你好");
        String json = gson.toJson(wi);
        ClientConfig config = new ClientConfig().register(SseFeature.class);
        config.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        config.property(ClientProperties.READ_TIMEOUT, 0);
        Client client = ClientBuilder.newClient(config);
        try {
            WebTarget target = client.target(url);
            EventInput eventInput = target.request(MediaType.SERVER_SENT_EVENTS)
                    .header("Authorization", apiKey)
                    .post(Entity.json(json), EventInput.class);
            while (!eventInput.isClosed()) {
                final InboundEvent inboundEvent = eventInput.read();
                if (inboundEvent == null) {
                    break;
                }
                String eventName = StringUtils.isBlank(inboundEvent.getName()) ? "message"
                        : inboundEvent.getName();
                String eventData = inboundEvent.readData(String.class);
                System.out.println(eventName + ":" + eventData);
                if (END_SSE.equals(eventData)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
        System.out.println("o2 chat end");
    }
    private static void aliChat(){
        System.out.println("ali chat start");
        String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
        String apiKey = "sk-d586c1805c214e4ea68f348ebe339881";
        Gson gson = new Gson();
        Map<String, Object> wi = new HashMap<>();
        wi.put("model", "qwen-turbo");
        wi.put("stream", true);
        wi.put("messages", List.of(Map.of("role", "system", "content", CHAT_SYSTEM_MESSAGE),
                Map.of("role", "user", "content", "你好，请介绍一下你自己")));
        String json = gson.toJson(wi);
        ClientConfig config = new ClientConfig().register(SseFeature.class);
        config.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        config.property(ClientProperties.READ_TIMEOUT, 0);
        Client client = ClientBuilder.newClient(config);
        try {
            WebTarget target = client.target(url);
            EventInput eventInput = target.request(MediaType.SERVER_SENT_EVENTS)
                    .header("Authorization", "Bearer "+apiKey)
                    .post(Entity.json(json), EventInput.class);
            while (!eventInput.isClosed()) {
                final InboundEvent inboundEvent = eventInput.read();
                if (inboundEvent == null) {
                    break;
                }
                String eventName = StringUtils.isBlank(inboundEvent.getName()) ? "message"
                        : inboundEvent.getName();
                String eventData = inboundEvent.readData(String.class);
                System.out.println(eventName + ":" + eventData);
                if (END_SSE.equals(eventData)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
        System.out.println("ali chat end");

    }

    public static void deepSeekChat() {
        System.out.println("deepSeek chat start");
        String url = "https://api.deepseek.com/v1/chat/completions";
        String apiKey = "sk-e22789021a12484daa6dff976fc91a9a";
        Gson gson = new Gson();
        Map<String, Object> wi = new HashMap<>();
        wi.put("model", "deepseek-chat");
        wi.put("stream", true);
        wi.put("messages", List.of(Map.of("role", "system", "content", CHAT_SYSTEM_MESSAGE),
                Map.of("role", "user", "content", "你好，请介绍一下你自己")));
        String json = gson.toJson(wi);
        ClientConfig config = new ClientConfig().register(SseFeature.class);
        config.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        config.property(ClientProperties.READ_TIMEOUT, 0);
        Client client = ClientBuilder.newClient(config);
        try {
            WebTarget target = client.target(url);
            EventInput eventInput = target.request(MediaType.SERVER_SENT_EVENTS)
                    .header("Authorization", "Bearer " + apiKey)
                    .post(Entity.json(json), EventInput.class);
            while (!eventInput.isClosed()) {
                final InboundEvent inboundEvent = eventInput.read();
                if (inboundEvent == null) {
                    break;
                }
                String eventName = StringUtils.isBlank(inboundEvent.getName()) ? "message"
                        : inboundEvent.getName();
                String eventData = inboundEvent.readData(String.class);
                System.out.println(eventName + ":" + eventData);
                if (END_SSE.equals(eventData)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
        System.out.println("deepSeek chat end");
    }
}
