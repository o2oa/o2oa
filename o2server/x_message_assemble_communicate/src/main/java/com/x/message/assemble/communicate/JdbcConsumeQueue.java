package com.x.message.assemble.communicate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.config.Message.JdbcConsumer;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DateTools;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Message_;

public class JdbcConsumeQueue extends AbstractQueue<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcConsumeQueue.class);

    private static final Gson gson = XGsonBuilder.instance();

    protected void execute(Message message) throws Exception {
        if (null != message) {
            update(message);
        }
        List<String> ids = listOverStay();
        if (!ids.isEmpty()) {
            LOGGER.info("滞留 jdbc 消息数量:{}.", ids.size());
            for (String id : ids) {
                Optional<Message> optional = find(id);
                if (optional.isPresent()) {
                    message = optional.get();
                    update(message);
                }
            }
        }
    }

    private Optional<Message> find(String id) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return Optional.of(emc.find(id, Message.class));
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return Optional.empty();
    }

    private void update(Message message) {
        try {
            JdbcConsumer consumer = gson.fromJson(message.getProperties().getConsumerJsonElement(), JdbcConsumer.class);
            Class.forName(consumer.getDriverClass());
            LOGGER.debug("consumer:{}.", consumer);
            try (Connection connection = DriverManager.getConnection(consumer.getUrl(), consumer.getUsername(),
                    consumer.getPassword()); Statement statement = connection.createStatement()) {
                List<Column> columns = this.columns(connection, consumer);
                Map<String, Object> map = values(columns, gson.fromJson(message.getBody(), JsonObject.class));
                String sql = createSql(consumer.getSchema(), consumer.getTable(), map);
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                int idx = 1;
                for (Entry<String, Object> en : map.entrySet()) {
                    preparedStatement.setObject(idx++, en.getValue());
                }
                preparedStatement.executeUpdate();
            }
            success(message.getId());
        } catch (Exception e) {
            failure(message.getId(), e);
            LOGGER.error(e);
        }

    }

    private String createSql(String schema, String table, Map<String, Object> map) {
        List<String> aux = new ArrayList<>();
        for (int i = 0; i < map.keySet().size(); i++) {
            aux.add("?");
        }
        return "INSERT INTO " + (StringUtils.isEmpty(schema) ? table : (schema + "." + table)) + " ("
                + StringUtils.join(map.keySet(), ",") + ") VALUES (" + StringUtils.join(aux, ",") + ")";
    }

    private void success(String id) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Message message = emc.find(id, Message.class);
            if (null != message) {
                emc.beginTransaction(Message.class);
                message.setConsumed(true);
                emc.commit();
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private void failure(String id, Exception exception) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Message message = emc.find(id, Message.class);
            if (null != message) {
                emc.beginTransaction(Message.class);
                Integer failure = message.getProperties().getFailure();
                failure = (null == failure) ? 1 : failure + 1;
                message.getProperties().setFailure(failure);
                message.getProperties().setError(exception.getMessage());
                emc.commit();
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private Map<String, Object> values(List<Column> columns, JsonObject jsonObject) {
        Map<String, String> auxLowerCase = new HashMap<>();
        jsonObject.entrySet().forEach(en -> {
            auxLowerCase.put(StringUtils.lowerCase(en.getKey()), en.getKey());
        });
        Map<String, Object> map = new LinkedHashMap<>();
        String key;
        for (Column column : columns) {
            key = auxLowerCase.get(StringUtils.lowerCase(column.getName()));
            if (StringUtils.isNotEmpty(key)) {
                map.put(column.getName(), getValue(column, jsonObject.get(key)));
            }
        }
        return map;
    }

    private Object getValue(Column column, JsonElement jsonElement) {
        if (null == jsonElement || jsonElement.isJsonNull()) {
            return null;
        }
        switch (column.getDataType()) {
            case java.sql.Types.ARRAY:
                return toARRAY();
            case java.sql.Types.BIGINT:
                return toBIGINT(jsonElement);
            case java.sql.Types.BINARY:
                return toBINARY();
            case java.sql.Types.BIT:
                return toBIT(jsonElement);
            case java.sql.Types.BLOB:
                return toBLOB();
            case java.sql.Types.BOOLEAN:
                return toBOOLEAN(jsonElement);
            case java.sql.Types.CHAR:
                return toCHAR(jsonElement);
            case java.sql.Types.CLOB:
                return toCLOB(jsonElement);
            case java.sql.Types.DATALINK:
                return toDATALINK();
            case java.sql.Types.DATE:
                return toDATE(jsonElement);
            case java.sql.Types.DECIMAL:
                return toDECIMAL(jsonElement);
            case java.sql.Types.DISTINCT:
                return toDISTINCT();
            case java.sql.Types.DOUBLE:
                return toDOUBLE(jsonElement);
            case java.sql.Types.FLOAT:
                return toFLOAT(jsonElement);
            case java.sql.Types.INTEGER:
                return toINTEGER(jsonElement);
            case java.sql.Types.JAVA_OBJECT:
                return toJAVA_OBJECT();
            case java.sql.Types.LONGNVARCHAR:
                return toLONGNVARCHAR(jsonElement);
            case java.sql.Types.LONGVARBINARY:
                return toLONGVARBINARY();
            case java.sql.Types.LONGVARCHAR:
                return toLONGVARCHAR(jsonElement);
            case java.sql.Types.NCHAR:
                return toNCHAR(jsonElement);
            case java.sql.Types.NCLOB:
                return toNCLOB(jsonElement);
            case java.sql.Types.NULL:
                return toNULL();
            case java.sql.Types.NUMERIC:
                return toNUMERIC(jsonElement);
            case java.sql.Types.NVARCHAR:
                return toNVARCHAR(jsonElement);
            case java.sql.Types.OTHER:
                return toOTHER();
            case java.sql.Types.REAL:
                return toREAL();
            case java.sql.Types.REF:
                return toREF();
            case java.sql.Types.REF_CURSOR:
                return toREF_CURSOR();
            case java.sql.Types.ROWID:
                return toROWID();
            case java.sql.Types.SMALLINT:
                return toSMALLINT(jsonElement);
            case java.sql.Types.SQLXML:
                return toSQLXML(jsonElement);
            case java.sql.Types.STRUCT:
                return toSTRUCT();
            case java.sql.Types.TIME:
                return toTIME(jsonElement);
            case java.sql.Types.TIME_WITH_TIMEZONE:
                return toTIME_WITH_TIMEZONE();
            case java.sql.Types.TIMESTAMP:
                return toTIMESTAMP(jsonElement);
            case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
                return toTIMESTAMP_WITH_TIMEZONE();
            case java.sql.Types.TINYINT:
                return toTINYINT(jsonElement);
            case java.sql.Types.VARBINARY:
                return toVARBINARY();
            case java.sql.Types.VARCHAR:
                return toVARCHAR(jsonElement);
            default:
                return null;
        }
    }

    private Object toARRAY() {
        return null;
    }

    private Object toBIGINT(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return Integer.valueOf(jsonElement.getAsJsonPrimitive().getAsNumber().intValue());
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toBINARY() {
        return null;
    }

    private Object toBIT(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if (jsonPrimitive.isBoolean()) {
                    return Integer.valueOf(jsonPrimitive.getAsBoolean() ? 1 : 0);
                } else if (jsonPrimitive.isNumber()) {
                    return Integer.valueOf((jsonPrimitive.getAsInt() > 0) ? 1 : 0);
                } else if (jsonPrimitive.isString()) {
                    String str = jsonPrimitive.getAsString();
                    if (NumberUtils.isCreatable(str)) {
                        return Integer.valueOf((NumberUtils.toInt(str) > 0) ? 1 : 0);
                    } else {
                        return Integer.valueOf(BooleanUtils.toBoolean(str) ? 1 : 0);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toBLOB() {
        return null;
    }

    private Object toBOOLEAN(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if (jsonPrimitive.isBoolean()) {
                    return jsonElement.getAsBoolean();
                } else if (jsonPrimitive.isNumber()) {
                    return Boolean.valueOf((jsonPrimitive.getAsNumber().intValue() > 0) ? true : false);
                } else if (jsonPrimitive.isString()) {
                    String str = jsonPrimitive.getAsString();
                    if (NumberUtils.isCreatable(str)) {
                        return Boolean.valueOf((NumberUtils.toInt(str) > 0) ? true : false);
                    } else {
                        return BooleanUtils.toBooleanObject(str);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toCHAR(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return jsonElement.getAsJsonPrimitive().getAsCharacter();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toCLOB(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return jsonElement.getAsJsonPrimitive().getAsString();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toDATALINK() {
        return null;
    }

    private Object toDATE(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return DateTools.parse(jsonElement.getAsJsonPrimitive().getAsString());
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toDECIMAL(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return BigDecimal.valueOf(jsonElement.getAsJsonPrimitive().getAsNumber().doubleValue());
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toDISTINCT() {
        return null;
    }

    private Object toDOUBLE(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return Double.valueOf(jsonElement.getAsJsonPrimitive().getAsNumber().doubleValue());
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toFLOAT(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return Float.valueOf(jsonElement.getAsJsonPrimitive().getAsNumber().floatValue());
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toINTEGER(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return Integer.valueOf(jsonElement.getAsJsonPrimitive().getAsNumber().intValue());
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toJAVA_OBJECT() {
        return null;
    }

    private Object toLONGNVARCHAR(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return jsonElement.getAsJsonPrimitive().getAsString();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toLONGVARBINARY() {
        return null;
    }

    private Object toLONGVARCHAR(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return jsonElement.getAsJsonPrimitive().getAsString();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toNCHAR(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return jsonElement.getAsJsonPrimitive().getAsString();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toNCLOB(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return jsonElement.getAsJsonPrimitive().getAsString();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toNULL() {
        return null;
    }

    private Object toNUMERIC(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return jsonElement.getAsJsonPrimitive().getAsBigDecimal();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toNVARCHAR(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return jsonElement.getAsJsonPrimitive().getAsString();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toOTHER() {
        return null;
    }

    private Object toREAL() {
        return null;
    }

    private Object toREF() {
        return null;
    }

    private Object toREF_CURSOR() {
        return null;
    }

    private Object toROWID() {
        return null;
    }

    private Object toSMALLINT(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return Short.valueOf(jsonElement.getAsJsonPrimitive().getAsNumber().shortValue());
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toSQLXML(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return jsonElement.getAsJsonPrimitive().getAsString();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toSTRUCT() {
        return null;
    }

    private Object toTIME(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return DateTools.parse(jsonElement.getAsJsonPrimitive().getAsString());
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toTIME_WITH_TIMEZONE() {
        return null;
    }

    private Object toTIMESTAMP(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return DateTools.parse(jsonElement.getAsJsonPrimitive().getAsString());
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toTIMESTAMP_WITH_TIMEZONE() {
        return null;
    }

    private Object toTINYINT(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return Short.valueOf(jsonElement.getAsJsonPrimitive().getAsNumber().shortValue());
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private Object toVARBINARY() {
        return null;
    }

    private Object toVARCHAR(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            try {
                return jsonElement.getAsJsonPrimitive().getAsString();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return null;
    }

    private List<Column> columns(Connection connection, JdbcConsumer consumer) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        String catalog = StringUtils.isEmpty(consumer.getCatalog()) ? null : consumer.getCatalog();
        String schema = StringUtils.isEmpty(consumer.getSchema()) ? "%" : consumer.getSchema();
        String table = consumer.getTable();
        List<Column> list = new ArrayList<>();
        try (ResultSet resultSet = databaseMetaData.getColumns(catalog, schema, table, "%")) {
            while (resultSet.next()) {
                list.add(new Column(resultSet.getString("COLUMN_NAME"), resultSet.getInt("DATA_TYPE")));
            }
        }
        return list;
    }

    private List<String> listOverStay() {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(Message.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> cq = cb.createQuery(String.class);
            Root<Message> root = cq.from(Message.class);
            Predicate p = cb.equal(root.get(Message_.consumer), MessageConnector.CONSUME_JDBC);
            p = cb.and(p, cb.notEqual(root.get(Message_.consumed), true));
            p = cb.and(p, cb.lessThan(root.get(JpaObject_.updateTime), DateUtils.addMinutes(new Date(), -20)));
            cq.select(root.get(Message_.id)).where(p);
            return em.createQuery(cq).setMaxResults(20).getResultList();
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new ArrayList<>();
    }

    public static class Column {

        public Column(String name, int dataType) {
            this.name = name;
            this.dataType = dataType;
        }

        private String name;
        private int dataType;

        public String getName() {
            return name;
        }

        public int getDataType() {
            return dataType;
        }

    }
}
