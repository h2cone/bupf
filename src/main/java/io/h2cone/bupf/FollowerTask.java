package io.h2cone.bupf;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import io.vertx.core.json.Json;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author h^2
 */
@ApplicationScoped
public class FollowerTask {
    @ConfigProperty(name = "api.bilibili.relation.stat.url")
    String relationStatUrl;
    @ConfigProperty(name = "scheduled.syncFollowerStats.userIds")
    long[] userIds;

    @Inject
    @Named("localExecutor")
    ManagedExecutor localExecutor;
    @Inject
    FollowerStatsRepo followerStatsRepo;

    @Scheduled(cron = "{scheduled.syncFollowerStats.cron}")
    void syncFollowerStats() {
        putFollowerStats();
    }

    Map<Long, Integer> putFollowerStats() {
        Map<Long, Integer> uid2Count;
        List<CompletableFuture<Integer>> futures = new ArrayList<>(userIds.length);
        uid2Count = new HashMap<>(userIds.length);
        for (Long uid : userIds) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                int count = 0;
                JsonNode body = null;
                try {
                    HttpResponse<JsonNode> resp = Unirest.get(String.format(relationStatUrl, uid)).asJson();
                    if (!resp.isSuccess()) {
                        Log.errorf("failed to get follower stats for user %s, status: %d, text: %s", uid, resp.getStatus(), resp.getStatusText());
                        uid2Count.put(uid, count);
                        return count;
                    }
                    body = resp.getBody();
                    JSONObject root = body.getObject();
                    JSONObject data = root.getJSONObject("data");
                    int followers = data.getInt("follower");
                    FollowerStats stats = FollowerStats.create(uid, followers);
                    Log.infof("prepare to insert: %s", Json.encode(stats));
                    count = followerStatsRepo.save(stats);
                    uid2Count.put(uid, count);
                } catch (Exception e) {
                    Log.errorf(e, "failed to putFollowerStats, uid: %s, body: %s", uid, Objects.isNull(body) ? null : body.toString());
                }
                return count;
            }, localExecutor);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return uid2Count;
    }
}
