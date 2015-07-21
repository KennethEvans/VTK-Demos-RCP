package actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import plugin.IPluginConstants;
import plugin.ScrolledInfoDialog;

public class ScrolledInfoAction extends Action implements
  IWorkbenchWindowActionDelegate, IPluginConstants
{
  private final static String ID = SCROLLED_INFO_ACTION_ID;
  private String info = "No information available";
  private String title = "Information";
  private String text = "Information...";
  private String toolTipText = "";

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
   */
  @Override
  public void dispose() {
    // Do nothing
  }
  
  @Override
  public void setId(String id) {
    // TODO Auto-generated method stub
    super.setId(id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.
   * IWorkbenchWindow)
   */
  @Override
  public void init(IWorkbenchWindow window) {
    setText(text);
    if(toolTipText.length() != 0) {
    setToolTipText(toolTipText);
    }
    setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
      .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.action.Action#run()
   */
  @Override
  public void run() {
    IWorkbenchWindow window = PlatformUI.getWorkbench()
      .getActiveWorkbenchWindow();
    ScrolledInfoDialog dialog = new ScrolledInfoDialog(window.getShell());
    dialog.setText(title);
    dialog.setInfo(info);
    dialog.open();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
   */
  @Override
  public void run(IAction action) {
    run();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
   * .IAction, org.eclipse.jface.viewers.ISelection)
   */
  @Override
  public void selectionChanged(IAction action, ISelection selection) {
    // Do nothing
  }

  /**
   * Get the ID of this action.
   * 
   * @return
   */
  public static String getID() {
    return ID;
  }

  /**
   * @return The value of info.
   */
  public String getInfo() {
    return info;
  }

  /**
   * @param info The new value for info.
   */
  public void setInfo(String info) {
    this.info = info;
  }

  /**
   * @return The value of title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title The new value for title.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return The value of text.
   */
  public String getText() {
    return text;
  }

  /**
   * @param text The new value for text.
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * @return The value of toolTipText.
   */
  public String getToolTipText() {
    return toolTipText;
  }

  /**
   * @param toolTipText The new value for toolTipText.
   */
  public void setToolTipText(String toolTipText) {
    this.toolTipText = toolTipText;
  }

}
