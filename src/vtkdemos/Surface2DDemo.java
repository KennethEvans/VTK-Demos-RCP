/*
 * Program to 
 * Created on Oct 10, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.Axes;
import utils.Histogram2D;
import utils.VTKUtils;
import vtk.vtkActor;
import vtk.vtkCamera;
import vtk.vtkContourFilter;
import vtk.vtkDataSet;
import vtk.vtkDataSetMapper;
import vtk.vtkFloatArray;
import vtk.vtkImageData;
import vtk.vtkImageDataGeometryFilter;
import vtk.vtkLookupTable;
import vtk.vtkMergeFilter;
import vtk.vtkPanel;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderer;
import vtk.vtkWarpScalar;
import demolauncher.VTKDemo;

public class Surface2DDemo extends VTKDemo
{
  private static final int DEBUG_LEVEL = 0;
  protected int functionType = 0;

  private static String arrayName = "Scalars";

  private vtkRenderer renderer = null;
  private vtkImageData idata0 = null;
  
  int nPoints1 = 100;
  int nPoints2 = 100;
  int nRandom = 100000;

  double a1 = 3;
  double sigma1 = .5;
  double mean1 = 0;
  double upper1 = 3.0;
  double lower1 = -3.0;
  double delta1 = (upper1 - lower1) / (nPoints1 -1);

  double a2 = 1;
  double sigma2 = 2.0;
  double mean2 = 0;
  double upper2 = 3.0;
  double lower2 = -3.0;
  double delta2 = (upper2 - lower2) / (nPoints2 -1);
  

  double xMax = -Double.MAX_VALUE;
  double xMin = Double.MAX_VALUE;
  double yMax = -Double.MAX_VALUE;
  double yMin = Double.MAX_VALUE;
  double zMax = -Double.MAX_VALUE;
  double zMin = Double.MAX_VALUE;

  private static final boolean DO_IDATA0 = false;
  private boolean doIdata0 = DO_IDATA0;
  private vtkActor idata0Actor =null;

  private static final boolean DO_CONTOURS0 = true;
  private boolean doContours0 = DO_CONTOURS0;
  private vtkActor contours0Actor =null;

  private static final boolean DO_IDATA1 = false;
  private boolean doIdata1 = DO_IDATA1;
  private vtkActor idata1Actor = null;

  private static final boolean DO_CONTOURS1 = false;
  private boolean doContours1 = DO_CONTOURS1;
  private vtkActor contours1Actor =null;

  private static final boolean DO_AXES = false;
  private boolean doAxes = DO_AXES;
  private Axes axes = null;

  public Surface2DDemo() {
    super();
    setName("Surface 2D");
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
    
    // Make a control panel and add it to the JPanel
    JPanel controlPanel = createControlPanel();
    panel.add(controlPanel, BorderLayout.SOUTH);

    // Make a vtkPanel and add it to the JPanel
    vtkPanel renWin = new vtkPanel();
    panel.add(renWin, BorderLayout.CENTER);
    
    // Get the renderer
    renderer = renWin.GetRenderer();
    renderer.SetBackground(1, 1, 1); // background color white

    // Make a rainbow lookup table
    vtkLookupTable lut = VTKUtils.getRainbowLookupTable();

    // Create the structured grid dataset with the appropriate function
    switch(functionType) {
    case 0:
      arrayName = "Gaussian2D"; 
      idata0 = createGaussianData();
      break;
    case 1:
      arrayName = "Gaussian2dDistribution"; 
      idata0 = createDistributionData();
      break;
    }
    double[] bounds = idata0.GetBounds();
    if(xMin == Double.MAX_VALUE) xMin = bounds[0];
    if(xMax == -Double.MAX_VALUE) xMax = bounds[1];
    if(yMin == Double.MAX_VALUE) yMin = bounds[2];
    if(yMax == -Double.MAX_VALUE) yMax = bounds[3];
    double[] range = idata0.GetScalarRange();
    if(zMin == Double.MAX_VALUE) zMin = range[0];
    if(zMax == -Double.MAX_VALUE) zMax = range[1];
    zMax = range[1];
    zMin = range[0];
    if(DEBUG_LEVEL > 0) {
      System.out.println("idata0:");
      System.out.println("functionType=" + functionType);
      System.out.println("arrayName=" + arrayName);
      System.out.println("zMin,zMax=" + zMin + "," + zMax);
      range = idata0.GetScalarRange();
      System.out.println("ScalarRange=" + range[0] + "," + range[1]);
      System.out.println("Bounds");
      for(int i = 0; i < bounds.length; i++) {
        System.out.println("  " + i + " " + bounds[i]);
      }
      System.out.print(idata0);
    }
    
    //// idata0 //////////////////////////////////////////////////////////////

    // Make a dataset mapper for idata0
    vtkDataSetMapper idata0Mapper = new vtkDataSetMapper();
    idata0Mapper.SetLookupTable(lut);
    idata0Mapper.SetInput(idata0);
//    idata0Mapper.SetScalarRange(zMin, zMax);
    // Color by another array (needs both of these)
//    datasetMapper.SetScalarModeToUsePointFieldData();
//    datasetMapper.ColorByArrayComponent("W", 0);
//    datasetMapper.SetUseLookupTableScalarRange(1);
//    datasetMapper.SetLookupTable(table1);
    if(DEBUG_LEVEL > 1) {
      idata0Mapper.Update();
      System.out.println("idata0Mapper:");
      System.out.println("zMin,zMax=" + zMin + "," + zMax);
      System.out.print(idata0Mapper);
    }

    // Make the actor for the idata0 mapper
    idata0Actor = new vtkActor();
    idata0Actor.SetMapper(idata0Mapper);
    idata0Actor.SetVisibility(doIdata0 ? 1 : 0);
    renderer.AddActor(idata0Actor);

    // Make a contour filter for idata0
    vtkContourFilter cont0Filter = new vtkContourFilter();
    cont0Filter.SetInput(idata0);
    cont0Filter.GenerateValues(10, zMin, zMax);

    // Create a mapper for the idata0 contour filter
    vtkPolyDataMapper cont0Mapper = new vtkPolyDataMapper();
    cont0Mapper.SetLookupTable(lut);
    cont0Mapper.SetInput(cont0Filter.GetOutput());
    cont0Mapper.SetScalarRange(zMin, zMax);

    // Make the actor for the idata0 contour filter
    contours0Actor = new vtkActor();
    contours0Actor.SetMapper(cont0Mapper);
    contours0Actor.SetVisibility(doContours0 ? 1 : 0);
    renderer.AddActor(contours0Actor);
    
    //// idata1 //////////////////////////////////////////////////////////////
    
    // Use a geometry filter to extract the geometry from the vtkImageData
    vtkImageDataGeometryFilter geometry = new vtkImageDataGeometryFilter();
    geometry.SetInput(idata0);
    
    // Warp to a 2D surface
    vtkWarpScalar warp = new vtkWarpScalar();
    warp.SetInput(geometry.GetOutput());
    double scale = (xMax - xMin) / (zMax - zMin);
    warp.SetScaleFactor(scale);
    
    // Merge the warp with the original data to get the scalars back
    vtkMergeFilter merge = null;
    vtkDataSet idata1 = null;
    if(false) {
      // Not necessary if using the original scalar to color
      merge = new vtkMergeFilter();
      merge.SetGeometry(warp.GetOutput());
      merge.SetScalars(idata0);
      idata1 = warp.GetOutput();
    } else {
      idata1 = warp.GetOutput();
    }

    // Create a mapper for idata1
    vtkDataSetMapper idata1Mapper = new vtkDataSetMapper();
    idata1Mapper.SetLookupTable(lut);
    idata1Mapper.SetInput(idata1);
    idata1Mapper.SetScalarRange(zMin, zMax);

    // Set the actor for the idata1 mapper
    idata1Actor = new vtkActor();
    idata1Actor.SetMapper(idata1Mapper);
    idata1Actor.SetVisibility(doIdata1 ? 1 : 0);
    renderer.AddActor(idata1Actor);

    // Make a contour filter for idata1
    vtkContourFilter cont1Filter = new vtkContourFilter();
    cont1Filter.SetInput(idata1);
    cont1Filter.GenerateValues(10, zMin, zMax);

    // Create a mapper for the idata1 contour filter
    vtkPolyDataMapper cont1Mapper = new vtkPolyDataMapper();
    cont1Mapper.SetLookupTable(lut);
    cont1Mapper.SetInput(cont1Filter.GetOutput());
    cont1Mapper.SetScalarRange(zMin, zMax);

    // Make the actor for the idata1 contour filter
    contours1Actor = new vtkActor();
    contours1Actor.SetMapper(cont1Mapper);
    contours1Actor.SetVisibility(doContours1 ? 1 : 0);
    renderer.AddActor(contours1Actor);
    
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
    axes = new Axes(3.5);
    axes.setZScale(2);
    axes.createActors();
    axes.addActor(renderer);
    axes.setCamera(camera);
    axes.SetVisibility(doAxes ? 1 : 0);
    renderer.ResetCamera();
    if(DEBUG_LEVEL > 1) {
      System.out.println("Renderer 0:");
      System.out.println(renderer);
      System.out.println("Camera 0:");
      System.out.println(camera);
    }

    created = true;
  }

  public JPanel createControlPanel() {
    JPanel controlPanel = new JPanel();
    FlowLayout layout = new FlowLayout();
    controlPanel.setLayout(layout);
    
    if(false) {
      JLabel label = new JLabel("Control Panel");
      controlPanel.add(label);
    }
    
    // Reset
    final JButton resetButton = new JButton("Reset");
    controlPanel.add(resetButton);
    resetButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        vtkCamera camera = renderer.GetActiveCamera();
        camera.SetViewUp(0, 1, 0);
        camera.SetPosition(0, 0, 1);
        camera.SetFocalPoint(0, 0, 0);
        camera.SetViewAngle(30);
        camera.SetClippingRange(.1, 1000);
        camera.ComputeViewPlaneNormal();
        renderer.ResetCamera();
//        renderer.UpdateLightsGeometryToFollowCamera();
        renderer.GetRenderWindow().Render();
        if(DEBUG_LEVEL > 1) {
          System.out.println("Renderer:");
          System.out.println(renderer);
          System.out.println("Camera:");
          System.out.println(camera);
        }
      }
    });
    
    // Contours0
    final JCheckBox contoursCheck0 = new JCheckBox();
    contoursCheck0.setText("2D Contours");
    contoursCheck0.setSelected(doContours0);
    controlPanel.add(contoursCheck0);
    contoursCheck0.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doContours0 = contoursCheck0.isSelected();
        if(contours0Actor != null) {
          contours0Actor.SetVisibility(doContours0 ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });
    
    // idata
    final JCheckBox idataCheck = new JCheckBox();
    idataCheck.setText("2D Colors");
    idataCheck.setSelected(doIdata0);
    controlPanel.add(idataCheck);
    idataCheck.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doIdata0 = idataCheck.isSelected();
        if(idata0Actor != null) {
          idata0Actor.SetVisibility(doIdata0 ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });

    // Contours1
    final JCheckBox contoursCheck1 = new JCheckBox();
    contoursCheck1.setText("3D Contours");
    contoursCheck1.setSelected(doContours1);
    controlPanel.add(contoursCheck1);
    contoursCheck1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doContours1 = contoursCheck1.isSelected();
        if(contours1Actor != null) {
          contours1Actor.SetVisibility(doContours1 ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });
    
    // idata1
    final JCheckBox idata1Check = new JCheckBox();
    idata1Check.setText("3D Colors");
    idata1Check.setSelected(doIdata1);
    controlPanel.add(idata1Check);
    idata1Check.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doIdata1 = idata1Check.isSelected();
        if(idata1Actor != null) {
          idata1Actor.SetVisibility(doIdata1 ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });

    // Axes
    final JCheckBox axesCheck = new JCheckBox();
    axesCheck.setSelected(doAxes);
    axesCheck.setText("Axes");
    controlPanel.add(axesCheck);
    axesCheck.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doAxes = axesCheck.isSelected();
        if(axes != null) {
          axes.SetVisibility(doAxes ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });

    return controlPanel;
  }
  
  protected vtkImageData createGaussianData() {
    // Create the vtkImageData
    vtkImageData idata = new vtkImageData();
    idata.SetDimensions(nPoints1, nPoints2, 1);
    idata.SetSpacing(delta1, delta2, 0);
    idata.SetOrigin(lower1, lower2, 0);

    // Make arrays for vectors, scalars, and points
    vtkFloatArray gArray = new vtkFloatArray();
    gArray.SetNumberOfComponents(1);
    gArray.SetName(arrayName);

    double x, y, z, arg1, arg2;
    for(int j = 0; j < nPoints2; j++) {
      y = lower2 + delta2 * j;
      arg2 = (y - mean2) / sigma2;
      for(int i = 0; i < nPoints1; i++) {
        x = lower1 + delta1 * i;
        arg1 = (x - mean1) / sigma1;
        z = a1 * Math.exp(-.5 * arg1 * arg1) * a2 * Math.exp(-.5 * arg2 * arg2);
        if(x > xMax) xMax = x;
        if(x < xMin) xMin = x;
        if(y > yMax) yMax = y;
        if(y < yMin) yMin = y;
        if(z > zMax) zMax = z;
        if(z < zMin) zMin = z;
        gArray.InsertNextTuple1(z);
      }
    }

    // Reset
    zMax = a1 * a2;
    zMin = 0;
    
    idata.GetPointData().AddArray(gArray);
    idata.GetPointData().SetActiveScalars(arrayName);
    
    return idata;
  }

  protected vtkImageData createDistributionData() {
    // Create the vtkImageData
    vtkImageData idata = new vtkImageData();
    idata.SetDimensions(nPoints1, nPoints2, 1);
    idata.SetSpacing(delta1, delta2, 0);
    idata.SetOrigin(lower1, lower2, 0);

    // Make arrays for vectors, scalars, and points
    vtkFloatArray gArray = new vtkFloatArray();
    gArray.SetNumberOfComponents(1);
    gArray.SetName(arrayName);

    // Define range data
    Histogram2D hist = new Histogram2D("hist", nPoints1, lower1, upper1,
      nPoints2, lower2, upper2);
    Random r = new Random();
    for(int i = 0; i < nRandom; i++) {
      hist.fill(sigma1 * r.nextGaussian(), sigma2 * r.nextGaussian());
    }
    double[][] cellVals = hist.getCellVals();
    double x, y, z;
//    double arg1, arg2;
    for(int j = 0; j < nPoints2; j++) {
      y = lower2 + delta2 * j;
//      arg2 = (y - mean2) / sigma2;
      for(int i = 0; i < nPoints1; i++) {
        x = lower1 + delta1 * i;
//        arg1 = (x - mean1) / sigma1;
        z = cellVals[i][j];
        if(x > xMax) xMax = x;
        if(x < xMin) xMin = x;
        if(y > yMax) yMax = y;
        if(y < yMin) yMin = y;
        if(z > zMax) zMax = z;
        if(z < zMin) zMin = z;
        gArray.InsertNextTuple1(z);
      }
    }
    
    // Reset
    zMin = 0;
    
    idata.GetPointData().AddArray(gArray);
    idata.GetPointData().SetActiveScalars(arrayName);
    
    return idata;
  }

}
