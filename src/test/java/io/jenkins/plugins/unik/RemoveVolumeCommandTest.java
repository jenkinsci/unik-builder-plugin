package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.RemoveVolumeCommand;
import org.junit.Test;

public class RemoveVolumeCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildSuccess() throws Exception {
        RemoveVolumeCommand command = new RemoveVolumeCommand("volumeName");
        command.setForce(true);
        successTest(command);
    }

    @Test
    public void testBuildMissingVolumeNameFailure() throws Exception {
        RemoveVolumeCommand command = new RemoveVolumeCommand(null);
        command.setForce(true);
        failureTest(command, "Volume name can not be empty");
    }
}
