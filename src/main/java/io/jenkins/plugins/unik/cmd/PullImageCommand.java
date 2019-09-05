package io.jenkins.plugins.unik.cmd;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Item;
import hudson.model.Run;
import hudson.util.FormValidation;
import io.jenkins.plugins.unik.UnikHubEndpoint;
import io.jenkins.plugins.unik.log.ConsoleLogger;
import io.jenkins.plugins.unik.utils.Resolver;
import io.jenkins.plugins.unik.validator.ValidatorUtils;
import it.mathiasmah.junik.client.exceptions.UnikException;
import it.mathiasmah.junik.client.models.Hub;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

/**
 * An implementation of {@link UnikCommand} equivalent to the <i>unik pull</i> CLI command
 *
 * @see UnikCommand
 */
public class PullImageCommand extends UnikCommand {

    private String imageName;
    private String provider;
    private boolean force;

    @DataBoundConstructor
    public PullImageCommand(String imageName, String provider, boolean force, UnikHubEndpoint unikHubEndpoint) {
        super(unikHubEndpoint);
        this.imageName = imageName;
        this.provider = provider;
        this.force = force;
    }

    public String getImageName() {
        return imageName;
    }

    public String getProvider() {
        return provider;
    }

    public boolean isForce() {
        return force;
    }

    @Override
    public void execute(Launcher launcher, Run<?, ?> run, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String imageNameRes = Resolver.buildVar(run, imageName);
        if (StringUtils.isBlank(imageNameRes)) {
            throw new IllegalArgumentException("Image name can not be empty");
        }

        final String providerRes = Resolver.buildVar(run, provider);
        if (StringUtils.isBlank(providerRes)) {
            throw new IllegalArgumentException("Provider can not be empty");
        }

        final Hub hub = getUnikHubConfig(run);
        if (hub == null) {
            throw new IllegalArgumentException("Hub config not valid");
        }

        getClient().hubs().pull(hub, imageNameRes, providerRes, force);
        console.logInfo("Pulled " + imageNameRes + " from " + hub.getUrl());
    }


    @Symbol("pull")
    @Extension
    public static class PullImageDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.PullImageCommand_DescriptorImpl_DisplayName();
        }

        @POST
        public FormValidation doCheckImageName(@QueryParameter String imageName, @AncestorInPath Item item) {
            if (item == null) {
                return FormValidation.ok();
            }
            item.checkPermission(Item.CONFIGURE);


            return ValidatorUtils.validateStringNotEmpty(imageName);
        }

        @POST
        public FormValidation doCheckProvider(@QueryParameter String provider, @AncestorInPath Item item) {
            if (item == null) {
                return FormValidation.ok();
            }
            item.checkPermission(Item.CONFIGURE);


            return ValidatorUtils.validateStringNotEmpty(provider);
        }

        @Override
        public boolean showCredentials() {
            return true;
        }
    }
}
