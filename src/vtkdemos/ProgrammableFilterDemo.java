/*
 * Program to 
 * Created on Sep 5, 2006
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
import vtk.vtkPlaneSource;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkProgrammableFilter;
import vtk.vtkRenderer;
import vtk.vtkTransform;
import vtk.vtkTransformPolyDataFilter;
import vtk.vtkWarpScalar;
import demolauncher.VTKDemo;

public class ProgrammableFilterDemo extends VTKDemo
{
  private vtkProgrammableFilter progFilter;
  // Mode = 0: Original expcos
  // Mode = 1: 2D Gaussian
  // Mode = 1: UVW
  protected int MODE = 0;
//  private static final boolean DEBUG = false;
  private static final boolean PRINT_INFO = true;
  // Scalars are necessary for colors.  Calculate them in the func.
  private static final boolean DO_SCALARS = true;
  // Use this to transform the default plane, otherwise set the plane itself
  private static final boolean DO_TRANSFORM = false;
  private static final boolean DO_SURFACE = true;
  private static final boolean DO_CONTOURS = true;
  private static final int NPOINTS = 100;
  private static final double SCALEX = 3.;
  private static final double SCALEY = 3.;
  private static final double WARP_SCALE_FACTOR = 1;
  private double zMax = -Double.MAX_VALUE;
  private double zMin = Double.MAX_VALUE;

  public ProgrammableFilterDemo() {
    super();
    setName("Programmable Filter");
  }

  public String getInfo() {
    String info = "This demo illustrates a function calculated\n"
                + "by a programmable filter.  It also uses a warp\n"
                + "filter (not a good idea for this problem) and\n"
                + "a contour filter.\n"
                ;
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
    
    // Create a plane source to sample
    vtkPlaneSource plane = new vtkPlaneSource();
    plane.SetXResolution(NPOINTS);
    plane.SetYResolution(NPOINTS);
    if(!DO_TRANSFORM) {
      plane.SetOrigin(-SCALEX, -SCALEY, 0);
      plane.SetPoint1(SCALEX, -SCALEY, 0);
      plane.SetPoint2(-SCALEX, SCALEY, 0);
      plane.SetCenter(0, -SCALEX, 0);
    }
    if(PRINT_INFO) {
      System.out.println("plane:");
      double[] val = plane.GetOrigin();
      System.out.println("Origin: " + val[0] + "," + val[1] + "," + val[2]);
      val = plane.GetCenter();
      System.out.println("Center: " + val[0] + "," + val[1] + "," + val[2]);
      val = plane.GetPoint1();
      System.out.println("Point1: " + val[0] + "," + val[1] + "," + val[2]);
      val = plane.GetPoint2();
      System.out.println("Point2: " + val[0] + "," + val[1] + "," + val[2]);
      val = plane.GetNormal();
      System.out.println("Normal: " + val[0] + "," + val[1] + "," + val[2]);
    }

    // Transform the plane by a factor of 10 on X and Y
    vtkTransform transform = new vtkTransform();
    transform.Scale(2 * SCALEX, 2 * SCALEY, 1);
    vtkTransformPolyDataFilter transFilter = new vtkTransformPolyDataFilter();
    transFilter.SetInput(plane.GetOutput());
    transFilter.SetTransform(transform);
    
    vtkDataSet filterInput = null;
    if(DO_TRANSFORM) filterInput = transFilter.GetOutput();
    else filterInput = plane.GetOutput();

    // Set up the programmable filter to call func, which is defined in this
    // class
    progFilter = new vtkProgrammableFilter();
    progFilter.SetInput(filterInput);
    progFilter.SetExecuteMethod(this, "func");
    if(PRINT_INFO) {
      progFilter.Update();
      vtkPolyData data = progFilter.GetPolyDataOutput();
      System.out.println("progFilter Output:");
      System.out.print(data);
    }

    // Do a warp surface
    // Note that this will warp the already existing surface by the scalar times
    // the scale factor. Since in our case the surface is already OK, we don't
    // really want to warp. If we do the warp surface will be higher than the
    // contours.
    vtkActor surfActor = null;
    if(DO_SURFACE) {
      // Warp the plane based on the scalar values calculated above
      vtkWarpScalar warp = new vtkWarpScalar();
      warp.SetInput(progFilter.GetPolyDataOutput());
      warp.XYPlaneOn();
      warp.SetScaleFactor(WARP_SCALE_FACTOR);
      if(PRINT_INFO) {
        warp.Update();
        vtkPolyData data = warp.GetPolyDataOutput();
        System.out.println("GetScaleFactor: " + warp.GetScaleFactor());
        System.out.println("GetXYPlane: " + warp.GetXYPlane());
        System.out.println("GetUseNormal: " + warp.GetUseNormal());
        System.out.println("warp Output:");
        System.out.print(data);
      }

      // Create a mapper and actor as usual. In this case adjust the
      // scalar range of the mapper to match that of the computed scalars
      vtkPolyDataMapper warpMapper = new vtkPolyDataMapper();
      warpMapper.SetInput(warp.GetPolyDataOutput());
      warpMapper.SetScalarRange(progFilter.GetPolyDataOutput().GetScalarRange());
//      surfMapper.SetUseLookupTableScalarRange(1);
//      surfMapper.ScalarVisibilityOn();
//      surfMapper.CreateDefaultLookupTable();
//      surfMapper.SetScalarModeToUsePointFieldData();
//      surfMapper.SetScalarModeToUseCellFieldData();
//      surfMapper.SelectColorArray(0);
      if(PRINT_INFO) {
        warpMapper.Update();
        System.out.println("warpMapper:");
        System.out.println("GetArrayName: " + warpMapper.GetArrayName());
        System.out.println("GetArrayId: " + warpMapper.GetArrayId());
        System.out.println("GetArrayComponent: " + warpMapper.GetArrayComponent());
        System.out.print(warpMapper);
      }

      // Set the actor
      surfActor = new vtkActor();
      surfActor.SetMapper(warpMapper);
    }

    // Do countours
    vtkActor contActor = null;
    if(DO_CONTOURS) {
    // Make a contour filter
    vtkContourFilter iso = new vtkContourFilter();
    iso.SetInput(progFilter.GetPolyDataOutput());
    iso.GenerateValues(10, zMin, zMax);
    if(PRINT_INFO) {
      iso.Update();
      vtkPolyData data = iso.GetOutput();
      System.out.println("iso Output:");
      System.out.print(data);
    }

//    vtkPolyDataNormals normals = new vtkPolyDataNormals();
//    normals.SetInput(iso.GetOutput());
//    normals.SetFeatureAngle(45);

    // Create a mapper and actor as usual. In this case adjust the
    // scalar range of the mapper to match that of the computed scalars
    vtkPolyDataMapper contMapper = new vtkPolyDataMapper();
    contMapper.SetInput(iso.GetOutput());
    contMapper.SetScalarRange(progFilter.GetPolyDataOutput().GetScalarRange());
//    contMapper.ScalarVisibilityOn();
//    contourMapper.SetScalarRange(0, 1500);
//    contourMapper.SetScalarModeToUsePointFieldData();
//    contourMapper.ColorByArrayComponent("VelocityMagnitude", 0);
    if(PRINT_INFO) {
        contMapper.Update();
        System.out.println("contourMapper:");
        System.out.println("GetArrayName: " + contMapper.GetArrayName());
        System.out.println("GetArrayId: " + contMapper.GetArrayId());
        System.out.println("GetArrayComponent: "
          + contMapper.GetArrayComponent());
        System.out.print(contMapper);
      }

    // Set the actor
    contActor = new vtkActor();
    contActor.SetMapper(contMapper);
}

    // Get the renderer
    vtkRenderer renderer = renWin.GetRenderer();
    renderer.SetBackground(1, 1, 1); // background color white

    // Add actors
    if(DO_CONTOURS) renderer.AddActor(contActor);
    if(DO_SURFACE) renderer.AddActor(surfActor);

    // Set the camera
    vtkCamera camera = new vtkCamera();
    renderer.SetActiveCamera(camera);
    renderer.ResetCamera();

    created = true;
  }
  
  /**
   * The SetExecuteMethod takes an object reference and a function name
   * as an argument. In here is where all the processing is done.
   */
  public void func() {
    System.out.println();
    vtkPolyData input = progFilter.GetPolyDataInput();
    int numPts = input.GetNumberOfPoints();
    vtkPoints newPts = new vtkPoints();
    vtkFloatArray scalars = new vtkFloatArray();

    // Calculate and set the values
    zMax = -Double.MAX_VALUE;
    zMin = Double.MAX_VALUE;
    for(int i = 0; i < numPts; ++i) {
      double[] x = input.GetPoint(i);
      double x0 = x[0];
      double x1 = x[1];
      double x2 = 0;
      double scalar = 0;

      switch(MODE) {
      case 0: {
        // Based on expcos example
        double r = Math.sqrt(x0 * x0 + x1 * x1);
        x2 = Math.exp(-r) * Math.cos(10.0 * r);
        // Make scalar d(x2)/dr
        scalar = -Math.exp(-r)
          * (Math.cos(10.0 * r) + 10.0 * Math.sin(10.0 * r));
        break;
      }
      case 1: {
        // It is not efficient to define these here, but convenient
        // double a = SCALE / 3.;
        double a1 = 1;
        double a2 = 1;
        double sigma1 = .5;
        double sigma2 = 2.;
        double mean1 = 0.;
        double mean2 = 0.;
        double arg1 = (x0 - mean1) / sigma1;
        double arg2 = (x1 - mean2) / sigma2;
        x2 = a1 * Math.exp(-.5 * arg1 * arg1) * a2
          * Math.exp(-.5 * arg2 * arg2);
        // Make scalar the same as x2
        scalar = x2;
        break;
      }
      case 2: {
        double w0 = 0;
        x2 = w0 - (x0 * x0 - x1 * x1);
        // Make scalar the same as x2
        scalar = x2;
        break;
      }
      }
      if(x2 > zMax) zMax = x2;
      if(x2 < zMin) zMin = x2;
      newPts.InsertPoint(i, x0, x1, x2);
      scalars.InsertValue(i, scalar);
    }

    // Set the output
    vtkPolyData data = progFilter.GetPolyDataOutput();
    data.CopyStructure(input);
    data.SetPoints(newPts);
    if(DO_SCALARS) data.GetPointData().SetScalars(scalars);
  }


}
