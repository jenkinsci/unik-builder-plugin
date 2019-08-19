package io.jenkins.plugins.unik.log;

import hudson.console.LineTransformationOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Console annotator which annotates Unik messages using {@link UnikConsoleNote}. Annotated message has to start
 * with <i>[Unik]</i> prefix.
 * 
 * @see {@link http://javadoc.jenkins-ci.org/hudson/console/LineTransformationOutputStream.html LineTransformationOutputStream} 
 *
 */
public class UnikConsoleAnnotator extends LineTransformationOutputStream {

    private final OutputStream out;

    public UnikConsoleAnnotator(OutputStream out) {
        this.out = out;
    }

    @Override
    protected void eol(byte[] b, int len) throws IOException {
        String line = Charset.defaultCharset().decode(ByteBuffer.wrap(b, 0, len)).toString();
        if (line.startsWith("[Unik]"))
            new UnikConsoleNote().encodeTo(out);
        out.write(b, 0, len);
    }

    @Override
    public void close() throws IOException {
        super.close();
        out.close();
    }

}
