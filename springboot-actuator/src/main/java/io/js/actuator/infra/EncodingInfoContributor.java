package io.js.actuator.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;

public class EncodingInfoContributor implements InfoContributor {

    @Value("${project.build.sourceEncoding}")
    private String encoding;

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("encoding", encoding);
    }
}
