package com.x.ai.assemble.control.jaxrs.chat;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.bean.ChartWi;
import com.x.ai.core.entity.AiModel;
import com.x.ai.core.entity.Clue;
import com.x.ai.core.entity.Completion;
import com.x.ai.core.entity.Completion_;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.StringTools;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 * @author sword
 */
public class ActionChat extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionChat.class);
    private static final String CHART_URL = "/ai-gateway-completion/generate";
    private static final String END_SSE = "[DONE]";
    private static final String EVENT_NAME_MESSAGE = "message";
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

    void execute(EffectivePerson effectivePerson, Sse sse, SseEventSink eventSink,
            JsonElement jsonElement) {
        ActionResult<String> actionResult = new ActionResult<>();
        actionResult.setType(ActionResult.Type.error);
        try (eventSink) {
            try {
                Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
                wi.setPerson(effectivePerson.getUnique());
                wi.setToken(effectivePerson.getToken());
                if (StringUtils.isNotBlank(wi.getInput())) {
                    AiConfig aiConfig = Business.getConfig();
                    if (BooleanUtils.isTrue(aiConfig.getO2AiEnable())
                            && StringUtils.isNotBlank(aiConfig.getO2AiBaseUrl())
                            && StringUtils.isNotBlank(aiConfig.getO2AiToken())) {
                        o2Chat(wi, sse, eventSink, aiConfig);
                    } else {
                        AiModel model = getActiveModel();
                        if (model != null) {
                            aiChat(wi, sse, eventSink, model);
                        } else {
                            logger.warn("未配置可用的模型");
                            actionResult.setMessage("请联系管理员配置可用的模型");
                            if (!eventSink.isClosed()) {
                                this.sendMsg(sse, eventSink, EVENT_NAME_MESSAGE, gson.toJson(actionResult));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e);
                actionResult.setMessage("系统异常：" + e.getMessage());
                if (!eventSink.isClosed()) {
                    this.sendMsg(sse, eventSink, EVENT_NAME_MESSAGE, gson.toJson(actionResult));
                }
            }
        }
    }

    private AiModel getActiveModel() throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return emc.firstEqual(AiModel.class, AiModel.asDefault_FIELDNAME, true);
        }
    }

    private void o2Chat(Wi wi, Sse sse, SseEventSink eventSink, AiConfig aiConfig)
            throws Exception {
        if (StringUtils.isBlank(wi.getGenerateType())) {
            wi.setGenerateType(ChartWi.GENERATE_TYPE_AUTO);
        }
        if (!ChartWi.GENERATE_TYPE_CHAT.equals(wi.getGenerateType())) {
            final Business business = new Business(null);
            List<String> permissionList = new ArrayList<>(
                    business.organization().person().getAuthInfo(wi.getPerson()));
            List<String> unitList = permissionList.stream()
                    .filter(OrganizationDefinition::isUnitDistinguishedName)
                    .collect(Collectors.toList());
            permissionList.addAll(business.organization().unit().listWithUnitSupNested(unitList));
            permissionList.addAll(
                    permissionList.stream().filter(OrganizationDefinition::isDistinguishedName)
                            .map(o -> StringUtils.substringAfter(o,
                                    OrganizationDefinition.NAME_JOIN_CHAR)).collect(
                                    Collectors.toList()));
            permissionList = permissionList.stream().distinct().filter(StringUtils::isNoneBlank)
                    .collect(Collectors.toList());
            wi.setPermissionList(permissionList);
        }

        String json = gson.toJson(wi);
        logger.info("chat to {} body: {}", CHART_URL, json);
        ClientConfig config = new ClientConfig().register(SseFeature.class);
        config.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        config.property(ClientProperties.READ_TIMEOUT, 0);
        Client client = ClientBuilder.newClient(config);
        try {
            WebTarget target = client.target(aiConfig.getO2AiBaseUrl() + CHART_URL);
            EventInput eventInput = target.request(MediaType.SERVER_SENT_EVENTS)
                    .header("Authorization", "Bearer " + aiConfig.getO2AiToken())
                    .post(Entity.json(json), EventInput.class);
            while (!eventInput.isClosed() && !eventSink.isClosed()) {
                final InboundEvent inboundEvent = eventInput.read();
                if (inboundEvent == null) {
                    logger.warn("chart sse from {} read nothing", CHART_URL);
                    break;
                }
                String eventName = StringUtils.isBlank(inboundEvent.getName()) ? EVENT_NAME_MESSAGE
                        : inboundEvent.getName();
                String eventData = inboundEvent.readData(String.class);
                if (StringUtils.isNotBlank(eventData)) {
                    this.sendMsg(sse, eventSink, eventName, eventData);
                }
            }
        } finally {
            client.close();
        }
    }

    private void aiChat(Wi wi, Sse sse, SseEventSink eventSink, AiModel model) throws Exception {
        logger.info("chat to {} model:{} input:{}", model.getType(), model.getModel(),
                StringTools.utf8SubString(wi.getInput(), 20) + "...");
        Map<String, Object> data = new HashMap<>();
        data.put("model", model.getModel());
        data.put("stream", true);
        List<Map<String, String>> messages = new ArrayList<>();
        final String contentKey = "content";
        final String roleKey = "role";
        messages.add(Map.of(roleKey, "system", contentKey, CHAT_SYSTEM_MESSAGE));
        if (StringUtils.isNotBlank(wi.getClueId())) {
            List<Completion> completions = listCompletion(wi.getClueId());
            completions.stream().sorted(Comparator.comparing(Completion::getCreateTime))
                    .forEach(o -> {
                        messages.add(Map.of(roleKey, "user", contentKey, o.getInput()));
                        messages.add(Map.of(roleKey, "assistant", contentKey, o.getContent()));
                    });
        }
        messages.add(Map.of(roleKey, "user", contentKey, wi.getInput()));
        data.put("messages", messages);
        String json = gson.toJson(data);
        ClientConfig config = new ClientConfig().register(SseFeature.class);
        config.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        config.property(ClientProperties.READ_TIMEOUT, 0);
        Client client = ClientBuilder.newClient(config);
        StringBuilder sb = new StringBuilder();
        try {
            WebTarget target = client.target(model.getCompletionUrl());
            EventInput eventInput = target.request(MediaType.SERVER_SENT_EVENTS)
                    .header("Authorization", "Bearer " + model.getApiKey())
                    .post(Entity.json(json), EventInput.class);
            this.saveClue(wi);
            boolean isFirst = true;
            while (!eventInput.isClosed()) {
                final InboundEvent inboundEvent = eventInput.read();
                if (inboundEvent == null) {
                    logger.warn("chart sse from {} read nothing", CHART_URL);
                    break;
                }
                if (isFirst) {
                    this.sendMsg(sse, eventSink, "status",
                            "{\"generateType\":\"chat\", \"clueId\":\"" + wi.getClueId() + "\"}");
                    isFirst = false;
                }
                String eventName = StringUtils.isBlank(inboundEvent.getName()) ? EVENT_NAME_MESSAGE
                        : inboundEvent.getName();
                String eventData = inboundEvent.readData(String.class);
                if (StringUtils.isNotBlank(eventData)) {
                    if (EVENT_NAME_MESSAGE.equals(eventName) && !END_SSE.equals(eventData)) {
                        sb.append(picContent(eventData));
                    }
                    this.sendMsg(sse, eventSink, eventName, eventData);
                }
                if (END_SSE.equals(eventData)) {
                    break;
                }
            }
        } finally {
            client.close();
        }
        wi.setContent(sb.toString());
        this.saveChart(wi);
    }

    private void sendMsg(Sse sse, SseEventSink eventSink, String eventName, String eventData) {
        final OutboundSseEvent sseEvent = sse.newEventBuilder()
                .name(eventName)
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, eventData).build();
        eventSink.send(sseEvent);
    }

    private void saveClue(Wi wi) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Clue clue = null;
            if (StringUtils.isNotBlank(wi.getClueId())) {
                clue = emc.find(wi.getClueId(), Clue.class);
            }
            if (clue == null) {
                clue = new Clue(wi.getPerson(), wi.getInput());
                emc.beginTransaction(Clue.class);
                emc.persist(clue);
                emc.commit();
            }
            wi.setClueId(clue.getId());
        }
    }

    private void saveChart(Wi wi) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Clue clue = null;
            if (StringUtils.isNotBlank(wi.getClueId())) {
                clue = emc.find(wi.getClueId(), Clue.class);
            }
            if (clue == null) {
                clue = new Clue(wi.getPerson(), wi.getInput());
                emc.beginTransaction(Clue.class);
                emc.persist(clue);
                emc.commit();
            }
            Completion completion = new Completion(wi.getPerson(), clue.getId(), wi.getInput(),
                    wi.getContent());
            emc.beginTransaction(Completion.class);
            emc.persist(completion);
            emc.commit();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private String picContent(String content) {
        String data = "";
        if (StringUtils.isBlank(content)) {
            return data;
        }
        JsonObject jo = gson.fromJson(content, JsonObject.class);
        if (jo != null && jo.has("choices")) {
            JsonArray choices = jo.getAsJsonArray("choices");
            if (!choices.isEmpty()) {
                data = XGsonBuilder.extractString(choices.get(0), "delta.content");
            }
        }
        return data == null ? "" : data;
    }

    private List<Completion> listCompletion(String clueId) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(Completion.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Completion> cq = cb.createQuery(Completion.class);
            Root<Completion> root = cq.from(Completion.class);
            Predicate p = cb.equal(root.get(Completion_.clueId), clueId);
            cq.select(root).where(p).orderBy(cb.desc(root.get(JpaObject_.createTime)));
            return em.createQuery(cq).setMaxResults(5).getResultList();
        }
    }

    public static class Wi extends ChartWi {

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

}
