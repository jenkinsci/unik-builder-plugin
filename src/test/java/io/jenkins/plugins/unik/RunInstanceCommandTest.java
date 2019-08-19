package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.RunInstanceCommand;
import org.junit.Test;

public class RunInstanceCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildAllParamsSuccess() throws Exception {
        successTest(new RunInstanceCommand("instanceName", "imageName", "100", true, true, "key1=value1 key2=value2", "volume1:mount1 volume2:mount2"));
    }

    @Test
    public void testBuildMinimalParamsSuccess() throws Exception {
        successTest(new RunInstanceCommand("instanceName", "imageName", null, true, true, null, null));
    }

    @Test
    public void testBuildMemorySizeNaNSuccess() throws Exception {
        successTest(new RunInstanceCommand("instanceName", "imageName", "NaN", true, true, null, null), "Not a valid memory size");
    }

    @Test
    public void testBuildMemorySizeNegativeSuccess() throws Exception {
        successTest(new RunInstanceCommand("instanceName", "imageName", "-1", true, true, null, null), "Not a valid memory size");
    }

    @Test
    public void testBuildMissingInstanceNameFailure() throws Exception {
        failureTest(new RunInstanceCommand(null, "imageName", null, true, true, null, null), "Instance name can not be empty");
    }

    @Test
    public void testBuildMissingImageNameFailure() throws Exception {
        failureTest(new RunInstanceCommand("instanceName", null, null, true, true, null, null), "Image name can not be empty");
    }

}
