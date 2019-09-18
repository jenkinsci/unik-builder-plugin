package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.RemoveInstanceCommand;
import org.junit.Test;

public class RemoveInstanceCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildSuccess() throws Exception {
        RemoveInstanceCommand command = new RemoveInstanceCommand("instanceName");
        command.setForce(true);
        successTest(command);
    }

    @Test
    public void testBuildMissingInstanceNameFailure() throws Exception {
        RemoveInstanceCommand command = new RemoveInstanceCommand(null);
        command.setForce(true);
        failureTest(command, "Instance name can not be empty");
    }
}
