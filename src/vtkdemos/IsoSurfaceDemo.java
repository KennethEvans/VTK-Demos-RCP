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
import utils.VTKUtils;
import vtk.vtkActor;
import vtk.vtkAssignAttribute;
import vtk.vtkCamera;
import vtk.vtkContourFilter;
import vtk.vtkDataSetMapper;
import vtk.vtkFloatArray;
import vtk.vtkHedgeHog;
import vtk.vtkPanel;
import vtk.vtkPoints;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderer;
import vtk.vtkStructuredGrid;
import demolauncher.VTKDemo;

public class IsoSurfaceDemo extends VTKDemo
{
  private static final int DEBUG_LEVEL = 0;
  private static final boolean DO_HEDGEHOG = false;
  private static final boolean DO_CONTOURS0 = false;
  private static final boolean DO_CONTOURS1 = false;
  private static final boolean DO_CONTOURS2 = true;
  private static final boolean DO_DATASET = false;
  private static final boolean DO_AXES = true;
  
  private vtkRenderer renderer = null;
  private vtkActor hedgehogActor = null;
  private vtkActor contActor0 = null;
  private vtkActor contActor1 = null;
  private vtkActor contActor2 = null;
  private vtkActor datasetActor =null;
  private Axes2 axes2 = null;
  
  private boolean doHedgehog = DO_HEDGEHOG;
  private boolean doContours0 = DO_CONTOURS0;
  private boolean doContours1 = DO_CONTOURS1;
  private boolean doContours2 = DO_CONTOURS2;
  private boolean doDataset = DO_DATASET;
  private boolean doAxes = DO_AXES;
  
  JCheckBox hedgehogCheck = null;
  JCheckBox contoursCheck0 = null;
  JCheckBox contoursCheck1 = null;
  JCheckBox contoursCheck2 = null;
  JCheckBox datasetCheck = null;
  JCheckBox axesCheck = null;

  public IsoSurfaceDemo() {
    super();
    setName("Iso Surfaces");
  }

  public String getInfo() {
    String info = "This demo illustrates iso surfaces of\n"
      + "a mapping, x,y,z -> u,v,w:\n"  
      + "   x = u + v\n"
      + "   y = u - v\n"
      + "   z = 2 * u * v + w\n";
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

    vtkPoints points = new vtkPoints();

    double deltaX = 2.0 / (dims[0] - 1);
    double deltaY = 2.0 / (dims[1] - 1);
    double deltaZ = 2.0 / (dims[2] - 1);
    double uMax = -Double.MAX_VALUE;
    double uMin = Double.MAX_VALUE;
    double vMax = -Double.MAX_VALUE;
    double vMin = Double.MAX_VALUE;
    double wMax = -Double.MAX_VALUE;
    double wMin = Double.MAX_VALUE;
    for(int k = 0; k < dims[2]; k++) {
      x[2] = -1.0 + k * deltaZ;
      for(int j = 0; j < dims[1]; j++) {
        x[1] = -1.0 + j * deltaY;
        for(int i = 0; i < dims[0]; i++) {
          x[0] = -1.0 + i * deltaX;
          v[0] = .5 * (x[0] + x[1]);
          v[1] = .5 * (x[0] - x[1]);
          v[2] = x[2] - .5 * (x[0] * x[0] - x[1] * x[1]);
          if(v[0] > uMax) uMax = v[0];
          if(v[0] < uMin) uMin = v[0];
          if(v[1] > vMax) vMax = v[1];
          if(v[1] < vMin) vMin = v[1];
          if(v[2] > wMax) wMax = v[2];
          if(v[2] < wMin) wMin = v[2];
          points.InsertNextPoint(x);
          vectors.InsertNextTuple3(v[0], v[1], v[2]);
          uArray.InsertNextTuple1(v[0]);
          vArray.InsertNextTuple1(v[1]);
          wArray.InsertNextTuple1(v[2]);
        }
      }
    }
    sgrid.SetPoints(points);
    sgrid.GetPointData().SetVectors(vectors);
    sgrid.GetPointData().AddArray(uArray);
    sgrid.GetPointData().AddArray(vArray);
    sgrid.GetPointData().AddArray(wArray);
    sgrid.GetPointData().SetActiveScalars("W");
    if(DEBUG_LEVEL > 0) {
      System.out.println("sgrid:");
      System.out.print(sgrid);
    }

    // Create a hedgehog
    if(true) {
      vtkHedgeHog hedgehog = new vtkHedgeHog();
      hedgehog.SetInput(sgrid);
      hedgehog.SetScaleFactor(0.1);
      if(DEBUG_LEVEL > 0) {
        hedgehog.Update();
        System.out.println("hedgehog output:");
        System.out.print(hedgehog.GetOutput());
      }

      // Create a hedgehog mapper
      vtkPolyDataMapper hedgehogMapper = new vtkPolyDataMapper();
      hedgehogMapper.SetInput(hedgehog.GetOutput());
      hedgehogMapper.SetScalarRange(sgrid.GetScalarRange());

      // Create a hedgehog actor
      hedgehogActor = new vtkActor();
      hedgehogActor.SetMapper(hedgehogMapper);
      hedgehogActor.GetProperty().SetColor(0, 0, 0);  // ???
      hedgehogActor.SetVisibility(doHedgehog?1:0);
     
      renderer.AddActor(hedgehogActor);
    }

    // Make a u contour filter
    if(true) {
      // Set the active scalar attribute
      vtkAssignAttribute assign = new vtkAssignAttribute();
      assign.SetInput(sgrid);
      assign.Assign("U", "SCALARS", "POINT_DATA");
      if(DEBUG_LEVEL > 0) {
        assign.Update();
        VTKUtils.printOutput("assign", assign.GetOutput());
      }
      
      // Make a contour filter
      vtkContourFilter contFilter = new vtkContourFilter();
      contFilter.SetInput(assign.GetOutput());
      contFilter.GenerateValues(9, wMin, wMax);

      // Create a mapper 
      vtkPolyDataMapper contMapper = new vtkPolyDataMapper();
      contMapper.SetInput(contFilter.GetOutput());
      contMapper.SetScalarRange(uMin, uMax);
      // Color by another array (needs both of these)
      contMapper.SetScalarModeToUsePointFieldData();
      contMapper.ColorByArrayComponent("U", 0);

        // Set the actor
      contActor0 = new vtkActor();
      contActor0.SetMapper(contMapper);
      contActor0.SetVisibility(doContours0 ? 1 : 0);
      renderer.AddActor(contActor0);
    }

    // Make a v contour filter
    if(true) {
      // Set the active scalar attribute
      vtkAssignAttribute assign = new vtkAssignAttribute();
      assign.SetInput(sgrid);
      assign.Assign("V", "SCALARS", "POINT_DATA");
      
      // Make a contour filter
      vtkContourFilter contFilter = new vtkContourFilter();
      contFilter.SetInput(assign.GetOutput());
      contFilter.GenerateValues(9, wMin, wMax);

      // Create a mapper 
      vtkPolyDataMapper contMapper = new vtkPolyDataMapper();
      contMapper.SetInput(contFilter.GetOutput());
      contMapper.SetScalarRange(vMin, vMax);
      // Color by another array (needs both of these)
      contMapper.SetScalarModeToUsePointFieldData();
      contMapper.ColorByArrayComponent("V", 0);

        // Set the actor
      contActor1 = new vtkActor();
      contActor1.SetMapper(contMapper);
      contActor1.SetVisibility(doContours1 ? 1 : 0);
      renderer.AddActor(contActor1);
    }

    // Make a w contour filter
    if(true) {
      // Set the active scalar attribute
      vtkAssignAttribute assign = new vtkAssignAttribute();
      assign.SetInput(sgrid);
      assign.Assign("W", "SCALARS", "POINT_DATA");

      // Make a contour filter
      vtkContourFilter contFilter = new vtkContourFilter();
      contFilter.SetInput(assign.GetOutput());
      contFilter.GenerateValues(9, wMin, wMax);

      // Create a mapper 
      vtkPolyDataMapper contMapper = new vtkPolyDataMapper();
      contMapper.SetInput(contFilter.GetOutput());
      contMapper.SetScalarRange(wMin, wMax);
      // Color by another array (needs both of these)
      contMapper.SetScalarModeToUsePointFieldData();
      contMapper.ColorByArrayComponent("W", 0);

        // Set the actor
      contActor2 = new vtkActor();
      contActor2.SetMapper(contMapper);
      contActor2.SetVisibility(doContours2 ? 1 : 0);
      renderer.AddActor(contActor2);
    }

    // Make a dataset mapper
    if(true) {
      vtkDataSetMapper sgridMapper = new vtkDataSetMapper();
      sgridMapper.SetInput(sgrid);
      sgridMapper.SetScalarRange(wMin, wMax);
//      Color by another array (needs both of these)
//      sgridMapper.SetScalarModeToUsePointFieldData();
//      sgridMapper.ColorByArrayComponent("W", 0);
//      sgridMapper.SetUseLookupTableScalarRange(1);
//      sgridMapper.SetLookupTable(table1);

      // Make an actor
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

    // Contours0
    contoursCheck0 = new JCheckBox();
    contoursCheck0.setText("U Contours");
    contoursCheck0.setSelected(doContours0);
    controlPanel.add(contoursCheck0);
    contoursCheck0.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doContours0 = contoursCheck0.isSelected();
        if(contActor0 != null) {
          contActor0.SetVisibility(doContours0 ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });

    // Contours1
    contoursCheck1 = new JCheckBox();
    contoursCheck1.setText("V Contours");
    contoursCheck1.setSelected(doContours1);
    controlPanel.add(contoursCheck1);
    contoursCheck1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doContours1 = contoursCheck1.isSelected();
        if(contActor1 != null) {
          contActor1.SetVisibility(doContours1 ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });

    // Contours
    contoursCheck2 = new JCheckBox();
    contoursCheck2.setText("W Contours");
    contoursCheck2.setSelected(doContours2);
    controlPanel.add(contoursCheck2);
    contoursCheck2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doContours2 = contoursCheck2.isSelected();
        if(contActor2 != null) {
          contActor2.SetVisibility(doContours2 ? 1 : 0);
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
