package io.jenkins.plugins.unik.cmd;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Item;
import hudson.model.Run;
import hudson.util.FormValidation;
import io.jenkins.plugins.unik.log.ConsoleLogger;
import io.jenkins.plugins.unik.utils.Resolver;
import io.jenkins.plugins.unik.validator.ValidatorUtils;
import it.mathiasmah.junik.client.exceptions.UnikException;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

/**
 * An implementation of {@link UnikCommand} equivalent to the <i>unik delete-image</i> CLI command
 *
 * @see UnikCommand
 */
public class RemoveImageCommand extends UnikCommand {

    private String imageName;
    private boolean force;

    @DataBoundConstructor
    public RemoveImageCommand(String imageName) {
        this.imageName = imageName;
        this.force = false;
    }

    public String getImageName() {
        return imageName;
    }

    public boolean isForce() {
        return force;
    }

    @DataBoundSetter
    public void setForce(boolean force) {
        this.force = force;
    }

    @Override
    public void execute(Launcher launcher, Run<?, ?> run, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String imageNameRes = Resolver.buildVar(run, imageName);
        if (StringUtils.isBlank(imageNameRes)) {
            throw new IllegalArgumentException("Image name can not be empty");
        }

        getClient().images().delete(imageNameRes, force);
        console.logInfo("Image " + imageNameRes + "is removed");

    }


    @Symbol("delete-image")
    @Extension
    public static class RemoveImageCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.RemoveImageCommand_DescriptorImpl_DisplayName();
        }

        @POST
        public FormValidation doCheckImageName(@QueryParameter String imageName, @AncestorInPath Item item) {
            if (item == null) {
                return FormValidation.ok();
            }
            item.checkPermission(Item.CONFIGURE);

            return ValidatorUtils.validateStringNotEmpty(imageName);
        }

    }


}
