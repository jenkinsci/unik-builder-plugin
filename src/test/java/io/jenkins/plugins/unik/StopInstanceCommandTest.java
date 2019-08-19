package io.jenkins.plugins.unik;

import io.jenkins.plugins.unik.cmd.StopInstanceCommand;
import org.junit.Test;

public class StopInstanceCommandTest extends AbstractUnikCommandTest {

    @Test
    public void testBuildSuccess() throws Exception {
        successTest(new StopInstanceCommand("instanceName"));
    }

    @Test
    public void testBuildMissingInstanceNameFailure() throws Exception {
        failureTest(new StopInstanceCommand(null), "Instance name can not be empty");
    }
}
