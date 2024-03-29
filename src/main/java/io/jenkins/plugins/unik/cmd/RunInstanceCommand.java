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
import it.mathiasmah.junik.client.models.Instance;
import it.mathiasmah.junik.client.models.RunInstance;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation of {@link UnikCommand} equivalent to the <i>unik run</i> CLI command
 *
 * @see UnikCommand
 */
public class RunInstanceCommand extends UnikCommand {

    private String instanceName;
    private String imageName;
    private String memoryMb;
    private boolean noCleanup;
    private boolean debug;
    private String envs;
    private String mounts;

    @DataBoundConstructor
    public RunInstanceCommand(String instanceName, String imageName) {
        this.instanceName = instanceName;
        this.imageName = imageName;
        this.memoryMb = "0";
        this.noCleanup = false;
        this.debug = false;
        this.envs = StringUtils.EMPTY;
        this.mounts = StringUtils.EMPTY;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getImageName() {
        return imageName;
    }

    public String getMemoryMb() {
        return memoryMb;
    }

    @DataBoundSetter
    public void setMemoryMb(String memoryMb) {
        this.memoryMb = memoryMb;
    }

    public boolean isNoCleanup() {
        return noCleanup;
    }

    @DataBoundSetter
    public void setNoCleanup(boolean noCleanup) {
        this.noCleanup = noCleanup;
    }

    public boolean isDebug() {
        return debug;
    }

    @DataBoundSetter
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getEnvs() {
        return envs;
    }

    @DataBoundSetter
    public void setEnvs(String envs) {
        this.envs = envs;
    }

    public String getMounts() {
        return mounts;
    }

    @DataBoundSetter
    public void setMounts(String mounts) {
        this.mounts = mounts;
    }

    @Override
    public void execute(Launcher launcher, Run<?, ?> run, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String instanceNameRes = Resolver.buildVar(run, instanceName);
        if (StringUtils.isBlank(instanceNameRes)) {
            throw new IllegalArgumentException("Instance name can not be empty");
        }

        final String imageNameRes = Resolver.buildVar(run, imageName);
        if (StringUtils.isBlank(imageNameRes)) {
            throw new IllegalArgumentException("Image name can not be empty");
        }

        final String memoryMbRawRes = Resolver.buildVar(run, memoryMb);
        int memoryMbRes;
        try {
            memoryMbRes = Integer.parseInt(memoryMbRawRes);
            if (memoryMbRes <= 0) {
                memoryMbRes = 0;
                console.logWarn("Not a valid memory size " + memoryMbRes + ", will be ignored");
            }
        } catch (NumberFormatException e) {
            memoryMbRes = 0;
            console.logWarn("Not a valid memory size " + memoryMbRes + ", will be ignored");
        }

        final String envsRawRes = Resolver.buildVar(run, envs);
        final String mountsRawRes = Resolver.buildVar(run, mounts);

        final RunInstance runInstance = new RunInstance();
        runInstance.setInstanceName(instanceNameRes);
        runInstance.setImageName(imageNameRes);
        runInstance.setDebugMode(debug);
        runInstance.setNoCleanUp(noCleanup);
        runInstance.setMemoryMb(memoryMbRes);
        runInstance.setEnv(resolvePairs(envsRawRes, "="));
        runInstance.setMounts(resolvePairs(mountsRawRes, ":"));

        Instance instance = getClient().instances().run(runInstance);
        console.logInfo("Instance " + instanceNameRes + "is started");
        console.logInfo(instance.toString());

        if (instance.getId() != null) {
            attachInstanceOutput(run, instance.getId(), instance.getName());
            console.logInfo("Attach log action");
        } else {
            console.logWarn("Could not attach log action because instance id is null");
        }
    }

    private Map<String, String> resolvePairs(String rawString, String delimiter) {
        if (StringUtils.isBlank(rawString)) {
            return Collections.emptyMap();
        }
        return Arrays.stream(rawString.split("/n")).collect(Collectors.toMap((m -> m.substring(0, m.indexOf(delimiter) - 1)), (m -> m.substring(m.indexOf(delimiter)))));
    }


    @Symbol("run")
    @Extension
    public static class RunInstanceCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.RunInstanceCommand_DescriptorImpl_DisplayName();
        }

        @POST
        public FormValidation doCheckInstanceName(@QueryParameter String instanceName, @AncestorInPath Item item) {
            if (item == null) {
                return FormValidation.ok();
            }
            item.checkPermission(Item.CONFIGURE);

            return ValidatorUtils.validateStringNotEmpty(instanceName);
        }

        @POST
        public FormValidation doCheckImageName(@QueryParameter String imageName, @AncestorInPath Item item) {
            if (item == null) {
                return FormValidation.ok();
            }
            item.checkPermission(Item.CONFIGURE);

            return ValidatorUtils.validateStringNotEmpty(imageName);
        }
    }
}
