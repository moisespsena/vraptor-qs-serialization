/***
 * Copyright (c) 2011 Moises P. Sena - www.moisespsena.com
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring;

/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 28. November 2008 by Joerg Schaible
 */

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.format.ObjectQSFormatWriter;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * A simple writer that outputs Query String in a pretty-printed stream. Arrays,
 * Lists and Sets rely on you NOT using XStream.addImplicitCollection(..).
 * 
 * @author Moises P. Sena &lt;moisespsena@gmail.com&gt;
 * @since 1.0 23/08/2011
 * 
 */
public class QSWriter implements ExtendedHierarchicalStreamWriter {

	/**
	 * The Node
	 * 
	 * @author Moises P. Sena &lt;moisespsena@gmail.com&gt;
	 * @since 1.0 23/08/2011
	 * 
	 */
	public class Node {
		public final Class<?> clazz;
		public boolean fieldAlready;
		public boolean isCollection;
		public final String name;

		public Node(final String name, final Class<?> clazz) {
			this.name = name;
			this.clazz = clazz;
			isCollection = isCollection(clazz);
		}
	}

	/**
	 * The Path
	 * 
	 * @author Moises P. Sena &lt;moisespsena@gmail.com&gt;
	 * @since 1.0 23/08/2011
	 * 
	 */
	public class Path {
		private final boolean index;
		private String path;

		public Path(final String path) {
			this(path, false);
		}

		public Path(final String path, final boolean index) {
			super();
			this.index = index;
			this.path = path;
		}

		public String getPath() {
			return path;
		}

		public boolean isIndex() {
			return index;
		}

		public void setPath(final String path) {
			this.path = path;
		}

		@Override
		public String toString() {
			return path;
		}
	}

	public interface PathListener {
		/**
		 * Retorna Se este Listener vai ser executado
		 * 
		 * @param depth
		 *            Profundidade da variavel.
		 * 
		 *            <dl>
		 *            <dt>Exemplo</dt>
		 *            <dd>
		 * 
		 *            <pre>
		 * product : 0
		 * product.name : 1
		 * product.users : 1
		 * product.users[0] : 2
		 * product.users[1] : 2
		 * product.users[2].name : 3
		 * </pre>
		 * 
		 *            </dd>
		 */
		public boolean accepts(int depth);

		public String pathFromDepth(String currentName, int depth,
				Class<?> clazz);
	}

	/**
	 * DROP_ROOT_MODE drops the Query String root path.
	 * <p>
	 * The root node is the first level of the Query String object i.e.
	 * 
	 * <pre>
	 * <code>
	 * person.name=Moi&amp;person.lastName=Sena
	 * </code>
	 * </pre>
	 * 
	 * will be written without root simply as
	 * 
	 * <pre>
	 * <code>name=Moi&amp;lastName=Sena</code>
	 * </pre>
	 * 
	 * Without a root node, the top level element might now also be an array.
	 * However, it is possible to generate invalid JSON unless
	 * {@link #STRICT_MODE} is also set.
	 * </p>
	 * 
	 * @since 1.0
	 */
	public static final int DROP_ROOT_MODE = 1;

	/**
	 * STRICT_MODE prevents invalid Query String for single value objects when
	 * dropping the root.
	 * <p>
	 * The mode is only useful in combination with the {@link #DROP_ROOT_MODE}.
	 * An object with a single value as first node i.e.
	 * 
	 * <pre>
	 * <code>name=Moi&amp;lastName=Sena</code>
	 * </pre>
	 * 
	 * is simply written as
	 * 
	 * <pre>
	 * <code>Moi&amp;Sena</code>
	 * </pre>
	 * 
	 * However, this is no longer valid JSON. Therefore you can activate
	 * {@link #STRICT_MODE} and a {@link ConversionException} is thrown instead.
	 * </p>
	 * 
	 * @since 1.0
	 */
	public static final int STRICT_MODE = 2;
	private final Map<Integer, Integer> collIndexMap = new HashMap<Integer, Integer>();
	private final Map<Integer, Boolean> collIsList = new HashMap<Integer, Boolean>();
	private final Map<Integer, Boolean> collIsMap = new HashMap<Integer, Boolean>();
	private int depth;

	private final FastStack elementStack = new FastStack(16);
	private final int mode;
	private final Set<PathListener> pathListeners = new HashSet<QSWriter.PathListener>();

	private final List<Path> paths = new ArrayList<Path>();

	private final QSFormatWriter qsFormatWriter;

	private final QuickWriter writer;

	public QSWriter(final Writer writer) {
		this(writer, DROP_ROOT_MODE);
	}

	/**
	 * Create a QSWriter where the writer mode can be chosen.
	 * <p>
	 * Following constants can be used as bit mask:
	 * <ul>
	 * <li>{@link #DROP_ROOT_MODE}: drop the root node</li>
	 * <li>{@link #STRICT_MODE}: do not throw {@link ConversionException}, if
	 * writer should generate invalid JSON</li>
	 * </ul>
	 * </p>
	 * 
	 * @param writer
	 *            the {@link Writer} where the JSON is written to
	 * @param mode
	 *            the QSWriter mode
	 * @since 1.3.1
	 */
	public QSWriter(final Writer writer, final int mode) {
		this(writer, mode, new ObjectQSFormatWriter());
	}

	/**
	 * @since 1.3.1
	 */
	public QSWriter(final Writer writer, final int mode,
			final QSFormatWriter qsFormatWriter) {
		this.mode = mode;
		this.qsFormatWriter = qsFormatWriter;
		this.writer = new QuickWriter(writer);
	}

	/**
	 * @param writer
	 * @param formatWriter
	 */
	public QSWriter(final Writer writer, final QSFormatWriter formatWriter) {
		this(writer, DROP_ROOT_MODE, formatWriter);
	}

	@Override
	public void addAttribute(final String key, final String value) {
		final Node currNode = (Node) elementStack.peek();
		if ((currNode == null) || !currNode.isCollection) {
			startNode('@' + key, String.class);
			writeText(value, String.class);
			endNode();
		}
	}

	private void addIndexPath() {
		int v = -1;
		if (collIndexMap.containsKey(depth)) {
			v = collIndexMap.get(depth);
		}
		v++;
		collIndexMap.put(depth, v);

		if (paths.size() <= depth) {
			paths.add(new Path("" + v, true));
		}
	}

	private void addPath(final String name) {
		if (paths.size() <= depth) {
			paths.add(new Path(name));
		}
	}

	public QSWriter addPathListener(final PathListener listener) {
		pathListeners.add(listener);
		return this;
	}

	@Override
	public void close() {
		writer.close();
	}

	@Override
	public void endNode() {
		collIndexMap.remove(depth);
		collIsMap.remove(depth);
		collIsList.remove(depth);
		depth--;
		if (paths.size() > depth) {
			paths.remove(depth);
		}

	}

	@Override
	public void flush() {
		writer.flush();
	}

	private boolean isCollection(final Class<?> clazz) {
		return (clazz != null)
				&& (Collection.class.isAssignableFrom(clazz) || clazz.isArray()
						|| Map.class.isAssignableFrom(clazz) || Map.Entry.class
							.isAssignableFrom(clazz));
	}

	private String pathFor(final String currentName, final Class<?> clazz) {
		String newName = null;
		for (final PathListener pathListener : pathListeners) {
			if (pathListener.accepts(depth)) {
				newName = pathListener.pathFromDepth(currentName, depth, clazz);

				break;
			}
		}

		if (newName == null) {
			return currentName;
		} else {
			return newName;
		}
	}

	@Override
	public void setValue(final String text) {
		final Node currNode = (Node) elementStack.peek();
		if ((currNode != null) && currNode.fieldAlready) {
			startNode("$", String.class);
			writeText(text, String.class);
			endNode();
		} else {
			if (((mode & (DROP_ROOT_MODE | STRICT_MODE)) == (DROP_ROOT_MODE | STRICT_MODE))
					&& (depth == 1)) {
				throw new ConversionException(
						"Single value cannot be Query String root element");
			}

			writeText(writer, text);
		}
	}

	/**
	 * @deprecated since 1.2, use startNode(String name, Class clazz) instead.
	 */
	@Deprecated
	@Override
	public void startNode(final String name) {
		startNode(name, null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void startNode(final String name, final Class clazz) {
		final Node currNode = (Node) elementStack.peek();

		if (currNode != null) {
			if (isCollection(clazz)) {
				if (Map.class.isAssignableFrom(clazz)) {
					addIndexPath();
				} else if (Map.Entry.class.isAssignableFrom(clazz)) {
					addIndexPath();
					collIsMap.put(depth, true);
				} else if (List.class.isAssignableFrom(clazz)) {
					collIsMap.put(depth, true);
					addPath(pathFor(name, clazz));
				}
			} else if ((collIsMap.containsKey(depth - 1) && collIsMap
					.get(depth - 1))) {
				addIndexPath();
			} else {
				addPath(pathFor(name, clazz));
			}
		} else if ((currNode == null) && ((mode & DROP_ROOT_MODE) == 1)) {
			addPath(pathFor(name, clazz));
		}
		elementStack.push(new Node(name, clazz));
		depth++;
	}

	@Override
	public HierarchicalStreamWriter underlyingWriter() {
		return this;
	}

	protected void writeAttributeValue(final QuickWriter writer,
			final String text) {
		writeText(text, null);
	}

	private void writePath() {
		qsFormatWriter.writePath(paths, writer);
	}

	protected void writeText(final QuickWriter writer, final String text) {
		final Node foo = (Node) elementStack.peek();

		writeText(text, foo.clazz);
	}

	private void writeText(final String text, final Class<?> clazz) {
		writePath();
		qsFormatWriter.writeValue(text, writer);
	}
}
