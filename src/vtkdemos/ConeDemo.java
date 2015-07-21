/*
 * Program to 
 * Created on Aug 15, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import vtk.vtkActor;
import vtk.vtkCamera;
import vtk.vtkConeSource;
import vtk.vtkPanel;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderer;
import demolauncher.VTKDemo;

public class ConeDemo extends VTKDemo
{

  public ConeDemo() {
    super();
    setName("Cone");
  }

  /*
   * (non-Javadoc)
   * 
   * @see demolauncher.VTKDemo#getInfo()
   */
  @Override
  public String getInfo() {
    String info = "This demo illustrates making a simple cone.\n";
    return info;
  }

  /*
   * (non-Javadoc)
   * 
   * @see demolauncher.VTKDemo#createPanel()
   */
  @Override
  public void createPanel() {
    if(created) return;

    // Make a JPanel
    JPanel panel = new JPanel();
    BorderLayout layout = new BorderLayout();
    panel.setLayout(layout);
    setPanel(panel);

    // Make a vtkPanel and add it to the JPanel
    // DEBUG
    if(false) {
      System.out.println("Cone: createPanel");
      System.out.println("Thread: " + Thread.currentThread());
      System.out.println("ClassLoader: " + getClass().getClassLoader());
      System.out.println("vtkPanel static ClassLoader: "
        + vtkPanel.class.getClassLoader());
    }
    vtkPanel renWin = new vtkPanel();
    // Debug
    panel.add(renWin, BorderLayout.CENTER);

    // Get the renderer
    vtkRenderer renderer = renWin.GetRenderer();
    renderer.SetBackground(1, 1, 1); // background color white

    // Create a cone
    vtkConeSource cone = new vtkConeSource();
    cone.SetRadius(1.0);
    cone.SetResolution(50);
    cone.SetCenter(0, 0, 0);
    cone.SetHeight(2.);
    cone.SetDirection(0, 0, 1);
    vtkPolyDataMapper coneMapper = new vtkPolyDataMapper();
    coneMapper.SetInput(cone.GetOutput());
    vtkActor coneActor = new vtkActor();
    coneActor.SetMapper(coneMapper);
    coneActor.GetProperty().SetColor(0, 0, 1); // color blue
    coneActor.SetPosition(0, 0, 0);

    // Add actors
    renderer.AddActor(coneActor);

    // Set the camera
    vtkCamera camera = new vtkCamera();
    camera.SetClippingRange(1, 1000);
    camera.SetFocalPoint(0, 0, 0);
    camera.SetPosition(0, 0, 5);
    camera.SetViewUp(0, 1, 0);
    camera.Zoom(.8);
    renderer.SetActiveCamera(camera);

    if(false) {
      System.out.println("ViewAngle=" + camera.GetViewAngle());
    }

    created = true;
  }

}
