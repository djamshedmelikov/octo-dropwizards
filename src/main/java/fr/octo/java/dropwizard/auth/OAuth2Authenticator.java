package fr.octo.java.dropwizard.auth;

import com.google.common.base.Optional;
import fr.octo.java.dropwizard.dao.AccessTokenDAO;
import fr.octo.java.dropwizard.domain.AccessToken;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.UUID;

/**
 * Checks if a given token is valid or not.
 * Users must identify themselves through {@link fr.octo.java.dropwizard.resources.OAuth2Resource} before trying to access to secured resources.
 */
public class OAuth2Authenticator implements Authenticator<String, Long> {

    public static final int ACCESS_TOKEN_EXPIRE_TIME_MIN = 30;

    private AccessTokenDAO tokenDAO;

    public OAuth2Authenticator(AccessTokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Long> authenticate(String tokenId) throws AuthenticationException {

        // Check input, must be a valid UUID
        UUID accessTokenUUID;
        try {
            accessTokenUUID = UUID.fromString(tokenId);
        } catch (IllegalArgumentException e) {
            return Optional.absent();
        }

        // Get the access token from the database
        Optional<AccessToken> accessToken = tokenDAO.findAccessTokenById(accessTokenUUID);
        if (accessToken == null || !accessToken.isPresent()) {
            return Optional.absent();
        }

        // Check if the last access time is not too far in the past (the access token is expired)
        Period period = new Period(accessToken.get().getLastAccessUTC(), new DateTime());
        if (period.getMinutes() > ACCESS_TOKEN_EXPIRE_TIME_MIN) {
            return Optional.absent();
        }

        // Update the access time for the token
        tokenDAO.setLastAccessTime(accessTokenUUID, new DateTime());

        // Return the user's id for processing
        return Optional.of(accessToken.get().getUserId());
    }

}