package io.jenkins.plugins.unik.cmd;

import com.google.common.base.Strings;
import hudson.AbortException;
import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.util.FormValidation;
import io.jenkins.plugins.unik.UnikBuilder;
import io.jenkins.plugins.unik.UnikHubEndpoint;
import io.jenkins.plugins.unik.log.ConsoleLogger;
import it.mathiasmah.junik.client.Client;
import it.mathiasmah.junik.client.exceptions.UnikException;
import it.mathiasmah.junik.client.models.Hub;
import jenkins.model.Jenkins;

import static jenkins.model.Jenkins.get;

public abstract class UnikCommand implements Describable<UnikCommand>, ExtensionPoint {

    private UnikHubEndpoint unikHubEndpoint;

    public UnikCommand() {
        this(null);
    }

    public UnikCommand(UnikHubEndpoint unikHubEndpoint) {
        this.unikHubEndpoint = unikHubEndpoint;
    }

    public UnikHubEndpoint getUnikHubEndpoint() {
        return unikHubEndpoint;
    }

    public Hub getUnikHubConfig(Item item) {
        if (unikHubEndpoint == null || Strings.isNullOrEmpty(unikHubEndpoint.getCredentialsId())) {
            return null;
        }

        return unikHubEndpoint.getHub(item);
    }

    public static DescriptorExtensionList<UnikCommand, UnikCommandDescriptor> all() {
        return get().getDescriptorList(UnikCommand.class);
    }

    public static Client getClient() throws UnikException {
        UnikBuilder.DescriptorImpl descriptor = (UnikBuilder.DescriptorImpl) Jenkins.get().getDescriptor(UnikBuilder.class);
        if (descriptor != null) {
            return descriptor.getUnikClient();
        } else throw new UnikException("Could not create Unik client");
    }

    public abstract void execute(Launcher launcher, AbstractBuild<?, ?> build, ConsoleLogger console)
            throws UnikException, AbortException;

    @Override
    public Descriptor<UnikCommand> getDescriptor() {
        return (UnikCommandDescriptor) get().getDescriptor(getClass());
    }

    public abstract static class UnikCommandDescriptor extends Descriptor<UnikCommand> {

        protected UnikCommandDescriptor(Class<? extends UnikCommand> clazz) {
            super(clazz);
        }

        protected UnikCommandDescriptor() {
        }   public UnikHubEndpoint.DescriptorImpl getUnikHubEndpointDescriptor() {
            return (UnikHubEndpoint.DescriptorImpl) Jenkins.get().getDescriptor(UnikHubEndpoint.class);
        }

        public boolean showCredentials() {
            return false;
        }



    }
}
