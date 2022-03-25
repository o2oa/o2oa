/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.x.base.core.entity.annotation;

import org.apache.openjpa.jdbc.identifier.DBIdentifier;
import org.apache.openjpa.jdbc.kernel.JDBCStore;
import org.apache.openjpa.jdbc.meta.ValueMapping;
import org.apache.openjpa.jdbc.meta.strats.AbstractValueHandler;
import org.apache.openjpa.jdbc.schema.Column;
import org.apache.openjpa.jdbc.schema.ColumnIO;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.meta.JavaTypes;
import org.apache.openjpa.util.InternalException;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;

public class JsonPropertiesValueHandler extends AbstractValueHandler {

	private static final long serialVersionUID = 1L;
	// private static final String PROXY_SUFFIX = "$proxy";

	private Gson gson = XGsonBuilder.instance();

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	public Column[] map(ValueMapping vm, String name, ColumnIO io, boolean adapt) {
		DBDictionary dict = vm.getMappingRepository().getDBDictionary();
		DBIdentifier colName = DBIdentifier.newColumn(name, dict != null ? dict.delimitAll() : false);
		return map(vm, colName, io, adapt);
	}

	public Column[] map(ValueMapping vm, DBIdentifier name, ColumnIO io, boolean adapt) {
		Column col = new Column();
		col.setIdentifier(name);
		col.setJavaType(JavaTypes.STRING);
		col.setSize(-1);
		return new Column[] { col };
	}

	@Override
	public Object toDataStoreValue(ValueMapping vm, Object val, JDBCStore store) {
		// check for null value.
		if (val == null) {
			return null;
		}
		return gson.toJson(val);
	}

	@Override
	public Object toObjectValue(ValueMapping vm, Object val) {
		if (val == null)
			return null;
		try {
			String className = vm.getDeclaredType().getName();
			Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(className);
			return gson.fromJson(val.toString(), cls);
		} catch (ClassNotFoundException e) {
			throw new InternalException(e);
		}
	}
}