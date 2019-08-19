package io.jenkins.plugins.unik.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CompressUtils {

    public static String CreateTarGz(String path, String fileName) throws IOException {
        TarArchiveOutputStream tOut = null;
        try {
            String tarGzPath = path+File.separator+fileName+".tar.gz";
            FileOutputStream fOut = new FileOutputStream(new File(tarGzPath));
         //   BufferedOutputStream bOut = new BufferedOutputStream(fOut);
         //   GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(fOut);
            tOut = new TarArchiveOutputStream(fOut);

            tOut.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
            tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

            List<File> files = new ArrayList<>(FileUtils.listFiles(
                    new File(path),
                    new NotFileFilter(FileFilterUtils.nameFileFilter(fileName+".tar.gz")),
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
