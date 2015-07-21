/*
 * Program to 
 * Created on Aug 15, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.Axes;
import vtk.vtkCamera;
import vtk.vtkPanel;
import vtk.vtkRenderer;
import demolauncher.VTKDemo;

public class Templet extends VTKDemo
{
  private static final int DEBUG_LEVEL = 1;

  private vtkRenderer renderer = null;

  private static final boolean DO_AXES = true;
  private boolean doAxes = DO_AXES;
  private Axes axes = null;

  public Templet() {
    super();
    setName("Templet");
  }

  public String getInfo() {
    String info = "This demo illustrates xxx.\n";
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

    // Add actors
    renderer.AddActor(null);

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
