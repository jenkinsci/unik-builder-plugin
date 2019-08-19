package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.RemoveVolumeCommand;
import org.junit.Test;

public class RemoveVolumeCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildSuccess() throws Exception {
        successTest(new RemoveVolumeCommand("volumeName", true));
    }

    @Test
    public void testBuildMissingVolumeNameFailure() throws Exception {
        failureTest(new RemoveVolumeCommand(null, true), "Volume name can not be empty");
    }
}
