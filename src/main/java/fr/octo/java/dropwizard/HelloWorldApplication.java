package fr.octo.java.dropwizard;

import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner;
import com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider;
import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jaxrs.listing.ResourceListingProvider;
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader;
import com.wordnik.swagger.reader.ClassReaders;
import fr.octo.java.dropwizard.auth.BasicAuthenticator;
import fr.octo.java.dropwizard.auth.OAuth2Authenticator;
import fr.octo.java.dropwizard.dao.AccessTokenDAO;
import fr.octo.java.dropwizard.dao.UserDAO;
import fr.octo.java.dropwizard.domain.User;
import fr.octo.java.dropwizard.resources.HelloWorldResource;
import fr.octo.java.dropwizard.resources.OAuth2Resource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.auth.oauth.OAuthProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by thomaspepio on 15/09/14.
 */
public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "hello-world";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(new AssetsBundle("/assets/swagger-ui/", "/swagger-ui"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(HelloWorldConfiguration configuration,
                    Environment environment) {
        // HelloWorld resources.
        final HelloWorldResource resource = new HelloWorldResource(configuration.getTemplate(),
                                                                   configuration.getDefaultName());
        environment.jersey().register(resource);
        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(configuration.getTemplate());
                                                                        environment.healthChecks().register("template", healthCheck);
                                                                        environment.jersey().register(resource);

        // Authentication configuration.
        this.connfigureHTTPBasicAuth(environment);
//        this.configureOAuth2(environment);

        // Swagger configuration
        this.configureSwagger(environment);
    }

    /**
     * Configures authentication.
     *
     * @param environment
     */
    private void connfigureHTTPBasicAuth(Environment environment) {
        // Enables HTTP basic authentication.
        environment.jersey().register(new BasicAuthProvider<User>(new BasicAuthenticator(), "SUPER SECRET STUFF"));
    }

    /**
     * Configures authentication.
     *
     * @param environment
     */
    private void configureOAuth2(Environment environment) {
        AccessTokenDAO tokenDAO = new AccessTokenDAO();
        UserDAO userDAO = new UserDAO();
        List<String> allowedGrantTypes = new ArrayList<String>();
        allowedGrantTypes.add("password");

        // Registration of OAuth2 authentication endpoint.
        environment.jersey().register(new OAuth2Resource(allowedGrantTypes, tokenDAO, userDAO));

        // Registration of token checking component.
        environment.jersey().register(new OAuthProvider<Long>(new OAuth2Authenticator(tokenDAO), "SUPER SECRET STUFF"));
    }

    /**
     * Swagger configuration : register lots of stuff in dropwizard's environement.
     *
     * @param environment
     */
    private void configureSwagger(Environment environment) {
        // Swagger Resource
        environment.jersey().register(new ApiListingResourceJSON());

        // Swagger providers
        environment.jersey().register(new ApiDeclarationProvider());
        environment.jersey().register(new ResourceListingProvider());

        // Swagger Scanner, which finds all the resources for @Api Annotations
        ScannerFactory.setScanner(new DefaultJaxrsScanner());

        // Add the reader, which scans the resources and extracts the resource information
        ClassReaders.setReader(new DefaultJaxrsApiReader());

        // Set the swagger config options
        SwaggerConfig config = ConfigFactory.config();
        config.setApiVersion("1.0");
        config.setBasePath("http://localhost:8000");
    }

}
