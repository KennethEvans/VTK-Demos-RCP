/*
 * Program to 
 * Created on Sep 18, 2006
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
import javax.swing.JPanel;

import utils.Axes;
import utils.Utils;
import vtk.vtkActor;
import vtk.vtkActorCollection;
import vtk.vtkCamera;
import vtk.vtkPanel;
import vtk.vtkRenderWindow;
import vtk.vtkRenderer;
import vtk.vtkVRMLImporter;
import demolauncher.VTKDemo;

public class VRMLDemo extends VTKDemo
{
  private static final int DEBUG_LEVEL = 1;
  private String defaultPath = "VRML"; 

  private vtkRenderer renderer = null;
  private vtkVRMLImporter importer = null;

  private static final boolean DO_AXES = true;
  private boolean doAxes = DO_AXES;
  private Axes axes = null;

  public VRMLDemo() {
    super();
    setName("VRML");
  }

  public String getInfo() {
    String info = "This demo illustrates reading a VRML file.\n";
    return info;
  }

  /**
   * Hack to enable substituting vtkPanel's ren and rw members.
   */
  private static class CustomVtkPanel extends vtkPanel
  {
    private static final long serialVersionUID = 1L;

    public CustomVtkPanel(vtkRenderer ren, vtkRenderWindow rw) {
      super();
      this.ren = ren;
      this.rw = rw;
      rw.AddRenderer(ren);
      super.setSize(200, 200);
    }
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

    // Get the file name using a JChooser
    File file = Utils.getOpenFile(defaultPath);
    if(file == null) {
      Utils.errMsg("No file selected\nUse File|Reset Current to try again");
      return;
    }
    
    // Create the VRML importer
    importer = new vtkVRMLImporter();
//    importer.SetDebug('\1');
    importer.SetFileName(file.getAbsolutePath());
    importer.Read();
    if(DEBUG_LEVEL > 0) {
      System.out.println("importer");
      System.out.println(importer);
    }

    // Make a vtkPanel and add it to the JPanel
    vtkPanel renWin = null;
    // Use the renderer created by the importer
    renWin = new CustomVtkPanel(importer.GetRenderer(), importer
      .GetRenderWindow());
    renderer = renWin.GetRenderer();
    panel.add(renWin, BorderLayout.CENTER);

    // Get the renderer
    vtkRenderer renderer = renWin.GetRenderer();
    renderer.SetBackground(1, 1, 1); // background color white
//    renderer.SetBackground(.5, .5, .5); // background color white
    if(DEBUG_LEVEL > 0) {
      System.out.println("renderer");
      System.out.println(renderer);
      vtkActorCollection actors = renderer.GetActors();
      System.out.println("actors");
      System.out.println(actors);
      int nItems = actors.GetNumberOfItems();
      System.out.println("items [" + nItems + "]:");
      actors.InitTraversal();
      for(int i = 0; i < nItems; i++) {
        vtkActor actor =  actors.GetNextActor();
        System.out.println("Item " + i + ":");
        if(actor == null) {
          System.out.println("Null");
        } else {
          System.out.println(actor.Print());
        }
      }
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
    
    // New
    if(false) {
      final JButton newButton = new JButton("New");
      controlPanel.add(newButton);
      newButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          // Get the file name using a JChooser
          File file = Utils.getOpenFile(defaultPath);
          if(file != null && importer != null) {
            defaultPath = file.getParentFile().getPath();
            importer.SetFileName(file.getAbsolutePath());
            importer.Read();
            renderer.GetRenderWindow().Render();
          }
        }
      });
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
