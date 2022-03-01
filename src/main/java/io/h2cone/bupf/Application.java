package io.h2cone.bupf;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * @author h^2
 */
@QuarkusMain
public class Application {

    public static void main(String... args) {
        Quarkus.run(args);
    }
}
