package fr.octo.java.dropwizard.resources;

import com.google.common.base.Optional;
import fr.octo.java.dropwizard.dao.AccessTokenDAO;
import fr.octo.java.dropwizard.dao.UserDAO;
import fr.octo.java.dropwizard.domain.AccessToken;
import fr.octo.java.dropwizard.domain.User;
import fr.octo.java.dropwizard.view.OAuth2View;
import org.joda.time.DateTime;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Given HTTP post parameters : performs a user authentication and generates a token on success.
 */
@Path("/oauth2/token")
@Produces(MediaType.APPLICATION_JSON)
public class OAuth2Resource {

    private List<String> allowedGrantTypes;
    private AccessTokenDAO accessTokenDAO;
    private UserDAO userDAO;

    public OAuth2Resource(List<String> allowedGrantTypes, AccessTokenDAO accessTokenDAO, UserDAO userDAO) {
        this.allowedGrantTypes = allowedGrantTypes;
        this.accessTokenDAO = accessTokenDAO;
        this.userDAO = userDAO;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public OAuth2View getForm() {
        return new OAuth2View();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String postForToken(@FormParam("username") String username,
                               @FormParam("password") String password) {
        // Try to find a user with the supplied credentials.
        Optional<User> user = userDAO.findUser(username, password);
        if (user == null || !user.isPresent()) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        // User was found, generate a token and return it.
        AccessToken accessToken = accessTokenDAO.generateNewAccessToken(user.get(), new DateTime());
        return accessToken.getAccessTokenId().toString();
    }

}