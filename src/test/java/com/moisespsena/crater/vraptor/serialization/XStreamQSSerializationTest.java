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
package com.moisespsena.crater.vraptor.serialization;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.serialization.NullProxyInitializer;

import com.moisespsena.vraptor.qsserialization.serialization.xstream.XStreamQSSerialization;
import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSWriter;
import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSHierarchicalStreamDriver.WriterCreatedListener;
import com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSWriter.PathListener;

/**
 * 
 * @author Moises P. Sena &lt;moisespsena@gmail.com&gt;
 * @since 1.0 23/08/2011
 * 
 */
public class XStreamQSSerializationTest {
	public class Product {
		private Map<Long, Integer> data;
		private String name;
		private Product old;
		private List<Product> users;

		public Product() {

		}

		public Product(final String name) {
			this.name = name;
		}

		public Map<Long, Integer> getData() {
			return data;
		}

		public String getName() {
			return name;
		}

		public Product getOld() {
			return old;
		}

		public List<Product> getUsers() {
			return users;
		}

		public void setData(final Map<Long, Integer> data) {
			this.data = data;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public void setOld(final Product old) {
			this.old = old;
		}

		public void setUsers(final List<Product> users) {
			this.users = users;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.moisespsena.vraptor.qsserialization.serialization.xstream.XStreamQSSerialization#from(java.lang.Object, java.lang.String)}
	 * .
	 */
	@Test
	public void testFromComplexPropertiesFormat() {
		final Writer writer = new StringWriter();

		final XStreamQSSerialization serialization = new XStreamQSSerialization(
				writer, new DefaultTypeNameExtractor(),
				new NullProxyInitializer());

		final Product product = new Product("Goiaba");
		final List<Product> users = new ArrayList<Product>();
		final Product p2 = new Product("mamao");
		users.add(p2);
		users.add(new Product("abacaxi"));
		users.add(new Product("ameixa"));

		final List<Product> users2 = new ArrayList<Product>();
		users2.add(new Product("feijao"));
		users2.add(new Product("angu"));

		p2.setUsers(users2);

		product.setUsers(users);

		final Map<Long, Integer> data = new HashMap<Long, Integer>();
		data.put(1L, 2);
		data.put(2L, 4);

		product.setData(data);

		serialization.propertiesFormat();
		serialization.from(product, "produto").recursive().serialize();

		final String result = writer.toString();

		final String expectedResult = "produto[0][0][0]=1\n"
				+ "produto[0][0][1]=2\n" + "produto[0][1][0]=2\n"
				+ "produto[0][1][1]=4\n" + "produto.name=Goiaba\n"
				+ "produto.users[0].name=mamao\n"
				+ "produto.users[0].users[0].name=feijao\n"
				+ "produto.users[0].users[1].name=angu\n"
				+ "produto.users[1].name=abacaxi\n"
				+ "produto.users[2].name=ameixa\n";

		Assert.assertEquals(expectedResult, result);
	}

	/**
	 * Test method for
	 * {@link com.moisespsena.vraptor.qsserialization.serialization.xstream.XStreamQSSerialization#from(java.lang.Object, java.lang.String)}
	 * .
	 */
	@Test
	public void testWithArrayFormat() {
		final Writer writer = new StringWriter();
		final Product product = new Product("Goiaba");
		product.setOld(new Product("uva"));
		final XStreamQSSerialization serialization = new XStreamQSSerialization(
				writer, new DefaultTypeNameExtractor(),
				new NullProxyInitializer());

		serialization.arrayFormat();
		serialization.from(product).recursive().serialize();

		final String result = writer.toString();
		final String expectedResult = "product[name]=Goiaba&product[old][name]=uva&";

		Assert.assertEquals(expectedResult, result);
	}

	/**
	 * Test method for
	 * {@link com.moisespsena.vraptor.qsserialization.serialization.xstream.XStreamQSSerialization#from(java.lang.Object, java.lang.String)}
	 * .
	 */
	@Test
	public void testWithPropertiesFormat() {
		final Writer writer = new StringWriter();
		final Product product = new Product("Goiaba");
		product.setOld(new Product("uva"));
		final XStreamQSSerialization serialization = new XStreamQSSerialization(
				writer, new DefaultTypeNameExtractor(),
				new NullProxyInitializer());

		serialization.propertiesFormat();
		serialization.from(product).recursive().serialize();

		final String result = writer.toString();
		final String expectedResult = "product.name=Goiaba\nproduct.old.name=uva\n";

		Assert.assertEquals(expectedResult, result);
	}

	/**
	 * Test method for
	 * {@link com.moisespsena.vraptor.qsserialization.serialization.xstream.XStreamQSSerialization#from(java.lang.Object, java.lang.String)}
	 * .
	 */
	@Test
	public void testWithPropertiesFormatComplexKey() {
		final Writer writer = new StringWriter();
		final Product product = new Product("Goiaba");
		product.setOld(new Product("uva"));
		final XStreamQSSerialization serialization = new XStreamQSSerialization(
				writer, new DefaultTypeNameExtractor(),
				new NullProxyInitializer());

		serialization.propertiesFormat();
		serialization.from(product, "name spaced").recursive().serialize();

		final String result = writer.toString();
		final String expectedResult = "name\\ spaced.name=Goiaba\nname\\ spaced.old.name=uva\n";

		Assert.assertEquals(expectedResult, result);
	}

	/**
	 * Test method for
	 * {@link com.moisespsena.vraptor.qsserialization.serialization.xstream.XStreamQSSerialization#from(java.lang.Object, java.lang.String)}
	 * .
	 */
	@Test
	public void testWithPropertiesFormatComplexValue() {
		final Writer writer = new StringWriter();
		final Product product = new Product("Goiaba\nda Amazonia");
		product.setOld(new Product("uva"));
		final XStreamQSSerialization serialization = new XStreamQSSerialization(
				writer, new DefaultTypeNameExtractor(),
				new NullProxyInitializer());

		serialization.propertiesFormat();
		serialization.from(product).recursive().serialize();

		final String result = writer.toString();
		final String expectedResult = "product.name=Goiaba\\\n\tda Amazonia\nproduct.old.name=uva\n";

		Assert.assertEquals(expectedResult, result);
	}

	/**
	 * Test method for
	 * {@link com.moisespsena.vraptor.qsserialization.serialization.xstream.XStreamQSSerialization#from(java.lang.Object, java.lang.String, com.moisespsena.vraptor.qsserialization.serialization.xstream.querystring.QSHierarchicalStreamDriver.WriterCreatedListener)}
	 * .
	 */
	@Test
	public void testWithWriterCreatedListener() {
		final Writer writer = new StringWriter();
		final Product product = new Product("Goiaba");
		final XStreamQSSerialization serialization = new XStreamQSSerialization(
				writer, new DefaultTypeNameExtractor(),
				new NullProxyInitializer());

		serialization.from(product, "__cutomAlias",
				new WriterCreatedListener() {
					@Override
					public void postWriterCreated(final QSWriter writer) {
						writer.addPathListener(new PathListener() {
							@Override
							public boolean accepts(final int depth) {
								return depth == 1;
							}

							@Override
							public String pathFromDepth(
									final String currentName, final int depth,
									final Class<?> clazz) {
								if ("name".equals(currentName)) {
									return "_name_modified";
								}
								return null;
							}
						});
					}
				}).serialize();

		final String result = writer.toString();
		final String expectedResult = "__cutomAlias._name_modified=Goiaba&";

		Assert.assertEquals(expectedResult, result);
	}
}
