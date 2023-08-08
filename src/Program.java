import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Program {

    public static void main(final String[] args) throws SQLException {

        final String otlpEndpoint = System.getenv("OTLP-TEST");

        final Logger rootLogger = LogManager.getLogManager().getLogger("");
        //rootLogger.setLevel(Level.FINE);
        for (final Handler h : rootLogger.getHandlers()) {
            //h.setLevel(Level.FINE);
        }

        final Logger logger = Logger.getLogger(Program.class.getName());
        logger.log(Level.INFO, "Test log at INFO");
        logger.log(Level.FINE, "Test log at FINE");

        try {

            System.out.println("running otlp test");
            final OtlpTest otlp = new OtlpTest();
            otlp.start(otlpEndpoint);
            otlp.addCounter(0); // send an initial value

            final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                final String input = in.readLine().trim();
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting");
                    break;
                }
                try {
                    final int value = Integer.valueOf(input);
                    otlp.addCounter(value);
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println("running otlp test done");
            otlp.stop();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        System.out.println("the end");
    }
}
