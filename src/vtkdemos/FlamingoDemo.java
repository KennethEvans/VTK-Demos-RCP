/*
 * Program to 
 * Created on Aug 15, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import vtk.vtk3DSImporter;
import vtk.vtkCamera;
import vtk.vtkPanel;
import vtk.vtkRenderWindow;
import vtk.vtkRenderer;
import demolauncher.VTKDemo;

public class FlamingoDemo extends VTKDemo
{
  private static final String VTK_DATA_ROOT = "C:/VTK";
  private static final String FILE_NAME = VTK_DATA_ROOT + "/Data/iflamigm.3ds";

  public FlamingoDemo() {
    super();
    setName("Flamingo");
  }

  public String getInfo() {
    String info = "This demo demonstrates the use of vtk3DSImporter.\n"
      + "vtk3DSImporter is used to load 3D Studio files.  Unlike writers,\n"
      + "importers can load scenes (data as well as lights, cameras, actors\n"
      + "etc.). Importers will either generate an instance of vtkRenderWindow\n"
      + "and/or vtkRenderer or will use the ones you specify.";
    return info;
  }

  /**
   * Hack to enable substituting vtkPanel's ren and rw members.
   */
  private static class CustomVtkPanel extends vtkPanel
  {
    private static final long serialVersionUID = 1L;

    public CustomVtkPanel(vtkRenderer ren, vtkRenderWindow rw) {
      super();
      this.ren = ren;
      this.rw = rw;
      rw.AddRenderer(ren);
      super.setSize(200, 200);
    }
  }

  public void createPanel() {
    if(created) return;

    // Make a JPanel
    JPanel panel = new JPanel();
    BorderLayout layout = new BorderLayout();
    panel.setLayout(layout);
    setPanel(panel);

    // Create the importer and read a file
    vtk3DSImporter importer = new vtk3DSImporter();
    importer.ComputeNormalsOn();
    importer.SetFileName(FILE_NAME);
    importer.Read();

    // Make a vtkPanel and add it to the JPanel
    vtkPanel renWin = null;
    // Use the renderer created by the importer
    renWin = new CustomVtkPanel(importer.GetRenderer(), importer
      .GetRenderWindow());
    panel.add(renWin, BorderLayout.CENTER);

    // Get the renderer
    vtkRenderer renderer = renWin.GetRenderer();
    renderer.SetBackground(1, 1, 1); // background color white

    // Set the camera
    vtkCamera camera = null;
    // Use the camera from the renderer
    camera = renderer.GetActiveCamera();
    camera.SetPosition(0, 1, 0);
    camera.SetFocalPoint(0, 0, 0);
    camera.SetViewUp(0, 0, 1);
    // Let the renderer compute a good position and focal point
    renderer.ResetCamera();
//    camera.Dolly(1.4);
//    camera.Zoom(.8);
    renderer.ResetCameraClippingRange();

    created = true;
  }

}
