package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.PullImageCommand;
import org.junit.Test;

public class PullImageCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildSuccess() throws Exception {
        successTest(new PullImageCommand("imageName", "provider", true, new UnikHubEndpoint("url", "credentialsId")));
    }

    @Test
    public void testBuildMissingImageNameFailure() throws Exception {
        failureTest(new PullImageCommand(null, "provider", true, new UnikHubEndpoint("url", "credentialsId")), "Image name can not be empty");
    }

    @Test
    public void testBuildMissingProviderFailure() throws Exception {
        failureTest(new PullImageCommand("imageName", null, true, new UnikHubEndpoint("url", "credentialsId")), "Provider can not be empty");
    }

    @Test
    public void testBuildMissingHubConfigFailure() throws Exception {
        failureTest(new PullImageCommand("imageName", "provider", true, null), "Hub config not valid");
    }
}
