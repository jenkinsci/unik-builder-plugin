package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.StartInstanceCommand;
import org.junit.Test;

public class StartInstanceCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildSuccess() throws Exception {
        successTest(new StartInstanceCommand("instanceName"));
    }

    @Test
    public void testBuildMissingInstanceNameFailure() throws Exception {
        failureTest(new StartInstanceCommand(null), "Instance name can not be empty");
    }
}
