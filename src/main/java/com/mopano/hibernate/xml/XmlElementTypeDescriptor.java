/*
 * Copyright (c) Mak-Si Management Ltd. Varna, Bulgaria
 *
 * License: BSD 3-Clause license.
 * See the LICENSE.md file in the root directory or <https://opensource.org/licenses/BSD-3-Clause>.
 * See also <https://tldrlegal.com/license/bsd-3-clause-license-(revised)>.
 */
package com.mopano.hibernate.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlElementTypeDescriptor extends AbstractTypeDescriptor<Element> {

	public static final XmlElementTypeDescriptor INSTANCE = new XmlElementTypeDescriptor();
	private static final long serialVersionUID = 2512931794752093025L;

	private final DocumentBuilderFactory XMLFactory;
	private final TransformerFactory XMLtransformerFactory;

	public XmlElementTypeDescriptor() {
		super(Element.class);
		XMLFactory = DocumentBuilderFactory.newInstance();
		XMLtransformerFactory = TransformerFactory.newInstance();
		XMLFactory.setNamespaceAware(true);
		XMLFactory.setValidating(false);
	}

	@Override
	public String toString(Element value) {
		try {
			Transformer transformer = XMLtransformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			StringWriter sw = new StringWriter();
			transformer.transform(new DOMSource(value), new StreamResult(sw));
			return sw.toString();
		}
		catch (TransformerException ex) {
			throw new RuntimeException("XML Transformer could not process your document", ex);
		}
	}

	@Override
	public Element fromString(String xmlstring) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xmlstring))).getDocumentElement();
		}
		catch (IOException | ParserConfigurationException | SAXException ex) {
			throw new RuntimeException("Could not read XML data. See nested exception.", ex);
		}
	}

	private Element fromStream(InputStream xmlstream) {
		try {
			DocumentBuilder builder = XMLFactory.newDocumentBuilder();
			return builder.parse(xmlstream).getDocumentElement();
		}
		catch (IOException | ParserConfigurationException | SAXException ex) {
			throw new RuntimeException("Could not read XML data. See nested exception.", ex);
		}
	}

	@Override
	public <X> X unwrap(Element value, Class<X> type, WrapperOptions options) {
		if (value == null) {
			return null;
		}

		if (String.class.isAssignableFrom(type)) {
			return (X) this.toString(value);
		}

		throw unknownUnwrap(type);
	}

	@Override
	public <X> Element wrap(X value, WrapperOptions options) {
		if (value == null) {
			return null;
		}

		Class type = value.getClass();

		if (String.class.isAssignableFrom(type)) {
			return fromString((String) value);
		}

		if (InputStream.class.isAssignableFrom(type)) {
			return fromStream((InputStream) value);
		}

		throw unknownWrap(type);
	}

}
