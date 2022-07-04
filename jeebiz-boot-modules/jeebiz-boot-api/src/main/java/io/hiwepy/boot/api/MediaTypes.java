package io.hiwepy.boot.api;

import org.springframework.http.MediaType;

public class MediaTypes {

    /**
     * Public constant media type for {@code application/vnd.spring-boot.actuator.v2+json}.
     */
    public static final MediaType APPLICATION_ACTUATOR2_JSON;

    /**
     * A String equivalent of {@link MediaTypes#APPLICATION_ACTUATOR3_JSON}.
     */
    public static final String APPLICATION_ACTUATOR2_JSON_VALUE = "application/vnd.spring-boot.actuator.v2+json";

    /**
     * Public constant media type for {@code application/vnd.spring-boot.actuator.v3+json}.
     */
    public static final MediaType APPLICATION_ACTUATOR3_JSON;

    /**
     * A String equivalent of {@link MediaTypes#APPLICATION_ACTUATOR3_JSON}.
     */
    public static final String APPLICATION_ACTUATOR3_JSON_VALUE = "application/vnd.spring-boot.actuator.v3+json";

    static {
        APPLICATION_ACTUATOR2_JSON = new MediaType("application", "vnd.spring-boot.actuator.v2+json");
        APPLICATION_ACTUATOR3_JSON = new MediaType("application", "vnd.spring-boot.actuator.v3+json");
    }

}
