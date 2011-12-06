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
package com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Query String Stream Driver Hirárquico
 * 
 * @author Moises P. Sena &lt;moisespsena@gmail.com&gt;
 * @since 1.0 23/08/2011
 * 
 */
public class QSHierarchicalStreamDriver implements HierarchicalStreamDriver {

	public interface WriterCreatedListener {
		void postWriterCreated(QSWriter writer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.io.HierarchicalStreamDriver#createReader(java
	 * .io.File)
	 */
	@Override
	public HierarchicalStreamReader createReader(final File in) {
		throw new UnsupportedOperationException(
				"The JsonHierarchicalStreamDriver can only write JSON");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.io.HierarchicalStreamDriver#createReader(java
	 * .io.InputStream)
	 */
	@Override
	public HierarchicalStreamReader createReader(final InputStream in) {
		throw new UnsupportedOperationException(
				"The JsonHierarchicalStreamDriver can only write JSON");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.io.HierarchicalStreamDriver#createReader(java
	 * .io.Reader)
	 */
	@Override
	public HierarchicalStreamReader createReader(final Reader in) {
		throw new UnsupportedOperationException(
				"The JsonHierarchicalStreamDriver can only write JSON");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.io.HierarchicalStreamDriver#createReader(java
	 * .net.URL)
	 */
	@Override
	public HierarchicalStreamReader createReader(final URL in) {
		throw new UnsupportedOperationException(
				"The JsonHierarchicalStreamDriver can only write JSON");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.io.HierarchicalStreamDriver#createWriter(java
	 * .io.OutputStream)
	 */
	@Override
	public HierarchicalStreamWriter createWriter(final OutputStream out) {
		return createWriter(out, null);
	}

	/**
	 * Cria o writer e executa um callback
	 * 
	 * @param out
	 *            The OutPut
	 * @param writerCreatedListener
	 *            The Callback
	 */
	public HierarchicalStreamWriter createWriter(final OutputStream out,
			final WriterCreatedListener writerCreatedListener) {
		final QSWriter writer = new QSWriter(new OutputStreamWriter(out));
		postWriterCreated(writer, writerCreatedListener);
		return writer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.io.HierarchicalStreamDriver#createWriter(java
	 * .io.Writer)
	 */
	@Override
	public HierarchicalStreamWriter createWriter(final Writer out) {
		return createWriter(out, null);
	}

	/**
	 * Cria o writer e executa um callback
	 * 
	 * @param out
	 *            The OutPut
	 * @param writerCreatedListener
	 *            The Callback
	 */
	public HierarchicalStreamWriter createWriter(final Writer out,
			final WriterCreatedListener writerCreatedListener) {
		final QSWriter writer = new QSWriter(out);
		postWriterCreated(writer, writerCreatedListener);
		return writer;
	}

	/**
	 * Executa a callback
	 * 
	 * @param writer
	 * @param writerCreatedListener
	 */
	protected void postWriterCreated(final QSWriter writer,
			final WriterCreatedListener writerCreatedListener) {
		if (writerCreatedListener != null) {
			writerCreatedListener.postWriterCreated(writer);
		}
	}

}
