/*
 * Program to 
 * Created on Oct 10, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import vtk.vtkActor;
import vtk.vtkDataSetMapper;
import vtk.vtkFloatArray;
import vtk.vtkImageData;
import vtk.vtkPanel;
import vtk.vtkRenderer;
import demolauncher.VTKDemo;

public class Surface2D3Demo extends VTKDemo
{
//  private static final int DEBUG_LEVEL = 1;

  private vtkRenderer renderer = null;
  private vtkImageData idata = null;
  private vtkActor actor = null;

  public Surface2D3Demo() {
    super();
    setName("Surface 2D 3");
  }

  public String getInfo() {
    String info = "This demo illustrates a 2D Gaussian\n"
                + "using a vtkImageData.";
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
    renderer = renWin.GetRenderer();
    renderer.SetBackground(1, 1, 1); // background color white
    
    // Create the data
    vtkFloatArray scalars = new vtkFloatArray();
    scalars.SetNumberOfComponents(1);
    scalars.SetName("Scalars");
    
    int nPoints1 = 100;
    int nPoints2 = 100;

    double a1 = 3;
    double sigma1 = .5;
    double mean1 = 0;
    double upper1 = 3.0;
    double lower1 = -3.0;
    double delta1 = (upper1 - lower1) / (nPoints1 - 1);

    double a2 = 1;
    double sigma2 = 2.0;
    double mean2 = 0;
    double upper2 = 3.0;
    double lower2 = -3.0;
    double delta2 = (upper2 - lower2) / (nPoints2 - 1);
    
    // Get the programmable source output
//  TEMP
//    vtkImageData output = source.GetImageDataOutput();
//    output.SetDimensions(nPoints1, nPoints2, 1);
//    output.GetPointData().SetScalars(scalars);
//    output.SetSpacing(delta1, delta2, 0);
//    output.SetOrigin(lower1, lower2, 0);

    // Create a new structured grid with the same data
    idata = new vtkImageData();
    idata.SetDimensions(nPoints1, nPoints2, 1);
    idata.GetPointData().SetScalars(scalars);
    idata.SetSpacing(delta1, delta2, 0);
    idata.SetOrigin(lower1, lower2, 0);

    double x, y, z, arg1, arg2;
    for(int j = 0; j < nPoints2; j++) {
      y = lower2 + delta2 * j;
      arg2 = (y - mean2) / sigma2;
      for(int i = 0; i < nPoints1; i++) {
        x = lower1 + delta1 * i;
        arg1 = (x - mean1) / sigma1;
        z = a1 * Math.exp(-.5 * arg1 * arg1) * a2 * Math.exp(-.5 * arg2 * arg2);
        scalars.InsertNextTuple1(z);
      }
    }

    // Make a dataset mapper
    vtkDataSetMapper mapper = new vtkDataSetMapper();
    mapper.SetInput(idata);

    // Make the actor
    actor = new vtkActor();
    actor.SetMapper(mapper);
    renderer.AddActor(actor);

    // Reset the camera
    renderer.ResetCamera();

    created = true;
  }

}
