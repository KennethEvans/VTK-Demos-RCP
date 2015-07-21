/*
 * Program to 
 * Created on Sep 11, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import utils.Axes2;
import vtk.vtkActor;
import vtk.vtkCamera;
import vtk.vtkCellArray;
import vtk.vtkDataSetWriter;
import vtk.vtkFloatArray;
import vtk.vtkLookupTable;
import vtk.vtkPanel;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderer;
import demolauncher.VTKDemo;

public class PolyDataDemo extends VTKDemo
{

  public PolyDataDemo() {
    super();
    setName("PolyData");
  }

  public String getInfo() {
    String info = "This demo illustrates a generated simple vtkPolyData.\n"
                 + "The points are the 8 corners of a cube, and the cells\n"
                 + "are the faces.";
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

    double[][] x = { {0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}, {0, 0, 1},
      {1, 0, 1}, {1, 1, 1}, {0, 1, 1}};
    int[][] pts = { {3, 2, 1, 0}, {4, 5, 6, 7}, {0, 1, 5, 4}, {1, 2, 6, 5},
      {2, 3, 7, 6}, {3, 0, 4, 7}};

    // Create the building blocks of polydata including data attributes
    vtkPolyData cube = new vtkPolyData();
    vtkPoints points = new vtkPoints();
    vtkCellArray polys = new vtkCellArray();
    vtkFloatArray scalars = new vtkFloatArray();

    // Load the point, cell, and data attributes
    for(int i = 0; i < x.length; i++)
      points.InsertPoint(i, x[i]);
    for(int i = 0; i < pts.length; i++) {
      polys.InsertNextCell(pts[i].length);
      for(int j = 0; j < pts[i].length; j++) {
        polys.InsertCellPoint(pts[i][j]);
      }
    }
    for(int i = 0; i < x.length; i++)
      scalars.InsertTuple1(i, i);

    // Assign the pieces to the vtkPolyData
    cube.SetPoints(points);
    cube.SetPolys(polys);
    scalars.SetName("Calculated Scalars");
    cube.GetPointData().SetScalars(scalars);
    
    // Write the data
    vtkDataSetWriter writer = new vtkDataSetWriter();
    writer.SetInput(cube);
    writer.SetWriteToOutputString(1);
    writer.Write();
    System.out.println("Cube:");
    String output = writer.GetOutputString();
    System.out.println(output);
    
    // Make a color table
    int maxColors = 256;
    vtkLookupTable table1 = new vtkLookupTable();
    table1.SetTableRange(0, maxColors);
    table1.SetNumberOfColors(maxColors);
    table1.Build();
//    table1.SetTableValue(maxColors - 1, 0.0, 0.0, 0.0, 0.0);

    // Make a mapper
    vtkPolyDataMapper cubeMapper = new vtkPolyDataMapper();
    cubeMapper.SetInput(cube);
    cubeMapper.SetScalarRange(cube.GetScalarRange());
//    cubeMapper.SetUseLookupTableScalarRange(1);
    cubeMapper.SetLookupTable(table1);

    // Make actor
    vtkActor cubeActor = new vtkActor();
    cubeActor.SetMapper(cubeMapper);
    
    // Get lookup table info
    vtkLookupTable lut = (vtkLookupTable)cubeMapper.GetLookupTable();
    System.out.println("Lookup table:");
    for(int i = 0; i < x.length; i++) {
      double[] color = lut.GetColor(i);
      System.out.println("  " + i + ": " + color[0] + ", " + color[1] + ", "
        + color[2]);
    }
    double[] range = lut.GetTableRange();
    System.out.println("Range: " + range[0] + "," + range[1]);
//    System.out.println(lut);
     
    // Add actors
    renderer.AddActor(cubeActor);

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
    Axes2 axes2 = new Axes2(1.25);
    axes2.addActor(renderer);
    axes2.setCamera(camera);
    renderer.ResetCamera();

    created = true;
  }

}
