package fr.octo.java.dropwizard.auth;

import com.google.common.base.Optional;
import fr.octo.java.dropwizard.domain.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

/**
 * Created by thomaspepio on 18/09/14.
 */
public class BasicAuthenticator implements Authenticator<BasicCredentials, User> {

    /** Every user is "user" */
    private static final String USERNAME = "user";

    /** Evey user has "pwd" for password. */
    private static final String PASSWORD = "pwd";

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        Optional<User> optional = null;

        if(USERNAME.equals(credentials.getUsername()) && PASSWORD.equals(credentials.getPassword())) {
            optional = Optional.of(new User(USERNAME, PASSWORD));
        } else {
            optional = Optional.absent();
        }

        return optional;
    }
}
