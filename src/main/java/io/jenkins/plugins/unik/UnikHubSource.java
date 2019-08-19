package io.jenkins.plugins.unik;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import it.mathiasmah.junik.client.models.Hub;
import jenkins.authentication.tokens.api.AuthenticationTokenException;
import jenkins.authentication.tokens.api.AuthenticationTokenSource;

public class UnikHubSource extends AuthenticationTokenSource<Hub, StandardUsernamePasswordCredentials> {

    public UnikHubSource() {
        super(Hub.class, StandardUsernamePasswordCredentials.class);
    }

    @NonNull
    @Override
    public Hub convert(@NonNull StandardUsernamePasswordCredentials credential) throws AuthenticationTokenException {
        //TODO impl
        return new Hub();
    }
}
