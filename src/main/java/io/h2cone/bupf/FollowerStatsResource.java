package io.h2cone.bupf;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Map;

/**
 * @author h^2
 */
@Path("/followerStats")
public class FollowerStatsResource {
    @Inject
    FollowerTask followerTask;

    @POST
    @Path("/sync")
    public Map<Long, Integer> syncFollowers() {
        return followerTask.putFollowerStats();
    }
}
