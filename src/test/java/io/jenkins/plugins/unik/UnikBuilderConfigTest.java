package io.jenkins.plugins.unik;

import hudson.model.FreeStyleProject;
import io.jenkins.plugins.unik.cmd.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class UnikBuilderConfigTest {

    private final UnikCommand command;

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    public UnikBuilderConfigTest(UnikCommand command) {
        this.command = command;
    }

    @Parameterized.Parameters
    public static Collection commands() {

        CreateImageCommand createImageCommand = new CreateImageCommand("folder", "imageName", "provider", "base", "language");
        createImageCommand.setForce(true);
        createImageCommand.setNoCleanup(true);
        createImageCommand.setArgs("arg1 arg2");
        createImageCommand.setMounts("mount1 mount2");

        CreateVolumeCommand createVolumeCommand = new CreateVolumeCommand("volumeName", "provider");
        createVolumeCommand.setRaw(true);
        createVolumeCommand.setNoCleanup(true);
        createVolumeCommand.setType("ext2");
        createVolumeCommand.setSize("100");
        createVolumeCommand.setData("data");

        UnikHubEndpoint hub = new UnikHubEndpoint();
        hub.setCredentialsId("credentialsId");
        hub.setUrl("url");

        PullImageCommand pullImageCommand = new PullImageCommand("imageName", "provider");
        pullImageCommand.setForce(true);
        pullImageCommand.setUnikHubEndpoint(hub);

        PushImageCommand pushImageCommand = new PushImageCommand("imageName");
        pushImageCommand.setUnikHubEndpoint(hub);

        RemoveImageCommand removeImageCommand = new RemoveImageCommand("imageName");
        removeImageCommand.setForce(true);

        RemoveInstanceCommand removeInstanceCommand = new RemoveInstanceCommand("instanceName");
        removeInstanceCommand.setForce(true);

        RemoveVolumeCommand removeVolumeCommand = new RemoveVolumeCommand("volumeName");
        removeVolumeCommand.setForce(true);

        RunInstanceCommand runInstanceCommand = new RunInstanceCommand("istanceName", "imageName");
        runInstanceCommand.setDebug(true);
        runInstanceCommand.setNoCleanup(true);
        runInstanceCommand.setEnvs("key1=value1 key2=value2");
        runInstanceCommand.setMounts("volume1:mount1 volume2:mount2");
        runInstanceCommand.setMemoryMb("100");

        return Arrays.asList(
//                new AttachVolumeCommand("testVolume", "instanceId", "mountPoint"),
//                createImageCommand,
//                createVolumeCommand,
//                new DetachVolumeCommand("volumeName"),
//                removeImageCommand,
//                removeInstanceCommand,
//                removeVolumeCommand,
//                runInstanceCommand,
//                new StartInstanceCommand("instanceName"),
//                new StopInstanceCommand("instanceName"),
                pullImageCommand,
                pushImageCommand
        );
    }


    @Test
    public void testConfigRoundtrip() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();

        project.getBuildersList().add(new UnikBuilder(command));
        project = jenkins.configRoundtrip(project);
        jenkins.assertEqualDataBoundBeans(new UnikBuilder(command), project.getBuildersList().get(0));
    }
}
