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

    public static void main(String[] args) {
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
        mainThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            App.shutdown();
            mainThread.interrupt();
        }));
        App.init();
    }
}
