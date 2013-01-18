/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.crsh.guice;

import java.util.Iterator;

import org.crsh.util.SimpleMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.spi.DefaultBindingScopingVisitor;

class GuiceMap extends SimpleMap<String, Object> {

	private final Injector injector;

	GuiceMap(Injector injector) {
		this.injector = injector;
	}

	@Override
	protected Iterator<String> keys() {
		Builder<String> types = ImmutableList.<String>builder();
		for (Entry<Key<?>, Binding<?>> entry: injector.getAllBindings().entrySet()) {
			if (isSingleton(entry)) {
				types.add(entry.getKey().getTypeLiteral().toString());
			}
		}
		return types.build().iterator();
	}

	private boolean isSingleton(Entry<Key<?>, Binding<?>> entry) {
		return Boolean.TRUE.equals(entry.getValue().acceptScopingVisitor(new DefaultBindingScopingVisitor<Boolean>() {
			@Override
			public Boolean visitScope(Scope scope) {
				return scope == Scopes.SINGLETON;
			}
		}));
	}

	@Override
	public Object get(Object key)  {
		if (key instanceof String) {
			String className = (String) key;
			try {
				Class<?> clazz = Class.forName(className);
				if (clazz != null) {
					return injector.getInstance(clazz);
				}
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return null;
	}
}
