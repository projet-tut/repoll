package repoll.server;

import org.apache.log4j.Logger;
import repoll.core.rmi.RmiServiceFacade;
import repoll.server.rmi.RmiServiceFacadeImpl;

import javax.swing.*;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;

/**
 * Server application main entry point
 */
public class Repoll {
    // alternatively could call BasicConfigurator#configure()
    public final static Logger LOG = Logger.getLogger(Repoll.class);

    public static final String REST_SERVICE_URL = "http://localhost:8000";

    public static void main(String[] args) {

        String action = args.length >= 1 ? args[0] : "";
        try {
            switch (action) {
                case "rest":
                    runRestServer();
                    break;
                case "rmi":
                    runRmiServer();
                    break;
                case "gui":
                    runGraphicalClient();
                    break;
                case "user":
                    runCustom();
                    break;
                default:
                    LOG.error("Unknown command: " + action);
                    System.exit(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            LOG.error(e);
        }
    }

    private static void runCustom() {
        throw new UnsupportedOperationException();
    }

    private static void runGraphicalClient() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
//                MainApplication.getInstance().createAndShowGUI();
            }
        });
    }

    private static void runRestServer() throws Exception {
//        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
//                UriBuilder.fromUri(REST_SERVICE_URL).build(),
//                new ResourceConfig(PollsResource.class));
//        LOG.info("REST server started");
//        waitKeyPressed();
//        server.stop();
    }

    private static void runRmiServer() throws Exception {
        RmiServiceFacade serviceFacade = RmiServiceFacadeImpl.getInstance();
        try {
            System.setSecurityManager(new RMISecurityManager());
            Naming.rebind(RmiServiceFacade.SERVICE_URL, serviceFacade);
            LOG.info("RMI server started");
            waitKeyPressed();
        } finally {
            // unbind and unexport service to terminate RMI daemon thread
            try {
                Naming.unbind(RmiServiceFacade.SERVICE_URL);
                UnicastRemoteObject.unexportObject(serviceFacade, true);
            } catch (Exception ignored) {
            }
        }
    }

    private static void waitKeyPressed() throws IOException {
        System.out.println("Press enter to stop");
        try {
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
        } catch (IOException ignored) {
        }
    }
}
