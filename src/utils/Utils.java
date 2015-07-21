package utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Utils
{
  public static final String LS = System.getProperty("line.separator");

  /**
   * Determines if ImageIO supports the image type of this file and returns a
   * String with the status information. The String starts with "Supported" if
   * the file is supported or is a longer string including suggestions if it is
   * not.
   * 
   * @param file
   * @return a String indicating the status.
   */
  public static String getImageIOSupportedStatus(File file) {
    String info = "";
    // Check the format
    String[] formats = ImageIO.getReaderFormatNames();
    String ext = getExtension(file);
    boolean found = false;
    if(formats != null && formats.length > 0 && ext != null) {
      for(int i = 0; i < formats.length; i++) {
        String format = formats[i];
        if(ext.equals(format)) {
          found = true;
          break;
        }
      }
    }
    if(!found) {
      info += "Extension ." + ext + " represents an unsupported format." + LS;
      String extDir = System.getProperty("java.ext.dirs");
      if(extDir != null) {
        info += "JAI might not be installed.  Check the following" + LS;
        info += "directory(s) for jai_imageio.jar:" + LS;
        info += extDir;
      } else {
        String home = System.getProperty("java.home");
        info += "JAI might not be installed.  Check the following" + LS;
        info += "directory for lib/ext/jai_imageio.jar:" + LS;
        info += home;
      }
    } else {
      info += "Supported";
    }
    return info;
  }

  /**
   * Convenience function to determine if ImageIO supports the image type of
   * this file. Calls getImageIOSupportedStatus(file) and checks the String
   * returned to determine if the file is supported or not.
   * 
   * @param file
   * @return whether the file is supported or not.
   */
  public static boolean isImageIOSupported(File file) {
    String status = getImageIOSupportedStatus(file);
    boolean supported = status.startsWith("Supported");
    return supported;
  }

  /**
   * Generic method to get a file suing a JFileChooder
   * 
   * @param defaultPath
   * @return the File or null if aborted.
   */
  public static File getOpenFile(String defaultPath) {
    File file = null;
    JFileChooser chooser = new JFileChooser();
    if(defaultPath != null) {
      chooser.setCurrentDirectory(new File(defaultPath));
    }
    int result = chooser.showOpenDialog(null);
    if(result == JFileChooser.APPROVE_OPTION) {
      // Save the selected path for next time
      defaultPath = chooser.getSelectedFile().getParentFile().getPath();
      // Process the file
      file = chooser.getSelectedFile();
    }
    return file;
  }

  /**
   * Prints info for a Container and its children to System.out.
   * 
   * @param container
   */
  public static void printComponents(Container container) {
    int level = 0;
    printNextComponent(level, container, System.out);
  }

  /**
   * Prints info for a Component and its children to a PrintStream.
   * 
   * @param container
   * @param out
   */
  public static void printComponents(Container container, PrintStream out) {
    int level = 0;
    printNextComponent(level, container, out);
  }

  private static void printNextComponent(int level, Container container,
    PrintStream out) {
    int n = container.getComponentCount();
    out.println(getIndent(level) + container.getClass() + " [" + n + "]");
    out.println(getIndent(level) + "=" + container);
    out.println(getIndent(level) + "=size=" + container.getSize());
    out.println(getIndent(level) + "=visible=" + container.isVisible());
    out.println(getIndent(level) + "=valid=" + container.isValid());
    out.println(getIndent(level) + "=showing=" + container.isShowing());
    Component[] components = container.getComponents();
    for(int i = 0; i < n; i++) {
      Component component1 = components[i];
      if(component1 instanceof JComponent) {
        printNextComponent(level + 1, (JComponent)component1, out);
      } else {
        int n1 = container.getComponentCount();
        out.println(getIndent(level + 1) + component1.getClass() + " [" + n1
          + "]");
        out.println(getIndent(level + 1) + "=" + component1);
        out.println(getIndent(level + 1) + "=size=" + component1.getSize());
        out
          .println(getIndent(level + 1) + "=visible=" + component1.isVisible());
        out.println(getIndent(level + 1) + "=valid=" + component1.isValid());
        out
          .println(getIndent(level + 1) + "=showing=" + component1.isShowing());
      }
    }
  }

  private static String getIndent(int level) {
    String indent = "  ";
    String string = "";
    for(int i = 0; i < level; i++) {
      string += indent;
    }
    return string;
  }

  /**
   * Error message dialog
   * 
   * @param msg
   */
  public static void errMsg(String msg) {
    // Show it in a message box
    JOptionPane
      .showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    System.out.println(msg);
  }

  /**
   * Exception message dialog. Displays message plus the exception and exception
   * message.
   * 
   * @param msg
   * @param ex
   */
  public static void excMsg(String msg, Exception ex) {
    msg += "\n" + "Exception: " + ex + "\n" + ex.getMessage();
    // Show it in a message box
    JOptionPane
      .showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    System.out.println(msg);
  }

  /**
   * Exception message dialog. Displays message plus the exception and exception
   * message.
   * 
   * @param msg
   * @param ex
   */
  public static void throwMsg(String msg, Throwable ex) {
    msg += "\n" + "Throwable: " + ex + "\n" + ex.getMessage();
    // Show it in a message box
    JOptionPane
      .showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    System.out.println(msg);
  }

  /**
   * Warning message dialog
   * 
   * @param msg
   */
  public static void warnMsg(String msg) {
    // Show it in a message box
    JOptionPane.showMessageDialog(null, msg, "Warning",
      JOptionPane.WARNING_MESSAGE);
    System.out.println(msg);
  }

  /**
   * Information message dialog
   * 
   * @param msg
   */
  public static void infoMsg(String msg) {
    // Show it in a message box
    JOptionPane.showMessageDialog(null, msg, "Information",
      JOptionPane.INFORMATION_MESSAGE);
    System.out.println(msg);
  }

  public static File savePanelToFile(JPanel panel, String defaultPath) {
    File file = null;
    if(panel == null) return null;

    // Get the file name
    JFileChooser chooser = new JFileChooser();
    if(defaultPath != null) {
      chooser.setCurrentDirectory(new File(defaultPath));
    }
    // chooser.addChoosableFileFilter(new GifFilter());
    int result = chooser.showSaveDialog(panel);
    if(result != JFileChooser.APPROVE_OPTION) return file;
    // Process the file
    String fileName = chooser.getSelectedFile().getPath();
    file = new File(fileName);
    if(file.exists()) {
      int selection = JOptionPane.showConfirmDialog(panel,
        "File already exists:\n" + fileName + "\nOK to replace?", "Warning",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
      if(selection != JOptionPane.OK_OPTION) return null;
    }

    // Check the format
    String[] formats = ImageIO.getReaderFormatNames();
    String ext = getExtension(file);
    boolean found = false;
    if(formats != null && formats.length > 0 && ext != null) {
      for(int i = 0; i < formats.length; i++) {
        String format = formats[i];
        if(ext.equals(format)) {
          found = true;
          break;
        }
      }
    }
    if(!found) {
      errMsg("Extension [" + ext + "] represents an invalid format");
      return file;
    }

    // Get the image and write it
    try {
      BufferedImage image = getBufferedImageFromPanel(panel);
      ImageIO.write(image, ext, file);
      image.flush();
    } catch(Exception ex) {
      errMsg("Capture to file failed:\n" + "File: " + file + "\n" + ex + "\n"
        + ex.getMessage());
    }

    return file;
  }

  public static File savePanelToClipboard(JPanel panel) {
    File file = null;
    if(panel == null) return null;

    // Get the image
    try {
      BufferedImage image = getBufferedImageFromPanel(panel);
      Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
      ImageSelection sel = new ImageSelection(image);
      clip.setContents(sel, null);
    } catch(Exception ex) {
      errMsg("Failed to get image\n" + ex + "\n" + ex.getMessage());
    }

    return file;
  }

  public static BufferedImage getBufferedImageFromPanel(JPanel panel) {
    BufferedImage image = null;
    image = new BufferedImage(panel.getWidth(), panel.getHeight(),
      BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
      RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    panel.paint(g);
    g.dispose();

    return image;
  }

  /**
   * Get the extension for a file.
   * 
   * @param f
   * @return
   */
  public static String getExtension(File file) {
    String ext = null;
    String s = file.getName();
    int i = s.lastIndexOf('.');
    if(i > 0 && i < s.length() - 1) {
      ext = s.substring(i + 1).toLowerCase();
    }
    return ext;
  }

}
