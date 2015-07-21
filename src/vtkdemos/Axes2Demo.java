/*
 * Program to 
 * Created on Aug 15, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import vtk.vtkActor;
import vtk.vtkAssembly;
import vtk.vtkCamera;
import vtk.vtkConeSource;
import vtk.vtkCylinderSource;
import vtk.vtkFollower;
import vtk.vtkPanel;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderer;
import vtk.vtkVectorText;
import demolauncher.VTKDemo;

public class Axes2Demo extends VTKDemo
{
  private static final int AXES_RESOLUTION = 10;
  private static final double AXES_LENGTH = 1.;
  private static final double AXES_RADIUS = .005;
  private static final double[] X_AXIS_COLOR = {1, 0, 0};
  private static final double[] Y_AXIS_COLOR = {0, 1, 0};
  private static final double[] Z_AXIS_COLOR = {0, 0, 1};
  private static final boolean USE_ARROWS = true;
  private static final boolean USE_LABELS = true;
  private static String X_AXIS_LABEL = "X";
  private static String Y_AXIS_LABEL = "Y";
  private static String Z_AXIS_LABEL = "Z";
  private static final double LABEL_SCALE = .05;
  private static final double LABEL_OFFSET = .1 * AXES_LENGTH;

  public Axes2Demo() {
    super();
    setName("Axes 2");
  }

  public String getInfo() {
    String info =
      "This demo illustrates using elaborate axes.\n";
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

    // Get the renderer
    vtkRenderer renderer = renWin.GetRenderer();
    renderer.SetBackground(1, 1, 1); // background color white

    // Create x axis
    vtkCylinderSource xAxis = new vtkCylinderSource();
    xAxis.SetRadius(AXES_RADIUS);
    xAxis.SetHeight(AXES_LENGTH);
    xAxis.SetResolution(AXES_RESOLUTION);
    vtkPolyDataMapper xAxisMapper = new vtkPolyDataMapper();
    xAxisMapper.SetInput(xAxis.GetOutput());
    vtkActor xAxisActor = new vtkActor();
    xAxisActor.SetMapper(xAxisMapper);
    xAxisActor.GetProperty().SetColor(X_AXIS_COLOR);
    xAxisActor.RotateZ(90.);
    xAxisActor.SetPosition(AXES_LENGTH / 2., 0, 0);

    // Create y axis
    vtkCylinderSource yAxis = new vtkCylinderSource();
    yAxis.SetRadius(AXES_RADIUS);
    yAxis.SetHeight(AXES_LENGTH);
    yAxis.SetResolution(AXES_RESOLUTION);
    vtkPolyDataMapper yAxisMapper = new vtkPolyDataMapper();
    yAxisMapper.SetInput(yAxis.GetOutput());
    vtkActor yAxisActor = new vtkActor();
    yAxisActor.SetMapper(yAxisMapper);
    yAxisActor.GetProperty().SetColor(Y_AXIS_COLOR);
    yAxisActor.SetPosition(0, AXES_LENGTH / 2., 0);

    // Create z axis
    vtkCylinderSource zAxis = new vtkCylinderSource();
    zAxis.SetRadius(AXES_RADIUS);
    zAxis.SetHeight(AXES_LENGTH);
    zAxis.SetResolution(AXES_RESOLUTION);
    vtkPolyDataMapper zAxisMapper = new vtkPolyDataMapper();
    zAxisMapper.SetInput(zAxis.GetOutput());
    vtkActor zAxisActor = new vtkActor();
    zAxisActor.SetMapper(zAxisMapper);
    zAxisActor.GetProperty().SetColor(Z_AXIS_COLOR);
    zAxisActor.RotateX(90.);
    zAxisActor.SetPosition(0, 0, AXES_LENGTH / 2.);
    
    // Create arrows
    vtkActor xArrowActor = null;
    vtkActor yArrowActor = null;
    vtkActor zArrowActor = null;
    if(USE_ARROWS) {
      // Create xArrow
      vtkConeSource xArrow = new vtkConeSource();
      xArrow.SetRadius(4. * AXES_RADIUS);
      xArrow.SetResolution(AXES_RESOLUTION);
      xArrow.SetHeight(10. * AXES_RADIUS);
      xArrow.SetDirection(1, 0, 0);
      vtkPolyDataMapper xArrowMapper = new vtkPolyDataMapper();
      xArrowMapper.SetInput(xArrow.GetOutput());
      xArrowActor = new vtkActor();
      xArrowActor.SetMapper(xArrowMapper);
      xArrowActor.GetProperty().SetColor(X_AXIS_COLOR);
      xArrowActor.SetPosition(AXES_LENGTH, 0, 0);

      // Create yArrow
      vtkConeSource yArrow = new vtkConeSource();
      yArrow.SetRadius(4. * AXES_RADIUS);
      yArrow.SetResolution(AXES_RESOLUTION);
      yArrow.SetHeight(10. * AXES_RADIUS);
      yArrow.SetDirection(0, 1, 0);
      vtkPolyDataMapper yArrowMapper = new vtkPolyDataMapper();
      yArrowMapper.SetInput(yArrow.GetOutput());
      yArrowActor = new vtkActor();
      yArrowActor.SetMapper(yArrowMapper);
      yArrowActor.GetProperty().SetColor(Y_AXIS_COLOR);
      yArrowActor.SetPosition(0, AXES_LENGTH, 0);

      // Create zArrow
      vtkConeSource zArrow = new vtkConeSource();
      zArrow.SetRadius(4. * AXES_RADIUS);
      zArrow.SetResolution(AXES_RESOLUTION);
      zArrow.SetHeight(10. * AXES_RADIUS);
      zArrow.SetDirection(0, 0, 1);
      vtkPolyDataMapper zArrowMapper = new vtkPolyDataMapper();
      zArrowMapper.SetInput(zArrow.GetOutput());
      zArrowActor = new vtkActor();
      zArrowActor.SetMapper(zArrowMapper);
      zArrowActor.GetProperty().SetColor(Z_AXIS_COLOR);
      zArrowActor.SetPosition(0, 0, AXES_LENGTH);
    }
    
    // Create labels
    vtkFollower xLabelActor = null;
    vtkFollower yLabelActor = null;
    vtkFollower zLabelActor = null;
    if(USE_LABELS) {
      // Create xLabel
      vtkVectorText xLabelText = new vtkVectorText();
      xLabelText.SetText(X_AXIS_LABEL);

      vtkPolyDataMapper xLabelTextMapper = new vtkPolyDataMapper();
      xLabelTextMapper.SetInput(xLabelText.GetOutput());

      xLabelActor = new vtkFollower();
      xLabelActor.SetMapper(xLabelTextMapper);
      xLabelActor.SetScale(LABEL_SCALE);
      xLabelActor.GetProperty().SetColor(X_AXIS_COLOR);
      xLabelActor.SetPosition(AXES_LENGTH + LABEL_OFFSET, 0, 0);

      // Create yLabel
      vtkVectorText yLabelText = new vtkVectorText();
      yLabelText.SetText(Y_AXIS_LABEL);

      vtkPolyDataMapper yLabelTextMapper = new vtkPolyDataMapper();
      yLabelTextMapper.SetInput(yLabelText.GetOutput());

      yLabelActor = new vtkFollower();
      yLabelActor.SetMapper(yLabelTextMapper);
      yLabelActor.SetScale(LABEL_SCALE);
      yLabelActor.GetProperty().SetColor(Y_AXIS_COLOR);
      yLabelActor.SetPosition(0, AXES_LENGTH + LABEL_OFFSET, 0);

      // Create zLabel
      vtkVectorText zLabelText = new vtkVectorText();
      zLabelText.SetText(Z_AXIS_LABEL);

      vtkPolyDataMapper zLabelTextMapper = new vtkPolyDataMapper();
      zLabelTextMapper.SetInput(zLabelText.GetOutput());

      zLabelActor = new vtkFollower();
      zLabelActor.SetMapper(zLabelTextMapper);
      zLabelActor.SetScale(LABEL_SCALE);
      zLabelActor.GetProperty().SetColor(Z_AXIS_COLOR);
      zLabelActor.SetPosition(0, 0, AXES_LENGTH + LABEL_OFFSET);
}

    // Make an assembly
    vtkAssembly axisAssembly = new vtkAssembly();
    axisAssembly.AddPart(xAxisActor);
    axisAssembly.AddPart(yAxisActor);
    axisAssembly.AddPart(zAxisActor);
    if(USE_ARROWS) {
      axisAssembly.AddPart(xArrowActor);
      axisAssembly.AddPart(yArrowActor);
      axisAssembly.AddPart(zArrowActor);
    }
    // (Followers don't work if added to the assembly)
    
    // Create a cone
    vtkConeSource cone = new vtkConeSource();
    cone.SetRadius(.5);
    cone.SetResolution(50);
    cone.SetCenter(0, 0, 0);
    cone.SetHeight(1.);
    cone.SetDirection(0, 0, 1);
    vtkPolyDataMapper coneMapper = new vtkPolyDataMapper();
    coneMapper.SetInput(cone.GetOutput());
    vtkActor coneActor = new vtkActor();
    coneActor.SetMapper(coneMapper);
    coneActor.GetProperty().SetColor(0, 0, 1); // color blue
    coneActor.SetPosition(0, 0, 0);

    // Add actors
    renderer.AddActor(axisAssembly);
    if(USE_LABELS) {
      renderer.AddActor(xLabelActor);
      renderer.AddActor(yLabelActor);
      renderer.AddActor(zLabelActor);
    }
    renderer.AddActor(coneActor);

    // Set the camera
    vtkCamera camera = new vtkCamera();
    camera.SetClippingRange(1, 1000);
    camera.SetFocalPoint(0, 0, 0);
    camera.SetPosition(0, 0, 5);
    camera.SetViewUp(0, 1, 0);
    camera.Zoom(.8);
    renderer.SetActiveCamera(camera);
    if(USE_LABELS) {
      xLabelActor.SetCamera(camera);
      yLabelActor.SetCamera(camera);
      zLabelActor.SetCamera(camera);
    }

    created = true;
  }

}
