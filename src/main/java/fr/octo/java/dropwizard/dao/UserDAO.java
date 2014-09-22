package fr.octo.java.dropwizard.dao;

import com.google.common.base.Optional;
import fr.octo.java.dropwizard.domain.User;

/**
 * Created by thomaspepio on 22/09/14.
 */
public class UserDAO {

    /** Every user is "user" */
    private static final String USERNAME = "user";

    /** Evey user has "pwd" for password. */
    private static final String PASSWORD = "pwd";

    public Optional<User> findUser(String username, String password) {
        Optional<User> optional = null;

        if(USERNAME.equals(username) && PASSWORD.equals(password)) {
            optional = Optional.of(new User(USERNAME, PASSWORD));
        } else {
            optional = Optional.absent();
        }

        return optional;
    }

}
