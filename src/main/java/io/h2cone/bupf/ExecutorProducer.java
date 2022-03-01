package io.h2cone.bupf;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.StringJoiner;

/**
 * @author h^2
 */
@Singleton
public class ExecutorProducer {
    @ConfigProperty(name = "localExecutor.maxAsync")
    int maxAsync;
    @ConfigProperty(name = "localExecutor.maxQueued")
    int maxQueued;

    @Startup
    @Produces
    @Singleton
    @Named("localExecutor")
    ManagedExecutor produce() {
        Log.infof("creating %s", this);
        return ManagedExecutor.builder()
                .maxAsync(maxAsync)
                .maxQueued(maxQueued)
                .build();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExecutorProducer.class.getSimpleName() + "[", "]")
                .add("maxAsync=" + maxAsync)
                .add("maxQueued=" + maxQueued)
                .toString();
    }
}
