/*
 * Program to 
 * Created on Aug 15, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import vtk.vtkActor;
import vtk.vtkAxes;
import vtk.vtkCamera;
import vtk.vtkConeSource;
import vtk.vtkPanel;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderer;
import demolauncher.VTKDemo;

public class AxesDemo extends VTKDemo
{

  public AxesDemo() {
    super();
    setName("Axes");
  }

  public String getInfo() {
    String info =
      "This demo illustrates using vtkAxes.  There is a cone centered\n"
      + "at (0,0,0) with height 1 and radius .5 pointing toward z.";
    return info;
  }

  public void createPanel() {
    if(created) return;

    // Make a JPanel
    JPanel panel = new JPanel();
    BorderLayout layout = new BorderLayout();
    panel.setLayout(layout);
    setPanel(panel);

    // Make a vtkPanel and add it to the JPanel
    vtkPanel renWin = new vtkPanel();
    panel.add(renWin, BorderLayout.CENTER);

    // Get the renderer
    vtkRenderer renderer = renWin.GetRenderer();
    renderer.SetBackground(1, 1, 1); // background color white

    // Create axes
    vtkAxes axes = new vtkAxes();
    vtkPolyDataMapper axesMapper = new vtkPolyDataMapper();
    axesMapper.SetInput(axes.GetOutput());
    vtkActor axesActor = new vtkActor();
    axesActor.SetMapper(axesMapper);
    if(true) {
      System.out.println("axes: GetScaleFactor=" + axes.GetScaleFactor());
    }
    
    // Create a cone
    vtkConeSource cone = new vtkConeSource();
    cone.SetRadius(.5);
    cone.SetResolution(50);
    cone.SetCenter(0, 0, 0);
    cone.SetHeight(1.);
    cone.SetDirection(0, 0, 1);
    vtkPolyDataMapper coneMapper = new vtkPolyDataMapper();
    coneMapper.SetInput(cone.GetOutput());
    vtkActor coneActor = new vtkActor();
    coneActor.SetMapper(coneMapper);
    coneActor.GetProperty().SetColor(0, 0, 1); // color blue
    coneActor.SetPosition(0, 0, 0);

    // Add actors
    renderer.AddActor(axesActor);
    renderer.AddActor(coneActor);

    // Set the camera
    vtkCamera camera = new vtkCamera();
    camera.SetClippingRange(1, 1000);
    camera.SetFocalPoint(0, 0, 0);
    camera.SetPosition(0, 0, 5);
    camera.SetViewUp(0, 1, 0);
    camera.Zoom(.8);
    renderer.SetActiveCamera(camera);

    created = true;
  }

}
