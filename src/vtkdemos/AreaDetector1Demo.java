/*
 * Program to 
 * Created on Sep 12, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utils.Axes2;
import vtk.vtkActor;
import vtk.vtkCamera;
import vtk.vtkConeSource;
import vtk.vtkLight;
import vtk.vtkLightCollection;
import vtk.vtkPanel;
import vtk.vtkPlaneSource;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderer;
import demolauncher.VTKDemo;

public class AreaDetector1Demo extends VTKDemo
{
  private static final int DEBUG_LEVEL = 0;
  private static final double unit = 1.0;
  private static final double CONE_OPACITY = 1;
  private static final double C_PLANE_OPACITY = .25;
  private static final double D_PLANE_OPACITY = 1;
  private static final double THETA_MAX = 30;
  private static final double BETA_MAX = 90;
  private static final double PHI_MAX = 45;
  private static final double THETA_MIN = 0;
  private static final double BETA_MIN = -BETA_MAX;
  private static final double PHI_MIN = -PHI_MAX;
  private static final boolean DO_ORTHOGONAL = false;
  private static final boolean DO_AXES = true;
  private static final int SLIDER_WIDTH = 75;
  
  private double beta = 30;
  private double phi = 10;
  private double theta = 22.5;
  private double d = 5 * unit;
  private double planeSize = 6 * unit;
  private double coneHeight = 1.5 * d;
  private double[] cPlaneColors = {0, 0, 1};
  private double[] dPlaneColors = {0, 1, 0};
  
  private vtkRenderer renderer = null;
  private Axes2 axes2 = null;
  private vtkConeSource cone = null;
  vtkPlaneSource cPlane = null;
  vtkPlaneSource dPlane = null;
  
  private boolean doOrthogonal = DO_ORTHOGONAL;
  private boolean doAxes = DO_AXES;
  
  JCheckBox orthogonalCheck = null;
  JCheckBox axesCheck = null;

  public AreaDetector1Demo() {
    super();
    setName("Area Detector 1");
  }

  /* (non-Javadoc)
   * @see demolauncher.VTKDemo#getInfo()
   */
  public String getInfo() {
    String info = "This demo illustrates the geometry of\n"
      + "an area detector rotated by an angle phi and\n"
      + "tilted by an angle beta.";
    return info;
  }
  
  /* (non-Javadoc)
   * @see demolauncher.VTKDemo#createPanel()
   */
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

    // Create a cone
    cone = new vtkConeSource();
    cone.SetRadius(d * Math.tan(Math.toRadians(theta)));
    cone.SetResolution(50);
    cone.SetCenter(0, 0, coneHeight / 2);
    cone.SetHeight(coneHeight);
    cone.SetDirection(0, 0, -1);
    vtkPolyDataMapper coneMapper = new vtkPolyDataMapper();
    coneMapper.SetInput(cone.GetOutput());
    vtkActor coneActor = new vtkActor();
    coneActor.SetMapper(coneMapper);
    coneActor.GetProperty().SetColor(1, 1, 0); // color yellow
    coneActor.SetPosition(0, 0, 0);
    coneActor.GetProperty().SetOpacity(CONE_OPACITY);
    renderer.AddActor(coneActor);

    // Create the c plane
    cPlane = new vtkPlaneSource();
    setupCPlane();
    vtkPolyDataMapper cPlaneMapper = new vtkPolyDataMapper();
    cPlaneMapper.SetInput(cPlane.GetOutput());
    vtkActor cPlaneActor = new vtkActor();
    cPlaneActor.SetMapper(cPlaneMapper);
    cPlaneActor.GetProperty().SetColor(cPlaneColors);
    cPlaneActor.GetProperty().SetOpacity(C_PLANE_OPACITY);
    renderer.AddActor(cPlaneActor);

    // Create the d plane
    dPlane = new vtkPlaneSource();
    setupDPlane();
    vtkPolyDataMapper dPlaneMapper = new vtkPolyDataMapper();
    dPlaneMapper.SetInput(dPlane.GetOutput());
    vtkActor dPlaneActor = new vtkActor();
    dPlaneActor.SetMapper(dPlaneMapper);
    dPlaneActor.GetProperty().SetColor(dPlaneColors);
    dPlaneActor.GetProperty().SetOpacity(D_PLANE_OPACITY);
    renderer.AddActor(dPlaneActor);

    // Set the camera
    vtkCamera camera = new vtkCamera();
    setCameraToDefault(camera);
    renderer.SetActiveCamera(camera);
    renderer.ResetCamera();

    // Add axes
    if(true) {
      axes2 = new Axes2(.5 * d);
      axes2.addActor(renderer);
      axes2.setCamera(camera);
      axes2.SetVisibility(doAxes?1:0);
      renderer.ResetCamera();
    }

    created = true;
  }
  
  private void setupCPlane() {
    cPlane.SetOrigin(0, 0, 0);
    cPlane.SetPoint1(planeSize, 0, 0);
    cPlane.SetPoint2(0, planeSize, 0);
    cPlane.SetNormal(0, 0, 0);
    cPlane.SetCenter(0, 0, d);
    // System.out.println(getPlaneInfo(cPlane));
  }
  
  private void setupDPlane() {
    double cosBeta = planeSize * Math.cos(Math.toRadians(beta));
    double sinBeta = planeSize * Math.sin(Math.toRadians(beta));
    double cosPhi = planeSize * Math.cos(Math.toRadians(phi));
    double sinPhi = planeSize * Math.sin(Math.toRadians(phi));
    dPlane.SetOrigin(0, 0, 0);
    dPlane.SetPoint1(cosBeta, sinBeta, 0);
    dPlane.SetPoint2(-sinBeta, cosBeta, 0);
    dPlane.SetNormal(sinPhi, 0, cosPhi);
    dPlane.SetCenter(0, 0, d);
//    System.out.println(getPlaneInfo(dPlane));
  }

  public void setCameraToDefault(vtkCamera camera) {
    if(doOrthogonal) {
      camera.ParallelProjectionOn();
    } else {
      camera.ParallelProjectionOff();
    }
    camera.SetClippingRange(.1, 1000);
    camera.SetFocalPoint(0, 0, 0);
    camera.SetPosition(25, 0, 0);
    camera.SetViewUp(0, 1, 0);
    camera.ComputeViewPlaneNormal();
  }
  
  public String getPlaneInfo(vtkPlaneSource plane) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // FIXME This isn't writing to ps
    PrintStream ps = new PrintStream(baos);
    PrintStream out = System.out;
    out.println("Origin: " + plane.GetOrigin()[0] + ","
      + plane.GetOrigin()[1]);
    out.println("Point1: " + plane.GetPoint1()[0] + ","
      + plane.GetPoint1()[1]);
    out.println("Point2: " + plane.GetPoint2()[0] + ","
      + plane.GetPoint2()[1]);
    out.println("Normal: " + plane.GetNormal()[0] + ","
      + plane.GetNormal()[1] + "," + plane.GetNormal()[2]);
    out.println("Center: " + plane.GetCenter()[0] + ","
      + plane.GetCenter()[1]);
    ps.close();
    return baos.toString();
  }

  public JPanel createControlPanel() {
    JPanel controlPanel = new JPanel();
    FlowLayout layout = new FlowLayout();
    controlPanel.setLayout(layout);

    // Reset
    final JButton resetButton = new JButton("Reset");
    controlPanel.add(resetButton);
    resetButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        vtkCamera camera = renderer.GetActiveCamera();
        // Reset the camera
        setCameraToDefault(camera);
        // Have to reset the light (shouldn't have to)
        vtkLightCollection lights = renderer.GetLights();
        lights.InitTraversal();
        vtkLight light = lights.GetNextItem();
        light.SetPosition(camera.GetPosition());
        light.SetFocalPoint(camera.GetFocalPoint());
        // Reset the camera and render
        renderer.ResetCamera();
        renderer.GetRenderWindow().Render();
        if(DEBUG_LEVEL > 1) {
          System.out.println("Renderer:");
          System.out.println(renderer);
          System.out.println("Camera:");
          System.out.println(camera);
        }
      }
    });
    
    // Orthogonal
    orthogonalCheck = new JCheckBox();
    orthogonalCheck.setText("Orthogonal");
    orthogonalCheck.setSelected(doOrthogonal);
    controlPanel.add(orthogonalCheck);
    orthogonalCheck.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doOrthogonal = orthogonalCheck.isSelected();
        vtkCamera camera = renderer.GetActiveCamera();
        if(doOrthogonal) {
          camera.ParallelProjectionOn();
        } else {
          camera.ParallelProjectionOff();
        }
        renderer.GetRenderWindow().Render();
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
    
    // Theta
    JLabel label = new JLabel("Theta:");
    controlPanel.add(label);
    int initialValue = (int)Math.round(100 * (theta - THETA_MIN)
      / (THETA_MAX - THETA_MIN));
    JSlider slider = new JSlider(0, 100, initialValue);
    Dimension size = slider.getPreferredSize();
    size.width = SLIDER_WIDTH;
    slider.setPreferredSize(size);
    controlPanel.add(slider);
    slider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ev) {
        JSlider slider = (JSlider)ev.getSource();
        theta = THETA_MIN + slider.getValue() * (THETA_MAX - THETA_MIN) / 100;
        cone.SetRadius(d * Math.tan(Math.toRadians(theta)));
        renderer.GetRenderWindow().Render();
      }
    });

    // Beta
    label = new JLabel("Beta:");
    controlPanel.add(label);
    initialValue = (int)Math.round(100 * (beta - BETA_MIN)
      / (BETA_MAX - BETA_MIN));
    slider = new JSlider(0, 100, initialValue);
    size = slider.getPreferredSize();
    size.width = SLIDER_WIDTH;
    slider.setPreferredSize(size);
    controlPanel.add(slider);
    slider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ev) {
        JSlider slider = (JSlider)ev.getSource();
        beta = BETA_MIN + slider.getValue() * (BETA_MAX - BETA_MIN) / 100;
        setupDPlane();
        renderer.GetRenderWindow().Render();
      }
    });

    // Phi
    label = new JLabel("Phi:");
    controlPanel.add(label);
    initialValue = (int)Math.round(100 * (phi - PHI_MIN) / (PHI_MAX - PHI_MIN));
    slider = new JSlider(0, 100, initialValue);
    size = slider.getPreferredSize();
    size.width = SLIDER_WIDTH;
    slider.setPreferredSize(size);
    controlPanel.add(slider);
    slider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ev) {
        JSlider slider = (JSlider)ev.getSource();
        phi = PHI_MIN + slider.getValue() * (PHI_MAX - PHI_MIN) / 100;
        setupDPlane();
        renderer.GetRenderWindow().Render();
      }
    });

    return controlPanel;
  }

}
