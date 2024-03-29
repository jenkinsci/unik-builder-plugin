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
import it.mathiasmah.junik.client.models.Hub;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

/**
 * An implementation of {@link UnikCommand} equivalent to the <i>unik push</i> CLI command
 *
 * @see UnikCommand
 */
public class PushImageCommand extends UnikCommand {

    private String imageName;

    @DataBoundConstructor
    public PushImageCommand(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    @Override
    public void execute(Launcher launcher, Run<?, ?> run, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String imageNameRes = Resolver.buildVar(run, imageName);
        if (StringUtils.isBlank(imageNameRes)) {
            throw new IllegalArgumentException("Image name can not be empty");
        }

        final Hub hub = getUnikHubConfig(run);
        if (hub == null) {
            throw new IllegalArgumentException("Hub config not valid");
        }

        getClient().hubs().push(hub, imageNameRes);
        console.logInfo("Pushed " + imageNameRes + " to " + hub);
    }


    @Symbol("push")
    @Extension
    public static class PushImageCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.PushImageCommand_DescriptorImpl_DisplayName();
        }

        @POST
        public FormValidation doCheckImageName(@QueryParameter String imageName, @AncestorInPath Item item) {
            if (item == null) {
                return FormValidation.ok();
            }
            item.checkPermission(Item.CONFIGURE);

            return ValidatorUtils.validateStringNotEmpty(imageName);
        }

        @Override
        public boolean showCredentials() {
            return true;
        }
    }
}
