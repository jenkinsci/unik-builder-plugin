package io.jenkins.plugins.unik.cmd;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.util.FormValidation;
import io.jenkins.plugins.unik.UnikHubEndpoint;
import io.jenkins.plugins.unik.log.ConsoleLogger;
import io.jenkins.plugins.unik.utils.Resolver;
import io.jenkins.plugins.unik.validator.ValidatorUtils;
import it.mathiasmah.junik.client.exceptions.UnikException;
import it.mathiasmah.junik.client.models.Hub;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

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
    public void execute(Launcher launcher, AbstractBuild<?, ?> build, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String imageNameRes = Resolver.buildVar(build, imageName);
        if (StringUtils.isBlank(imageNameRes)) {
            throw new IllegalArgumentException("Image name can not be empty");
        }

        final String providerRes = Resolver.buildVar(build, provider);
        if (StringUtils.isBlank(imageNameRes)) {
            throw new IllegalArgumentException("Provider can not be empty");
        }

        final Hub hub = getUnikHubConfig(build);
        if (hub == null) {
            throw new IllegalArgumentException("Hub config not valid");
        }

        getClient().hubs().pull(hub, imageNameRes,providerRes,force);
        console.logInfo("Pulled " + imageNameRes + " from " + hub.getUrl());
    }


    @Extension
    public static class PullImageDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.PullImageCommand_DescriptorImpl_DisplayName();
        }

        public FormValidation doCheckImageName(@QueryParameter String imageName) {
            return ValidatorUtils.validateStringNotEmpty(imageName);
        }

        public FormValidation doCheckProvider(@QueryParameter String provider) {
            return ValidatorUtils.validateStringNotEmpty(provider);
        }

        @Override
        public boolean showCredentials() {
            return true;
        }
    }
}
