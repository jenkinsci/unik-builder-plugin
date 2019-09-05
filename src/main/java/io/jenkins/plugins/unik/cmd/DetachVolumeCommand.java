package io.jenkins.plugins.unik.cmd;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Run;
import hudson.util.FormValidation;
import io.jenkins.plugins.unik.log.ConsoleLogger;
import io.jenkins.plugins.unik.utils.Resolver;
import io.jenkins.plugins.unik.validator.ValidatorUtils;
import it.mathiasmah.junik.client.exceptions.UnikException;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

/**
 * An implementation of {@link UnikCommand} equivalent to the <i>unik detach-volume</i> CLI command
 *
 * @see UnikCommand
 */
public class DetachVolumeCommand extends UnikCommand {

    private String volumeName;

    @DataBoundConstructor
    public DetachVolumeCommand(String volumeName) {
        this.volumeName = volumeName;
    }

    public String getVolumeName() {
        return volumeName;
    }

    @Override
    public void execute(Launcher launcher, Run<?, ?> run, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String volumeNameRes = Resolver.buildVar(run, volumeName);
        if (StringUtils.isBlank(volumeNameRes)) {
            throw new IllegalArgumentException("Volume name can not be empty");
        }

        getClient().volumes().detach(volumeNameRes);
        console.logInfo("Volume " + volumeNameRes + "is detached");
    }


    @Extension
    public static class DetachVolumeCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.DetachVolumeCommand_DescriptorImpl_DisplayName();
        }

        @POST
        public FormValidation doCheckVolumeName(@QueryParameter String volumeName) {
            return ValidatorUtils.validateStringNotEmpty(volumeName);
        }

    }
}
