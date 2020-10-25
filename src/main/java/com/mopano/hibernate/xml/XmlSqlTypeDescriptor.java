/*
 * Copyright (c) Mak-Si Management Ltd. Varna, Bulgaria
 *
 * License: BSD 3-Clause license.
 * See the LICENSE.md file in the root directory or <https://opensource.org/licenses/BSD-3-Clause>.
 * See also <https://tldrlegal.com/license/bsd-3-clause-license-(revised)>.
 */
package com.mopano.hibernate.xml;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Types;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class XmlSqlTypeDescriptor implements SqlTypeDescriptor {

	public static final XmlSqlTypeDescriptor INSTANCE = new XmlSqlTypeDescriptor();
	private static final long serialVersionUID = -5970769752292197312L;

	@Override
	public int getSqlType() {
		return Types.SQLXML;
	}

	@Override
	public boolean canBeRemapped() {
		return false;
	}

	@Override
	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
		return new BasicBinder<X>(javaTypeDescriptor, this) {

			@Override
			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
					throws SQLException {
				if (value == null) {
					st.setNull(index, Types.SQLXML);
					return;
				}
				SQLXML sqlxml = st.getConnection().createSQLXML();
				final String printed = javaTypeDescriptor.unwrap(value, String.class, options);
				sqlxml.setString(printed);
				st.setSQLXML(index, sqlxml);
			}

			@Override
			protected void doBind(CallableStatement st, X value, String name, WrapperOptions options)
					throws SQLException {
				if (value == null) {
					st.setNull(name, Types.SQLXML);
					return;
				}
				SQLXML sqlxml = st.getConnection().createSQLXML();
				final String printed = javaTypeDescriptor.unwrap(value, String.class, options);
				sqlxml.setString(printed);
				st.setSQLXML(name, sqlxml);
			}
		};
	}

	@Override
	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
		return new BasicExtractor<X>(javaTypeDescriptor, this) {
			@Override
			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
				SQLXML sqlxml = rs.getSQLXML(name);
				if (sqlxml == null) {
					return null;
				}
				return javaTypeDescriptor.wrap(sqlxml.getString(), options);
			}

			@Override
			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
				SQLXML sqlxml = statement.getSQLXML(index);
				if (sqlxml == null) {
					return null;
				}
				return javaTypeDescriptor.wrap(sqlxml.getString(), options);
			}

			@Override
			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
				SQLXML sqlxml = statement.getSQLXML(name);
				if (sqlxml == null) {
					return null;
				}
				return javaTypeDescriptor.wrap(sqlxml.getString(), options);
			}
		};
	}

}
