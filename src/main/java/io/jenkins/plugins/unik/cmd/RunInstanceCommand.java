package io.jenkins.plugins.unik.cmd;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.util.FormValidation;
import io.jenkins.plugins.unik.action.UnikInstanceConsoleAction;
import io.jenkins.plugins.unik.log.ConsoleLogger;
import io.jenkins.plugins.unik.utils.Resolver;
import io.jenkins.plugins.unik.validator.ValidatorUtils;
import it.mathiasmah.junik.client.Client;
import it.mathiasmah.junik.client.exceptions.UnikException;
import it.mathiasmah.junik.client.models.Instance;
import it.mathiasmah.junik.client.models.RunInstance;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class RunInstanceCommand extends UnikCommand {

    private String instanceName;
    private String imageName;
    private String memoryMb;
    private boolean noCleanup;
    private boolean debug;
    private String envs;
    private String mounts;

    @DataBoundConstructor
    public RunInstanceCommand(String instanceName, String imageName, String memoryMb, boolean noCleanup, boolean debug, String envs, String mounts) {
        this.instanceName = instanceName;
        this.imageName = imageName;
        this.memoryMb = memoryMb;
        this.noCleanup = noCleanup;
        this.debug = debug;
        this.envs = envs;
        this.mounts = mounts;
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

    public boolean isNoCleanup() {
        return noCleanup;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getEnvs() {
        return envs;
    }

    public String getMounts() {
        return mounts;
    }

    @Override
    public void execute(Launcher launcher, AbstractBuild<?, ?> build, ConsoleLogger console) throws UnikException {
        console.logInfo("Execute Command: " + getDescriptor().getDisplayName());

        final String instanceNameRes = Resolver.buildVar(build, instanceName);
        if (StringUtils.isBlank(instanceNameRes)) {
            throw new IllegalArgumentException("Instance name can not be empty");
        }

        final String imageNameRes = Resolver.buildVar(build, imageName);
        if (StringUtils.isBlank(imageNameRes)) {
            throw new IllegalArgumentException("Image name can not be empty");
        }

        final String memoryMbRawRes = Resolver.buildVar(build, memoryMb);
        int memoryMbRes;
        try {
            memoryMbRes = Integer.valueOf(memoryMbRawRes);
            if (memoryMbRes <= 0) {
                memoryMbRes = 0;
                console.logWarn("Not a valid memory size " + memoryMbRes + ", will be ignored");
            }
        } catch (NumberFormatException e) {
            memoryMbRes = 0;
            console.logWarn("Not a valid memory size " + memoryMbRes + ", will be ignored");
        }

        final String envsRawRes = Resolver.buildVar(build, envs);
        final String mountsRawRes = Resolver.buildVar(build, mounts);

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

        if(instance.getId() != null) {
            attachInstanceOutput(build, instance.getId(), instance.getName());
            console.logInfo("Attach log action");
        } else {
            console.logWarn("Could not attach log action because instance id is null");
        }
    }

    private Map<String, String> resolvePairs(String rawString, String delimiter) {
        if(StringUtils.isBlank(rawString)) {
            return Collections.emptyMap();
        }
        return Arrays.stream(rawString.split("/n")).collect(Collectors.toMap((m -> m.substring(0, m.indexOf(delimiter) - 1)), (m -> m.substring(m.indexOf(delimiter)))));
    }


    @Extension
    public static class RunInstanceCommandDescriptor extends UnikCommandDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.RunInstanceCommand_DescriptorImpl_DisplayName();
        }

        public FormValidation doCheckInstanceName(@QueryParameter String instanceName) {
            return ValidatorUtils.validateStringNotEmpty(instanceName);
        }

        public FormValidation doCheckImageName(@QueryParameter String imageName) {
            return ValidatorUtils.validateStringNotEmpty(imageName);
        }
    }
}
