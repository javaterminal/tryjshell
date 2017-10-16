package com.kodedu.tryjshell.helper;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by usta on 12.09.2016.
 */
public class IOHelper {

    public static void close(Closeable... closables) {
        for (Closeable closable : closables) {
            try {
                closable.close();
            } catch (Exception e) {

            }
        }
    }

    public static synchronized void copyLibPty(Path dataDir) {

        Path donePath = dataDir.resolve(".DONE");

        if (Files.exists(donePath)) {
            return;
        }

        Set<String> nativeFiles = getNativeFiles();

        for (String nativeFile : nativeFiles) {
            Path nativePath = dataDir.resolve(nativeFile);

            if (Files.notExists(nativePath)) {
                try {
                    Files.createDirectories(nativePath.getParent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream inputStream = IOHelper.class.getResourceAsStream("/" + nativeFile);
                try {
                    Files.copy(inputStream, nativePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                close(inputStream);
            }

        }

        try {
            Files.createFile(donePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Set<String> getNativeFiles() {

        final Set<String> nativeFiles = new HashSet<>();

        List<String> freebsd = Arrays.asList("libpty/freebsd/x86/libpty.so", "libpty/freebsd/x86_64/libpty.so");
        List<String> linux = Arrays.asList("libpty/linux/x86/libpty.so", "libpty/linux/x86_64/libpty.so");
        List<String> macosx = Arrays.asList("libpty/macosx/x86/libpty.dylib", "libpty/macosx/x86_64/libpty.dylib");
        List<String> win_x86 = Arrays.asList("libpty/win/x86/winpty.dll", "libpty/win/x86/winpty-agent.exe");
        List<String> win_x86_64 = Arrays.asList("libpty/win/x86_64/winpty.dll", "libpty/win/x86_64/winpty-agent.exe", "libpty/win/x86_64/cyglaunch.exe");
        List<String> win_xp = Arrays.asList("libpty/win/xp/winpty.dll", "libpty/win/xp/winpty-agent.exe");

        nativeFiles.addAll(freebsd);
        nativeFiles.addAll(linux);
        nativeFiles.addAll(macosx);
        nativeFiles.addAll(win_x86);
        nativeFiles.addAll(win_x86_64);
        nativeFiles.addAll(win_xp);

        return nativeFiles;
    }

}
