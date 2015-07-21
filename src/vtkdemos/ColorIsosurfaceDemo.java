/*
 * Program to 
 * Created on Sep 4, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import vtk.vtkActor;
import vtk.vtkCamera;
import vtk.vtkContourFilter;
import vtk.vtkDataObject;
import vtk.vtkLODActor;
import vtk.vtkPLOT3DReader;
import vtk.vtkPanel;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkPolyDataNormals;
import vtk.vtkRenderer;
import vtk.vtkStructuredGrid;
import vtk.vtkStructuredGridOutlineFilter;
import demolauncher.VTKDemo;

public class ColorIsosurfaceDemo extends VTKDemo
{
  private static final boolean DEBUG = false;
  private static final boolean OUTLINE = true;
  private static final boolean COLOR_CONTOURS = true;
  private static final boolean PRINT_INFO = true;
  private static final String VTK_DATA_ROOT = "C:/VTK";
  private static final String FILE_NAME1 = VTK_DATA_ROOT + "/Data/combxyz.bin";
  private static final String FILE_NAME2 = VTK_DATA_ROOT + "/Data/combq.bin";
//  private static final String FILE_NAME1 = VTK_DATA_ROOT + "/Data/bluntfinxyz.bin";
//  private static final String FILE_NAME2 = VTK_DATA_ROOT + "/Data/bluntfinq.bin";

  public ColorIsosurfaceDemo() {
    super();
    setName("Color Isosurface");
  }

  public String getInfo() {
    String info = "This demo illustrates a color isosurface from the VTK\n"
      + "examples.  The data is combustion data from a PLOT3D file.";
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

    // Read some data. The important thing here is to read a function as a
    // data array as well as the scalar and vector.  (Here function 153 is
    // named "Velocity Magnitude"). Later this data array will be used to
    // color the isosurface.  The numbers are PLOT2D standards.
    // The data are from an annular combustor. A combustor burns fuel
    // and air in a gas turbine (e.g., a jet engine); and the hot gas
    // eventually makes its way to the turbine section.
    vtkPLOT3DReader pl3d = new vtkPLOT3DReader();
    pl3d.SetXYZFileName(FILE_NAME1);
    pl3d.SetQFileName(FILE_NAME2);
    pl3d.SetScalarFunctionNumber(100);
    pl3d.SetVectorFunctionNumber(202);
    if(COLOR_CONTOURS) pl3d.AddFunction(153);
    pl3d.Update();
    if(DEBUG) pl3d.DebugOn();
    if(PRINT_INFO) {
      System.out.println("vtkPLOT3DReader Information for pl3d:");
      if(false) {
        // Same as below
        String info = pl3d.Print();
        System.out.println(info);
      } else {
        System.out.print(pl3d);
      }
      System.out.println("FileName:");
      System.out.println(pl3d.GetFileName());
      System.out.println("XYZileName:");
      System.out.println(pl3d.GetXYZFileName());
      System.out.println("QFileName:");
      System.out.println(pl3d.GetQFileName());
      System.out.println();
      if(true) {
        vtkStructuredGrid output = pl3d.GetOutput();
        System.out.println("pl3d output:");
        System.out.println(output);
      }
      if(false) {
        int nOutputs = pl3d.GetNumberOfOutputs();
        for(int i = 0; i < nOutputs; i++) {
          vtkDataObject dataObject = pl3d.GetOutputDataObject(i);
          System.out.println("vtkDataObject for Output " + i + ":");
          System.out.println(dataObject);
        }
      }
    }

    // The contour filter uses the labeled scalar (function number 100
    // above to generate the contour surface; all other data is
    // interpolated during the contouring process.
    vtkContourFilter iso = new vtkContourFilter();
    iso.SetInput(pl3d.GetOutput());
    iso.SetValue(0, .24);
    if(true) {
      iso.Update();
      vtkPolyData output = iso.GetOutput();
      System.out.println("iso output:");
      System.out.println(output);
    }

    vtkPolyDataNormals normals = new vtkPolyDataNormals();
    normals.SetInput(iso.GetOutput());
    // A shared edge is considered "sharp" above this angle
    normals.SetFeatureAngle(45);

    // We indicate to the mapper to use the velocity magnitude, which is a
    // vtkDataArray that makes up part of the point attribute data.
    vtkPolyDataMapper isoMapper = new vtkPolyDataMapper();
    isoMapper.SetInput(normals.GetOutput());
    isoMapper.ScalarVisibilityOn();
    isoMapper.SetScalarRange(0, 1500);
    isoMapper.SetScalarModeToUsePointFieldData();
    isoMapper.ColorByArrayComponent("VelocityMagnitude", 0);

    // Make a level-of-detail actor
    vtkLODActor isoActor = new vtkLODActor();
    isoActor.SetMapper(isoMapper);
    isoActor.SetNumberOfCloudPoints(1000);

    if(OUTLINE) {
      vtkStructuredGridOutlineFilter outline = new vtkStructuredGridOutlineFilter();
      outline.SetInput(pl3d.GetOutput());
      vtkPolyDataMapper outlineMapper = new vtkPolyDataMapper();
      outlineMapper.SetInput(outline.GetOutput());
      vtkActor outlineActor = new vtkActor();
      outlineActor.SetMapper(outlineMapper);
      outlineActor.GetProperty().SetColor(0, 0, 0);
      renderer.AddActor(outlineActor);
    }
    
    // Add actors
    renderer.AddActor(isoActor);
//    renderer.SetBackground(0.1, 0.2, 0.4);

    // Set the camera
    if(true) {
      vtkCamera cam1 = renderer.GetActiveCamera();
      cam1.SetClippingRange(3.95297, 50);
      cam1.SetFocalPoint(9.71821, 0.458166, 29.3999);
      cam1.SetPosition(2.7439, -37.3196, 38.7167);
      cam1.SetViewUp(-0.16123, 0.264271, 0.950876);
    } else {
      vtkCamera camera = new vtkCamera();
      camera.SetClippingRange(1, 1000);
      camera.SetFocalPoint(0, 0, 0);
      camera.SetPosition(0, 0, 5);
      camera.SetViewUp(0, 1, 0);
      camera.Zoom(.8);
      renderer.SetActiveCamera(camera);
    }

    created = true;
  }

}
