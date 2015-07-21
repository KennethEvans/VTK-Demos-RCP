/*
 * Program to 
 * Created on Sep 12, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import utils.Axes2;
import vtk.vtkActor;
import vtk.vtkCamera;
import vtk.vtkContourFilter;
import vtk.vtkDataSet;
import vtk.vtkDataSetMapper;
import vtk.vtkFloatArray;
import vtk.vtkHedgeHog;
import vtk.vtkPanel;
import vtk.vtkPoints;
import vtk.vtkPolyDataMapper;
import vtk.vtkPolyDataNormals;
import vtk.vtkRenderer;
import vtk.vtkScalarsToColors;
import vtk.vtkStructuredGrid;
import demolauncher.VTKDemo;

public class StructuredGridDemo extends VTKDemo
{
  private static final boolean PRINT_INFO = true;
  private static final boolean DO_HEDGEHOG = true;
  private static final boolean DO_CONTOURS = true;
  private static final boolean DO_DATASET = false;
  private static final boolean DO_AXES = true;
  
  private vtkRenderer renderer = null;
  private vtkActor hedgehogActor = null;
  private vtkActor contActor = null;
  private vtkActor datasetActor =null;
  private Axes2 axes2 = null;
  
  private boolean doHedgehog = DO_HEDGEHOG;
  private boolean doContours = DO_CONTOURS;
  private boolean doDataset = DO_DATASET;
  private boolean doAxes = DO_AXES;
  
  JCheckBox hedgehogCheck = null;
  JCheckBox contoursCheck = null;
  JCheckBox datasetCheck = null;
  JCheckBox axesCheck = null;

  public StructuredGridDemo() {
    super();
    setName("Structured Grid");
  }

  public String getInfo() {
    String info = "This demo illustrates a generated simple\n"
                 + "vtkStructuredGrid.  The points are in a\n"
                 + "half cylinder, with vectors indicating\n"
                 + "rotation about the axis.";
    return info;
  }
  
  public void createPanel() {
    if(created) return;
    
//    vtkFileOutputWindow outWin = new vtkFileOutputWindow();
//    outWin.SetInstance(outWin);
//    outWin.SetFileName(OUTPUT_NAME);
//    
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

    // Create the structured grid
    double[] x = new double[3];
    double[] v = new double[3];
    double rMin = 0.5, rMax = 1.0, deltaRad, deltaZ;
    int[] dims = {13, 11, 11};
    vtkStructuredGrid sgrid = new vtkStructuredGrid();
    sgrid.SetDimensions(dims);

    // We also create the points and vectors. The points
    // form a hemi-cylinder of data.
    vtkFloatArray vectors = new vtkFloatArray();
    vectors.SetNumberOfComponents(3);
    // Don't do this if using InsertNextTuple
    //    vectors.SetNumberOfTuples(dims[0] * dims[1] * dims[2]);
    vectors.SetName("Vectors");
    vtkPoints points = new vtkPoints();
    //    points.Allocate(dims[0]*dims[1]*dims[2]);

    deltaZ = 2.0 / (dims[2] - 1);
    deltaRad = (rMax - rMin) / (dims[1] - 1);
    v[2] = 0.0;
    vtkFloatArray scalars = new vtkFloatArray();
    for(int k = 0; k < dims[2]; k++) {
      x[2] = -1.0 + k * deltaZ;
//      double kOffset = k * dims[0] * dims[1];
      for(int j = 0; j < dims[1]; j++) {
        double radius = rMin + j * deltaRad;
//        double jOffset = j * dims[0];
        for(int i = 0; i < dims[0]; i++) {
          double theta = Math.toRadians(i * 15);
          x[0] = radius * Math.cos(theta);
          x[1] = radius * Math.sin(theta);
          v[0] = -x[1] * radius / rMax;
          v[1] = x[0] * radius / rMax;
//          double offset = i + jOffset + kOffset;
          points.InsertNextPoint(x);
          vectors.InsertNextTuple3(v[0], v[1], v[2]);
          if(false) {
            // Scalar is z
            scalars.InsertNextTuple1(x[2]);
          } else {
            // Scalar is r
            scalars.InsertNextTuple1(radius);
          }
        }
      }
    }
    sgrid.SetPoints(points);
    sgrid.GetPointData().SetVectors(vectors);
    sgrid.GetPointData().SetScalars(scalars);
    if(PRINT_INFO) {
      System.out.println("sgrid:");
      System.out.print(sgrid);
    }

    // Create a hedgehog
    if(true) {
      vtkHedgeHog hedgehog = new vtkHedgeHog();
      hedgehog.SetInput(sgrid);
      hedgehog.SetScaleFactor(0.1);
      if(PRINT_INFO) {
        hedgehog.Update();
        System.out.println("hedgehog output:");
        System.out.print(hedgehog.GetOutput());
      }

      // Create a hedgehog mapper
      vtkPolyDataMapper hedgehogMapper = new vtkPolyDataMapper();
      // hedgehogMapper.SetInputConnection(hedgehog.GetOutputPort());
      hedgehogMapper.SetInput(hedgehog.GetOutput());
      hedgehogMapper.SetScalarRange(sgrid.GetScalarRange());

      // Create a hedgehog actor
      hedgehogActor = new vtkActor();
      hedgehogActor.SetMapper(hedgehogMapper);
      hedgehogActor.GetProperty().SetColor(0, 0, 0);  // ???
      hedgehogActor.SetVisibility(doHedgehog?1:0);
     
      renderer.AddActor(hedgehogActor);
    }

    // Make a contour filter
    if(true) {
      vtkContourFilter contFilter = new vtkContourFilter();
//      contFilter.DebugOn();
      contFilter.SetInput(sgrid);
      contFilter.GenerateValues(5, sgrid.GetScalarRange());
//      contFilter.SetValue(0, 0);
//      contFilter.SetValue(1, .99);
//      contFilter.SetValue(2, -.99);
      if(PRINT_INFO) {
        contFilter.Update();
        double[] range = sgrid.GetScalarRange();
        System.out
          .println("sgrid.GetScalarRange: " + range[0] + "," + range[1]);
        vtkDataSet output = contFilter.GetOutput();
        System.out.println("contFilter Output:");
        System.out.print(output);
      }

      // Calculate normals
      // This was necessary to avoid the contours being black
      vtkPolyDataNormals normals = new vtkPolyDataNormals();
      normals.SetInput(contFilter.GetOutput());
      normals.SetFeatureAngle(45);

      // Create a mapper and actor as usual. In this case adjust the
      // scalar range of the mapper to match that of the computed scalars
      vtkPolyDataMapper contMapper = new vtkPolyDataMapper();
//      contMapper.SetInput(contFilter.GetOutput());
      contMapper.SetInput(normals.GetOutput());
      contMapper.SetScalarRange(sgrid.GetScalarRange());
//    contMapper.ScalarVisibilityOn();
//      contMapper.ScalarVisibilityOff();
//      contMapper.SetScalarRange(-1, 1);
//      contMapper.SetScalarModeToUsePointFieldData();
//      contMapper.ColorByArrayComponent("Scalars", 0);
      if(PRINT_INFO) {
        contMapper.Update();
        System.out.println("contourMapper:");
        System.out.println("GetArrayName: " + contMapper.GetArrayName());
        System.out.println("GetArrayId: " + contMapper.GetArrayId());
        System.out.println("GetArrayComponent: "
          + contMapper.GetArrayComponent());
        System.out.print(contMapper);
        vtkScalarsToColors lut = contMapper.GetLookupTable();
        System.out.println("contourMapper LookupTable:");
        System.out.println(lut);
      }

      // Set the actor
      contActor = new vtkActor();
      contActor.SetMapper(contMapper);
      contActor.SetVisibility(doContours ? 1 : 0);

      renderer.AddActor(contActor);
    }

    // Make a dataset mapper
    if(true) {
      vtkDataSetMapper sgridMapper = new vtkDataSetMapper();
      sgridMapper.SetInput(sgrid);
      sgridMapper.SetScalarRange(sgrid.GetScalarRange());
      // sgridMapper.SetUseLookupTableScalarRange(1);
      // sgridMapper.SetLookupTable(table1);
      if(PRINT_INFO) {
        sgridMapper.Update();
        System.out.println("sgridMapper:");
        System.out.print(sgridMapper);
        vtkScalarsToColors lut = sgridMapper.GetLookupTable();
        System.out.println("sgridMapper LookupTable:");
        System.out.println(lut);
      }

      // Make sgrid actor
      datasetActor = new vtkActor();
      datasetActor.SetMapper(sgridMapper);
      datasetActor.SetVisibility(doDataset?1:0);

      renderer.AddActor(datasetActor);
    }

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
    if(true) {
      axes2 = new Axes2(1.5);
      axes2.addActor(renderer);
      axes2.setCamera(camera);
      axes2.SetVisibility(doAxes?1:0);
      renderer.ResetCamera();
    }

    created = true;
  }

  public JPanel createControlPanel() {
    JPanel controlPanel = new JPanel();
    FlowLayout layout = new FlowLayout();
    controlPanel.setLayout(layout);

    // Hedgehog
    hedgehogCheck = new JCheckBox();
    hedgehogCheck.setText("Hedgehog");
    hedgehogCheck.setSelected(doHedgehog);
   
    controlPanel.add(hedgehogCheck);
    hedgehogCheck.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doHedgehog = hedgehogCheck.isSelected();
        if(hedgehogActor != null) {
          hedgehogActor.SetVisibility(doHedgehog ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });

    // Contours
    contoursCheck = new JCheckBox();
    contoursCheck.setText("Contours");
    contoursCheck.setSelected(doContours);
    controlPanel.add(contoursCheck);
    contoursCheck.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doContours = contoursCheck.isSelected();
        if(contActor != null) {
          contActor.SetVisibility(doContours ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });

    // DataSet
    datasetCheck = new JCheckBox();
    datasetCheck.setText("Dataset");
    datasetCheck.setSelected(doDataset);
    controlPanel.add(datasetCheck);
    datasetCheck.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doDataset = datasetCheck.isSelected();
        if(datasetActor != null) {
          datasetActor.SetVisibility(doDataset ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });

    // Axes
    axesCheck = new JCheckBox();
    axesCheck.setSelected(doAxes);
    axesCheck.setText("Axes");
    controlPanel.add(axesCheck);
    axesCheck.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doAxes = axesCheck.isSelected();
        if(axes2 != null) {
          axes2.SetVisibility(doAxes ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });

    return controlPanel;
  }

}
