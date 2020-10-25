/*
 * Copyright (c) Mak-Si Management Ltd. Varna, Bulgaria
 *
 * License: BSD 3-Clause license.
 * See the LICENSE.md file in the root directory or <https://opensource.org/licenses/BSD-3-Clause>.
 * See also <https://tldrlegal.com/license/bsd-3-clause-license-(revised)>.
 */
package com.mopano.hibernate.xml;

import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlDocumentType extends AbstractSingleColumnStandardBasicType<Document> {

	public static final XmlDocumentType INSTANCE = new XmlDocumentType();
	private static final long serialVersionUID = 3771177104096221706L;

	private final String[] regKeys;
	private final String name;

	public XmlDocumentType() {
		super(XmlSqlTypeDescriptor.INSTANCE, XmlDocumentTypeDescriptor.INSTANCE);
		ArrayList<String> classNames = new ArrayList<>();
		classNames.add(Document.class.getName());
		classNames.add(Element.class.getName());
		String implName;
		try {
			// get the full name of a document class
			// this works with the default provider. If you're using multiple, that's your problem
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			implName = doc.getClass().getName();
		}
		catch (ParserConfigurationException ex) {
			throw new RuntimeException("Default XML API provider is no good", ex);
		}
		regKeys = new String[]{
			Document.class.getName(),
			implName
		};
		name = "SqlXmlDocument";
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
