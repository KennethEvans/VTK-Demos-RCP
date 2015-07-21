/*
 * Program to 
 * Created on Sep 12, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import utils.Axes2;
import vtk.vtkActor;
import vtk.vtkCamera;
import vtk.vtkContourFilter;
import vtk.vtkFloatArray;
import vtk.vtkPanel;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderer;
import vtk.vtkStructuredPoints;
import demolauncher.VTKDemo;

public class StructuredPointsDemo extends VTKDemo
{

  public StructuredPointsDemo() {
    super();
    setName("Structured Points");
  }

  public String getInfo() {
    String info = "This demo illustrates a generated simple\n"
                 + "vtkStructuredPoints.  The points are on\n"
                 + "a spherical surface, s = x^2 + y^2 + z^2.";
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

    vtkStructuredPoints vol = new vtkStructuredPoints();
    vol.SetDimensions(26, 26, 26);
    vol.SetOrigin(-0.5, -0.5, -0.5);
    double sp = 1.0 / 25.0;
    vol.SetSpacing(sp, sp, sp);

    double x, y, z, s;
    vtkFloatArray scalars = new vtkFloatArray();
    for(int k = 0; k < 26; k++) {
      z = -0.5 + k * sp;
//      double kOffset = k * 26 * 26;
      for(int j = 0; j < 26; j++) {
        y = -0.5 + j * sp;
//        double jOffset = j * 26;
        for(int i = 0; i < 26; i++) {
          x = -0.5 + i * sp;
          s = x * x + y * y + z * z - (0.4 * 0.4);
//          double offset = i + jOffset + kOffset;
          scalars.InsertNextTuple1(s);
        }
      }
    }
    vol.GetPointData().SetScalars(scalars);

    // Make a contour filter
    vtkContourFilter contour = new vtkContourFilter();
    contour.SetInput(vol);
    contour.SetValue(0, 0.0);

    vtkPolyDataMapper volMapper = new vtkPolyDataMapper();
    volMapper.SetInput(contour.GetOutput());
    volMapper.ScalarVisibilityOff();

    vtkActor volActor = new vtkActor();
    volActor.SetMapper(volMapper);

    // Add actors
    renderer.AddActor(volActor);

    // Set the camera
    vtkCamera camera = new vtkCamera();
    if(false) {
      camera.SetClippingRange(1, 1000);
      camera.SetFocalPoint(0, 0, 0);
      camera.SetPosition(0, 0, 5);
      camera.SetViewUp(0, 1, 0);
      camera.Zoom(.8);
    }
    renderer.SetActiveCamera(camera);
    renderer.ResetCamera();

    // Add axes
    Axes2 axes2 = new Axes2(.75);
    axes2.addActor(renderer);
    axes2.setCamera(camera);
    renderer.ResetCamera();

    created = true;
  }

}
