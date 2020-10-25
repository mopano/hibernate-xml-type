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
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlDocumentTypeDescriptor extends AbstractTypeDescriptor<Document> {

	public static final XmlDocumentTypeDescriptor INSTANCE = new XmlDocumentTypeDescriptor();
	private static final long serialVersionUID = -5702943275557871122L;

	private final DocumentBuilderFactory XMLFactory;
	private final TransformerFactory XMLtransformerFactory;

	public XmlDocumentTypeDescriptor() {
		super(Document.class);
		XMLFactory = DocumentBuilderFactory.newInstance();
		XMLtransformerFactory = TransformerFactory.newInstance();
		XMLFactory.setNamespaceAware(true);
		XMLFactory.setValidating(false);
	}

	@Override
	public String toString(Document value) {
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
	public Document fromString(String xmlstring) {
		try {
			DocumentBuilder builder = XMLFactory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xmlstring)));
		}
		catch (IOException | ParserConfigurationException | SAXException ex) {
			throw new RuntimeException("Could not read XML data. See nested exception.", ex);
		}
	}

	private Document fromStream(InputStream xmlstream) {
		try {
			DocumentBuilder builder = XMLFactory.newDocumentBuilder();
			return builder.parse(xmlstream);
		}
		catch (IOException | ParserConfigurationException | SAXException ex) {
			throw new RuntimeException("Could not read XML data. See nested exception.", ex);
		}
	}

	@Override
	public <X> X unwrap(Document value, Class<X> type, WrapperOptions options) {
		if (value == null) {
			return null;
		}

		if (String.class.isAssignableFrom(type)) {
			return (X) this.toString(value);
		}

		throw unknownUnwrap(type);
	}

	@Override
	public <X> Document wrap(X value, WrapperOptions options) {
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
