package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.DetachVolumeCommand;
import org.junit.Test;

public class DetachVolumeCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildSuccess() throws Exception {
        successTest(new DetachVolumeCommand("volumeName"));
    }

    @Test
    public void testBuildMissingVolumeNameFailure() throws Exception {
        failureTest(new DetachVolumeCommand(null), "Volume name can not be empty");
    }
}
