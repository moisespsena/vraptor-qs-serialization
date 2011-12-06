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
package com.moisespsena.vraptor.qsserialization.serialization;

import java.io.IOException;

import br.com.caelum.vraptor.serialization.NoRootSerialization;
import br.com.caelum.vraptor.serialization.Serialization;
import br.com.caelum.vraptor.serialization.Serializer;

import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSFormatWriter;
import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSHierarchicalStreamDriver.WriterCreatedListener;

/**
 * Query String Serialization
 * 
 * @author Moises P. Sena &lt;moisespsena@gmail.com&gt;
 * @since 1.0 23/08/2011
 * 
 */
public interface QSSerialization extends Serialization {
	public QSSerialization arrayFormat();

	<T> Serializer from(T object, String alias, QSFormatWriter formatWriter);

	<T> Serializer from(T object, String alias,
			final WriterCreatedListener writerCreatedListener);

	<T> Serializer from(T object, String alias,
			final WriterCreatedListener writerCreatedListener,
			QSFormatWriter formatWriter);

	public QSSerialization objectFormat();

	public QSSerialization propertiesFormat();

	/**
	 * Exclude the root alias from serialization.
	 * 
	 * @since 1.0
	 * @throws IOException
	 */
	<T> NoRootSerialization withoutRoot();
}
