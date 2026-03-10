package me.r1ver.skin.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQL {

	private final HikariDataSource dataSource;
	private final Logger logger = Logger.getLogger("Minecraft");

	public MySQL(String user, String host, String database, String password, int port) {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
		config.setUsername(user);
		config.setPassword(password);

		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setMaximumPoolSize(10);

		this.dataSource = new HikariDataSource(config);
	}

	public CompletableFuture<Boolean> contains(UUID uuid) {
		return CompletableFuture.supplyAsync(() -> {
			String sql = "SELECT 1 FROM players_skin WHERE uuid = ? LIMIT 1";
			try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

				stmt.setString(1, uuid.toString());
				try (ResultSet rs = stmt.executeQuery()) {
					return rs.next();
				}
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Erro ao verificar existência de " + uuid, e);
				return false;
			}
		});
	}

	public CompletableFuture<Object> read(UUID uuid, String column) {
		return CompletableFuture.supplyAsync(() -> {
			String sql = "SELECT " + column + " FROM players_skin WHERE uuid = ?";
			try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

				stmt.setString(1, uuid.toString());
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next())
						return rs.getObject(column);
				}
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Erro ao ler coluna " + column + " de " + uuid, e);
			}
			return null;
		});
	}

	public void createTables() {
		CompletableFuture.runAsync(() -> {
			String sql = "CREATE TABLE IF NOT EXISTS players_skin (uuid VARCHAR(36) PRIMARY KEY, nick VARCHAR(16), skin TEXT)";
			try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.executeUpdate();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Erro ao criar tabelas", e);
			}
		});
	}

	public CompletableFuture<String> getSkin(UUID uuid) {
		return CompletableFuture.supplyAsync(() -> {
			String sql = "SELECT skin FROM players_skin WHERE uuid = ?";
			try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, uuid.toString());
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next())
						return rs.getString("skin");
				}
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Erro ao ler skin de " + uuid, e);
			}
			return null;
		});
	}

	public void updateSkin(UUID uuid, String skinValue) {
		CompletableFuture.runAsync(() -> {
			String sql = "INSERT INTO players_skin (uuid, skin) VALUES (?, ?) " + "ON DUPLICATE KEY UPDATE skin = ?";
			try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, uuid.toString());
				stmt.setString(2, skinValue);
				stmt.setString(3, skinValue);
				stmt.executeUpdate();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Erro ao atualizar skin de " + uuid, e);
			}
		});
	}

	public void close() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}
	
	public void deleteUser(UUID uuid) {
	    CompletableFuture.runAsync(() -> {
	        String sql = "DELETE FROM players_skin WHERE uuid = ?";
	        try (Connection conn = dataSource.getConnection(); 
	             PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, uuid.toString());
	            stmt.executeUpdate();
	        } catch (SQLException e) {
	            logger.log(Level.SEVERE, "Erro ao deletar usuário " + uuid, e);
	        }
	    });
	}
}