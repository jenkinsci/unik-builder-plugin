package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.PushImageCommand;
import org.junit.Test;

public class PushImageCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildSuccess() throws Exception {
        successTest(new PushImageCommand("imageName", new UnikHubEndpoint("url", "credentialsId")));
    }

    @Test
    public void testBuildMissingImageNameFailure() throws Exception {
        failureTest(new PushImageCommand(null, new UnikHubEndpoint("url", "credentialsId")), "Image name can not be empty");
    }

    @Test
    public void testBuildMissingHubConfigFailure() throws Exception {
        failureTest(new PushImageCommand("imageName", null), "Hub config not valid");
    }
}
