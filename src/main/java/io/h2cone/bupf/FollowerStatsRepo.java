package io.h2cone.bupf;

import io.agroal.api.AgroalDataSource;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.sql.SQLException;

/**
 * @author h^2
 */
@ApplicationScoped
public class FollowerStatsRepo {
    @Inject
    AgroalDataSource dataSource;

    void onStart(@Observes StartupEvent event) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS bupf_fo_stats (id serial NOT NULL, uid bigint NOT NULL, followers bigint NOT NULL DEFAULT 0, created_at timestamptz NOT NULL DEFAULT now(), CONSTRAINT bupf_fo_stats_pk PRIMARY KEY (id))";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    int save(FollowerStats stats) throws SQLException {
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO bupf_fo_stats(uid, followers, created_at) VALUES (?, ?, ?)")) {
            stmt.setLong(1, stats.uid);
            stmt.setLong(2, stats.followers);
            stmt.setTimestamp(3, stats.createdAt);
            return stmt.executeUpdate();
        }
    }
}
