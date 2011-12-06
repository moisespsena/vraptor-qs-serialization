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
package com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.format;

import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSWriter.Path;
import com.thoughtworks.xstream.core.util.QuickWriter;

/**
 * Formatação no estilo array
 * 
 * <p>
 * Formata os valores da Query String em forma de array
 * </p>
 * <p>
 * Considere a Classe:
 * </p>
 * 
 * <pre>
 * <code>puclic class User {
 * 	private String name = "MyName";
 * 	private String lastName = "TheLastName";
 * 
 * 	// getters and setters ...
 * }</code>
 * </pre>
 * 
 * <p>
 * Apos a serializacao, tera resultado semelhante a:
 * </p>
 * 
 * <pre>
 * <code>user[name]=MyName&amp;user[lastName]=TheLastName</code>
 * </pre>
 * 
 * @author Moises P. Sena &lt;moisespsena@gmail.com&gt;
 * @since 1.0 23/08/2011
 * 
 */
public class ArrayQSFormatWriter extends AbstractQSFormatWriter {

	public ArrayQSFormatWriter() {
		super();
	}

	public ArrayQSFormatWriter(final char[] varSeparator) {
		super(varSeparator);
	}

	public ArrayQSFormatWriter(final String encoding) {
		super(encoding);
	}

	public ArrayQSFormatWriter(final String encoding, final char[] varSeparator) {
		super(encoding, varSeparator);
	}

	@Override
	protected void writePathEntryBegin(final Path path,
			final QuickWriter writer, final boolean hasMore,
			final boolean isFirst) {
		if (!isFirst) {
			writer.write('[');
		}
	}

	@Override
	protected void writePathEntryEnd(final Path path, final QuickWriter writer,
			final boolean hasMore, final boolean isFirst) {
		if (!isFirst) {
			writer.write(']');
		}
	}

}
