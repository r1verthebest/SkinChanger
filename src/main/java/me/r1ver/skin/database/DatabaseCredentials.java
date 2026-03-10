package me.r1ver.skin.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class DatabaseCredentials {

	private final HikariDataSource dataSource;

	public DatabaseCredentials(String host, int port, String database, String user, String password) {
		HikariConfig config = new HikariConfig();

		config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s", host, port, database));
		config.setUsername(user);
		config.setPassword(password);

		config.setMaximumPoolSize(10);
		config.setMinimumIdle(2);
		config.setIdleTimeout(TimeUnit.MINUTES.toMillis(10));
		config.setMaxLifetime(TimeUnit.MINUTES.toMillis(30));
		config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(5));

		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.addDataSourceProperty("useServerPrepStmts", "true");
		config.addDataSourceProperty("useLocalSessionState", "true");
		config.addDataSourceProperty("rewriteBatchedStatements", "true");
		config.addDataSourceProperty("cacheResultSetMetadata", "true");
		config.addDataSourceProperty("cacheServerConfiguration", "true");
		config.addDataSourceProperty("elideSetAutoCommits", "true");
		config.addDataSourceProperty("maintainTimeStats", "false");

		this.dataSource = new HikariDataSource(config);
	}

	public Connection getConnection() throws SQLException {
		if (dataSource == null || dataSource.isClosed()) {
			throw new SQLException("DataSource está fechado ou inacessível.");
		}
		return dataSource.getConnection();
	}

	public void shutdown() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}
}