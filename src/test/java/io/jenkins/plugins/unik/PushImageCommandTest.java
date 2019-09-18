package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.PushImageCommand;
import org.junit.Before;
import org.junit.Test;

public class PushImageCommandTest extends AbstractUnikCommandTest {

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
        PushImageCommand command = new PushImageCommand("imageName");
        command.setUnikHubEndpoint(hub);
        successTest(command);
    }

    @Test
    public void testBuildMissingImageNameFailure() throws Exception {
        PushImageCommand command = new PushImageCommand(null);
        command.setUnikHubEndpoint(hub);
        failureTest(command, "Image name can not be empty");
    }

}
