package com.siliconboy.keyvalue.health;

import com.codahale.metrics.health.HealthCheck;

public class TemplateHealthCheck extends HealthCheck {
    private final String template;

    public TemplateHealthCheck(String template) {
        this.template = template;
    }

    @Override
    protected Result check() throws Exception {
        final String testcase = String.format(template, "TESTER");
        if (!testcase.contains("TESTER")) {
            return Result.unhealthy("template doesn't include a name");
        }
        return Result.healthy();
    }

}
