/*
 * Program to 
 * Created on Aug 16, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import vtk.vtkCamera;
import vtk.vtkDataObject;
import vtk.vtkDoubleArray;
import vtk.vtkFieldData;
import vtk.vtkLegendBoxActor;
import vtk.vtkPanel;
import vtk.vtkRenderer;
import vtk.vtkTextProperty;
import vtk.vtkXYPlotActor;
import demolauncher.VTKDemo;

public class Plot2DDemo extends VTKDemo
{
  private static final boolean PRINT_DATA_INFO = true;
  private static final boolean PRINT_INFO = true;
  private static final double PI = Math.PI;
  private static final double SCALE_LEGEND = .5;

  public Plot2DDemo() {
    super();
    setName("2D Plot");
  }

  public String getInfo() {
    String info = "This demo illustrates a 2D plot.\n";
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

    // Define the data
    int nPoints = 50;
    vtkDoubleArray dataArray1 = new vtkDoubleArray();
    dataArray1.SetNumberOfComponents(2);
    dataArray1.SetNumberOfTuples(nPoints);
    vtkDoubleArray dataArray2 = new vtkDoubleArray();
    dataArray2.SetNumberOfComponents(2);
    dataArray2.SetNumberOfTuples(nPoints);
    double delta = 2 * PI / (nPoints - 1);
    for(int i = 0; i < nPoints; i++) {
      double x = i * delta;
      dataArray1.SetTuple2(i, x, Math.sin(x));
      dataArray2.SetTuple2(i, x, Math.cos(x));
    }

    // Make the FieldData's (Must be one for each curve?)
    vtkFieldData fieldData1 = new vtkFieldData();
    fieldData1.AllocateArrays(1);
    fieldData1.AddArray(dataArray1);
    vtkFieldData fieldData2 = new vtkFieldData();
    fieldData2.AllocateArrays(1);
    fieldData2.AddArray(dataArray2);
    if(PRINT_DATA_INFO) {
      System.out.println("vtkFieldData 1:");
      System.out.print(fieldData1);
      System.out.println("vtkFieldData 2:");
      System.out.print(fieldData2);
    }
    
    // Convert to DataObject's
    vtkDataObject dataObject1 = new vtkDataObject();
    dataObject1.SetFieldData(fieldData1);
    vtkDataObject dataObject2 = new vtkDataObject();
    dataObject2.SetFieldData(fieldData2);
    if(PRINT_DATA_INFO) {
      System.out.println("vtkDataObject 1:");
      System.out.print(fieldData1);
      System.out.println("vtkfieldData 2:");
      System.out.print(fieldData2);
    }

    // Make a plot
    vtkXYPlotActor plotActor = new vtkXYPlotActor();
    plotActor.AddDataObjectInput(dataObject1);
    plotActor.AddDataObjectInput(dataObject2);
    plotActor.GetPositionCoordinate().SetValue(.05, 0.05, 0); // LL
    plotActor.GetPosition2Coordinate().SetValue(0.9, 0.9, 0); // UR (relative)
    plotActor.SetXValuesToValue();  // [Normalized]ArcLength, Value, Index
    plotActor.SetNumberOfXLabels(1);
    plotActor.SetNumberOfYLabels(2);
    plotActor.SetTitle("Trignometric Functions");
    plotActor.SetXTitle("x");
    plotActor.SetYTitle("");
    plotActor.SetXRange(0.0, 2 * PI);
    plotActor.SetYRange(-1.0, 1.0);
    plotActor.GetProperty().SetColor(0, 0, 0);
    plotActor.LegendOn();
    if(false) {
      plotActor.LogxOff();
      plotActor.ExchangeAxesOff();
      plotActor.ReverseXAxisOff();
    }
    plotActor.GetProperty().SetPointSize(2);
    plotActor.GetProperty().SetLineWidth(2);
    plotActor.PlotPointsOff();
    plotActor.PlotLinesOn();
    // Curve 0
    plotActor.SetPlotColor(0, 1, 0, 0);
    plotActor.SetDataObjectXComponent(0, 0);
    plotActor.SetDataObjectYComponent(0, 1);
    plotActor.SetPlotLabel(0, "sin");
    // Curve 1
    plotActor.SetPlotColor(1, 0, 0, 1);
    plotActor.SetDataObjectXComponent(1, 0);
    plotActor.SetDataObjectYComponent(1, 1);
    plotActor.SetPlotLabel(1, "cos");
    plotActor.SetPlotLines(1, 1);   // Doesn't work ?
    plotActor.SetPlotPoints(1, 0);  // Doesn't work ?
    // Set text prop color (same color for backward compat with test);
    // Assign same object to all text props
    vtkTextProperty tprop = plotActor.GetTitleTextProperty();
    tprop.SetColor(plotActor.GetProperty().GetColor());
    plotActor.SetAxisTitleTextProperty(tprop);
    plotActor.SetAxisLabelTextProperty(tprop);
    
    // Fix the LegendBox
    if(SCALE_LEGEND != 1.0) {
      double[] pos2 = plotActor.GetLegendPosition2();
      pos2[0] *= SCALE_LEGEND;
      pos2[1] *= SCALE_LEGEND;
      plotActor.SetLegendPosition2(pos2);
      if(false) {
        // Doesn't work becasue it scales the fonts
        vtkLegendBoxActor legendActor = plotActor.GetLegendActor();
        legendActor.GetEntryTextProperty().SetFontSize(
          plotActor.GetTitleTextProperty().GetFontSize() / 2);
      }
    }
    
    if(PRINT_INFO) {
      int index = 10;
      int curve = 1;
      System.out.println("Title FontSize="
        + plotActor.GetTitleTextProperty().GetFontSize());
      System.out.println("Legend FontSize="
        + plotActor.GetLegendActor().GetEntryTextProperty().GetFontSize());
      System.out.println("GetDataObjectXComponent(curve)="
        + plotActor.GetDataObjectXComponent(curve));
      System.out.println("GetDataObjectYComponent(curve)="
        + plotActor.GetDataObjectYComponent(curve));
      System.out.println("GetPointComponent(curve)="
        + plotActor.GetPointComponent(curve));
      System.out.println("GetDataObjectPlotMode="
        + plotActor.GetDataObjectPlotMode());
      System.out.println("GetPlotSymbol(curve)="
        + plotActor.GetPlotSymbol(curve));
      System.out.println("index=" + index + " curve=" + curve);
    }

    // Add actors
    renderer.AddActor(plotActor);

    // Set the camera
    vtkCamera camera = new vtkCamera();
    camera.SetClippingRange(1, 1000);
    camera.SetFocalPoint(0, 0, 0);
    camera.SetPosition(0, 0, 5);
    camera.SetViewUp(0, 1, 0);
    camera.Zoom(.8);
    renderer.SetActiveCamera(camera);

    created = true;
  }

}
