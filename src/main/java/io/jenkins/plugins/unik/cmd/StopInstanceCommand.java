package io.jenkins.plugins.unik.cmd;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.util.FormValidation;
import io.jenkins.plugins.unik.log.ConsoleLogger;
import io.jenkins.plugins.unik.utils.Resolver;
import io.jenkins.plugins.unik.validator.ValidatorUtils;
import it.mathiasmah.junik.client.Client;
import it.mathiasmah.junik.client.exceptions.UnikException;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class StopInstanceCommand extends UnikCommand {

    private String instanceName;

    @DataBoundConstructor
    public StopInstanceCommand(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    @Override
    public void execute(Launcher launcher, AbstractBuild<?, ?> build, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String instanceNameRes = Resolver.buildVar(build, instanceName);
        if (StringUtils.isBlank(instanceNameRes)) {
            throw new IllegalArgumentException("Instance name can not be empty");
        }

        getClient().instances().stop(instanceNameRes);
        console.logInfo("Instance " + instanceNameRes + "is stopped");
    }


    @Extension
    public static class StopInstanceCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.StopInstanceCommand_DescriptorImpl_DisplayName();
        }

        public FormValidation doCheckInstanceName(@QueryParameter String instanceName) {
            return ValidatorUtils.validateStringNotEmpty(instanceName);
        }


    }
}
