/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2011-2012  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.crsh.guice;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.crsh.plugin.PluginContext;
import org.crsh.plugin.PluginDiscovery;
import org.crsh.plugin.PluginLifeCycle;
import org.crsh.plugin.ServiceLoaderDiscovery;
import org.crsh.vfs.FS;
import org.crsh.vfs.Path;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class CrashGuiceSupport extends AbstractModule {

	public static class InjectorHolder extends PluginLifeCycle {

		private final Injector injector;
		private ClassLoader loader;
		private final Map<String, String> configuration;

		@Inject
		public InjectorHolder(Injector injector, @Named("crashConfiguration") Map<String, String> configuration) throws IOException, URISyntaxException {
			this.injector = injector;
			this.configuration = configuration;
			this.loader = getClass().getClassLoader();
			PluginDiscovery discovery = new ServiceLoaderDiscovery(loader);
			FS cmdFS = createCommandFS();
			FS confFS = createConfFS();

			PluginContext context = new PluginContext(
					discovery,
					buildGuiceMap(),
					cmdFS,
					confFS,
					loader);

			context.refresh();
			start(context);
		}

		private Map<String, Object> buildGuiceMap() {
			return ImmutableMap.of(
					"factory", injector,
					"properties", configuration,
					"beans", new GuiceMap(injector)
					);
		}

		protected FS createCommandFS() throws IOException, URISyntaxException {
			FS cmdFS = new FS();
			cmdFS.mount(loader, Path.get("/crash/commands/"));
			return cmdFS;
		}

		protected FS createConfFS() throws IOException, URISyntaxException {
			FS confFS = new FS();
			confFS.mount(loader, Path.get("/crash/"));
			return confFS;
		}
		
		public void destroy() throws Exception {
			stop();
		}
	}

	private final Map<String, String> configuration;
	
	public CrashGuiceSupport() {
		this(ImmutableMap.<String, String>of());
	}
	
	public CrashGuiceSupport(Map<String, String> configuration) {
		this.configuration = configuration;
	}

	@Override
	protected void configure() {
		bind(new TypeLiteral<Map<String, String>>(){}).annotatedWith(Names.named("crashConfiguration")).toInstance(configuration);
		bind(InjectorHolder.class).asEagerSingleton();
	}

}
