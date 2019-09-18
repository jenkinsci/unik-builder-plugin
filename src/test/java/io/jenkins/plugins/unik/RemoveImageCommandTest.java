package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.RemoveImageCommand;
import org.junit.Test;

public class RemoveImageCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildSuccess() throws Exception {
        RemoveImageCommand command = new RemoveImageCommand("imageName");
        command.setForce(true);
        successTest(command);
    }

    @Test
    public void testBuildMissingImageNameFailure() throws Exception {
        RemoveImageCommand command = new RemoveImageCommand(null);
        command.setForce(true);
        failureTest(command, "Image name can not be empty");
    }
}
