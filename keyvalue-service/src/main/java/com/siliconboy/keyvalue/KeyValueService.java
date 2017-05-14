package com.siliconboy.keyvalue;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import com.siliconboy.keyvalue.conf.KeyValueConfiguration;
import com.siliconboy.keyvalue.health.TemplateHealthCheck;
import com.siliconboy.keyvalue.resource.KeyValueResource;

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
