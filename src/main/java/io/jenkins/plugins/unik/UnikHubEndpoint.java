package io.jenkins.plugins.unik;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.Queue;
import hudson.model.queue.Tasks;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.unik.validator.ValidatorUtils;
import it.mathiasmah.junik.client.models.Hub;
import jenkins.authentication.tokens.api.AuthenticationTokens;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.Collections;

;

public class UnikHubEndpoint extends AbstractDescribableImpl<UnikHubEndpoint> {

    private final String url;
    private final String credentialsId;

    @DataBoundConstructor
    public UnikHubEndpoint(String url, String credentialsId) {
        this.url = Util.fixEmpty(url);
        this.credentialsId = Util.fixEmpty(credentialsId);
    }

    public String getUrl() {
        return url;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public Hub getHub(Item item) {
        return AuthenticationTokens.convert(
                Hub.class,
                CredentialsProvider.track(
                        item,
                        CredentialsMatchers.firstOrNull(
                                CredentialsProvider.lookupCredentials(
                                        StandardUsernamePasswordCredentials.class,
                                        item,
                                        item instanceof Queue.Task ? Tasks.getAuthenticationOf((Queue.Task) item) : ACL.SYSTEM,
                                        Collections.emptyList()
                                ),
                                CredentialsMatchers.withId(credentialsId)
                        )
                )
        );
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<UnikHubEndpoint> {

        public String getDisplayName() {
            return Messages.UnikHubEndpoint_DescriptorImpl_DisplayName();
        }

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item item, @QueryParameter String credentialsId) {
            StandardListBoxModel result = new StandardListBoxModel();
            if (item == null) {
                if (!Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
                    return result.includeCurrentValue(credentialsId);
                }
            } else {
                if (!item.hasPermission(Item.EXTENDED_READ)
                        && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
                    return result.includeCurrentValue(credentialsId);
                }
            }
            return result
                    .includeEmptyValue()
                    .includeMatchingAs(
                            item instanceof Queue.Task ? Tasks.getAuthenticationOf((Queue.Task) item) : ACL.SYSTEM,
                            item,
                            StandardUsernamePasswordCredentials.class,
                            Collections.emptyList(),
                            CredentialsMatchers.always()
                    )
                    .includeCurrentValue(credentialsId);
        }

        public FormValidation doCheckCredentialsId(@AncestorInPath Item item, @QueryParameter String credentialsId) {
            if (item == null) {
                if (!Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
                    return FormValidation.ok();
                }
            } else {
                if (!item.hasPermission(Item.EXTENDED_READ)
                        && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
                    return FormValidation.ok();
                }
            }
            if (StringUtils.isBlank(credentialsId)) {
                return FormValidation.ok();
            }
            if (StringUtils.startsWith(credentialsId, "${") && credentialsId.endsWith("}")) {
                return FormValidation.warning(Messages.UnikHubEndpoint_validation_expression_warning());
            }
            if (CredentialsProvider.listCredentials(
                    StandardUsernamePasswordCredentials.class,
                    item,
                    item instanceof Queue.Task ? Tasks.getAuthenticationOf((Queue.Task) item) : ACL.SYSTEM,
                    Collections.emptyList(),
                    CredentialsMatchers.withId(credentialsId)
            ).isEmpty()) {
                return FormValidation.error(Messages.UnikHubEndpoint_validation_notfound_error());
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckUrl(@QueryParameter String url) {
            return ValidatorUtils.validateStringNotEmpty(url);
        }
    }
}
