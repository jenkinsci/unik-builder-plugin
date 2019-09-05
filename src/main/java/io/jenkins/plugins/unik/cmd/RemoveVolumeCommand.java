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
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

/**
 * An implementation of {@link UnikCommand} equivalent to the <i>unik delete-volume</i> CLI command
 *
 * @see UnikCommand
 */
public class RemoveVolumeCommand extends UnikCommand {

    private String volumeName;
    private boolean force;

    @DataBoundConstructor
    public RemoveVolumeCommand(String volumeName, boolean force) {
        this.volumeName = volumeName;
        this.force = force;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public boolean isForce() {
        return force;
    }

    @Override
    public void execute(Launcher launcher, Run<?, ?> run, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String volumeNameRes = Resolver.buildVar(run, volumeName);
        if (StringUtils.isBlank(volumeNameRes)) {
            throw new IllegalArgumentException("Volume name can not be empty");
        }

        getClient().volumes().delete(volumeNameRes, force);
        console.logInfo("Volume " + volumeNameRes + "is removed");
    }


    @Extension
    public static class RemoveVolumeCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.RemoveVolumeCommand_DescriptorImpl_DisplayName();
        }

        @POST
        public FormValidation doCheckVolumeName(@QueryParameter String volumeName, @AncestorInPath Item item) {
            if (item == null) {
                return FormValidation.ok();
            }
            item.checkPermission(Item.CONFIGURE);

            return ValidatorUtils.validateStringNotEmpty(volumeName);
        }

    }
}
