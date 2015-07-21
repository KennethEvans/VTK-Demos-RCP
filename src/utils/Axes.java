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

public class Axes
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

  private vtkActor xAxisActor = null;
  private vtkActor yAxisActor = null;
  private vtkActor zAxisActor = null;
  private vtkFollower xLabelActor = null;
  private vtkFollower yLabelActor = null;
  private vtkFollower zLabelActor = null;
  private double axesLength = AXES_LENGTH;
  private double axesRadius = AXES_RADIUS * axesLength / AXES_LENGTH;
  private double labelScale = LABEL_SCALE * axesLength / AXES_LENGTH;
  private double xScale = 1;
  private double yScale = 1;
  private double zScale = 1;
  
  public Axes() {
    this(AXES_LENGTH);
  }

  public Axes(double axesLength) {
    if(axesLength == 0) return;
    
    this.axesLength = axesLength;
    this.axesRadius = AXES_RADIUS * axesLength / AXES_LENGTH;
    this.labelScale = LABEL_SCALE * axesLength / AXES_LENGTH;
  }
  
  public void createActors() {
    // Create x axis
    double xAxisLength = xScale * axesLength;
    vtkCylinderSource xAxis = new vtkCylinderSource();
    xAxis.SetRadius(axesRadius);
    xAxis.SetHeight(xAxisLength);
    xAxis.SetResolution(AXES_RESOLUTION);
    vtkPolyDataMapper xAxisMapper = new vtkPolyDataMapper();
    xAxisMapper.SetInput(xAxis.GetOutput());
    xAxisActor = new vtkActor();
    xAxisActor.SetMapper(xAxisMapper);
    xAxisActor.GetProperty().SetColor(X_AXIS_COLOR);
    xAxisActor.RotateZ(90.);
    xAxisActor.SetPosition(xAxisLength / 2., 0, 0);

    // Create y axis
    double yAxisLength = yScale * axesLength;
    vtkCylinderSource yAxis = new vtkCylinderSource();
    yAxis.SetRadius(axesRadius);
    yAxis.SetHeight(yAxisLength);
    yAxis.SetResolution(AXES_RESOLUTION);
    vtkPolyDataMapper yAxisMapper = new vtkPolyDataMapper();
    yAxisMapper.SetInput(yAxis.GetOutput());
    yAxisActor = new vtkActor();
    yAxisActor.SetMapper(yAxisMapper);
    yAxisActor.GetProperty().SetColor(Y_AXIS_COLOR);
    yAxisActor.SetPosition(0, yAxisLength / 2., 0);

    // Create z axis
    double zAxisLength = zScale * axesLength;
    vtkCylinderSource zAxis = new vtkCylinderSource();
    zAxis.SetRadius(axesRadius);
    zAxis.SetHeight(zAxisLength);
    zAxis.SetResolution(AXES_RESOLUTION);
    vtkPolyDataMapper zAxisMapper = new vtkPolyDataMapper();
    zAxisMapper.SetInput(zAxis.GetOutput());
    zAxisActor = new vtkActor();
    zAxisActor.SetMapper(zAxisMapper);
    zAxisActor.GetProperty().SetColor(Z_AXIS_COLOR);
    zAxisActor.RotateX(90.);
    zAxisActor.SetPosition(0, 0, zAxisLength / 2.);

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
      xArrowActor.GetProperty().SetColor(X_AXIS_COLOR);
      xArrowActor.SetPosition(xAxisLength, 0, 0);

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
      yArrowActor.GetProperty().SetColor(Y_AXIS_COLOR);
      yArrowActor.SetPosition(0, yAxisLength, 0);

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
      zArrowActor.GetProperty().SetColor(Z_AXIS_COLOR);
      zArrowActor.SetPosition(0, 0, zAxisLength);
    }

    // Create labels
    if(USE_LABELS) {
      // Create xLabel
      vtkVectorText xLabelText = new vtkVectorText();
      xLabelText.SetText(X_AXIS_LABEL);

      vtkPolyDataMapper xLabelTextMapper = new vtkPolyDataMapper();
      xLabelTextMapper.SetInput(xLabelText.GetOutput());

      xLabelActor = new vtkFollower();
      xLabelActor.SetMapper(xLabelTextMapper);
      xLabelActor.SetScale(labelScale);
      xLabelActor.GetProperty().SetColor(X_AXIS_COLOR);
      xLabelActor.SetPosition(xAxisLength + LABEL_OFFSET, 0, 0);

      // Create yLabel
      vtkVectorText yLabelText = new vtkVectorText();
      yLabelText.SetText(Y_AXIS_LABEL);

      vtkPolyDataMapper yLabelTextMapper = new vtkPolyDataMapper();
      yLabelTextMapper.SetInput(yLabelText.GetOutput());

      yLabelActor = new vtkFollower();
      yLabelActor.SetMapper(yLabelTextMapper);
      yLabelActor.SetScale(labelScale);
      yLabelActor.GetProperty().SetColor(Y_AXIS_COLOR);
      yLabelActor.SetPosition(0, yAxisLength + LABEL_OFFSET, 0);

      // Create zLabel
      vtkVectorText zLabelText = new vtkVectorText();
      zLabelText.SetText(Z_AXIS_LABEL);

      vtkPolyDataMapper zLabelTextMapper = new vtkPolyDataMapper();
      zLabelTextMapper.SetInput(zLabelText.GetOutput());

      zLabelActor = new vtkFollower();
      zLabelActor.SetMapper(zLabelTextMapper);
      zLabelActor.SetScale(labelScale);
      zLabelActor.GetProperty().SetColor(Z_AXIS_COLOR);
      zLabelActor.SetPosition(0, 0, zAxisLength + LABEL_OFFSET);
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
   * @return the axesLength
   */
  public double getAxesLength() {
    return axesLength;
  }

  /**
   * @param axesLength the axesLength to set
   */
  public void setAxesLength(double axesLength) {
    this.axesLength = axesLength;
  }

  /**
   * @return the axesRadius
   */
  public double getAxesRadius() {
    return axesRadius;
  }

  /**
   * @param axesRadius the axesRadius to set
   */
  public void setAxesRadius(double axesRadius) {
    this.axesRadius = axesRadius;
  }

  /**
   * @return the axisAssembly
   */
  public vtkAssembly getAxisAssembly() {
    return axisAssembly;
  }

  /**
   * @param axisAssembly the axisAssembly to set
   */
  public void setAxisAssembly(vtkAssembly axisAssembly) {
    this.axisAssembly = axisAssembly;
  }

  /**
   * @return the labelScale
   */
  public double getLabelScale() {
    return labelScale;
  }

  /**
   * @param labelScale the labelScale to set
   */
  public void setLabelScale(double labelScale) {
    this.labelScale = labelScale;
  }

  /**
   * @return the xScale
   */
  public double getXScale() {
    return xScale;
  }

  /**
   * @param scale the xScale to set
   */
  public void setXScale(double scale) {
    xScale = scale;
  }

  /**
   * @return the yScale
   */
  public double getYScale() {
    return yScale;
  }

  /**
   * @param scale the yScale to set
   */
  public void setYScale(double scale) {
    yScale = scale;
  }

  /**
   * @return the zScale
   */
  public double getZScale() {
    return zScale;
  }

  /**
   * @param scale the zScale to set
   */
  public void setZScale(double scale) {
    zScale = scale;
  }
  

}
