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

import java.util.List;

import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSWriter.Path;
import com.thoughtworks.xstream.core.util.QuickWriter;

/**
 * Escritor em Formação
 * 
 * @author Moises P. Sena &lt;moisespsena@gmail.com&gt;
 * @since 1.0 23/08/2011
 * 
 */
public interface QSFormatWriter {
	/**
	 * Grava o caminho formatado, ou seja, o nome da variavel Query String
	 * 
	 * @param paths
	 * @param writer
	 */
	public void writePath(List<Path> paths, QuickWriter writer);

	/**
	 * Grava o valor formatado
	 * 
	 * @param value
	 * @param writer
	 */
	public void writeValue(String value, QuickWriter writer);
}
