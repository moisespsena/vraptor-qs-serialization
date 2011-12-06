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
/**
 * 
 */
package com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.format;

import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang.SerializationException;

import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSFormatWriter;
import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSWriter.Path;
import com.thoughtworks.xstream.core.util.QuickWriter;

/**
 * Abstração para a incorporação de implementação possivelmente comum nas
 * implementações de {@link com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSFormatWriter}
 * 
 * @author Moises P. Sena &lt;moisespsena@gmail.com&gt;
 * @since 1.0 23/08/2011
 * 
 */
public abstract class AbstractQSFormatWriter implements QSFormatWriter {
	/**
	 * Codificação de carecters DEFAULT para
	 * {@link URLEncoder#encode(String, String)}
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * Separa a chave do valor com ":"
	 */
	public static final char[] KV_2POINTS_SEPARATOR = new char[] { ':' };

	/**
	 * Separa a chave do valor com "="
	 */
	public static final char[] KV_EQUALS_SEPARATOR = new char[] { '=' };
	/**
	 * Separa as variaveis com o "&"
	 */
	public static final char[] VAR_EAMP = new char[] { '&' };

	/**
	 * Separa as variaveis com uma quebra de linha
	 */
	public static final char[] VAR_NEW_LINE_SEPARATOR = new char[] { '\n' };

	/**
	 * Codificação de carecters para {@link URLEncoder#encode(String, String)}
	 */
	private final String encoding;

	private final char[] keyValueSeparator;

	private final char[] varSeparator;

	/**
	 * Constructor.
	 * 
	 * Definine {@link #VAR_EAMP} como separador de variáveis e
	 * {@link #DEFAULT_ENCODING} como codificação dos caracters.
	 */
	public AbstractQSFormatWriter() {
		this(VAR_EAMP);
	}

	/**
	 * Constructor.
	 * 
	 * Definine {@link #DEFAULT_ENCODING} como codificação dos caracters
	 * 
	 * @param varSeparator
	 *            Separador das variaveis
	 */
	public AbstractQSFormatWriter(final char[] varSeparator) {
		this(DEFAULT_ENCODING, varSeparator);
	}

	/**
	 * Constructor.
	 * 
	 * Definine {@link #VAR_EAMP} como separador de variáveis
	 * 
	 * @param encoding
	 *            Codificação de carecters para
	 *            {@link URLEncoder#encode(String, String)}
	 */
	public AbstractQSFormatWriter(final String encoding) {
		this(encoding, VAR_EAMP);
	}

	public AbstractQSFormatWriter(final String encoding,
			final char[] varSeparator) {
		this(encoding, varSeparator, KV_EQUALS_SEPARATOR);
	}

	/**
	 * @param encoding
	 *            Codificação de carecters para
	 *            {@link URLEncoder#encode(String, String)}
	 * @param varSeparator
	 *            Separador das variaveis
	 */
	public AbstractQSFormatWriter(final String encoding,
			final char[] varSeparator, final char[] keyValueSeparator) {
		this.encoding = encoding;
		this.varSeparator = varSeparator;
		this.keyValueSeparator = keyValueSeparator;
	}

	protected String formatedValue(final String value) throws Exception {
		if (value.length() > 0) {
			return URLEncoder.encode(value, encoding);
		} else {
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.moisespsena.crater.serialization.xstream.querystring.QSFormatWriter
	 * #writePath(java.util.List,
	 * com.thoughtworks.xstream.core.util.QuickWriter)
	 */
	@Override
	public void writePath(final List<Path> paths, final QuickWriter writer) {
		final int s = paths.size();
		if (s == 0) {
			return;
		}

		writePathInternal(paths.get(0), writer, s > 1, true);
		for (int i = 1; i < s; i++) {
			final Path path = paths.get(i);
			final boolean hasMore = (i - 1) < s;
			writePathIntersection(path, writer);
			writePathInternal(path, writer, hasMore, false);
		}
		writer.write(keyValueSeparator);
	}

	protected void writePathEntry(final Path path, final QuickWriter writer,
			final boolean hasMore, final boolean isFirst) {
		writer.write(path.getPath());
	}

	protected void writePathEntryBegin(final Path path,
			final QuickWriter writer, final boolean hasMore,
			final boolean isFirst) {

		if (path.isIndex()) {
			writer.write('[');
		}
	}

	protected void writePathEntryEnd(final Path path, final QuickWriter writer,
			final boolean hasMore, final boolean isFirst) {
		if (path.isIndex()) {
			writer.write(']');
		}
	}

	private void writePathInternal(final Path path, final QuickWriter writer,
			final boolean hasMore, final boolean isFirst) {
		writePathEntryBegin(path, writer, hasMore, isFirst);
		writePathEntry(path, writer, hasMore, isFirst);
		writePathEntryEnd(path, writer, hasMore, isFirst);
	}

	protected void writePathIntersection(final Path path,
			final QuickWriter writer) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.moisespsena.crater.serialization.xstream.querystring.QSFormatWriter
	 * #writeValue(java.lang.String,
	 * com.thoughtworks.xstream.core.util.QuickWriter)
	 */
	@Override
	public void writeValue(final String value, final QuickWriter writer) {
		try {
			final String formatedValue = formatedValue(value);
			writer.write(formatedValue);
			writer.write(varSeparator);
		} catch (final Exception e) {
			throw new SerializationException(e);
		}
	}
}
