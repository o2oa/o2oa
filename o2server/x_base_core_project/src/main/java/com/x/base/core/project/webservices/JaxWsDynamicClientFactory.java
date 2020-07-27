/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.x.base.core.project.webservices;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.endpoint.EndpointImplFactory;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.apache.cxf.helpers.JavaUtils;
import org.apache.cxf.jaxws.support.JaxWsEndpointImplFactory;

/**
 * This class reads a WSDL and creates a dynamic client from it with JAX-WS
 * functionality. This provides support for attachments and other useful things
 * that come with JAX-WS.
 *
 * Use {@link #newInstance} to obtain an instance, and then
 * {@link #createClient(String)} (or other overloads) to create a client.
 *
 * This factory uses the JAXB data binding.
 **/
public class JaxWsDynamicClientFactory extends DynamicClientFactory {

	protected JaxWsDynamicClientFactory(Bus bus) {
		super(bus);
	}

	@Override
	protected EndpointImplFactory getEndpointImplFactory() {
		return JaxWsEndpointImplFactory.getSingleton();
	}

	protected boolean allowWrapperOps() {
		return true;
	}

	/**
	 * Create a new instance using a specific <tt>Bus</tt>.
	 *
	 * @param b the <tt>Bus</tt> to use in subsequent operations with the instance
	 * @return the new instance
	 */
	public static JaxWsDynamicClientFactory newInstance(Bus b) {
		return new JaxWsDynamicClientFactory(b);
	}

	/**
	 * Create a new instance using a default <tt>Bus</tt>.
	 *
	 * @return the new instance
	 * @see CXFBusFactory#getDefaultBus()
	 */
	public static JaxWsDynamicClientFactory newInstance() {
		Bus bus = BusFactory.getThreadDefaultBus();
		return new JaxWsDynamicClientFactory(bus);
	}

	protected boolean compileJavaSrc(String classPath, List<File> srcList, String dest) {
		org.apache.cxf.common.util.Compiler javaCompiler = new org.apache.cxf.common.util.Compiler();
		javaCompiler.setEncoding(StandardCharsets.UTF_8.name());
		javaCompiler.setClassPath(classPath);
		javaCompiler.setOutputDir(dest);
		if (JavaUtils.isJava9Compatible()) {
			javaCompiler.setTarget("9");
		} else {
			javaCompiler.setTarget("1.8");
		}
		return javaCompiler.compileFiles(srcList);
	}
}