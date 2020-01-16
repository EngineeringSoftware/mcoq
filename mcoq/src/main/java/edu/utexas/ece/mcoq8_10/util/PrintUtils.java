package edu.utexas.ece.mcoq8_10.util;

/**
 * Util methods for threadsafe printing.
 *
 * @author Kush Jain <kjain14@utexas.edu>
 */
public class PrintUtils {

    public static void safePrintln(String ...text) {
        synchronized (System.out) {
            for (String component : text) {
                if (component != null) {
                    System.out.println(component);
                }
            }
        }
    }

}
