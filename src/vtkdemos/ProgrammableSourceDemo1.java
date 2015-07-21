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
import vtk.vtkFloatArray;
import vtk.vtkPanel;
import vtk.vtkPointDataToCellData;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkProgrammableSource;
import vtk.vtkRearrangeFields;
import vtk.vtkRenderer;
import demolauncher.VTKDemo;

public class ProgrammableSourceDemo1 extends VTKDemo
{
  private static final boolean PRINT_INFO = true;
  private static final String SCALAR_NAME = "Scalar";

  private vtkProgrammableSource source;

  public ProgrammableSourceDemo1() {
    super();
    setName("Programmable Source 1");
  }

  public String getInfo() {
    String info = "This demo illustrates a programmable source.\n";
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

    // Convert to cell data
    vtkPointDataToCellData point2Cell = new vtkPointDataToCellData();
    point2Cell.SetInput(source.GetPolyDataOutput());
    point2Cell.PassPointDataOn();
    if(PRINT_INFO) {
      point2Cell.Update();
      vtkDataSet output = point2Cell.GetOutput();
      System.out.println("point2Cell output:");
      System.out.print(output);
    }
    
    // Rearrange
    vtkRearrangeFields rearrange = new vtkRearrangeFields();
    rearrange.SetInput(source.GetPolyDataOutput());
    rearrange.AddOperation("COPY", SCALAR_NAME, "POINT_DATA", "CELL_DATA");
    if(PRINT_INFO) {
      rearrange.Update();
      vtkDataSet output = rearrange.GetOutput();
      System.out.println("rearrange output:");
      System.out.print(output);
    }
    
    // Pick convert or rearrange
     vtkPolyData contourInput = null;
    if(false) {
      contourInput = point2Cell.GetPolyDataOutput();
    } else {
      contourInput = rearrange.GetPolyDataOutput();
    }

    vtkContourFilter contour = new vtkContourFilter();
    contour.SetInput(contourInput);
    contour.GenerateValues(10, 0, 1);
    if(PRINT_INFO) {
      contour.Update();
      vtkDataSet output = contour.GetOutput();
      System.out.println("contour output:");
      System.out.print(output);
    }
    
   vtkPolyDataMapper mapper = new vtkPolyDataMapper();
    mapper.SetInput(contour.GetOutput());
//    map.ScalarVisibilityOff();
//    mapper.SetScalarRange(input.GetScalarRange());
//    mapper.ColorByArrayComponent(0, 0);
    
    vtkActor surfaceActor = new vtkActor();
    surfaceActor.SetMapper(mapper);
    if(false) {
      surfaceActor.GetProperty().SetDiffuseColor(1.0000, 0.3882, 0.2784);
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
    vtkFloatArray scalars = new vtkFloatArray();
    scalars.SetName(SCALAR_NAME);
    output.GetPointData().SetScalars(scalars);

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
    int numPoints = 0;
    for(int i = 0; i < nPoints1; i++) {
      double x = lower1 + delta1 * i;
      double arg1 = (x - mean1) / sigma1;
      for(int j = 0; j < nPoints2; j++) {
        double y = lower2 + delta2 * j;
        double arg2 = (y - mean2) / sigma2;
        double z = a1 * Math.exp(-.5 * arg1 * arg1) *
        a2 * Math.exp(-.5 * arg2 * arg2);
        points.InsertNextPoint(x, y, z);
        scalars.InsertValue(numPoints++, z);
      }
    }
  }
  
}
