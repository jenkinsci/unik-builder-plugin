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

public class StartInstanceCommand extends UnikCommand {

    private String instanceName;

    @DataBoundConstructor
    public StartInstanceCommand(String instanceName) {
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

        getClient().instances().start(instanceNameRes);
        console.logInfo("Instance " + instanceNameRes + "is started");
    }


    @Extension
    public static class StartInstanceCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.StartInstanceCommand_DescriptorImpl_DisplayName();
        }

        public FormValidation doCheckInstanceName(@QueryParameter String instanceName) {
            return ValidatorUtils.validateStringNotEmpty(instanceName);
        }

    }
}
