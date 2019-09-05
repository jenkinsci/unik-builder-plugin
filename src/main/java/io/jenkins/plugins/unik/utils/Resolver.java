package io.jenkins.plugins.unik.utils;

import hudson.EnvVars;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.util.LogTaskListener;
import hudson.util.VariableResolver;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Convenient class for resolving/expanding various variabales.
 */
public class Resolver {

    private static final Logger LOG = Logger.getLogger(Resolver.class.getName());

    public static String buildVar(final Run<?, ?> build, final String toResolve) {
        if (toResolve == null)
            return null;

        String resolved = toResolve;
        if (build instanceof AbstractBuild) {

            VariableResolver<String> vr = ((AbstractBuild<?, ?>) build).getBuildVariableResolver();
            resolved = Util.replaceMacro(toResolve, vr);
            try {
                EnvVars env = build.getEnvironment(new LogTaskListener(LOG, Level.INFO));
                resolved = env.expand(resolved);
            } catch (Exception e) {
                //TODO no-op?
            }
        }
        return resolved;
    }
}
