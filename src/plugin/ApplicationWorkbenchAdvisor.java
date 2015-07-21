package plugin;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor implements
  IPluginConstants
{

  private static final String PERSPECTIVE_ID = PLUGIN_ID + ".perspective";

  public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
    IWorkbenchWindowConfigurer configurer) {
    return new ApplicationWorkbenchWindowAdvisor(configurer);
  }

  public String getInitialWindowPerspectiveId() {
    return PERSPECTIVE_ID;
  }

}
