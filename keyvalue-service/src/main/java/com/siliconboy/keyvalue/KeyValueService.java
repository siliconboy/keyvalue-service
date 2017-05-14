package com.mytwitter.keyvalue.keyvalue;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import com.mytwitter.keyvalue.keyvalue.conf.KeyValueConfiguration;
import com.mytwitter.keyvalue.keyvalue.health.TemplateHealthCheck;
import com.mytwitter.keyvalue.keyvalue.resource.KeyValueResource;

public class KeyValueService extends Application<KeyValueConfiguration> {

    public static void main(String args[]) throws Exception {
        new KeyValueService().run(args);
    }

    @Override
    public void run(KeyValueConfiguration configuration,
            Environment environment) {

        final String template = configuration.getTemplate();
        final String defaultName = configuration.getDefaultName();
        environment.healthChecks().register("example health check", new TemplateHealthCheck(template));
        environment.jersey().register(new KeyValueResource());
    }

}
