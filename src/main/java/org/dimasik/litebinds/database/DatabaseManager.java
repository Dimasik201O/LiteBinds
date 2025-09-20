package org.dimasik.litebinds.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {
    private final HikariDataSource dataSource;

    public DatabaseManager(String host, int port, String user, String password, String database) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

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
        initializeDatabase();
    }

    private void initializeDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS player_actions (
                player VARCHAR(16) PRIMARY KEY,
                action_drop ENUM('NONE', 'SNOWBALL', 'JAKE', 'ALTERNATIVE_TRAP', 'STAN', 'TRAP', 'EXPLOSIVE', 'BACKPACK') NOT NULL DEFAULT 'NONE',
                action_swap ENUM('NONE', 'SNOWBALL', 'JAKE', 'ALTERNATIVE_TRAP', 'STAN', 'TRAP', 'EXPLOSIVE', 'BACKPACK') NOT NULL DEFAULT 'NONE',
                action_interact ENUM('NONE', 'SNOWBALL', 'JAKE', 'ALTERNATIVE_TRAP', 'STAN', 'TRAP', 'EXPLOSIVE', 'BACKPACK') NOT NULL DEFAULT 'NONE'
            )
            """;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public CompletableFuture<Void> savePlayerActions(PlayerActions playerActions) {
        return CompletableFuture.runAsync(() -> {
            String sql = """
                INSERT INTO player_actions (player, action_drop, action_swap, action_interact)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                action_drop = VALUES(action_drop),
                action_swap = VALUES(action_swap),
                action_interact = VALUES(action_interact)
                """;

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, playerActions.getPlayer());
                statement.setString(2, playerActions.getActionDrop().name());
                statement.setString(3, playerActions.getActionSwap().name());
                statement.setString(4, playerActions.getActionInteract().name());

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to save player actions", e);
            }
        });
    }

    public CompletableFuture<Optional<PlayerActions>> getPlayerActions(String player) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM player_actions WHERE player = ?";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, player);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        PlayerActions actions = new PlayerActions();
                        actions.setPlayer(resultSet.getString("player"));
                        actions.setActionDrop(ActionType.valueOf(resultSet.getString("action_drop")));
                        actions.setActionSwap(ActionType.valueOf(resultSet.getString("action_swap")));
                        actions.setActionInteract(ActionType.valueOf(resultSet.getString("action_interact")));

                        return Optional.of(actions);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get player actions", e);
            }

            return Optional.empty();
        });
    }

    public CompletableFuture<Void> deletePlayerActions(String player) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM player_actions WHERE player = ?";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, player);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to delete player actions", e);
            }
        });
    }

    public CompletableFuture<Boolean> playerExists(String player) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT COUNT(*) FROM player_actions WHERE player = ?";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, player);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to check if player exists", e);
            }

            return false;
        });
    }

    public CompletableFuture<Void> updateActionDrop(String player, ActionType actionDrop) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO player_actions (player, action_drop) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE action_drop = VALUES(action_drop)";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, player);
                statement.setString(2, actionDrop.name());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update action drop", e);
            }
        });
    }

    public CompletableFuture<Void> updateActionSwap(String player, ActionType actionSwap) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO player_actions (player, action_swap) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE action_swap = VALUES(action_swap)";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, player);
                statement.setString(2, actionSwap.name());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update action swap", e);
            }
        });
    }

    public CompletableFuture<Void> updateActionInteract(String player, ActionType actionInteract) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO player_actions (player, action_interact) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE action_interact = VALUES(action_interact)";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, player);
                statement.setString(2, actionInteract.name());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update action interact", e);
            }
        });
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}