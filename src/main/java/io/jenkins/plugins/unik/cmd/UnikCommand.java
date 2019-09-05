package io.jenkins.plugins.unik.cmd;

import com.google.common.base.Strings;
import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Run;
import io.jenkins.plugins.unik.UnikBuilder;
import io.jenkins.plugins.unik.UnikHubEndpoint;
import io.jenkins.plugins.unik.action.UnikInstanceConsoleAction;
import io.jenkins.plugins.unik.log.ConsoleLogger;
import it.mathiasmah.junik.client.Client;
import it.mathiasmah.junik.client.exceptions.UnikException;
import it.mathiasmah.junik.client.models.Hub;
import jenkins.model.Jenkins;

import java.io.IOException;

import static jenkins.model.Jenkins.get;

/**
 * Abstract Describable that holds all the general implementation of a Unik Command
 */
public abstract class UnikCommand implements Describable<UnikCommand>, ExtensionPoint {

    private UnikHubEndpoint unikHubEndpoint;

    public UnikCommand() {
        this(null);
    }

    public UnikCommand(UnikHubEndpoint unikHubEndpoint) {
        this.unikHubEndpoint = unikHubEndpoint;
    }

    /**
     * Get the descriptor list of all subtypes
     *
     * @return a {@link DescriptorExtensionList} of all subtypes
     */
    public static DescriptorExtensionList<UnikCommand, UnikCommandDescriptor> all() {
        return get().getDescriptorList(UnikCommand.class);
    }

    /**
     * Get a client that holds the connection to the Unik server
     *
     * @return a {@link Client} that holds the connection to the Unik server
     * @throws UnikException if no connection could be established
     */
    public static Client getClient() throws UnikException {
        UnikBuilder.DescriptorImpl descriptor = (UnikBuilder.DescriptorImpl) Jenkins.get().getDescriptor(UnikBuilder.class);
        if (descriptor != null) {
            return descriptor.getUnikClient();
        } else throw new UnikException("Could not create Unik client");
    }

    protected static void attachInstanceOutput(Run<?, ?> build, String containerId, String containerName) throws UnikException {
        try {
            UnikInstanceConsoleAction outAction = new UnikInstanceConsoleAction(build, containerId, containerName).start();
            build.addAction(outAction);
        } catch (IOException e) {
            throw new UnikException(e.getMessage());
        }
    }

    public UnikHubEndpoint getUnikHubEndpoint() {
        return unikHubEndpoint;
    }

    /**
     * Retrieves the information about the configured Unik Hub
     *
     * @param build the current build
     * @return a {@link Hub} containing the information needed to connect to a Unik Hub
     */
    public Hub getUnikHubConfig(Run<?, ?> build) {
        if (unikHubEndpoint == null || Strings.isNullOrEmpty(unikHubEndpoint.getCredentialsId())) {
            return null;
        }

        return unikHubEndpoint.getHub(build);
    }

    /**
     * Execute the Unik command
     *
     * @param launcher the {@link Launcher} of this build
     * @param run    the current build
     * @param console  the logger to log to the Jenkins console of this build
     * @throws UnikException if something went wrong with the execution
     */
    public abstract void execute(Launcher launcher, Run<?, ?> run, ConsoleLogger console)
            throws UnikException;

    @Override
    public Descriptor<UnikCommand> getDescriptor() {
        return (UnikCommandDescriptor) get().getDescriptor(getClass());
    }

    public abstract static class UnikCommandDescriptor extends Descriptor<UnikCommand> {

        protected UnikCommandDescriptor(Class<? extends UnikCommand> clazz) {
            super(clazz);
        }

        protected UnikCommandDescriptor() {
        }

        public UnikHubEndpoint.DescriptorImpl getUnikHubEndpointDescriptor() {
            return (UnikHubEndpoint.DescriptorImpl) Jenkins.get().getDescriptor(UnikHubEndpoint.class);
        }

        /**
         * Set to true if for this command the Unik Hub credentials should be configurable
         *
         * @return true if the Unik Hub credentials should be configurable
         */
        public boolean showCredentials() {
            return false;
        }


    }
}
