package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.RunInstanceCommand;
import org.junit.Test;

public class RunInstanceCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildAllParamsSuccess() throws Exception {
        RunInstanceCommand command = new RunInstanceCommand("istanceName", "imageName");
        command.setDebug(true);
        command.setNoCleanup(true);
        command.setEnvs("key1=value1 key2=value2");
        command.setMounts("volume1:mount1 volume2:mount2");
        command.setMemoryMb("100");
        successTest(command);
    }

    @Test
    public void testBuildMinimalParamsSuccess() throws Exception {
        RunInstanceCommand command = new RunInstanceCommand("istanceName", "imageName");

        successTest(command);
    }

    @Test
    public void testBuildMemorySizeNaNSuccess() throws Exception {
        RunInstanceCommand command = new RunInstanceCommand("istanceName", "imageName");
        command.setDebug(true);
        command.setNoCleanup(true);
        command.setEnvs("key1=value1 key2=value2");
        command.setMounts("volume1:mount1 volume2:mount2");
        command.setMemoryMb("NaN");
        successTest(command, "Not a valid memory size");
    }

    @Test
    public void testBuildMemorySizeNegativeSuccess() throws Exception {
        RunInstanceCommand command = new RunInstanceCommand("istanceName", "imageName");
        command.setDebug(true);
        command.setNoCleanup(true);
        command.setEnvs("key1=value1 key2=value2");
        command.setMounts("volume1:mount1 volume2:mount2");
        command.setMemoryMb("-1");
        successTest(command, "Not a valid memory size");
    }

    @Test
    public void testBuildMissingInstanceNameFailure() throws Exception {
        RunInstanceCommand command = new RunInstanceCommand(null, "imageName");
        command.setDebug(true);
        command.setNoCleanup(true);
        command.setEnvs("key1=value1 key2=value2");
        command.setMounts("volume1:mount1 volume2:mount2");
        command.setMemoryMb("100");
        failureTest(command, "Instance name can not be empty");
    }

    @Test
    public void testBuildMissingImageNameFailure() throws Exception {
        RunInstanceCommand command = new RunInstanceCommand("istanceName", null);
        command.setDebug(true);
        command.setNoCleanup(true);
        command.setEnvs("key1=value1 key2=value2");
        command.setMounts("volume1:mount1 volume2:mount2");
        command.setMemoryMb("100");
        failureTest(command, "Image name can not be empty");
    }

}
