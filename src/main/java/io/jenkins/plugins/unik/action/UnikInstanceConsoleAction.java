package io.jenkins.plugins.unik.action;

import com.google.common.base.Charsets;
import hudson.console.AnnotatedLargeText;
import hudson.model.*;
import hudson.security.ACL;
import hudson.security.Permission;
import io.jenkins.plugins.unik.UnikBuilder;
import jenkins.model.Jenkins;
import org.apache.commons.jelly.XMLOutput;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Jenkins action to add a 'Console Output' like page for the Unikernel instance output.
 */
public class UnikInstanceConsoleAction extends TaskAction {

    private static Logger LOGGER = Logger.getLogger(UnikInstanceConsoleAction.class.getName());

    private final Run<?, ?> build;

    private final String instanceId;
    private final String instanceName;


    public UnikInstanceConsoleAction(Run<?, ?> build, String instanceId, String instanceName) {
        super();
        this.build = build;
        this.instanceId = instanceId;
        this.instanceName = instanceName;
    }

    @Override
    public String getIconFileName() {
        return Jenkins.RESOURCE_PATH + "/plugin/unik-plugin/icons/unik-icon.png";
    }

    @Override
    public String getDisplayName() {
        return instanceName + " Output";
    }

    public String getFullDisplayName() {
        return build.getFullDisplayName() + ' ' + getDisplayName();
    }

    public Run<?, ?> getOwner() {
        return this.build;
    }

    public String getBuildStatusUrl() {
        return build.getIconColor().getImage();
    }

    @Override
    public String getUrlName() {
        return "unikconsole_" + instanceName;
    }

    @Override
    protected Permission getPermission() {
        return Item.READ;
    }

    @Override
    protected ACL getACL() {
        return build.getACL();
    }


    public File getLogFile() {
        return new File(build.getRootDir(), "unikernel_" + instanceName + ".log");
    }

    @Override
    public AnnotatedLargeText obtainLog() {
        return new AnnotatedLargeText<>(getLogFile(), Charsets.UTF_8, !isLogUpdated(), this);
    }

    public boolean isLogUpdated() {
        return (workerThread != null) && build.isLogUpdated();
    }

    public void writeLogTo(long offset, XMLOutput out) {
        try {
            obtainLog().writeHtmlTo(offset, out.asWriter());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not write logs", e);
        }
    }

    public UnikInstanceConsoleAction start() throws IOException {
        workerThread = new UnikLogWorkerThread(getLogFile());
        workerThread.start();
        return this;
    }

    public void stop() {
        workerThread.interrupt();
        workerThread = null;
    }

    public final class UnikLogWorkerThread extends TaskThread {

        protected UnikLogWorkerThread(File logFile) throws IOException {
            super(UnikInstanceConsoleAction.this, ListenerAndText.forFile(logFile, UnikInstanceConsoleAction.this));
        }

        @Override
        protected void perform(final TaskListener listener) throws Exception {

            final UnikBuilder.DescriptorImpl descriptor = ((UnikBuilder.DescriptorImpl) Jenkins.get().getDescriptor(UnikBuilder.class));

            if (descriptor != null) {
                descriptor.getUnikClient().instances().logToStream(instanceId, false, listener.getLogger());

            }
        }
    }
}
