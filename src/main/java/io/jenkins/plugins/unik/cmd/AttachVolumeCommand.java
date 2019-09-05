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
 * An implementation of {@link UnikCommand} equivalent to the <i>unik attach-volume</i> CLI command
 *
 * @see UnikCommand
 */
public class AttachVolumeCommand extends UnikCommand {

    private String volumeName;
    private String instanceId;
    private String mountPoint;

    @DataBoundConstructor
    public AttachVolumeCommand(String volumeName, String instanceId, String mountPoint) {
        this.volumeName = volumeName;
        this.instanceId = instanceId;
        this.mountPoint = mountPoint;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    @Override
    public void execute(Launcher launcher, Run<?, ?> run, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String volumeNameRes = Resolver.buildVar(run, volumeName);
        if (StringUtils.isBlank(volumeNameRes)) {
            throw new IllegalArgumentException("Volume name can not be empty");
        }

        final String instanceIdRes = Resolver.buildVar(run, instanceId);
        if (StringUtils.isBlank(instanceIdRes)) {
            throw new IllegalArgumentException("Instance id can not be empty");
        }

        final String mountPointRes = Resolver.buildVar(run, mountPoint);
        if (StringUtils.isBlank(mountPointRes)) {
            throw new IllegalArgumentException("Mount point can not be empty");
        }

        getClient().volumes().attach(volumeNameRes, instanceIdRes, mountPointRes);
        console.logInfo("Attached volume " + volumeNameRes + " to instance " + instanceIdRes + " at " + mountPointRes);
    }


    @Extension
    public static class AttachVolumeCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.AttachVolumeCommand_DescriptorImpl_DisplayName();
        }

        @POST
        public FormValidation doCheckVolumeName(@QueryParameter String volumeName) {
            return ValidatorUtils.validateStringNotEmpty(volumeName);
        }

        @POST
        public FormValidation doCheckInstanceId(@QueryParameter String instanceId) {
            return ValidatorUtils.validateStringNotEmpty(instanceId);
        }

        @POST
        public FormValidation doCheckMountPoint(@QueryParameter String mountPoint) {
            return ValidatorUtils.validateStringNotEmpty(mountPoint);
        }

    }
}
