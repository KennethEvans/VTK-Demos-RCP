package plugin;

import java.util.Dictionary;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;
import org.osgi.framework.Bundle;

/**
 * Custom splash handler.
 */
public class SplashHandler extends AbstractSplashHandler
{

  /**
   * SplashHandler constructor.
   */
  public SplashHandler() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.splash.AbstractSplashHandler#init(org.eclipse.swt.widgets
   * .Shell)
   */
  @Override
  public void init(final Shell splash) {
    // Store the shell
    super.init(splash);
    FillLayout layout = new FillLayout();
    getSplash().setLayout(layout);
    // Force shell to inherit the splash background
    getSplash().setBackgroundMode(SWT.INHERIT_DEFAULT);
    // Add version number to splash screen
    addVersion(splash, VTKDemoPlugin.PLUGIN_ID);
    // Force the splash screen to layout
    splash.layout(true);
  }

  /**
   * Adds plug-in name to the splash screen.
   * 
   * @param splash - splash screen Shell
   * @param pluginId - plugin id to add version for
   */
  static public void addVersion(Shell splash, String pluginId) {
    final Shell splashShell = splash;
    final String pluginName = getPluginName(pluginId);
    final String pluginVersion = getPluginVersion(pluginId);
    final Canvas canvas = new Canvas(splash, SWT.NONE);
    canvas.setBounds(0, 0, splash.getSize().x, splash.getSize().y);
    canvas.addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        Display display = splashShell.getDisplay();
        Image image = splashShell.getBackgroundImage();
        GC gc = e.gc;
        Color fgColor = display.getSystemColor(SWT.COLOR_BLACK);
        Font font1 = new Font(display, "Arial", 14, SWT.NONE);
        gc.drawImage(image, 0, 0);
        gc.setForeground(fgColor);
        gc.setFont(font1);
        gc.drawString(pluginName + " " + pluginVersion, 30, 10, true);
        font1.dispose();
        // Use this only if you create a new new Color(display, 100, 100, 100);
        // fgColor.dispose();
      }
    });
  }

  /**
   * Gets the plug-in name from its ID.
   * 
   * @param pluginId The plug-in's Activator ID.
   * @return
   */
  public static String getPluginName(String pluginId) {
    String name = "Unknown-Name";
    Bundle bundle = Platform.getBundle(pluginId);
    if(bundle == null) return name;
    String pluginName = bundle.getSymbolicName();
    if(pluginName != null) name = pluginName;
    return name;
  }

  /**
   * Gets the plug-in version from its ID.
   * 
   * @param pluginId The plug-in's Activator ID.
   * @return
   */
  public static String getPluginVersion(String pluginId) {
    String version = "Unknown-Version";
    Bundle bundle = Platform.getBundle(pluginId);
    if(bundle == null) return version;
    Dictionary<?, ?> bundleHeaders = bundle.getHeaders();
    if(bundleHeaders == null) return version;
    String pluginVersion = (String)bundleHeaders.get("Bundle-Version");
    if(pluginVersion != null) version = pluginVersion;
    return version;
  }

}
