package io.jenkins.plugins.unik;

import hudson.*;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import io.jenkins.plugins.unik.cmd.UnikCommand;
import io.jenkins.plugins.unik.log.ConsoleLogger;
import it.mathiasmah.junik.client.Client;
import it.mathiasmah.junik.client.exceptions.UnikException;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * A build step that lets you select a Unik command for execution
 *
 * @see UnikCommand
 */
public class UnikBuilder extends Builder implements SimpleBuildStep {

    private static Logger LOGGER = Logger.getLogger(UnikBuilder.class.getName());

    private UnikCommand command;

    @DataBoundConstructor
    public UnikBuilder(UnikCommand command) {
        this.command = command;
    }

    public UnikCommand getCommand() {
        return command;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws IOException {
        ConsoleLogger clog = new ConsoleLogger(listener);

        try {
            getDescriptor().getUnikClient();
        } catch (UnikException e) {
            clog.logError("unik client is not created, command '" + command.getDescriptor().getDisplayName()
                    + "' was aborted. Check Jenkins server log why client wasn't created");
            LOGGER.log(Level.SEVERE, "Failed to execute Unik command " + command.getDescriptor().getDisplayName(), e);
            throw new AbortException("No Unik client available");
        }

        try {
            command.execute(launcher, run, clog);
        } catch (UnikException e) {
            clog.logError("command '" + command.getDescriptor().getDisplayName() + "' failed: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Failed to execute Unik command " + command.getDescriptor().getDisplayName(), e);
            throw new AbortException(e.getMessage());
        }
    }

    @Symbol("unik")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        private Client unikClient;
        private String unikUrl;

        public DescriptorImpl() {
            load();

            if (isEmpty(unikUrl)) {
                LOGGER.warning("Docker URL is not set, docker client won't be initialized");
                return;
            }

            try {
                getAndTestConnection(unikUrl);
            } catch (Exception e) {
                LOGGER.warning("Cannot create Docker client: " + e.getCause());
            }
        }

        public String getUnikUrl() {
            return unikUrl;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            try {
                getAndTestConnection(unikUrl);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Can not create unik client, unik plugin not usable", e);
                return false;
            }
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.UnikBuilder_DescriptorImpl_DisplayName();
        }

        public DescriptorExtensionList<UnikCommand, UnikCommand.UnikCommandDescriptor> getCmdDescriptors() {
            return UnikCommand.all();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            unikUrl = formData.getString("unikUrl");

//            if (isBlank(unikUrl)) {
//                unikUrl = "http://localhost:3000";
//                LOGGER.info("No unik client url configured, using default " + unikUrl);
//            }

            save();

            try {
                getAndTestConnection(unikUrl);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Can not create unik client", e);
            }
            return super.configure(req, formData);

        }

        public Client getUnikClient() throws UnikException {
            if (unikClient == null) {
                unikClient = getAndTestConnection(unikUrl);
            }

            //test connection every time
            unikClient.compilers().getAllAvailable();

            return unikClient;
        }

        public FormValidation doTestConnection(@QueryParameter String unikUrl) {
            try {
                getAndTestConnection(unikUrl);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Cannot connect to " + unikUrl, e);
                return FormValidation.error("Something went wrong, cannot connect to " + unikUrl + ", cause: "
                        + e.getCause());
            }
            return FormValidation.ok("Connected to " + unikUrl);
        }

        private Client getAndTestConnection(String url) throws UnikException {

            Client client = new Client(url);

            //test connection
            client.compilers().getAllAvailable();

            return client;
        }
    }
}
