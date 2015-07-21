/*
 * Program to 
 * Created on Sep 20, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import utils.Axes;
import utils.Utils;
import vtk.vtkActor;
import vtk.vtkCamera;
import vtk.vtkCubeSource;
import vtk.vtkPanel;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderer;
import vtk.vtkVRMLExporter;
import demolauncher.VTKDemo;

public class CubesDemo extends VTKDemo
{
  private static final int DEBUG_LEVEL = 0;
  private String defaultPath = "VRML"; 

  private vtkRenderer renderer = null;

  private static final boolean DO_AXES = true;
  private boolean doAxes = DO_AXES;
  private Axes axes = null;

  public CubesDemo() {
    super();
    setName("Cubes");
  }

  public String getInfo() {
    String info = "This demo illustrates two cubes.\n";
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
    
    // Create cube1
    vtkCubeSource cube1 = new vtkCubeSource();
    cube1.SetXLength(2);
    cube1.SetYLength(2);
    cube1.SetZLength(2);
    cube1.SetCenter(0, 0, 0);
    vtkPolyDataMapper cube1Mapper = new vtkPolyDataMapper();
    cube1Mapper.SetInput(cube1.GetOutput());
    vtkActor cube1Actor = new vtkActor();
    cube1Actor.SetMapper(cube1Mapper);
    cube1Actor.GetProperty().SetColor(1, 0, 0);
    cube1Actor.SetPosition(0, 0, 0);
    renderer.AddActor(cube1Actor);
    
    // Create cube2
    vtkCubeSource cube2 = new vtkCubeSource();
    cube2.SetXLength(2);
    cube2.SetYLength(2);
    cube2.SetZLength(2);
    cube2.SetCenter(0, 0, 0);
    vtkPolyDataMapper cube2Mapper = new vtkPolyDataMapper();
    cube2Mapper.SetInput(cube2.GetOutput());
    vtkActor cube2Actor = new vtkActor();
    cube2Actor.SetMapper(cube2Mapper);
    cube2Actor.GetProperty().SetColor(0, 1, 0);
    cube2Actor.SetPosition(3, 0, 0);
    cube2Actor.SetOrientation(0, 45, 0);
    renderer.AddActor(cube2Actor);
    
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
    axes = new Axes(3);
    axes.createActors();
    axes.addActor(renderer);
    axes.setCamera(camera);
    axes.SetVisibility(doAxes ? 1 : 0);
    renderer.ResetCamera();


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
        if(camera != null) {
          camera.SetViewUp(0, 1, 0);
          camera.SetPosition(0, 0, 1);
          camera.SetFocalPoint(0, 0, 0);
          camera.SetViewAngle(30);
          camera.SetClippingRange(.1, 1000);
          camera.ComputeViewPlaneNormal();
        }
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

    // Save VRML
    if(true) {
      final JButton saveButton = new JButton("Save VRML");
      controlPanel.add(saveButton);
      saveButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          // Get the file name using a JChooser
          File file = Utils.getOpenFile(defaultPath);
          if(file != null) {
            defaultPath = file.getParentFile().getPath();
            if(file.exists()) {
              int selection = JOptionPane.showConfirmDialog(getPanel(),
                "File already exists:\n" + file.getAbsolutePath()
                + "\nOK to replace?", "Warning",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
              if(selection != JOptionPane.OK_OPTION) return;
            }
            vtkVRMLExporter exporter = new vtkVRMLExporter();
            exporter.SetInput(renderer.GetRenderWindow());
            exporter.SetFileName(file.getAbsolutePath());
            exporter.Write();
          }
        }
      });
    }
    
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
  
}
