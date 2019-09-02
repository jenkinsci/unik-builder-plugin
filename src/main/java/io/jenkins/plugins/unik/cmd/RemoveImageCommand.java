package io.jenkins.plugins.unik.cmd;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.util.FormValidation;
import io.jenkins.plugins.unik.log.ConsoleLogger;
import io.jenkins.plugins.unik.utils.Resolver;
import io.jenkins.plugins.unik.validator.ValidatorUtils;
import it.mathiasmah.junik.client.exceptions.UnikException;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * An implementation of {@link UnikCommand} equivalent to the <i>unik delete-image</i> CLI command
 *
 * @see UnikCommand
 */
public class RemoveImageCommand extends UnikCommand {

    private String imageName;
    private boolean force;

    @DataBoundConstructor
    public RemoveImageCommand(String imageName, boolean force) {
        this.imageName = imageName;
        this.force = force;
    }

    public String getImageName() {
        return imageName;
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

        getClient().images().delete(imageNameRes, force);
        console.logInfo("Image " + imageNameRes + "is removed");

    }


    @Extension
    public static class RemoveImageCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.RemoveImageCommand_DescriptorImpl_DisplayName();
        }

        public FormValidation doCheckImageName(@QueryParameter String imageName) {
            return ValidatorUtils.validateStringNotEmpty(imageName);
        }

    }


}
