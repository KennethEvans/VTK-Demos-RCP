package plugin;

import net.kenevans.core.utils.SWTUtils;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import utils.JavaInfo;
import demolauncher.VTKDemo;
import demolauncher.VTKDemoLauncher;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor implements
    IPluginConstants
{

    // Actions - important to allocate these only in makeActions, and then use
    // them
    // in the fill methods. This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.
    private IWorkbenchAction exitAction;
    private Action infoAction;
    private Action javaInfoAction;
    private Action debugInfoAction;
    private Action vtkVersionAction;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction preferencesAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(final IWorkbenchWindow window) {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml
        // file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.

        // Note that these actions use SWT and not the corresponding Swing
        // actions
        // in VTKDemoLauncher.

        // Make an action to do info
        infoAction = new Action() {
            public void run() {
                String info;
                VTKDemoLauncher launcher = VTKLauncherView.getLauncher();
                if(launcher != null) {
                    VTKDemo demo = launcher.getDemo();
                    if(demo != null) {
                        info = demo.getInfo();
                    } else {
                        info = "No demo selected";
                    }
                } else {
                    info = "No information available";
                }
                SWTUtils.infoMsgAsync(info);
            }
        };
        infoAction.setId(INFO_ACTION_ID);
        infoAction.setText("Info...");
        register(infoAction);

        // Make an action to display Java info
        javaInfoAction = new Action() {
            public void run() {
                String info = JavaInfo.getInfo();
                ScrolledInfoDialog dialog = new ScrolledInfoDialog(
                    window.getShell());
                dialog.open("Java Information", info);
            }
        };
        javaInfoAction.setId(DISPLAY_INFO_JAVA_ACTION_ID);
        javaInfoAction.setText("Java Info...");
        javaInfoAction
            .setToolTipText("Display information about the running Java");
        register(javaInfoAction);

        // Make an action to display debug info
        debugInfoAction = new Action() {
            public void run() {
                String info;
                VTKDemoLauncher launcher = VTKLauncherView.getLauncher();
                if(launcher != null) {
                    info = launcher.getDebugInfo();
                } else {
                    info = "No information available";
                }
                ScrolledInfoDialog dialog = new ScrolledInfoDialog(
                    window.getShell());
                dialog.open("Debug Information", info);
            }
        };
        debugInfoAction.setId(DEBUG_INFO_ACTION_ID);
        debugInfoAction.setText("Debug Info...");
        debugInfoAction.setToolTipText("Display debug information");
        register(debugInfoAction);

        // Make an action to display the VTK version
        vtkVersionAction = new Action() {
            public void run() {
                String info = VTKDemoLauncher.getVTKVersionInfo();
                SWTUtils.infoMsgAsync(info);
            }
        };
        vtkVersionAction.setId(DEBUG_INFO_ACTION_ID);
        vtkVersionAction.setText("VTK Version...");
        vtkVersionAction.setToolTipText("Display the VTK version");
        register(vtkVersionAction);

        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);

        preferencesAction = ActionFactory.PREFERENCES.create(window);
        register(preferencesAction);

        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
        MenuManager fileMenu = new MenuManager("&File",
            IWorkbenchActionConstants.M_FILE);
        menuBar.add(fileMenu);
        fileMenu.add(infoAction);
        fileMenu.add(new Separator());
        fileMenu.add(debugInfoAction);
        fileMenu.add(javaInfoAction);
        fileMenu.add(new Separator());
        fileMenu.add(preferencesAction);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);

        MenuManager help = new MenuManager("Help",
            IWorkbenchActionConstants.M_HELP);
        menuBar.add(help);
        help.add(vtkVersionAction);
        help.add(aboutAction);
    }

}
