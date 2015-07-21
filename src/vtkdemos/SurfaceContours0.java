/*
 * Program to 
 * Created on Sep 7, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import vtk.vtkActor;
import vtk.vtkCamera;
import vtk.vtkContourFilter;
import vtk.vtkDataSet;
import vtk.vtkDoubleArray;
import vtk.vtkPanel;
import vtk.vtkPoints;
import vtk.vtkPolyDataMapper;
import vtk.vtkPolyDataNormals;
import vtk.vtkProgrammableFilter;
import vtk.vtkRenderer;
import vtk.vtkStructuredGrid;
import demolauncher.VTKDemo;

public class SurfaceContours0 extends VTKDemo
{
  private static final boolean PRINT_INFO = true;

  private vtkProgrammableFilter progFilter;

  public SurfaceContours0() {
    super();
    setName("Surface Contours");
  }

  public String getInfo() {
    String info = "This demo illustrates surface contours\n"
      + "from a vtkStructuredGrid.\n";
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

    // Make a structured grid
    int nPoints1 = 100;
    int nPoints2 = 100;
    int nPoints3 = 100;
    vtkStructuredGrid grid = new vtkStructuredGrid();
    grid.SetDimensions(nPoints1, nPoints2, nPoints3);
    
    // Set up the data
    if(false) {
      vtkDoubleArray vectors = new vtkDoubleArray();
      vectors.SetNumberOfComponents(3);
      vectors.SetNumberOfTuples(nPoints1 * nPoints2 * nPoints3);
      vtkPoints points = new vtkPoints();

      // Calculate the data
      double[] x = new double[3];
      double[] v = new double[3];
      double rMin = 0.5;
      double rMax = 1.0;
      double deltaRad = (rMax - rMin) / (nPoints2 - 1);
      double deltaZ = 2.0 / (nPoints3 - 1);
      v[2] = 0.0f;
      int n = 0;
      for(int k = 0; k < nPoints3; k++) {
        x[2] = -1.0 + k*deltaZ;
        for(int j = 0; j < nPoints2; j++) {
          double radius = rMin + j*deltaRad;
          for(int i = 0; i < nPoints1; i++) {
            double theta = Math.toRadians(i * 15.0);
            x[0] = radius * Math.cos(theta);
            x[1] = radius * Math.sin(theta);
            v[0] = -x[1];
            v[1] = x[0];
            vectors.InsertTuple3(n, v[0], v[1], v[2]);
            points.InsertPoint(n, x);
            n++;
          }
        }
      }
    
//    grid.SetPoints(points);
//    grid.GetPointData().SetVectors(vectors);
    }
    if(PRINT_INFO) {
      grid.Update();
      System.out.println("grid:");
      System.out.print(grid);
    }

    // Set up the programmable filter using the structured grid as the input
    // and func as the execute method
    progFilter = new vtkProgrammableFilter();
    progFilter.SetInput(grid);
    progFilter.SetExecuteMethod(this, "func");
    if(PRINT_INFO) {
      progFilter.Update();
      vtkStructuredGrid input = progFilter.GetStructuredGridInput();
      System.out.println("progFilter Input:");
      System.out.print(input);
      vtkDataSet output = (vtkDataSet)progFilter.GetOutput();
      System.out.println("progFilter Output:");
      System.out.print(output);
    }

    // Make a contour filter
    vtkContourFilter contFilter = new vtkContourFilter();
//    contFilter.SetInput(progFilter.GetOutput());
    contFilter.SetInput(grid);
    contFilter.GenerateValues(10, 0, 1);

    vtkPolyDataNormals normals = new vtkPolyDataNormals();
    normals.SetInput(contFilter.GetOutput());
    normals.SetFeatureAngle(45);

    // Create a mapper and actor as usual. In this case adjust the
    // scalar range of the mapper to match that of the computed scalars
    vtkPolyDataMapper contMapper = new vtkPolyDataMapper();
    contMapper.SetInput(contFilter.GetOutput());
    //    contMapper.SetScalarRange(progFilter.GetPolyDataOutput().GetScalarRange());
    //    contMapper.ScalarVisibilityOn();
    //    contourMapper.SetScalarRange(0, 1500);
    //    contourMapper.SetScalarModeToUsePointFieldData();
    //    contourMapper.ColorByArrayComponent("VelocityMagnitude", 0);
    if(PRINT_INFO) {
      contMapper.Update();
      System.out.println("contourMapper:");
      System.out.println("GetArrayName: " + contMapper.GetArrayName());
      System.out.println("GetArrayId: " + contMapper.GetArrayId());
      System.out
        .println("GetArrayComponent: " + contMapper.GetArrayComponent());
      System.out.print(contMapper);
    }

    // Set the actor
    vtkActor contActor = new vtkActor();
    contActor.SetMapper(contMapper);

    // Add actors
    renderer.AddActor(contActor);

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
   * The function for the programmable filter.
   */
  public void func() {
    System.out.println();
    vtkDataSet input = progFilter.GetStructuredGridInput();
    int numPoints = input.GetNumberOfPoints();
//    vtkPoints newPts = new vtkPoints();
//    vtkPoints newPts = input.GetPointData();
//    vtkPointData pointData = input.GetPointData();
//    vtkDataArray array = pointData.GetArray(0);
//    array.SetName("Data");
//    array.SetNumberOfComponents(3);
//    array.SetNumberOfTuples(numPoints);
    vtkDoubleArray scalars = new vtkDoubleArray();
    scalars.SetName("Scalars");

    // Calculate and set the values
    for(int i = 0; i < numPoints; ++i) {
      double[] x = input.GetPoint(i);
      double x0 = x[0];
      double x1 = x[1];
      double x2 = x[2];
      double scalar = 0;

      double a1 = 1;
      double a2 = 1;
      double sigma1 = .5;
      double sigma2 = 2.;
      double mean1 = 0.;
      double mean2 = 0.;
      double arg1 = (x0 - mean1) / sigma1;
      double arg2 = (x1 - mean2) / sigma2;
      x2 = a1 * Math.exp(-.5 * arg1 * arg1) * a2 * Math.exp(-.5 * arg2 * arg2);
      // Make scalar the same as x2
      scalar = x2;
//      array.SetTuple3(i, x0, x1, x2);
//      newPts.InsertPoint(i, x0, x1, x2);
      scalars.InsertValue(i, scalar);
    }

    // Set the output
    vtkDataSet output = (vtkDataSet)progFilter.GetOutput();
    output.CopyStructure(input);
//    data.SetPoints(newPts);
    output.GetPointData().SetScalars(scalars);
  }

}
