package io.h2cone.bupf;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author h^2
 */
@RegisterForReflection
public class FollowerStats {
    public Long uid;

    public Integer followers;

    public Timestamp createdAt;

    public static FollowerStats create(Long uid, Integer followers) {
        FollowerStats stats = new FollowerStats();
        stats.uid = uid;
        stats.followers = followers;
        stats.createdAt = Timestamp.from(Instant.now());
        return stats;
    }
}
