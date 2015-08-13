package com.nway.spring.jdbc.performance;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.Stoppable;

@SuppressWarnings("serial")
public class JndiConnectionProvider implements ConnectionProvider, Configurable, Stoppable {

	private DataSource dataSource;

	@Override
	public boolean isUnwrappableAs(Class unwrapType) {
		return ConnectionProvider.class.equals(unwrapType)
				|| DatasourceConnectionProviderImpl.class.isAssignableFrom(unwrapType)
				|| DataSource.class.isAssignableFrom(unwrapType);
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public <T> T unwrap(Class<T> unwrapType) {
		if (ConnectionProvider.class.equals(unwrapType)
				|| DatasourceConnectionProviderImpl.class.isAssignableFrom(unwrapType)) {
			return (T) this;
		} else if (DataSource.class.isAssignableFrom(unwrapType)) {
			return (T) this.dataSource;
		} else {
			throw new UnknownUnwrapTypeException(unwrapType);
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (this.dataSource == null) {
			throw new HibernateException("Provider is closed!");
		}
		return dataSource.getConnection();
	}

	@Override
	public void closeConnection(Connection connection) throws SQLException {
		connection.close();
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return true;
	}

	@Override
	public void configure(Map configurationValues) {

		try 
		{
			Context initCtx = new InitialContext();
			this.dataSource = (DataSource) initCtx.lookup((String) configurationValues.get("connection.datasource"));
		} 
		catch (NamingException e) {
			throw new HibernateException("Unable to lookup Datasource");
		}
		
		if ( this.dataSource == null ) {
			throw new HibernateException( "Unable to determine appropriate DataSource to use" );
		}
	}

	@Override
	public void stop() {

		this.dataSource = null;
	}

}
