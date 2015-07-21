/*
 * Program to 
 * Created on Sep 8, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import vtk.vtkActor;
import vtk.vtkCamera;
import vtk.vtkContourFilter;
import vtk.vtkDataSet;
import vtk.vtkPanel;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkProgrammableSource;
import vtk.vtkRenderer;
import vtk.vtkReverseSense;
import vtk.vtkSurfaceReconstructionFilter;
import demolauncher.VTKDemo;

public class ReconstructedSurfaceDemo extends VTKDemo
{
  private static final boolean PRINT_INFO = true;
  private static final boolean DO_REVERSE = false;
  private static final boolean SET_COLOR = true;
  // 9 is good for showing several surfaces with SET_COLOR=false
  private static final int NSURFACES = 1;

  private vtkProgrammableSource source;

  public ReconstructedSurfaceDemo() {
    super();
    setName("Reconstructed Surface");
  }

  public String getInfo() {
    String info = "This demo illustrates a surface reconstructed\n"
      + "from points calculated in a programmable source.\n";
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
    
    // Set up the programmable source using func as the execute method
    source = new vtkProgrammableSource();
    source.SetExecuteMethod(this, "func");
    if(PRINT_INFO) {
      source.Update();
      vtkDataSet output = source.GetOutput();
      System.out.println("source output:");
      System.out.print(output);
    }

    // Construct the surface and create isosurface.
    vtkSurfaceReconstructionFilter surf = new vtkSurfaceReconstructionFilter();
    surf.SetInput(source.GetPolyDataOutput());
    if(PRINT_INFO) {
      surf.Update();
      vtkDataSet output = surf.GetOutput();
      System.out.println("surf output:");
      System.out.print(output);
    }

    vtkContourFilter cf = new vtkContourFilter();
    cf.SetInput(surf.GetOutput());
    // The reconstructed surface is at 0.0
    if(NSURFACES > 1) {
      // -1, 1 is empirical, don't know why
      cf.GenerateValues(NSURFACES, -1, 1);
    } else {
      cf.SetValue(0, 0.);
    }

    // Sometimes the contouring algorithm can create a volume whose gradient
    // vector and ordering of polygon (using the right hand rule); are
    // inconsistent. vtkReverseSense cures this problem.
    vtkReverseSense reverse = null;
   if(DO_REVERSE) {
      reverse = new vtkReverseSense();
      reverse.SetInput(cf.GetOutput());
      reverse.ReverseCellsOn();
      reverse.ReverseNormalsOn();
    }

    vtkPolyData input = null;
    if(DO_REVERSE) {
      input = reverse.GetOutput();
    } else {
      input = cf.GetOutput();
    }
    if(PRINT_INFO) {
      input.Update();
      System.out.println("mapper input:");
      System.out.print(input);
    }
    
   vtkPolyDataMapper mapper = new vtkPolyDataMapper();
    mapper.SetInput(input);
    if(SET_COLOR) mapper.ScalarVisibilityOff();
    
    vtkActor surfaceActor = new vtkActor();
    surfaceActor.SetMapper(mapper);
    if(SET_COLOR) {
//      surfaceActor.GetProperty().SetDiffuseColor(1.0000, 0.3882, 0.2784);
      surfaceActor.GetProperty().SetDiffuseColor(.4, .4, 1);
      surfaceActor.GetProperty().SetSpecularColor(1, 1, 1);
      surfaceActor.GetProperty().SetSpecular(.4);
      surfaceActor.GetProperty().SetSpecularPower(50);
    }

    // Add actors
    renderer.AddActor(surfaceActor);

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

    created = true;
  }
  
  /**
   * The function for the programmable source.
   */
  public void func() {
    vtkPolyData output = source.GetPolyDataOutput();
    vtkPoints points = new vtkPoints();
    output.SetPoints(points);
//    vtkDoubleArray vectors = new vtkDoubleArray();
//    vectors.SetNumberOfComponents(3);
//    vectors.SetNumberOfTuples(nPoints1 * nPoints2 * nPoints3);
//    vectors.SetName("Vector 0");
//    output.GetPointData().SetVectors(vectors);
//    vtkFloatArray scalars = new vtkFloatArray();
//    scalars.SetName("Z");
//    output.GetPointData().SetScalars(scalars);

    // Calculate the points
    int nPoints1 = 100;
    double sigma1 = .5;
    double upper1 = 3.0;
    double lower1 = -3.0;
    int nPoints2 = 100;
    double sigma2 = 2.0;
    double upper2 = 3.0;
    double lower2 = -3.0;
    double a1 = 1;
    double a2 = 1;
    double mean1 = 0;
    double mean2 = 0;
    double delta1 = (upper1 - lower1) / (nPoints1 -1);
    double delta2 = (upper2 - lower2) / (nPoints2 -1);
    for(int i = 0; i < nPoints1; i++) {
      double x = lower1 + delta1 * i;
      double arg1 = (x - mean1) / sigma1;
      for(int j = 0; j < nPoints2; j++) {
        double y = lower2 + delta2 * j;
        double arg2 = (y - mean2) / sigma2;
        double z = a1 * Math.exp(-.5 * arg1 * arg1) *
        a2 * Math.exp(-.5 * arg2 * arg2);
        points.InsertNextPoint(x, y, z);
//        scalars.InsertValue(i, z);
      }
    }
  }
  
}
