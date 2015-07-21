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
import vtk.vtkDataSetWriter;
import vtk.vtkFloatArray;
import vtk.vtkHedgeHog;
import vtk.vtkPanel;
import vtk.vtkPoints;
import vtk.vtkPolyDataMapper;
import vtk.vtkPolyDataNormals;
import vtk.vtkRenderer;
import vtk.vtkStructuredGrid;
import demolauncher.VTKDemo;

public class SurfaceContours1 extends VTKDemo
{
  private static final boolean PRINT_INFO = true;
  private static final boolean DO_HEDGEHOG = false;
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

  public SurfaceContours1() {
    super();
    setName("Surface Contours 1");
  }

  public String getInfo() {
    String info = "This demo illustrates a generated simple vtkStructuredGrid.\n";
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
    int[] dims = {10, 10, 10};
    vtkStructuredGrid sgrid = new vtkStructuredGrid();
    sgrid.SetDimensions(dims);

    // Make arrays for vectors, scalars, and points
    vtkFloatArray vectors = new vtkFloatArray();
    vectors.SetNumberOfComponents(3);
    vectors.SetName("Vectors");
    
    vtkFloatArray uArray = new vtkFloatArray();
    uArray.SetNumberOfComponents(1);
    uArray.SetName("U");

    vtkFloatArray vArray = new vtkFloatArray();
    vArray.SetNumberOfComponents(1);
    vArray.SetName("V");

    vtkFloatArray wArray = new vtkFloatArray();
    wArray.SetNumberOfComponents(1);
    wArray.SetName("W");

    vtkFloatArray scalars = new vtkFloatArray();
    scalars.SetName("Scalars");

    vtkPoints points = new vtkPoints();

    double deltaX = 2.0 / (dims[0] - 1);
    double deltaY = 2.0 / (dims[1] - 1);
    double deltaZ = 2.0 / (dims[2] - 1);
    double zMax = -Double.MAX_VALUE;
    double zMin = Double.MAX_VALUE;
    for(int k = 0; k < dims[2]; k++) {
      x[2] = -1.0 + k * deltaZ;
      for(int j = 0; j < dims[1]; j++) {
        x[1] = -1.0 + j * deltaY;
        for(int i = 0; i < dims[0]; i++) {
          x[0] = -1.0 + i * deltaX;
          v[0] = .5 * (x[0] + x[1]);
          v[1] = .5 * (x[0] - x[1]);
          v[2] = x[2] - .5 * (x[0] * x[0] - x[1] * x[1]);
          if(v[2] > zMax) zMax = v[2];
          if(v[2] < zMin) zMin = v[2];
          points.InsertNextPoint(x);
          vectors.InsertNextTuple3(v[0], v[1], v[2]);
          uArray.InsertNextTuple1(v[0]);
          vArray.InsertNextTuple1(v[1]);
          wArray.InsertNextTuple1(v[2]);
          scalars.InsertNextTuple1(v[2]);
        }
      }
    }
    sgrid.SetPoints(points);
    sgrid.GetPointData().SetVectors(vectors);
    sgrid.GetPointData().AddArray(uArray);
    sgrid.GetPointData().AddArray(vArray);
    sgrid.GetPointData().AddArray(wArray);
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
    vtkActor contActor = null;
    if(true) {
      vtkContourFilter contFilter = new vtkContourFilter();
//      contFilter.DebugOn();
      // contFilter.SetInput(source.GetOutput());
      contFilter.SetInput(sgrid);
      contFilter.GenerateValues(9, zMin, zMax);
//      contFilter.SetValue(0, 0);
      if(PRINT_INFO) {
        contFilter.Update();
        vtkDataSet output = contFilter.GetOutput();
        System.out.println("contFilter Output:");
        System.out.print(output);
        System.out.println();
        System.out.println("GetComputeScalars: "
          + contFilter.GetComputeScalars());
        System.out.println("GetComputeNormals: "
          + contFilter.GetComputeNormals());
        System.out.println("GetComputeGradients: "
          + contFilter.GetComputeGradients());
        System.out.println("GetArrayComponent: "
          + contFilter.GetArrayComponent());
        System.out.println("GetAbortExecute: " + contFilter.GetAbortExecute());
        System.out.println("GetErrorCode: " + contFilter.GetErrorCode());
        System.out.println();
        System.out.println(contFilter);
      }
      if(false) {
        // Write the data
        vtkDataSetWriter writer = new vtkDataSetWriter();
        writer.SetInput(contFilter.GetOutput());
        writer.SetFileName("c:/Scratch/VTK/contour.txt");
        writer.Write();
      }

      vtkPolyDataNormals normals = new vtkPolyDataNormals();
      normals.SetInput(contFilter.GetOutput());
      normals.SetFeatureAngle(45);

      // Create a mapper and actor as usual. In this case adjust the
      // scalar range of the mapper to match that of the computed scalars
      vtkPolyDataMapper contMapper = new vtkPolyDataMapper();
      contMapper.SetInput(contFilter.GetOutput());
//      contMapper.ScalarVisibilityOn();
      contMapper.SetScalarRange(zMin, zMax);
//      contMapper.SetScalarModeToUsePointFieldData();
//      contMapper.SetScalarModeToUsePointData();
//      contMapper.SetScalarModeToUseCellData();
//      contMapper.SetScalarRange(sgrid.GetScalarRange());
      // contMapper.ScalarVisibilityOn();
      // contMapper.SetScalarRange(0, 1500);
      if(true) {
        // Color by another array (needs both of these)
        contMapper.SetScalarModeToUsePointFieldData();
        contMapper.ColorByArrayComponent("U", 0);
      }
      if(PRINT_INFO) {
        contMapper.Update();
        System.out.println("contourMapper:");
        System.out.println("GetArrayName: " + contMapper.GetArrayName());
        System.out.println("GetArrayId: " + contMapper.GetArrayId());
        System.out.println("GetArrayComponent: "
          + contMapper.GetArrayComponent());
        System.out.println("GetAbortExecute: " + contMapper.GetAbortExecute());
        System.out.println("GetErrorCode: " + contMapper.GetErrorCode());
        System.out.println("GetColorModeAsString: "
          + contMapper.GetColorModeAsString());
        System.out.println();
        System.out.println(contMapper);
      }

        // Set the actor
      contActor = new vtkActor();
      contActor.SetMapper(contMapper);
      contActor.SetVisibility(doContours?1:0);

      renderer.AddActor(contActor);
    }

    // Make a dataset mapper
    if(true) {
      vtkDataSetMapper sgridMapper = new vtkDataSetMapper();
      sgridMapper.SetInput(sgrid);
      sgridMapper.SetScalarRange(sgrid.GetScalarRange());
      // sgridMapper.SetUseLookupTableScalarRange(1);
      // sgridMapper.SetLookupTable(table1);

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
