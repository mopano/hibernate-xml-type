/*
 * Copyright (c) Mak Ltd. Varna, Bulgaria
 * All rights reserved.
 *
 */
package com.mopano.hibernate.test;

import java.io.StringReader;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.xml.parsers.DocumentBuilder;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import static org.junit.Assert.*;

public class XmlContributionTest {

	private static EntityManagerFactory emf;
	private static DocumentBuilderFactory XMLFactory;
	private static TransformerFactory XMLtransformerFactory;
	private static final String documentString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<Request Date=\"21122016\" Mode=\"Test\" Time=\"101011\">"
			+ "<Avl ServiceType=\"T\">"
			+ "<StartDate>26062017</StartDate>"
			+ "<Adults>1</Adults>"
			+ "</Avl>"
			+ "<Person ID=\"1\">"
			+ "<PersonType>M</PersonType>"
			+ "<Name>Testovych</Name>"
			+ "<FirstName>Test</FirstName>"
			+ "<Birth>23051990</Birth>"
			+ "</Person>"
			+ "</Request>";
	private static final String elementString = "<Person ID=\"1\">"
			+ "<PersonType>M</PersonType>"
			+ "<FirstName>Test</FirstName>"
			+ "<Name>Testovych</Name>"
			+ "<Birth>23051990</Birth>"
			+ "</Person>";

	private static final Logger LOGGER = LogManager.getLogger(XmlContributionTest.class);

	@BeforeClass
	public static void setupJPA() {
		emf = Persistence.createEntityManagerFactory("com.mopano.hibernate");
		XMLFactory = DocumentBuilderFactory.newInstance();
		XMLtransformerFactory = TransformerFactory.newInstance();
		XMLFactory.setNamespaceAware(true);
		XMLFactory.setValidating(false);
	}

	@AfterClass
	public static void closeJPA() {
		emf.close();
	}

	@Test
	public void testWriteRead() throws Exception {

		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();

		DocumentBuilder builder = XMLFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(documentString)));
		Element elem = builder.parse(new InputSource(new StringReader(elementString))).getDocumentElement();

		try {
			MyEntity entity = new MyEntity();
			entity.id = 1l;
			entity.doc = doc;
			entity.elm = elem;
			LOGGER.info("Persisting entity: " + entity);
			em.persist(entity);
			entity = new MyEntity();
			entity.id = 2l;
			// leave nulls because postgres is being shitty with those
			LOGGER.info("Persisting entity: " + entity);
			em.persist(entity);
			em.getTransaction().commit();
		}
		finally {
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}

		em = emf.createEntityManager();
		em.getTransaction().begin();

		// force new instances
		doc = builder.parse(new InputSource(new StringReader(documentString)));
		elem = builder.parse(new InputSource(new StringReader(elementString))).getDocumentElement();

		try {
			MyEntity entity1 = new MyEntity();
			entity1.id = 1l;
			entity1.doc = doc;
			entity1.elm = elem;
			MyEntity entity2 = new MyEntity();
			entity2.id = 2l;
			MyEntity me1 = em.find(MyEntity.class, new Long(1));
			LOGGER.info("Extracted entity: " + me1);
			MyEntity me2 = em.find(MyEntity.class, new Long(2));
			LOGGER.info("Extracted entity: " + me2);
			assertEquals(entity1, me1);
			assertEquals(entity2, me2);
		}
		finally {
			if (em.getTransaction() != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}
	}

	@Entity
	@Table(name = "xml_entity")
	public static class MyEntity {

		@Id
		public Long id;
		@Column(columnDefinition = "xml")
		public Document doc;
		@Column(columnDefinition = "xml")
		public Element elm;

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof MyEntity)) {
				return false;
			}
			MyEntity it = (MyEntity) other;
			// assuming (possibly incorrectly) that the implementations take care of their equals() properly
			return Objects.equals(id, it.id)
					&& (doc == it.doc || Objects.equals(String.valueOf(doc), String.valueOf(it.doc)))
					&& (elm == it.elm || Objects.equals(String.valueOf(elm), String.valueOf(it.elm)));
		}

		@Override
		public int hashCode() {
			// assuming (possibly incorrectly) that the implementations take care of their hashCode() properly
			return Objects.hash(id, doc, elm);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append('[')
					.append("id = ")
					.append(id)
					.append(", doc = ")
					.append(doc)
					.append(", elm = ")
					.append(elm)
					.append(']');
			return sb.toString();
		}
	}
}
