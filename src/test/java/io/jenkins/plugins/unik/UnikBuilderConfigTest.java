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
        return Arrays.asList(
                new AttachVolumeCommand("testVolume", "instanceId", "mountPoint"),
                new CreateImageCommand("folder", "imageName", "provider", "base", "language", true, true, "arg1 arg2", "mount1 mount2"),
                new CreateVolumeCommand("volumeName", "ext2", true, true, "provider", "100", "data"),
                new DetachVolumeCommand("volumeName"),
                new RemoveImageCommand("imageName", true),
                new RemoveInstanceCommand("instanceName", true),
                new RemoveVolumeCommand("volumeName", true),
                new RunInstanceCommand("instanceName", "imageName", "100", true, true, "key1=value1 key2=value2", "volume1:mount1 volume2:mount2"),
                new StartInstanceCommand("instanceName"),
                new StopInstanceCommand("instanceName")
        );
    }


    @Test
    public void testConfigRoundtrip() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();

        project.getBuildersList().add(new UnikBuilder(command));
        project = jenkins.configRoundtrip(project);
        jenkins.assertEqualDataBoundBeans(new UnikBuilder(command), project.getBuildersList().get(0));
        jenkins.assertEqualDataBoundBeans(command, ((UnikBuilder) project.getBuildersList().get(0)).getCommand());
    }
}
