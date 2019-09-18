package io.jenkins.plugins.unik.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.NotFileFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A Utility class for compressed files
 */
public class CompressUtils {

    /**
     * Compress a folder to a tar.gz archive
     *
     * @param path     the path to the folder to be compressed
     * @param fileName the name of the archive
     * @return the path to the archive
     * @throws IOException if some error occurs during compression
     */
    public static String createTarGz(String path, String fileName) throws IOException {
        TarArchiveOutputStream tOut = null;
        try {
            String tarGzPath = path + File.separator + fileName + ".tar.gz";
            FileOutputStream fOut = new FileOutputStream(new File(tarGzPath));
            tOut = new TarArchiveOutputStream(fOut);

            tOut.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
            tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

            List<File> files = new ArrayList<>(FileUtils.listFiles(
                    new File(path),
                    new NotFileFilter(FileFilterUtils.nameFileFilter(fileName + ".tar.gz")),
                    DirectoryFileFilter.DIRECTORY
            ));

            for (File currentFile : files) {
                String relativeFilePath = new File(path).toURI().relativize(new File(currentFile.getAbsolutePath()).toURI()).getPath();

                TarArchiveEntry tarEntry = new TarArchiveEntry(currentFile, relativeFilePath);
                tarEntry.setSize(currentFile.length());

                tOut.putArchiveEntry(tarEntry);
                tOut.write(IOUtils.toByteArray(new FileInputStream(currentFile)));
                tOut.closeArchiveEntry();
            }
            return tarGzPath;

        } finally {
            if (tOut != null) {
                tOut.finish();
                tOut.close();
            }
        }
    }
}
