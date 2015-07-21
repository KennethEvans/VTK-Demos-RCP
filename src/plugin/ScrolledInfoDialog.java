package plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Class to implement a dialog to Java info.
 */
public class ScrolledInfoDialog extends Dialog
{
  private static final String DEFAULT_INFO = "No information available";
  private static final String DEFAULT_TITLE = "Information";
  private static final int TEXT_WIDTH = 400;
  private static final int TEXT_HEIGHT = 300;
  private String info = DEFAULT_INFO;
  private String title = DEFAULT_TITLE;

  /**
   * Constructor.
   * 
   * @param parent
   */
  public ScrolledInfoDialog(Shell parent) {
    // We want this to be modeless
    this(parent, SWT.DIALOG_TRIM | SWT.NONE);
  }

  /**
   * Constructor.
   * 
   * @param parent The parent of this dialog.
   * @param style Style passed to the parent.
   */
  public ScrolledInfoDialog(Shell parent, int style) {
    super(parent, style);
  }

  /**
   * Convenience method to supply the info and open the dialog.
   * 
   * @param title The dialog title.
   * @param info The info to be displayed.
   * @return
   */
  public String open(String title, String info) {
    if(title != null) {
      this.title = title;
    } else {
      title = DEFAULT_TITLE;
    }
    if(info != null) {
      this.info = info;
    } else {
      info = DEFAULT_INFO;
    }
    return open();
  }

  /**
   * Convenience method to open the dialog.
   * 
   * @return The return value is always null.
   */
  public String open() {
    Shell shell = new Shell(getParent(), getStyle() | SWT.RESIZE);
    shell.setText(title);
    Image image = null;
    try {
      image = PlatformUI.getWorkbench().getSharedImages().getImage(
        ISharedImages.IMG_OBJS_INFO_TSK);
      // Might be better to get the image from the main window, but
      // haven't figured out how
      // This doesn't seem to work:
      // image = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
      // .getShell().getImage();
    } catch(Exception ex) {
    }
    if(image != null) shell.setImage(image);
    // It can take a long time to do this so use a wait cursor
    // KE: Probably isn't necessary in this case
    Cursor waitCursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
    if(waitCursor != null) getParent().setCursor(waitCursor);
    createContents(shell);
    getParent().setCursor(null);
    waitCursor.dispose();
    shell.pack();
    shell.open();
    Display display = getParent().getDisplay();
    while(!shell.isDisposed()) {
      if(!display.readAndDispatch()) {
        display.sleep();
      }
    }
    return null;
  }

  /**
   * Creates the contents of the dialog.
   * 
   * @param shell
   */
  private void createContents(final Shell shell) {
    GridLayout grid = new GridLayout();
    grid.numColumns = 1;
    shell.setLayout(grid);

    Group box = new Group(shell, SWT.BORDER);
    box.setText("Information");
    grid = new GridLayout();
    grid.numColumns = 1;
    box.setLayout(grid);
    GridData gridData = new GridData(GridData.FILL_BOTH
      | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
    box.setLayoutData(gridData);

    Text text = new Text(box, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    text.setEditable(false);
    text.setText(getInfo());
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
      | GridData.VERTICAL_ALIGN_FILL);
    gridData.grabExcessVerticalSpace = true;
    gridData.grabExcessHorizontalSpace = true;
    gridData.widthHint = TEXT_WIDTH;
    gridData.heightHint = TEXT_HEIGHT;
    text.setLayoutData(gridData);

    Button close = new Button(shell, SWT.PUSH);
    close.setText("Close");
    gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
    close.setLayoutData(gridData);

    close.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        shell.close();
      }
    });

    shell.setDefaultButton(close);
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

}
