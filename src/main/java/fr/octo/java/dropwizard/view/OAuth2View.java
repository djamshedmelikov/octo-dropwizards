package fr.octo.java.dropwizard.view;

import io.dropwizard.views.View;

/**
 * Simple auth form.
 */
public class OAuth2View extends View {

    /**
     * Constructor.
     */
    public OAuth2View() {
        super("oauth2-form.mustache");
    }

}
