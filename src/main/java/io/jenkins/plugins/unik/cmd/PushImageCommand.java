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

public class PushImageCommand extends UnikCommand {

    private String imageName;

    @DataBoundConstructor
    public PushImageCommand(String imageName, UnikHubEndpoint unikHubEndpoint) {
        super(unikHubEndpoint);
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    @Override
    public void execute(Launcher launcher, AbstractBuild<?, ?> build, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String imageNameRes = Resolver.buildVar(build, imageName);
        if (StringUtils.isBlank(imageNameRes)) {
            throw new IllegalArgumentException("Image name can not be empty");
        }

        final Hub hub = getUnikHubConfig(build.getParent());
        if (hub == null) {
            throw new IllegalArgumentException("Hub config not valid");
        }

        getClient().hubs().push(hub, imageNameRes);
        console.logInfo("Pushed " + imageNameRes + " to " + hub);
    }


    @Extension
    public static class PushImageCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.PushImageCommand_DescriptorImpl_DisplayName();
        }

        public FormValidation doCheckImageName(@QueryParameter String imageName) {
            return ValidatorUtils.validateStringNotEmpty(imageName);
        }

        @Override
        public boolean showCredentials() {
            return true;
        }
    }
}
