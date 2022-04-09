package com.x.message.assemble.communicate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.MessageJdbc;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.message.core.entity.Message;

public class JdbcConsumeQueue extends AbstractQueue<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcConsumeQueue.class);

	private static final Gson gson = XGsonBuilder.instance();

	protected void execute(Message message) throws Exception {
		if (null != message && StringUtils.isNotEmpty(message.getItem())) {
			update(message);
		}
		List<String> ids = listOverStay();
		if (!ids.isEmpty()) {
			LOGGER.info("滞留 jdbc 消息数量:{}.", ids.size());
			for (String id : ids) {
				Optional<Message> optional = find(id);
				if (optional.isPresent()) {
					message = optional.get();
					if (StringUtils.isNotEmpty(message.getItem())) {
						update(message);
					}
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
			MessageJdbc.Item item = Config.messageJdbc().get(message.getItem());
			String catalog = "";
			String schema = "";
			String table = "";
			Class.forName("");
			try (Connection connection = DriverManager.getConnection(item.getUrl(), item.getUsername(),
					item.getPassword()); Statement statement = connection.createStatement()) {
				List<String> columns = this.columnNames(connection, item);
				Map<String, JsonElement> map = values(columns, null);

				List<String> params = new ArrayList<>();
				for (int i = 0; i < map.keySet().size(); i++) {
					params.add("?");
				}
				String sql = "INSERT INTO XXXXX (" + StringUtils.join(map.keySet(), ",") + ") VALUES ("
						+ StringUtils.join(params, ",") + ")";

				PreparedStatement preparedStatement = connection.prepareStatement(sql);

				int idx = 1;
				for (Entry<String, JsonElement> en : map.entrySet()) {
					preparedStatement.setObject(idx++, getObject(en.getValue()));
				}
				preparedStatement.executeUpdate();
			}

		} catch (Exception e) {
			failure(message.getId(), e);
			LOGGER.error(e);
		}
	}

	private Object getObject(JsonElement jsonElement) {
		if (null == jsonElement || jsonElement.isJsonNull()) {
			return null;
		}
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isBoolean()) {
				return jsonPrimitive.getAsBoolean();
			} else if (jsonPrimitive.isString()) {
				return jsonPrimitive.getAsString();
			} else if (jsonPrimitive.isNumber()) {
				return jsonPrimitive.getAsNumber();
			}
		}
		return null;
	}

	private Map<String, JsonElement> values(List<String> columns, JsonObject jsonObject) {
		Map<String, JsonElement> map = new LinkedHashMap<>();
		jsonObject.entrySet().forEach(en -> {
			int idx = ListUtils.indexOf(columns, o -> {
				return StringUtils.equalsIgnoreCase(en.getKey(), o);
			});
			if (idx > 0) {
				map.put(columns.get(idx), en.getValue());
			}
		});
		return map;
	}

	private List<String> columnNames(Connection connection, MessageJdbc.Item item) throws SQLException {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		String catalog = "";
		String schema = "";
		String table = "";
		List<String> list = new ArrayList<>();
		try (ResultSet resultSet = databaseMetaData.getColumns(catalog, schema, table, "%")) {
			while (resultSet.next()) {
				list.add(resultSet.getString("COLUMN_NAME"));
			}
		}
		return list;
	}
}
