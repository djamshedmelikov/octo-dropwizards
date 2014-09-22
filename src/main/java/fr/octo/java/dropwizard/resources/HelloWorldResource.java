package fr.octo.java.dropwizard.resources;


import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.*;
import fr.octo.java.dropwizard.domain.Saying;
import fr.octo.java.dropwizard.domain.User;
import io.dropwizard.auth.Auth;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by thomaspepio on 16/09/14.
 */
@Path("/hello-world")
@Api(value="/hello-world", description="A simple Hello to the world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    private final String template;
    private final String defaultName;
    private final AtomicLong counter;

    /**
     * Constructor.
     *
     * @param template rendering template.
     * @param defaultName a default name to populate the template.
     */
    public HelloWorldResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    /**
     * A hello world method.
     *
     * @param token credentials token.
     * @param name the name to say hello to.
     * @return a Saying which represents the hello.
     */
    @GET
    @Timed
    @ApiOperation(value = "Ask for a Hello.", notes = "More notes about this method", response = Saying.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Don't know what you could've been doint wrong..."),
            @ApiResponse(code = 404, message = "Don't know what you could've been doint wrong...")
    })
    public Saying sayHello(@Auth User token, @ApiParam(value="Your name.", required=false) String name) {
        if(StringUtils.isEmpty(name)) {
            name = defaultName;
        }
        final String value = String.format(template, name);
        return new Saying(counter.incrementAndGet(), value);
    }

}
