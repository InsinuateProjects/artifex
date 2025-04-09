package ink.ptms.artifex.appside;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import taboolib.common.PrimitiveIO;
import taboolib.common.TabooLib;
import taboolib.common.platform.PlatformFactory;
import taboolib.common.platform.service.PlatformIO;
import taboolib.platform.App;
import taboolib.platform.AppEnv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Handler;


/**
 * Artifex
 * ink.ptms.artifex.appside.Main
 *
 * @author scorez
 * @since 1/15/25 21:00.
 */
public class Main {

    public static Logger logger = LogManager.getLogger(Main.class);

    public static Thread mainThread;

    public static List<String> requestProjects = new ArrayList<>();

    public static void main(String[] args) {
//        System.setProperty("taboolib.dev", "true");
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
        System.setErr(IoBuilder.forLogger("SYSTEM_ERR").setLevel(Level.ERROR).buildPrintStream());
        System.setOut(IoBuilder.forLogger("SYSTEM_OUT").setLevel(Level.INFO).buildPrintStream());
        mainThread = new Thread(() -> {
            System.out.println("Main thread started.");
            while (!TabooLib.isStopped()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        requestProjects.addAll(Arrays.asList(args));
        if (requestProjects.isEmpty()) {
            mainThread.start();
        } else {
            StringJoiner joiner = new StringJoiner(",");
            requestProjects.forEach(joiner::add);
            System.out.println("artifex will build script for projects " + joiner);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            App.shutdown();
            mainThread.interrupt();
        }));
        App.init();
    }
}
