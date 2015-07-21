/*
 * Program to 
 * Created on Sep 23, 2006
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

public class Surface2D2Demo extends VTKDemo
{
  // private static final int DEBUG_LEVEL = 1;
  private static final int MODE = 1;
  // private static final boolean WRITE_OUTPUT = false;

  private vtkRenderer renderer = null;
  // private vtkProgrammableSource source = null;
  private vtkImageData idata = null;
  private vtkImageData idata1 = null;
  private vtkActor actor = null;

  public Surface2D2Demo() {
    super();
    setName("Surface 2D 2");
  }

  public String getInfo() {
    String info = "This demo illustrates a 2D Gaussian\n"
      + "from a programmable source.  Simplified.\n" + "Uses vtkImageData.";
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

    // TEMP
    // // Create the structured grid dataset with the appropriate function
    // source = new vtkProgrammableSource();
    // source.SetExecuteMethod(this, "func");
    // source.Update();
    // idata = source.GetImageDataOutput();
    // if(DEBUG_LEVEL > 0) {
    // idata.Update();
    // System.out.println("idata:");
    // System.out.print(idata);
    // }
    // String fileName = "Source.test.2.txt";
    // if(WRITE_OUTPUT) {
    // // Write the file
    // System.out.println("Written to " + fileName);
    // vtkDataSetWriter writer = new vtkDataSetWriter();
    // writer.SetInput(idata);
    // writer.SetFileName(fileName);
    //      
    // writer.Write();
    // }
    //    
    // // Read the file
    // vtkDataSetReader reader = new vtkDataSetReader();
    // reader.SetFileName(fileName);

    // Make a dataset mapper
    vtkDataSetMapper mapper = new vtkDataSetMapper();
    switch(MODE) {
    case 0:
      System.out.println("Using source");
      mapper.SetInput(idata);
      break;
    case 1:
      // TEMP
      func(); // //////////////////////////////////////////////// Temporary
      System.out.println("Using idata1");
      mapper.SetInput(idata1);
      break;
    // case 2:
    // System.out.println("Using reader with " + fileName);
    // mapper.SetInput(reader.GetOutput());
    // break;
    }

    // Make the actor
    actor = new vtkActor();
    actor.SetMapper(mapper);
    renderer.AddActor(actor);

    // Reset the camera
    renderer.ResetCamera();

    created = true;
  }

  /**
   * The function for the programmable source.
   */
  public void func() {
    // Make arrays for vectors, scalars, and points
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
    // TEMP
    // vtkImageData output = source.GetImageDataOutput();
    // output.SetDimensions(nPoints1, nPoints2, 1);
    // output.GetPointData().SetScalars(scalars);
    // output.SetSpacing(delta1, delta2, 0);
    // output.SetOrigin(lower1, lower2, 0);

    // Create a new structured grid with the same data
    idata1 = new vtkImageData();
    idata1.SetDimensions(nPoints1, nPoints2, 1);
    idata1.GetPointData().SetScalars(scalars);
    idata1.SetSpacing(delta1, delta2, 0);
    idata1.SetOrigin(lower1, lower2, 0);

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
  }

}
