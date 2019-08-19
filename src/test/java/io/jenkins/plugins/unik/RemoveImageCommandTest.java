package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.RemoveImageCommand;
import org.junit.Test;

public class RemoveImageCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildSuccess() throws Exception {
        successTest(new RemoveImageCommand("imageName", true));
    }

    @Test
    public void testBuildMissingImageNameFailure() throws Exception {
        failureTest(new RemoveImageCommand(null, true), "Image name can not be empty");
    }
}
