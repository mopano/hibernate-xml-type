/*
 * Copyright (c) Mak Ltd. Varna, Bulgaria
 * All rights reserved.
 *
 */
package com.mopano.hibernate.xml;

import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlElementType extends AbstractSingleColumnStandardBasicType<Element> {

	private static final long serialVersionUID = -3275328800446751533L;

	private final String[] regKeys;
	private final String name;

	public static final XmlElementType INSTANCE = new XmlElementType();

	public XmlElementType() {
		super(XmlSqlTypeDescriptor.INSTANCE, XmlElementTypeDescriptor.INSTANCE);
		ArrayList<String> classNames = new ArrayList<>();
		classNames.add(Element.class.getName());
		try {
			// get the full name of a document class
			// this works with the default provider. If you're using multiple, that's your problem
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			String simplename = doc.createElement("SimpleElement").getClass().getName();
			classNames.add(simplename);
			String spacedname = doc.createElementNS("http://example.com/ns", "SpacedElement").getClass().getName();
			if (!spacedname.equals(simplename)) {
				classNames.add(spacedname);
			}
		}
		catch (ParserConfigurationException ex) {
			throw new RuntimeException("Default XML API provider is no good", ex);
		}
		regKeys = classNames.toArray(new String[classNames.size()]);
		name = "SqlXmlElement";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String[] getRegistrationKeys() {
		return (String[]) regKeys.clone();
	}

	@Override
	protected boolean registerUnderJavaType() {
		return true;
	}

}
