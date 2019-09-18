package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.PullImageCommand;
import org.junit.Before;
import org.junit.Test;

public class PullImageCommandTest extends AbstractUnikCommandTest {

    private UnikHubEndpoint hub;

    @Before
    @Override
    public void setup() throws Exception {
        super.setup();

        hub = new UnikHubEndpoint();
        hub.setCredentialsId("credentialsId");
        hub.setUrl("url");
    }

    @Test
    public void testBuildSuccess() throws Exception {
        PullImageCommand command = new PullImageCommand("imageName", "provider");
        command.setForce(true);
        command.setUnikHubEndpoint(hub);
        successTest(command);
    }

    @Test
    public void testBuildMissingImageNameFailure() throws Exception {
        PullImageCommand command = new PullImageCommand(null, "provider");
        command.setForce(true);
        command.setUnikHubEndpoint(hub);
        failureTest(command, "Image name can not be empty");
    }

    @Test
    public void testBuildMissingProviderFailure() throws Exception {
        PullImageCommand command = new PullImageCommand("imageName", null);
        command.setForce(true);
        command.setUnikHubEndpoint(hub);
        failureTest(command, "Provider can not be empty");
    }
}
