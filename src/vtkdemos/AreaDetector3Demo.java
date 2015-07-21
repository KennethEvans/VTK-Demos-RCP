/*
 * Program to 
 * Created on Sep 12, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import vtk.vtkAssembly;
import vtk.vtkCamera;
import vtk.vtkConeSource;
import vtk.vtkLight;
import vtk.vtkLightCollection;
import vtk.vtkPanel;
import vtk.vtkPlaneSource;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderer;
import demolauncher.VTKDemo;

public class AreaDetector3Demo extends VTKDemo
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
  private static final boolean C_PLANE_VISIBLE = true;
  private static final boolean DO_ORTHOGONAL = true;
  private static final boolean DO_AXES = true;
  private static final boolean REVERSE_ROTATION = false;
  private static final int SLIDER_WIDTH = 100;
  private static final double PLANE_SIZE = 6 * unit;
  private static final double AXES_SIZE = 1.0 * PLANE_SIZE;
  public static final double[] RED = {1, 0, 0};
  public static final double[] GREEN = {0, 1, 0};
  public static final double[] BLUE = {0, 0, 1};
  public static final double[] BLACK = {0, 0, 0};
  
  private double beta = 0;
  private double phi = 0;
  private double theta = 22.5;
  private double d = 5 * unit;
  private double coneHeight = 1.5 * d;
  private double[] coneColors = {1, 1, 0};
  private double[] cPlaneColors = {0, 0, 1};
  private double[] dPlaneColors = {0, 1, 0};
  
  private vtkRenderer renderer = null;
  vtkActor cPlane1Actor = null;
  private Axes2 axes2 = null;
  private Axes2 dPlane1Axes = null;
  private Axes2 cPlane1Axes = null;
  private vtkConeSource cone = null;
  vtkAssembly cAssy = null;
  vtkAssembly dAssy1 = null;
  vtkAssembly dAssy2 = null;
  
  private boolean cPlaneVisible = C_PLANE_VISIBLE;
  private boolean doOrthogonal = DO_ORTHOGONAL;
  private boolean doAxes = DO_AXES;
  private boolean reverseRotations = REVERSE_ROTATION;
  
  JCheckBox cVisibleCheck = null;
  JCheckBox orthogonalCheck = null;
  JCheckBox axesCheck = null;
  JCheckBox reverseRotationsCheck = null;

  public AreaDetector3Demo() {
    super();
    setName("Area Detector 3");
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
    renderer.LightFollowCameraOn();
//    renderer.SetAmbient(100, 100, 100);
    renderer.SetTwoSidedLighting(1);

    // Create a cone
    cone = new vtkConeSource();
    cone.SetRadius(d * Math.tan(Math.toRadians(theta)));
    cone.SetResolution(50);
    cone.SetCenter(0, 0, (coneHeight - d) / 2);
    cone.SetHeight(coneHeight);
    cone.SetDirection(0, 0, 1);
    cone.SetCapping(0);
    vtkPolyDataMapper coneMapper = new vtkPolyDataMapper();
    coneMapper.SetInput(cone.GetOutput());
    vtkActor coneActor = new vtkActor();
    coneActor.SetMapper(coneMapper);
    coneActor.GetProperty().SetColor(coneColors);
    coneActor.SetPosition(0, 0, 0);
    coneActor.GetProperty().SetOpacity(CONE_OPACITY);
    renderer.AddActor(coneActor);

    // Create the c1 plane
    vtkPlaneSource cPlane1 = new vtkPlaneSource();
    cPlane1.SetOrigin(0, 0, 0);
    cPlane1.SetPoint1(PLANE_SIZE, 0, 0);
    cPlane1.SetPoint2(0, PLANE_SIZE, 0);
    cPlane1.SetNormal(0, 0, 0);
    cPlane1.SetCenter(0, 0, 0);
    vtkPolyDataMapper cPlane1Mapper = new vtkPolyDataMapper();
    cPlane1Mapper.SetInput(cPlane1.GetOutput());
    cPlane1Actor = new vtkActor();
    cPlane1Actor.SetMapper(cPlane1Mapper);
    cPlane1Actor.GetProperty().SetColor(cPlaneColors);
    cPlane1Actor.GetProperty().SetOpacity(C_PLANE_OPACITY);

    cPlane1Axes = new Axes2(AXES_SIZE, "XL", "YL", "ZL", cPlaneColors,
      cPlaneColors, cPlaneColors);
    
    cAssy = new vtkAssembly();
    cAssy.AddPart(cPlane1Actor);
    cAssy.AddPart(cPlane1Axes.getAxisAssembly());
    cAssy.AddPart(cPlane1Axes.getXLabelActor());
    cAssy.AddPart(cPlane1Axes.getYLabelActor());
    cAssy.AddPart(cPlane1Axes.getZLabelActor());
    cAssy.SetOrigin(0, 0, 0);
    cAssy.SetPosition(0, 0, 0);
    cAssy.SetVisibility(cPlaneVisible? 1 : 0);
    renderer.AddActor(cAssy);

    // Create the d1 plane
    vtkPlaneSource dPlane1 = new vtkPlaneSource();
    dPlane1.SetOrigin(0, 0, 0);
    dPlane1.SetPoint1(PLANE_SIZE, 0, 0);
    dPlane1.SetPoint2(0, PLANE_SIZE, 0);
    dPlane1.SetNormal(0, 0, 0);
    dPlane1.SetCenter(0, 0, 0);
    vtkPolyDataMapper dPlane1Mapper = new vtkPolyDataMapper();
    dPlane1Mapper.SetInput(dPlane1.GetOutput());
    vtkActor dPlane1Actor = new vtkActor();
    dPlane1Actor.SetMapper(dPlane1Mapper);
    dPlane1Actor.GetProperty().SetColor(dPlaneColors);
    dPlane1Actor.GetProperty().SetOpacity(D_PLANE_OPACITY);

    dPlane1Axes = new Axes2(AXES_SIZE, "XT", "YT", "ZT", dPlaneColors,
      dPlaneColors, dPlaneColors);
    
    // Assembly 1 is the detector plane and axes
    dAssy1 = new vtkAssembly();
    dAssy1.AddPart(dPlane1Actor);
    dAssy1.AddPart(dPlane1Axes.getAxisAssembly());
    dAssy1.AddPart(dPlane1Axes.getXLabelActor());
    dAssy1.AddPart(dPlane1Axes.getYLabelActor());
    dAssy1.AddPart(dPlane1Axes.getZLabelActor());
    dAssy1.SetOrigin(0, 0, 0);
    dAssy1.SetPosition(0, 0, 0);

    // Assembly 2 contains assembly 1 and can be rotated independently
    dAssy2 = new vtkAssembly();
    dAssy2.AddPart(dAssy1);
    dAssy2.SetOrigin(0, 0, 0);
    dAssy2.SetPosition(0, 0, 0);
    renderer.AddActor(dAssy2);
    
    doRotations();

    // Set the camera
    vtkCamera camera = new vtkCamera();
    setCameraToDefault(camera);
    renderer.SetActiveCamera(camera);
    renderer.ResetCamera();

    // Add axes
//    axes2 = new Axes2(AXES_SIZE);
//    axes2 = new Axes2(AXES_SIZE, "X", "Y", "Z", coneColors,
//    coneColors, coneColors);
    if(false) {
      axes2 = new Axes2(AXES_SIZE, "X", "Y", "Z", BLACK, BLACK, BLACK);
      axes2.addActor(renderer);
      axes2.setCamera(camera);
      axes2.SetVisibility(doAxes ? 1 : 0);
      renderer.ResetCamera();
    }

    created = true;
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
    PrintStream ps = new PrintStream(baos);
    // FIXME THis isn't writing to ps
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
  
  private void doRotations() {
    // Note the rotation in beta is backwards around yD to have the ellipses be
    // tilted at an angle beta in the detector plane
    dAssy1.SetOrientation(0, 0, 0);
    dAssy2.SetOrientation(0, 0, 0);
    if(!reverseRotations) {
      dAssy1.RotateZ(beta);
      dAssy1.RotateY(phi);
    } else {
      dAssy1.RotateY(phi);
      dAssy1.RotateZ(beta);
    }
  }

  public JPanel createControlPanel() {
    JPanel controlPanel = new JPanel();
    GridLayout gridLayout = new GridLayout(2,1);
    controlPanel.setLayout(gridLayout);
    
    JPanel panel = new JPanel();
    FlowLayout layout = new FlowLayout();
    panel.setLayout(layout);
    controlPanel.add(panel);

    // Reset
    final JButton resetButton = new JButton("Reset");
    panel.add(resetButton);
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
        if(true || DEBUG_LEVEL > 1) {
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
    panel.add(orthogonalCheck);
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

    // C visible
    cVisibleCheck = new JCheckBox();
    cVisibleCheck.setText("C Visible");
    cVisibleCheck.setSelected(cPlaneVisible);
    panel.add(cVisibleCheck);
    cVisibleCheck.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        cPlaneVisible = cVisibleCheck.isSelected();
        cAssy.SetVisibility(cPlaneVisible? 1 : 0);
        renderer.GetRenderWindow().Render();
      }
    });

    // Axes
    axesCheck = new JCheckBox();
    axesCheck.setSelected(doAxes);
    axesCheck.setText("Axes");
    panel.add(axesCheck);
    axesCheck.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doAxes = axesCheck.isSelected();
        if(axes2 != null) {
          axes2.SetVisibility(doAxes ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });
    
    // Reverse rotations
    reverseRotationsCheck = new JCheckBox();
    reverseRotationsCheck.setSelected(reverseRotations);
    reverseRotationsCheck.setText("Reverse Rotations");
    panel.add(reverseRotationsCheck);
    reverseRotationsCheck.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        reverseRotations = reverseRotationsCheck.isSelected();
        doRotations();    
        renderer.GetRenderWindow().Render();
      }
    });
    
    panel = new JPanel();
    layout = new FlowLayout();
    panel.setLayout(layout);
    controlPanel.add(panel);

    // Theta
    JLabel label = new JLabel("Theta:");
    panel.add(label);
    int initialValue = (int)Math.round(100 * (theta - THETA_MIN)
      / (THETA_MAX - THETA_MIN));
    JSlider slider = new JSlider(0, 100, initialValue);
    Dimension size = slider.getPreferredSize();
    size.width = SLIDER_WIDTH;
    slider.setPreferredSize(size);
    panel.add(slider);
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
    panel.add(label);
    initialValue = (int)Math.round(100 * (beta - BETA_MIN)
      / (BETA_MAX - BETA_MIN));
    slider = new JSlider(0, 100, initialValue);
    size = slider.getPreferredSize();
    size.width = SLIDER_WIDTH;
    slider.setPreferredSize(size);
    panel.add(slider);
    slider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ev) {
        JSlider slider = (JSlider)ev.getSource();
        beta = BETA_MIN + slider.getValue() * (BETA_MAX - BETA_MIN) / 100;
        doRotations();    
        renderer.GetRenderWindow().Render();
      }
    });

    // Phi
    label = new JLabel("Phi:");
    panel.add(label);
    initialValue = (int)Math.round(100 * (phi - PHI_MIN) / (PHI_MAX - PHI_MIN));
    slider = new JSlider(0, 100, initialValue);
    size = slider.getPreferredSize();
    size.width = SLIDER_WIDTH;
    slider.setPreferredSize(size);
    panel.add(slider);
    slider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ev) {
        JSlider slider = (JSlider)ev.getSource();
        phi = PHI_MIN + slider.getValue() * (PHI_MAX - PHI_MIN) / 100;
        doRotations();    
        renderer.GetRenderWindow().Render();
      }
    });

    return controlPanel;
  }

}
