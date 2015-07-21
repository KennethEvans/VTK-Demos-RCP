/*
 * Created on Oct 23, 2009
 * By Kenneth Evans, Jr.
 */

package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

public class JavaInfo
{
  /**
   * Prints all system properties to a PrintStream.
   * 
   * @param ps is the name of the PrintStream
   * @throws IOException
   */
  public static void getSystemProperties(PrintStream ps) throws IOException {
    Properties props = System.getProperties();
    props.store(ps, "System Properties");
  }

  /**
   * Gets Java info.
   */
  public static String getInfo() {
    String[] properties = {"java.home", "user.dir"};
    ByteArrayOutputStream baos = null;
    PrintStream ps = null;
    try {
      baos = new ByteArrayOutputStream();
      if(baos == null) {
        return "Could not create ByteArrayOutputStream for Java Info";
      }
      ps = new PrintStream(baos);
      if(ps == null) {
        return "Could not create PrintStream for Java Info";
      }
      String path = System.getProperty("java.library.path");
      String split[] = path.split(";");
      if(path != null) {
        ps.println("java.library.path:");
        for(int i = 0; i < split.length; i++) {
          ps.println("  " + split[i]);
        }
      }
      String property, value;
      for(int i = 0; i < properties.length; i++) {
        ps.println();
        property = properties[i];
        value = System.getProperty(property, "<undefined>");
        ps.println(property + ": " + value);
      }

      // Get the system properties
      ps.println();
      ps.println("All Defined Properties");
      ps.println();
      getSystemProperties(ps);
    } catch(Throwable t) {
      if(ps != null) {
        ps.println();
        ps.println("Error getting Java info:");
        ps.println(t.toString());
        t.printStackTrace(ps);
      } else {
        return "Could not get Java Info: " + t.toString();
      }
    } finally {
      if(ps != null) {
        ps.close();
      }
    }
    return baos == null ? "Could not get Java Info" : baos.toString();
  }

}
