/*
 * Program to 
 * Created on Sep 11, 2006
 * By Kenneth Evans, Jr.
 */

package utils;

import vtk.vtkActor;
import vtk.vtkAssembly;
import vtk.vtkCamera;
import vtk.vtkConeSource;
import vtk.vtkCylinderSource;
import vtk.vtkFollower;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderer;
import vtk.vtkVectorText;

public class Axes2
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
  
  private vtkAssembly axisAssembly = null;

  private vtkFollower xLabelActor = null;
  private vtkFollower yLabelActor = null;
  private vtkFollower zLabelActor = null;
  
  public Axes2() {
    this(AXES_LENGTH, X_AXIS_LABEL, Y_AXIS_LABEL, Z_AXIS_LABEL, X_AXIS_COLOR,
      Y_AXIS_COLOR, Z_AXIS_COLOR);
  }

  public Axes2(double axesLength) {
    this(axesLength, X_AXIS_LABEL, Y_AXIS_LABEL, Z_AXIS_LABEL, X_AXIS_COLOR,
      Y_AXIS_COLOR, Z_AXIS_COLOR);
  }

  public Axes2(double axesLength, String xLabel, String yLabel, String zLabel, 
    double[] xAxisColor, double[] yAxisColor, double[] zAxisColor) {
    if(axesLength == 0) return;
    
    double axesRadius = AXES_RADIUS * axesLength / AXES_LENGTH;
    double labelScale = LABEL_SCALE * axesLength / AXES_LENGTH;
     
    // Create x axis
    vtkCylinderSource xAxis = new vtkCylinderSource();
    xAxis.SetRadius(axesRadius);
    xAxis.SetHeight(axesLength);
    xAxis.SetResolution(AXES_RESOLUTION);
    vtkPolyDataMapper xAxisMapper = new vtkPolyDataMapper();
    xAxisMapper.SetInput(xAxis.GetOutput());
    vtkActor xAxisActor = new vtkActor();
    xAxisActor.SetMapper(xAxisMapper);
    xAxisActor.GetProperty().SetColor(xAxisColor);
    xAxisActor.RotateZ(90.);
    xAxisActor.SetPosition(axesLength / 2., 0, 0);

    // Create y axis
    vtkCylinderSource yAxis = new vtkCylinderSource();
    yAxis.SetRadius(axesRadius);
    yAxis.SetHeight(axesLength);
    yAxis.SetResolution(AXES_RESOLUTION);
    vtkPolyDataMapper yAxisMapper = new vtkPolyDataMapper();
    yAxisMapper.SetInput(yAxis.GetOutput());
    vtkActor yAxisActor = new vtkActor();
    yAxisActor.SetMapper(yAxisMapper);
    yAxisActor.GetProperty().SetColor(yAxisColor);
    yAxisActor.SetPosition(0, axesLength / 2., 0);

    // Create z axis
    vtkCylinderSource zAxis = new vtkCylinderSource();
    zAxis.SetRadius(axesRadius);
    zAxis.SetHeight(axesLength);
    zAxis.SetResolution(AXES_RESOLUTION);
    vtkPolyDataMapper zAxisMapper = new vtkPolyDataMapper();
    zAxisMapper.SetInput(zAxis.GetOutput());
    vtkActor zAxisActor = new vtkActor();
    zAxisActor.SetMapper(zAxisMapper);
    zAxisActor.GetProperty().SetColor(zAxisColor);
    zAxisActor.RotateX(90.);
    zAxisActor.SetPosition(0, 0, axesLength / 2.);

    // Create arrows
    vtkActor xArrowActor = null;
    vtkActor yArrowActor = null;
    vtkActor zArrowActor = null;
    if(USE_ARROWS) {
      // Create xArrow
      vtkConeSource xArrow = new vtkConeSource();
      xArrow.SetRadius(4. * axesRadius);
      xArrow.SetResolution(AXES_RESOLUTION);
      xArrow.SetHeight(10. * axesRadius);
      xArrow.SetDirection(1, 0, 0);
      vtkPolyDataMapper xArrowMapper = new vtkPolyDataMapper();
      xArrowMapper.SetInput(xArrow.GetOutput());
      xArrowActor = new vtkActor();
      xArrowActor.SetMapper(xArrowMapper);
      xArrowActor.GetProperty().SetColor(xAxisColor);
      xArrowActor.SetPosition(axesLength, 0, 0);

      // Create yArrow
      vtkConeSource yArrow = new vtkConeSource();
      yArrow.SetRadius(4. * axesRadius);
      yArrow.SetResolution(AXES_RESOLUTION);
      yArrow.SetHeight(10. * axesRadius);
      yArrow.SetDirection(0, 1, 0);
      vtkPolyDataMapper yArrowMapper = new vtkPolyDataMapper();
      yArrowMapper.SetInput(yArrow.GetOutput());
      yArrowActor = new vtkActor();
      yArrowActor.SetMapper(yArrowMapper);
      yArrowActor.GetProperty().SetColor(yAxisColor);
      yArrowActor.SetPosition(0, axesLength, 0);

      // Create zArrow
      vtkConeSource zArrow = new vtkConeSource();
      zArrow.SetRadius(4. * axesRadius);
      zArrow.SetResolution(AXES_RESOLUTION);
      zArrow.SetHeight(10. * axesRadius);
      zArrow.SetDirection(0, 0, 1);
      vtkPolyDataMapper zArrowMapper = new vtkPolyDataMapper();
      zArrowMapper.SetInput(zArrow.GetOutput());
      zArrowActor = new vtkActor();
      zArrowActor.SetMapper(zArrowMapper);
      zArrowActor.GetProperty().SetColor(zAxisColor);
      zArrowActor.SetPosition(0, 0, axesLength);
    }

    // Create labels
    if(USE_LABELS) {
      // Create xLabel
      vtkVectorText xLabelText = new vtkVectorText();
      xLabelText.SetText(xLabel);

      vtkPolyDataMapper xLabelTextMapper = new vtkPolyDataMapper();
      xLabelTextMapper.SetInput(xLabelText.GetOutput());

      xLabelActor = new vtkFollower();
      xLabelActor.SetMapper(xLabelTextMapper);
      xLabelActor.SetScale(labelScale);
      xLabelActor.GetProperty().SetColor(xAxisColor);
      xLabelActor.SetPosition(axesLength + LABEL_OFFSET, 0, 0);

      // Create yLabel
      vtkVectorText yLabelText = new vtkVectorText();
      yLabelText.SetText(yLabel);

      vtkPolyDataMapper yLabelTextMapper = new vtkPolyDataMapper();
      yLabelTextMapper.SetInput(yLabelText.GetOutput());

      yLabelActor = new vtkFollower();
      yLabelActor.SetMapper(yLabelTextMapper);
      yLabelActor.SetScale(labelScale);
      yLabelActor.GetProperty().SetColor(yAxisColor);
      yLabelActor.SetPosition(0, axesLength + LABEL_OFFSET, 0);

      // Create zLabel
      vtkVectorText zLabelText = new vtkVectorText();
      zLabelText.SetText(zLabel);

      vtkPolyDataMapper zLabelTextMapper = new vtkPolyDataMapper();
      zLabelTextMapper.SetInput(zLabelText.GetOutput());

      zLabelActor = new vtkFollower();
      zLabelActor.SetMapper(zLabelTextMapper);
      zLabelActor.SetScale(labelScale);
      zLabelActor.GetProperty().SetColor(zAxisColor);
      zLabelActor.SetPosition(0, 0, axesLength + LABEL_OFFSET);
    }

    // Make an assembly
    axisAssembly = new vtkAssembly();
    axisAssembly.AddPart(xAxisActor);
    axisAssembly.AddPart(yAxisActor);
    axisAssembly.AddPart(zAxisActor);
    if(USE_ARROWS) {
      axisAssembly.AddPart(xArrowActor);
      axisAssembly.AddPart(yArrowActor);
      axisAssembly.AddPart(zArrowActor);
    }
    // (Followers don't work if added to the assembly)
  }

  public void addActor(vtkRenderer renderer) {
    renderer.AddActor(axisAssembly);
    if(USE_LABELS) {
      renderer.AddActor(xLabelActor);
      renderer.AddActor(yLabelActor);
      renderer.AddActor(zLabelActor);
    }
  }
  
  public void setCamera(vtkCamera camera) {
    if(USE_LABELS) {
      xLabelActor.SetCamera(camera);
      yLabelActor.SetCamera(camera);
      zLabelActor.SetCamera(camera);
    }
  }

  public void SetVisibility(int visibility) {
    axisAssembly.SetVisibility(visibility);
    if(USE_LABELS) {
      xLabelActor.SetVisibility(visibility);
      yLabelActor.SetVisibility(visibility);
      zLabelActor.SetVisibility(visibility);
    }
  }

  /**
   * @return The value of axisAssembly.
   */
  public vtkAssembly getAxisAssembly() {
    return axisAssembly;
  }

  /**
   * @return The value of xLabelActor.
   */
  public vtkFollower getXLabelActor() {
    return xLabelActor;
  }

  /**
   * @return The value of yLabelActor.
   */
  public vtkFollower getYLabelActor() {
    return yLabelActor;
  }

  /**
   * @return The value of zLabelActor.
   */
  public vtkFollower getZLabelActor() {
    return zLabelActor;
  }

}
