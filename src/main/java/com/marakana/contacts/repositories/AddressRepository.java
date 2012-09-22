package com.marakana.contacts.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.marakana.contacts.entities.Address;

public class AddressRepository {
	private final DataSource ds;

	public AddressRepository() {
		try {
			Context context = new InitialContext();
			try {
				ds = (DataSource) context
						.lookup("java:comp/env/jdbc/trainingdb");
			} finally {
				context.close();
			}
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	public void init() throws SQLException {
		Connection connection = ds.getConnection();
		try {
			Statement statement = connection.createStatement();
			try {
				String sql = "create table address (id integer generated by default as identity primary key, street varchar(255), city varchar(255), state varchar(255), zip varchar(255))";
				statement.execute(sql);
			} finally {
				statement.close();
			}
		} finally {
			connection.close();
		}
	}

	public List<Address> findAll() throws SQLException {
		Connection connection = ds.getConnection();
		try {
			Statement statement = connection.createStatement();
			try {
				String sql = "select * from address";
				ResultSet results = statement.executeQuery(sql);
				try {
					List<Address> materialized = new ArrayList<Address>();
					while (results.next()) {
						materialized.add(unmarshal(results));
					}
					return materialized;
				} finally {
					results.close();
				}
			} finally {
				statement.close();
			}
		} finally {
			connection.close();
		}
	}

	public Address find(long id) throws SQLException {
		Connection connection = ds.getConnection();
		try {
			Statement statement = connection.createStatement();
			try {
				String sql = "select * from address where id = " + id;
				ResultSet results = statement.executeQuery(sql);
				try {
					if (!results.next()) {
						return null;
					} else {
						return unmarshal(results);
					}
				} finally {
					results.close();
				}
			} finally {
				statement.close();
			}
		} finally {
			connection.close();
		}
	}

	public void save(Address address) throws SQLException {
		Connection connection = ds.getConnection();
		try {
			Statement statement = connection.createStatement();
			try {
				String sql;
				if (address.getId() == null) {
					sql = "insert into address (street, city, state, zip) values ('"
							+ address.getStreet()
							+ "', '"
							+ address.getCity()
							+ "', '"
							+ address.getState()
							+ "', '"
							+ address.getZip() + "')";
				} else {
					sql = "update address set street='" + address.getStreet()
							+ "', city='" + address.getCity() + "', state='"
							+ address.getState() + "', zip='"
							+ address.getZip() + "' where id="
							+ address.getId();
				}
				statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				ResultSet keys = statement.getGeneratedKeys();
				if (keys.next()) {
					address.setId(keys.getLong(1));
				}
			} finally {
				statement.close();
			}
		} finally {
			connection.close();
		}
	}

	public void delete(Address address) throws SQLException {
		Connection connection = ds.getConnection();
		try {
			Statement statement = connection.createStatement();
			try {
				String sql = "delete from address where id="
						+ address.getId();
				statement.execute(sql);
			} finally {
				statement.close();
			}
		} finally {
			connection.close();
		}
	}

	private Address unmarshal(ResultSet results) throws SQLException {
		return new Address(results.getLong(1), results.getString(2),
				results.getString(3), results.getString(4),
				results.getString(5));
	}
}
