/*
 * Copyright (c) Mak-Si Management Ltd. Varna, Bulgaria
 *
 * License: BSD 3-Clause license.
 * See the LICENSE.md file in the root directory or <https://opensource.org/licenses/BSD-3-Clause>.
 * See also <https://tldrlegal.com/license/bsd-3-clause-license-(revised)>.
 */
package com.mopano.hibernate.xml;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BasicType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;

public class XmlTypeContributor implements TypeContributor {

	@Override
	public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		BasicType doctype, elemtype;
		JavaTypeDescriptor docjdesc, elemjdesc;
		try {
			doctype = XmlDocumentType.INSTANCE;
			docjdesc = XmlDocumentTypeDescriptor.INSTANCE;
			elemtype = XmlElementType.INSTANCE;
			elemjdesc = XmlElementTypeDescriptor.INSTANCE;
		}
		catch (Throwable t) {
			// Avoid logging system for a project so small
			System.err.println("XML type contribution failed! Message: " + t.getMessage());
			t.printStackTrace(System.err);
			return;
		}
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor(docjdesc);
		JavaTypeDescriptorRegistry.INSTANCE.addDescriptor(elemjdesc);
		typeContributions.contributeType(doctype);
		typeContributions.contributeType(elemtype);
	}

}
