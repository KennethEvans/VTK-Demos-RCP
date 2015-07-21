package demolauncher;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import utils.JavaInfo;
import utils.Utils;
import vtk.vtkFileOutputWindow;
import vtk.vtkOutputWindow;
import vtk.vtkVersion;
import vtkdemos.AreaDetector1Demo;
import vtkdemos.AreaDetector2Demo;
import vtkdemos.AreaDetector3Demo;
import vtkdemos.Axes2Demo;
import vtkdemos.AxesDemo;
import vtkdemos.ColorIsosurfaceDemo;
import vtkdemos.ConeDemo;
import vtkdemos.CubesDemo;
import vtkdemos.Distribution2DDemo;
import vtkdemos.FlamingoDemo;
import vtkdemos.IsoSurfaceDemo;
import vtkdemos.ParametricDemo;
import vtkdemos.Plot2DDemo;
import vtkdemos.PolyDataDemo;
import vtkdemos.ProgrammableFilterDemo;
import vtkdemos.ProgrammableFilterDemo1;
import vtkdemos.ProgrammableFilterDemo2;
import vtkdemos.ProgrammableSourceDemo;
import vtkdemos.ProgrammableSourceDemo1;
import vtkdemos.ReconstructedSurfaceDemo;
import vtkdemos.SphereDemo;
import vtkdemos.StructuredGridDemo;
import vtkdemos.StructuredPointsDemo;
import vtkdemos.Surface2D1Demo;
import vtkdemos.Surface2D2Demo;
import vtkdemos.Surface2D3Demo;
import vtkdemos.Surface2D4Demo;
import vtkdemos.Surface2DDemo;
import vtkdemos.TIFFDemo;
import vtkdemos.VRMLDemo;

public class VTKDemoLauncher extends JFrame
{
  public static final String LS = System.getProperty("line.separator");
  private final boolean USE_VTK_FILE_OUTPUT = false;
  private static final long serialVersionUID = 1L;
  private static final int WIDTH = 600;
  private static final int HEIGHT = WIDTH;
  private static final VTKDemo[] demos = new VTKDemo[] {new ConeDemo(),
    new AxesDemo(), new CubesDemo(), new Axes2Demo(), new SphereDemo(),
    new Plot2DDemo(), new TIFFDemo(), new FlamingoDemo(),
    new AreaDetector1Demo(), new AreaDetector2Demo(), new AreaDetector3Demo(),
    new VRMLDemo(), new ParametricDemo(), new ColorIsosurfaceDemo(),
    new ProgrammableFilterDemo(), new ProgrammableFilterDemo1(),
    new ProgrammableFilterDemo2(), new ReconstructedSurfaceDemo(),
    new ProgrammableSourceDemo(), new ProgrammableSourceDemo1(),
    new PolyDataDemo(), new StructuredPointsDemo(), new StructuredGridDemo(),
    new IsoSurfaceDemo(), new Distribution2DDemo(), new Surface2DDemo(),
    new Surface2D1Demo(), new Surface2D2Demo(), new Surface2D3Demo(),
    new Surface2D4Demo(),};

  private VTKDemoLauncher launcher = this;
  private VTKDemo demo = null;
  private String lastDirectory = null;
  private Container contentPane = this.getContentPane();
  private JPanel listPanel = new JPanel();
  private JPanel displayPanel = new JPanel();
  private JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
    listPanel, displayPanel);
  private JList list = new JList(demos);
  private JMenuBar menuBar;
  private JMenu menuFile;
  private JMenuItem menuFileResetCurrent;
  private JMenuItem menuFileInfo;
  private JMenuItem menuFileJavaInfo;
  private JMenuItem menuFileDebugInfo;
  private JMenuItem menuFileCopyToClipboard;
  private JMenuItem menuFileSaveAs;
  private JMenuItem menuFileExit;
  private JMenu menuHelp;
  private JMenuItem menuHelpHelp;
  private boolean useVtkFileOutput = USE_VTK_FILE_OUTPUT;
  private vtkFileOutputWindow fileOutputWindow = null;
  private vtkOutputWindow ouputWindow = null;

  // FIXME This causes problems in an RCP application as other classes, vtkPanel
  // in particular, also does this. The result is that the classes may be opened
  // in different class loaders (not allowed).
  // It apparently is no longer needed here since it is in vtkPanel, but this
  // should be checked. It causes problem with the RCP implementation.
  // Avoids java.lang.UnsatisfiedLinkError: VTKInit
  static {
    // DEBUG
    if(false) {
      System.out.println("VTKDemoLauncher static initializer");
      System.out.println("Thread: " + Thread.currentThread());
      System.out.println("ClassLoader: "
        + VTKDemoLauncher.class.getClassLoader());

      ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
      System.out.println("SystemClassLoader: " + sysClassLoader);
      String[] propNames = {"java.home", "java.system.class.loader"};
      String name;
      for(int i = 0; i < propNames.length; i++) {
        name = System.getProperty(propNames[i], "<Not found>");
        System.out.println(propNames[i] + ": " + name);
      }
      if(true) {
        // Causes problems not filling the list box
        String path = System.getProperty("java.library.path");
        String split[] = path.split(";");
        String msg = "";
        if(path != null) {
          msg += "java.library.path: \n";
          for(int i = 0; i < split.length; i++) {
            msg += "  " + split[i] + "\n";
          }
        }
        System.out.println(msg);
      }
      if(false) {
        String mapName = System.mapLibraryName("vtkCommonJava");
        System.out.println("System.mapLibraryName(\"vtkCommonJava\"): "
          + mapName);
      }
    }
    // DEBUG
    if(false) {
      try {
        System.loadLibrary("vtkCommonJava");
        System.loadLibrary("vtkFilteringJava");
        System.loadLibrary("vtkIOJava");
        System.loadLibrary("vtkImagingJava");
        System.loadLibrary("vtkGraphicsJava");
        System.loadLibrary("vtkRenderingJava");
        try {
          System.loadLibrary("vtkHybridJava");
        } catch(Throwable t) {
          String msg = "Cannot load vtkHybrid, skipping...\n" + t.getMessage();
          Utils.errMsg(msg);
          System.out.println(msg);
        }
        try {
          System.loadLibrary("vtkVolumeRenderingJava");
        } catch(Throwable t) {
          String msg = "Cannot load vtkVolumeRendering, skipping...\n"
            + t.getMessage();
          Utils.errMsg(msg);
          System.out.println(msg);
        }
        // DEBUG
        if(false) {
          // Causes problems not filling the list box
          String msg = "VTKDemoLauncher\n";
          String path = System.getProperty("java.library.path");
          String split[] = path.split(";");
          if(path != null) {
            msg += "\njava.library.path: \n";
            for(int i = 0; i < split.length; i++) {
              msg += "  " + split[i] + "\n";
            }
          }
          Utils.warnMsg(msg);
          System.out.println(msg);
        }
      } catch(Throwable t) {
        String msg = "Problems loading VTK libraries\n" + t.getMessage();
        String path = System.getProperty("java.library.path");
        String split[] = path.split(";");
        if(path != null) {
          msg += "\njava.library.path: \n";
          for(int i = 0; i < split.length; i++) {
            msg += "  " + split[i] + "\n";
          }
        }
        Utils.errMsg(msg);
        System.out.println(msg);
      }
    }
  }

  /**
   * VDemoLauncher default constructor. Same as VDemoLauncher(true, null);
   */
  public VTKDemoLauncher() {
    this(true, null);
  }

  /**
   * VDemoLauncher constructor.
   * 
   * @param addMenuBar Add the menu bar if true.
   * @param contentPane The content pane to use. If it is null, then use the
   *          default one.
   */
  public VTKDemoLauncher(boolean addMenuBar, Container contentPane) {
    if(contentPane == null)
      contentPane = this.getContentPane();
    else
      this.contentPane = contentPane;
    if(addMenuBar) {
      this.setTitle("VTK Demos");
      menuInit();
    }
    contentInit();

    // Set the vtkFileOutPutWindow
    if(useVtkFileOutput) setVtkFileOutputOn();
  }

  /**
   * Initializes the menu bar.
   */
  private void menuInit() {
    menuBar = new JMenuBar();
    menuFile = new JMenu();
    menuFileResetCurrent = new JMenuItem();
    menuFileInfo = new JMenuItem();
    menuFileJavaInfo = new JMenuItem();
    menuFileDebugInfo = new JMenuItem();
    menuFileCopyToClipboard = new JMenuItem();
    menuFileSaveAs = new JMenuItem();
    menuFileExit = new JMenuItem();
    menuHelp = new JMenu();
    menuHelpHelp = new JMenuItem();

    // Menu
    this.setJMenuBar(menuBar);

    menuFile.setText("File");
    menuBar.add(menuFile);

    // File Reset
    menuFileResetCurrent.setText("Reset Current");
    menuFileResetCurrent.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        resetCurrent();
      }
    });
    menuFile.add(menuFileResetCurrent);

    // File Info
    menuFileInfo.setText("Info...");
    menuFileInfo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        info();
      }
    });
    menuFile.add(menuFileInfo);

    menuFile.add(new JSeparator());

    // File Copy to Clipboard
    menuFileCopyToClipboard.setText("Copy to Clipboard");
    menuFileCopyToClipboard.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        copyToClipboard();
      }
    });
    menuFile.add(menuFileCopyToClipboard);

    // File Save as
    menuFileSaveAs.setText("Save As...");
    menuFileSaveAs.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        saveAs();
      }
    });
    menuFile.add(menuFileSaveAs);

    menuFile.add(new JSeparator());

    // File Java Info
    menuFileJavaInfo.setText("Java Info...");
    menuFileJavaInfo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        javaInfo();
      }
    });
    menuFile.add(menuFileJavaInfo);

    // File Debug Info
    menuFileDebugInfo.setText("Debug Info...");
    menuFileDebugInfo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        debugInfo();
      }
    });
    menuFile.add(menuFileDebugInfo);

    menuFile.add(new JSeparator());

    // File Exit
    menuFileExit.setText("Exit");
    menuFileExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        quit();
      }
    });
    menuFile.add(menuFileExit);

    menuHelp.setText("Help");
    menuBar.add(menuHelp);

    // Help help
    menuHelpHelp.setText("Help");
    menuHelpHelp.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        help();
      }
    });
    menuHelp.add(menuHelpHelp);
  }

  /**
   * Initializes the content pane.
   */
  private void contentInit() {
    // Main split pane
    mainPane.setContinuousLayout(true);
    if(false) {
      mainPane.setOneTouchExpandable(true);
    }

    // List panel
    listPanel.setLayout(new BorderLayout());
    listPanel.add(list, BorderLayout.CENTER);
    list.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent ev) {
        onListValueChanged(ev);
      }
    });

    // Display panel
    displayPanel.setLayout(new BorderLayout());
    displayPanel.setPreferredSize(new Dimension(HEIGHT, WIDTH));

    // Content pane
    contentPane.setLayout(new BorderLayout());
    contentPane.add(mainPane, BorderLayout.CENTER);
  }

  /**
   * Displays info.
   */
  public void info() {
    if(displayPanel == null || demo == null) return;
    Utils.infoMsg(demo.getInfo());
  }

  /**
   * Displays a scrolled text dialog with the given message.
   * 
   * @param message
   */
  public void scrolledTextMsg(String message) {
    final JDialog dialog = new JDialog(this);

    // Message
    JPanel jPanel = new JPanel();
    JTextArea textArea = new JTextArea(message);
    textArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(textArea);
    jPanel.add(scrollPane, BorderLayout.CENTER);
    dialog.getContentPane().add(scrollPane);

    // Close button
    jPanel = new JPanel();
    JButton button = new JButton("OK");
    jPanel.add(button);
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dialog.setVisible(false);
        dialog.dispose();
      }

    });
    dialog.getContentPane().add(jPanel, BorderLayout.SOUTH);

    // Settings
    dialog.setTitle("Java Information");
    dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    dialog.setSize(500, 500);
    // Has to be done after set size
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Displays Java info.
   */
  public void javaInfo() {
    // The info can be very wide and screw up the computer, so we make a custom
    // dialog
    String message = JavaInfo.getInfo();
    scrolledTextMsg(message);
  }

  /**
   * Resets the current demo.
   */
  public void resetCurrent() {
    if(displayPanel == null || demo == null) return;
    demo.setCreated(false);
    onListValueChanged(null);
  }

  /**
   * Displays info from the current demo.
   */
  public void displayInfo() {
    if(demo == null) {
      Utils.errMsg("No demo");
      return;
    }
    Utils.infoMsg(demo.getInfo());
  }

  /**
   * Displays debug info. Expected to be modified as needed.
   */
  public void debugInfo() {
    // Where to put the output "file" "stdout" "dialog"
    String outputDest = "dialog";
    String fileName = "c:\\scratch\\vtk_rcp.temp";
    String info = getDebugInfo();
    // Decide if anything was generated
    if(info.length() == 0) {
      Utils.infoMsg("No information available");
      return;
    }

    // Decide where to print it
    if(outputDest.equals("file")) {
      try {
        PrintWriter out = new PrintWriter(new FileWriter(fileName, true));
        Date now = new Date();
        SimpleDateFormat defaultFormatter = new SimpleDateFormat(
          "MMM dd HH:mm:ss");
        out.print(defaultFormatter.format(now) + "\n");
        out.print(info + "\n");
        out.close();
      } catch(Exception ex) {
        Utils.errMsg("Error writing " + fileName);
      }
      Utils.infoMsg("Debug information was printed to " + fileName);
    } else if(outputDest.equals("file")) {
      System.out.println(info);
      Utils.infoMsg("Debug information was printed to stdout");
    } else {
      scrolledTextMsg(info);
    }
  }

  /**
   * Returns information about the  VTK version.
   */
  public static String getVTKVersionInfo() {
    String info = "";
    vtkVersion version = new vtkVersion();
    info += "VTK Version\n";
    info += "  Version: " + version.GetVTKVersion() + "\n";
    info += "  Source Version: " + version.GetVTKSourceVersion() + "\n";
    info += "  Major Version: " + version.GetVTKMajorVersion() + "\n";
    info += "  Minor Version: " + version.GetVTKMinorVersion() + "\n";
    info += "  Build Version: " + version.GetVTKBuildVersion() + "\n";
    return info;
  }

  /**
   * Displays debug info. Expected to be modified as needed.
   */
  public String getDebugInfo() {
    String info = "";

    if(true) {
      info += getVTKVersionInfo();
    }

    // Print some properties
    if(false) {
      info += "\nProperties:\n";
      try {
        String[] name = {"user.dir", "java.version", "java.vm.version",
          "java.vm.home", "java.ext.dirs", "sun.boot.class.path",
          "sun.boot.library.path",};
        for(int i = 0; i < name.length; i++) {
          String value = System.getProperty(name[i]);
          String line = "  " + name[i] + ": " + value + "\n";
          info += line;
        }
      } catch(Exception ex) {
        info += "Get Property info failed: " + ex.getMessage() + "\n";
      }
    }

    return info;
  }

  /**
   * Saves the display frame to the clipboard
   */
  public void copyToClipboard() {
    if(displayPanel == null) return;
    Cursor oldCursor = getCursor();
    try {
      launcher.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      Utils.savePanelToClipboard(displayPanel);
    } catch(Exception ex) {
      Utils.excMsg("copy failed:", ex);
    } finally {
      setCursor(oldCursor);
    }
  }

  /**
   * Saves the display panel to a file
   */
  public void saveAs() {
    if(displayPanel == null) return;
    Cursor oldCursor = getCursor();
    try {
      launcher.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      File file = Utils.savePanelToFile(displayPanel, lastDirectory);
      if(file != null) {
        // Save the selected path for next time
        lastDirectory = file.getParentFile().getPath();
      }
    } catch(Exception ex) {
      Utils.excMsg("Saving failed:", ex);
    } finally {
      setCursor(oldCursor);
    }
  }

  /**
   * Provides help dialog.
   */
  public void help() {
    String info = "VTK Demos" + LS + LS;
    info += "Button 1: Rotate" + LS;
    info += "Button 2: Pan" + LS;
    info += "Button 3: Zoom" + LS;
    info += "Keypress r: Reset Pan and Zoom" + LS;
    info += "Keypress s: Surfaces" + LS;
    info += "Keypress w: Wireframe" + LS;
    Utils.infoMsg(info);
  }

  /**
   * Quits the application
   */
  public void quit() {
    System.exit(0);
  }

  /**
   * Handler for the list.
   * 
   * @param ev
   */
  private void onListValueChanged(ListSelectionEvent ev) {
    if(ev != null && ev.getValueIsAdjusting()) return;

    // DEBUG
    if(false) {
      System.out.println("onListValueChanged");
      System.out.println("Thread: " + Thread.currentThread());
      System.out.println("ClassLoader: " + getClass().getClassLoader());
    }
    Cursor oldCursor = getCursor();
    try {
      launcher.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      demo = (VTKDemo)list.getSelectedValue();
      displayPanel.removeAll();
      demo.createPanel();
      JPanel panel = demo.getPanel();
      launcher.setTitle(demo.getName());
      if(panel != null) {
        displayPanel.add(panel, BorderLayout.CENTER);
      } else {
        JLabel label = new JLabel();
        label.setText("Cannot create " + demo.getName());
        displayPanel.add(label);
      }

      // Look for a parent that is a Window and call its pack()
      Container parent = contentPane;
      while(parent != null) {
        if(parent instanceof Window) {
          Window window = (Window)parent;
          window.validate();
          break;
        }
        parent = parent.getParent();
      }
    } catch(Throwable t) {
      Utils.throwMsg("Launching failed:", t);
      // DEBUG
      if(true) {
        t.printStackTrace();
      }
    } finally {
      setCursor(oldCursor);
    }
  }

  /**
   * @return Returns the demo.
   */
  public VTKDemo getDemo() {
    return demo;
  }

  /**
   * @return Returns the displayPanel.
   */
  public JPanel getDisplayPanel() {
    return displayPanel;
  }

  public void setVtkFileOutputOn() {
    if(fileOutputWindow == null) {
      fileOutputWindow = new vtkFileOutputWindow();
      ouputWindow = fileOutputWindow.GetInstance();
      fileOutputWindow.SetInstance(fileOutputWindow);
      // fileOutputWindow.SetAppend(1);
      fileOutputWindow.SetFileName("output.txt");
      Date now = new Date();
      fileOutputWindow.DisplayText("============================");
      fileOutputWindow.DisplayText(now.toString());
    }
  }

  public void setVtkFileOutputOff() {
    if(ouputWindow != null) {
      ouputWindow.SetInstance(ouputWindow);
      fileOutputWindow = null;
    }
  }

  /**
   * @return the vtkFileOutPutWindow
   */
  public vtkFileOutputWindow getFileOutputWindow() {
    return fileOutputWindow;
  }

  /**
   * Main program.
   * 
   * @param args
   */
  public static void main(String[] args) {
    // List styles

    try {
      // Set window decorations
      JFrame.setDefaultLookAndFeelDecorated(true);

      // Set the native look and feel
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

      // Make the job run in the AWT thread
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          VTKDemoLauncher dl = new VTKDemoLauncher();
          // Make it exit when the window manager close button is clicked
          dl.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
          dl.pack();
          dl.setVisible(true);
          dl.setLocationRelativeTo(null);
        }
      });
    } catch(Throwable t) {
      t.printStackTrace();
    }
  }

}
