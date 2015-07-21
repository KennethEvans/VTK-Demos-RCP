package plugin;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.kenevans.core.utils.SWTUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import demolauncher.VTKDemoLauncher;

public class VTKLauncherView extends ViewPart implements IPluginConstants
{
    public static final String ID = PLUGIN_ID + ".view";
    private java.awt.Frame frame = null;
    Composite awtComposite = null;
    private static VTKDemoLauncher launcher = null;

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
        try {
            // Set the native look and feel
            JFrame.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Make a Frame in a Composite
            awtComposite = new Composite(parent, SWT.EMBEDDED);
            frame = SWT_AWT.new_Frame(awtComposite);
            awtComposite.setBackground(parent.getDisplay().getSystemColor(
                SWT.COLOR_CYAN));
            parent.setBackground(parent.getDisplay().getSystemColor(
                SWT.COLOR_DARK_MAGENTA));
            frame.setBackground(Color.GREEN);

            // Make the job run in the AWT thread
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        // Perform necessary magic
                        // See "Swing/SWT Integration" by Gordon Hirsch
                        // See VisAd Demos RCP for more choices
                        JApplet applet = new JApplet();
                        applet.setFocusCycleRoot(false);
                        frame.add(applet);
                        Container contentPane = applet.getRootPane()
                            .getContentPane();

                        // Make a launcher and add its content pane to the
                        // applet content
                        // pane
                        launcher = new VTKDemoLauncher(false, null);
                        contentPane.add(launcher.getContentPane());
                    } catch(Throwable t) {
                        SWTUtils.errMsgAsync("Unable to start demo launcher:\n"
                            + t + "\n" + t.getMessage());
                        t.printStackTrace();
                    }
                }
            });
        } catch(Throwable t) {
            SWTUtils.errMsgAsync("Unable to create view:\n" + t + "\n"
                + t.getMessage());
            t.printStackTrace();
        }
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
    }

    /**
     * @return Returns the launcher.
     */
    public static VTKDemoLauncher getLauncher() {
        return launcher;
    }

}
