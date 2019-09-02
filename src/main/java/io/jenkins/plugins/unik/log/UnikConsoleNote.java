package io.jenkins.plugins.unik.log;

import hudson.Extension;
import hudson.MarkupText;
import hudson.console.ConsoleAnnotationDescriptor;
import hudson.console.ConsoleAnnotator;
import hudson.console.ConsoleNote;
import hudson.model.Run;

/**
 * Annotator which adds color highlighting. There are three message categories: error, staring with <i>ERROR:</i> prefix,
 * waring, starting with <i>WARN:</i> prefix, and info, which starts with <i>INFO:</i> prefix.
 */
public class UnikConsoleNote extends ConsoleNote<Run<?, ?>> {

    private static final long serialVersionUID = 1L;

    @Override
    public ConsoleAnnotator<Run<?, ?>> annotate(Run<?, ?> context, MarkupText text, int charPos) {
        if (text.getText().contains("ERROR:"))
            text.addMarkup(0, text.length(), "<span style=\"font-weight: bold; color:red\">", "</span>");
        if (text.getText().contains("WARN:"))
            text.addMarkup(0, text.length(), "<span style=\"color:#FF8700\">", "</span>");
        if (text.getText().contains("INFO:"))
            text.addMarkup(0, text.length(), "<span style=\"color:#008BB8\">", "</span>");
        return null;
    }

    @Extension
    public static final class DescriptorImpl extends ConsoleAnnotationDescriptor {
        public String getDisplayName() {
            return "Unik Console Note";
        }
    }
}
