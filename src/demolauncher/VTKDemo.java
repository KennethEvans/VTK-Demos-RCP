/*
 * Program to provide a super class for a demo
 * Created on Mar 29, 2006
 * By Kenneth Evans, Jr.
 */

package demolauncher;

import java.awt.BorderLayout;

import javax.swing.JPanel;

abstract public class VTKDemo
{
  private String name = null;
  private JPanel panel = null;
  protected boolean created = false;

  /**
   * Gets information about the demo. Expected to be overridden.
   * 
   * @return
   */
  public String getInfo() {
    String info = "No information available.";
    return info;
  }

  /**
   * Creates the JPanel instance. Expected to be overridden.
   */
  public void createPanel() {
    if(created) return;

    // Make a JPanel
    JPanel panel = new JPanel();
    BorderLayout layout = new BorderLayout();
    panel.setLayout(layout);
    setPanel(panel);

    // Make the display
    // try {
    // } catch(Exception ex) {
    // Utils.errMsg("Unable to create display:\n" + ex + "\n" +
    // ex.getMessage());
    // return;
    // }

    // Add the display to the panel
    // Component component = display.getComponent();
    // panel.add(component, BorderLayout.CENTER);

    created = true;
  }

  public String toString() {
    return name;
  }

  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The name to set.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return Returns the panel.
   */
  public JPanel getPanel() {
    return panel;
  }

  /**
   * @param panel The panel to set.
   */
  public void setPanel(JPanel panel) {
    this.panel = panel;
  }

  /**
   * @return Returns if created.
   */
  public boolean isCreated() {
    return created;
  }

  /**
   * @param created The created value to set.
   */
  public void setCreated(boolean created) {
    this.created = created;
  }

}
