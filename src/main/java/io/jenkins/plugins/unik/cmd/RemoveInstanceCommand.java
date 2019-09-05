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
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

/**
 * An implementation of {@link UnikCommand} equivalent to the <i>unik delete-instance</i> CLI command
 *
 * @see UnikCommand
 */
public class RemoveInstanceCommand extends UnikCommand {

    private String instanceName;
    private boolean force;

    @DataBoundConstructor
    public RemoveInstanceCommand(String instanceName, boolean force) {
        this.instanceName = instanceName;
        this.force = force;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public boolean isForce() {
        return force;
    }

    @Override
    public void execute(Launcher launcher, Run<?, ?> run, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String instanceNameRes = Resolver.buildVar(run, instanceName);
        if (StringUtils.isBlank(instanceNameRes)) {
            throw new IllegalArgumentException("Instance name can not be empty");
        }

        getClient().instances().delete(instanceNameRes, force);
        console.logInfo("Instance " + instanceNameRes + "is removed");
    }


    @Symbol("delete-instance")
    @Extension
    public static class RemoveInstanceCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.RemoveInstanceCommand_DescriptorImpl_DisplayName();
        }

        @POST
        public FormValidation doCheckInstanceName(@QueryParameter String instanceName, @AncestorInPath Item item) {
            if (item == null) {
                return FormValidation.ok();
            }
            item.checkPermission(Item.CONFIGURE);

            return ValidatorUtils.validateStringNotEmpty(instanceName);
        }

    }
}
