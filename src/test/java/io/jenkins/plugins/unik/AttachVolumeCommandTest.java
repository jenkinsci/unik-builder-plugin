package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.AttachVolumeCommand;
import org.junit.Test;

public class AttachVolumeCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildSuccess() throws Exception {
        successTest(new AttachVolumeCommand("volumeName", "instanceId", "mountPoint"));
    }

    @Test
    public void testBuildMissingVolumeNameFailure() throws Exception {
        failureTest(new AttachVolumeCommand(null, "instanceId", "mountPoint"), "Volume name can not be empty");
    }

    @Test
    public void testBuildMissingInstanceIdFailure() throws Exception {
        failureTest(new AttachVolumeCommand("volumeName", "", "mountPoint"), "Instance id can not be empty");
    }

    @Test
    public void testBuildMissingMountPointFailure() throws Exception {
        failureTest(new AttachVolumeCommand("volumeName", "instanceId", null), "Mount point can not be empty");
    }

}
