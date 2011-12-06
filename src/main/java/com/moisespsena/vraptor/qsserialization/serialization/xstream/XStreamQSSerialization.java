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
package com.moisespsena.vraptor.qsserialization.serialization.xstream;

import java.io.Writer;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.NoRootSerialization;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.serialization.xstream.VRaptorClassMapper;
import br.com.caelum.vraptor.serialization.xstream.XStreamSerializer;

import com.moisespsena.vraptor.qsserialization.serialization.QSSerialization;
import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSFormatWriter;
import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSHierarchicalStreamDriver;
import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSHierarchicalStreamDriver.WriterCreatedListener;
import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSWriter;
import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.format.ArrayQSFormatWriter;
import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.format.ObjectQSFormatWriter;
import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.format.PropertiesQSFormatWriter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * Implementação Padrao de {@link QSSerialization}
 * 
 * @author Moises P. Sena &lt;moisespsena@gmail.com&gt;
 * @since 1.0 23/08/2011
 * 
 */
public class XStreamQSSerialization implements QSSerialization {
	private HierarchicalStreamDriver driver;
	protected final TypeNameExtractor extractor;

	protected final ProxyInitializer initializer;

	private QSFormatWriter qsFormatWriter;

	private boolean withoutRoot;

	private final Writer writer;

	public XStreamQSSerialization(final Writer writer,
			final TypeNameExtractor extractor,
			final ProxyInitializer initializer) {
		this.extractor = extractor;
		this.initializer = initializer;
		this.writer = writer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.caelum.vraptor.serialization.Serialization#accepts(java.lang.String
	 * )
	 */
	@Override
	public boolean accepts(final String format) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.moisespsena.vraptor.qsserialization.serialization.QSSerialization
	 * #arrayFormat()
	 */
	@Override
	public XStreamQSSerialization arrayFormat() {
		qsFormatWriter = new ArrayQSFormatWriter();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.caelum.vraptor.serialization.NoRootSerialization#from(java.lang
	 * .Object)
	 */
	@Override
	public <T> Serializer from(final T object) {
		return from(object, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.caelum.vraptor.serialization.Serialization#from(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public <T> Serializer from(final T object, final String alias) {
		return from(object, alias, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.moisespsena.crater.vraptor.serialization.QSSerialization#from(java
	 * .lang.Object, java.lang.String,
	 * com.moisespsena.crater.vraptor.serialization
	 * .xstream.querystring.QSFormatWriter)
	 */
	@Override
	public <T> Serializer from(final T object, final String alias,
			final QSFormatWriter formatWriter) {
		return from(object, alias, null, formatWriter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.moisespsena.crater.serialization.QSSerialization#from(java.lang.Object
	 * , java.lang.String,
	 * com.moisespsena.crater.serialization.xstream.querystring
	 * .QSHierarchicalStreamDriver.WriterCreatedListener)
	 */
	@Override
	public <T> Serializer from(final T object, final String alias,
			final WriterCreatedListener writerCreatedListener) {
		return from(object, alias, writerCreatedListener, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.moisespsena.crater.vraptor.serialization.QSSerialization#from(java
	 * .lang.Object, java.lang.String,
	 * com.moisespsena.crater.vraptor.serialization
	 * .xstream.querystring.QSHierarchicalStreamDriver.WriterCreatedListener,
	 * com
	 * .moisespsena.crater.vraptor.serialization.xstream.querystring.QSFormatWriter
	 * )
	 */
	@Override
	public <T> Serializer from(final T object, final String alias,
			final WriterCreatedListener writerCreatedListener,
			QSFormatWriter formatWriter) {
		if (qsFormatWriter == null) {
			objectFormat();
		}

		if (formatWriter == null) {
			formatWriter = this.qsFormatWriter;
		}

		return fromInternal(object, alias, writerCreatedListener, formatWriter);
	}

	private <T> Serializer fromInternal(final T object, final String alias,
			final WriterCreatedListener writerCreatedListener,
			final QSFormatWriter formatWriter) {

		driver = new QSHierarchicalStreamDriver() {
			@Override
			public HierarchicalStreamWriter createWriter(final Writer writer) {
				QSWriter qsWriter;
				if (withoutRoot) {
					qsWriter = new QSWriter(writer, QSWriter.DROP_ROOT_MODE,
							formatWriter);
				} else {
					qsWriter = new QSWriter(writer, formatWriter);
				}

				postWriterCreated(qsWriter, writerCreatedListener);
				return qsWriter;
			}
		};

		return getSerializer().from(object, alias);
	}

	/**
	 * You can override this method for configuring Driver before serialization
	 */
	protected HierarchicalStreamDriver getHierarchicalStreamDriver() {
		return driver;
	}

	protected SerializerBuilder getSerializer() {
		return new XStreamSerializer(getXStream(), writer, extractor,
				initializer);
	}

	/**
	 * You can override this method for configuring XStream before serialization
	 */
	protected XStream getXStream() {
		final XStream xStream = new XStream(getHierarchicalStreamDriver()) {
			{
				setMode(NO_REFERENCES);
			}

			@Override
			protected MapperWrapper wrapMapper(final MapperWrapper next) {
				return new VRaptorClassMapper(next, extractor);
			}
		};

		return xStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.moisespsena.vraptor.qsserialization.serialization.QSSerialization
	 * #objectFormat()
	 */
	@Override
	public XStreamQSSerialization objectFormat() {
		qsFormatWriter = new ObjectQSFormatWriter();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.moisespsena.vraptor.qsserialization.serialization.QSSerialization
	 * #propertiesFormat()
	 */
	@Override
	public XStreamQSSerialization propertiesFormat() {
		qsFormatWriter = new PropertiesQSFormatWriter();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.moisespsena.crater.serialization.QSSerialization#withoutRoot()
	 */
	@Override
	public <T> NoRootSerialization withoutRoot() {
		withoutRoot = true;
		return this;
	}
}
