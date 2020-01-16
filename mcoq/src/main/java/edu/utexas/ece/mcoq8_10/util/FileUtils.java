package edu.utexas.ece.mcoq8_10.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static edu.utexas.ece.mcoq8_10.util.PrintUtils.safePrintln;

/**
 * Util methods for processing files.
 *
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class FileUtils {

    public static Stream<String> streamOfLines(File file) throws IOException {
        return Files.lines(file.toPath());
    }

    public static String changeExtension(String filename, String oldExt, String newExt) {
        return filename.replaceAll("\\." + oldExt + "$", "." + newExt);
    }

    public static void backupAll(Collection<String> vfiles) throws IOException {
        for (String vfile : vfiles) {
            backupVOFile(vfile);
        }
    }

    public static void revertVOFile(String vfile) throws IOException {
        final String vofile = changeExtension(vfile, "v", "vo");
        final String vorgfile = changeExtension(vfile, "v", "vorg");
        Files.copy(Paths.get(vorgfile), Paths.get(vofile), REPLACE_EXISTING);
    }

    public static void backupVOFile(String vfile) throws IOException {
        final String vofile = changeExtension(vfile, "v", "vo");
        final String vorgfile = changeExtension(vfile, "v", "vorg");
        Files.copy(Paths.get(vofile), Paths.get(vorgfile), REPLACE_EXISTING);
    }

    public static void copyDirectory(Path source, Path destination) throws IOException {
        Files.walk(source).forEach(src -> {
            try {
                Files.copy(src, destination.resolve(source.relativize(src)), REPLACE_EXISTING);
            } catch (IOException e) {
                safePrintln("ERROR: " + e.getMessage());
                System.exit(1);
            }
        });
    }

    public static boolean deleteDirectory(Path directory) {
        try {
            Files.newDirectoryStream(directory).forEach( path -> {
                if (path.toFile().isDirectory()) {
                    deleteDirectory(path);
                } else {
                    path.toFile().delete();
                }
            });
            directory.toFile().delete();
        } catch (IOException e) {
            safePrintln("WARNING: Could not delete directory " + directory);
            return false;
        }
        return true;
    }
}
