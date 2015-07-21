/*
 * Program to 
 * Created on Aug 3, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import vtk.vtkActor;
import vtk.vtkCamera;
import vtk.vtkLineSource;
import vtk.vtkPanel;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderWindow;
import vtk.vtkRenderWindowInteractor;
import vtk.vtkRenderer;
import vtk.vtkSphereSource;
import vtk.vtkTextSource;
import demolauncher.VTKDemo;

public class SphereDemo extends VTKDemo
{
  private static final int SPHERE_RES = 100;
  private static final double SPHERE_RADIUS = .5;
  private static final double LINE_POS = 1.75;
  private static final boolean LABEL_AXES = true;
  private static final double TEXT_SIZE = .005;

  public SphereDemo() {
    super();
    setName("Sphere");
  }

  public String getInfo() {
    String info = "This demo illustrates creating simple spheres.\n";
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

    // Make maps
    vtkPolyDataMapper map = new vtkPolyDataMapper();

    // Create a line source for the x axis
    vtkPolyDataMapper xLineMapper = new vtkPolyDataMapper();
    vtkLineSource xAxis = new vtkLineSource();
    xAxis.SetPoint1(-LINE_POS, 0, 0);
    xAxis.SetPoint2(LINE_POS, 0, 0);
    xAxis.SetResolution(1);
    xLineMapper.SetInput(xAxis.GetOutput());
    vtkActor xAxisActor = new vtkActor();
    xAxisActor.SetMapper(xLineMapper);
    xAxisActor.GetProperty().SetColor(1, 0, 0);
    xAxisActor.SetPosition(0, 0, 0);

    // Create text source for the x axis
    vtkActor xTextActor = null;
    if(LABEL_AXES) {
      vtkPolyDataMapper xTextMapper = new vtkPolyDataMapper();
      vtkTextSource xText = new vtkTextSource();
      xText.SetText("X");
//      xText.SetForegroundColor(1, 0, 0);
//      xText.SetBackgroundColor(0, 0, 0);
      xText.SetBacking(0);   // Don't draw the background
      xTextMapper.SetInput(xText.GetOutput());
      xTextMapper.ScalarVisibilityOff();  // Set color with actor
      xTextActor = new vtkActor();
      xTextActor.SetMapper(xTextMapper);
      xTextActor.SetPosition(LINE_POS, 0, 0);
      xTextActor.SetScale(TEXT_SIZE);
      xTextActor.GetProperty().SetColor(1, 0, 0);
    }

    // Create a line source for the y axis
    vtkPolyDataMapper yLineMapper = new vtkPolyDataMapper();
    vtkLineSource yAxis = new vtkLineSource();
    yAxis.SetPoint1(0, -LINE_POS, 0);
    yAxis.SetPoint2(0, LINE_POS, 0);
    yAxis.SetResolution(1);
    yLineMapper.SetInput(yAxis.GetOutput());
    vtkActor yAxisActor = new vtkActor();
    yAxisActor.SetMapper(yLineMapper);
    yAxisActor.GetProperty().SetColor(0, 1, 0);
    yAxisActor.SetPosition(0, 0, 0);

    // Create text source for the y axis
    vtkActor yTextActor = null;
    if(LABEL_AXES) {
      vtkPolyDataMapper yTextMapper = new vtkPolyDataMapper();
      vtkTextSource yText = new vtkTextSource();
      yText.SetText("Y");
//      yText.SetForegroundColor(0, 1, 0);
//      yText.SetBackgroundColor(0, 0, 0);
      yText.SetBacking(0);   // Don't draw the background
      yTextMapper.SetInput(yText.GetOutput());
      yTextMapper.ScalarVisibilityOff();  // Set color with actor
      yTextActor = new vtkActor();
      yTextActor.SetMapper(yTextMapper);
      yTextActor.SetPosition(0, LINE_POS, 0);
      yTextActor.SetScale(TEXT_SIZE);
      yTextActor.GetProperty().SetColor(0, 1, 0);
    }

    // Create a line source for the z axis
    vtkPolyDataMapper zLineMapper = new vtkPolyDataMapper();
    vtkLineSource zAxis = new vtkLineSource();
    zAxis.SetPoint1(0, 0, -LINE_POS);
    zAxis.SetPoint2(0, 0, LINE_POS);
    zAxis.SetResolution(1);
    zLineMapper.SetInput(zAxis.GetOutput());
    vtkActor zAxisActor = new vtkActor();
    zAxisActor.SetMapper(zLineMapper);
    zAxisActor.GetProperty().SetColor(0, 0, 1);
    zAxisActor.SetPosition(0, 0, 0);

    // Create text source for the z axis
    vtkActor zTextActor = null;
    if(LABEL_AXES) {
      vtkPolyDataMapper zTextMapper = new vtkPolyDataMapper();
      vtkTextSource zText = new vtkTextSource();
      zText.SetText("Z");
//      zText.SetForegroundColor(0, 0, 1);
//      zText.SetBackgroundColor(0, 0, 0);
      zText.SetBacking(0);   // Don't draw the background
      zTextMapper.SetInput(zText.GetOutput());
      zTextMapper.ScalarVisibilityOff();  // Set color with actor
      zTextActor = new vtkActor();
      zTextActor.SetMapper(zTextMapper);
      zTextActor.SetPosition(0, 0, LINE_POS);
      zTextActor.SetScale(TEXT_SIZE);
      zTextActor.GetProperty().SetColor(0, 0, 1);
    }

    // Create a sphere geometry
    vtkSphereSource sphere1 = new vtkSphereSource();
    sphere1.SetRadius(SPHERE_RADIUS);
    sphere1.SetThetaResolution(SPHERE_RES);
    sphere1.SetPhiResolution(SPHERE_RES);
    map.SetInput(sphere1.GetOutput());
    vtkActor aSphere1 = new vtkActor();
    aSphere1.SetMapper(map);
    aSphere1.GetProperty().SetColor(0, 0, 1); // color blue
    aSphere1.SetPosition(1, 0, 0);

    // Create a second sphere geometry
    vtkSphereSource sphere2 = new vtkSphereSource();
    sphere2.SetRadius(SPHERE_RADIUS);
    sphere2.SetThetaResolution(SPHERE_RES);
    sphere2.SetPhiResolution(SPHERE_RES);
    map.SetInput(sphere2.GetOutput());
    vtkActor aSphere2 = new vtkActor();
    aSphere2.SetMapper(map);
    aSphere2.GetProperty().SetColor(1, 0, 0); // red
    aSphere2.SetPosition(-1, 0, 0);

    // Create a third sphere geometry
    vtkSphereSource sphere3 = new vtkSphereSource();
    sphere3.SetRadius(SPHERE_RADIUS);
    sphere3.SetThetaResolution(SPHERE_RES);
    sphere3.SetPhiResolution(SPHERE_RES);
    map.SetInput(sphere3.GetOutput());
    vtkActor aSphere3 = new vtkActor();
    aSphere3.SetMapper(map);
    aSphere3.GetProperty().SetColor(0, 1, 0); // green
    aSphere3.SetPosition(0, 1, 0);

    // Create a fourth sphere geometry
    vtkSphereSource sphere4 = new vtkSphereSource();
    sphere4.SetRadius(SPHERE_RADIUS);
    sphere4.SetThetaResolution(SPHERE_RES);
    sphere4.SetPhiResolution(SPHERE_RES);
    map.SetInput(sphere4.GetOutput());
    vtkActor aSphere4 = new vtkActor();
    aSphere4.SetMapper(map);
    aSphere4.GetProperty().SetColor(1, 1, 0); // yellow
    aSphere4.SetPosition(0, 0, 1);

    // Set up the renderer
    vtkRenderer renderer = renWin.GetRenderer();
    renderer.SetBackground(1, 1, 1); // background color white
    renderer.AddActor(aSphere1);
    renderer.AddActor(aSphere2);
    renderer.AddActor(aSphere3);
    renderer.AddActor(aSphere4);
    renderer.AddActor(xAxisActor);
    renderer.AddActor(yAxisActor);
    renderer.AddActor(zAxisActor);
    if(LABEL_AXES) {
      renderer.AddActor(xTextActor);
      renderer.AddActor(yTextActor);
      renderer.AddActor(zTextActor);
    }

    // Set the camera
    vtkCamera camera = new vtkCamera();
    camera.SetClippingRange(1.81325, 90.6627);
    camera.SetFocalPoint(0, 0, 0);
    camera.SetPosition(0, 0, 5);
    camera.SetViewUp(0, 1, 0);
    camera.Zoom(.8);
    renderer.SetActiveCamera(camera);

    created = true;
  }

  // the main function
  public static void main(String[] args) {
    // create sphere geometry
    vtkSphereSource sphere = new vtkSphereSource();
    sphere.SetRadius(1.0);
    sphere.SetThetaResolution(SPHERE_RES);
    sphere.SetPhiResolution(SPHERE_RES);

    // map to graphics objects
    vtkPolyDataMapper map = new vtkPolyDataMapper();
    map.SetInput(sphere.GetOutput());

    // actor coordinates geometry, properties, transformation
    vtkActor aSphere = new vtkActor();
    aSphere.SetMapper(map);
    aSphere.GetProperty().SetColor(0, 0, 1); // blue

    // a renderer for the data
    vtkRenderer ren1 = new vtkRenderer();
    ren1.AddActor(aSphere);
    ren1.SetBackground(1, 1, 1); // background color white

    // a render window to display the contents
    vtkRenderWindow renWin = new vtkRenderWindow();
    renWin.AddRenderer(ren1);
    renWin.SetSize(300, 300);

    // an interactor to allow control of the objects
    vtkRenderWindowInteractor iren = new vtkRenderWindowInteractor();
    iren.SetRenderWindow(renWin);

    // trigger the rendering and start the interaction
    renWin.Render();
    iren.Start();
  }
}
