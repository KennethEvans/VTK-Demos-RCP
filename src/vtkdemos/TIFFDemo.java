/*
 * Program to 
 * Created on Aug 16, 2006
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
import javax.swing.JTextField;

import utils.Axes;
import utils.Utils;
import vtk.vtkActor;
import vtk.vtkBMPReader;
import vtk.vtkCamera;
import vtk.vtkImageActor;
import vtk.vtkImageData;
import vtk.vtkImageFlip;
import vtk.vtkImageGaussianSmooth;
import vtk.vtkImageReader2;
import vtk.vtkImageShiftScale;
import vtk.vtkPanel;
import vtk.vtkRenderer;
import vtk.vtkTIFFReader;
import demolauncher.VTKDemo;

public class TIFFDemo extends VTKDemo
{
  private static final int DEBUG_LEVEL = 0;
  private String defaultPath = "c:\\Java\\FIT2D"; 
  private static final String TIF_FILE_NAME = "C:/Java/Fit2D/evans.tif";
  private static final String TIF_FILE_NAME2 = "C:/Java/Fit2D/LaB6_80kev_40cm_0001.tif";
//  private static final String BMP_FILE_NAME = "C:/BMP/angel.bmp";
  private static final int IMAGE_TYPE = 2;
  private static final boolean DO_SMOOTH = false;
  private static final boolean DO_FLIP = false;
  private static final boolean DO_SCALE = true;
  private static final double SHIFT = 0;
  private static final double SCALE = 1;
  private double shift = SHIFT;
  private double scale = SCALE;

  private vtkRenderer renderer = null;
  private vtkImageReader2 reader = null;
  private vtkImageShiftScale shiftScale = null;
  
  private JTextField shiftText = null;
  private JTextField scaleText = null;

  private static final boolean DO_WARP = false;
  private boolean doWarp = DO_WARP;
  private vtkActor warpActor = null;

  private static final boolean DO_FLAT = true;
  private boolean doFlat = DO_FLAT;
  private vtkImageActor imageActor = null;
  private vtkActor flatActor = null;

  private static final boolean DO_AXES = false;
  private boolean doAxes = DO_AXES;
  private Axes axes = null;

  public TIFFDemo() {
    super();
    setName("TIFF File");
  }

  public String getInfo() {
    String info = "This demo illustrates reading a TIFF file.\n";
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

    // Make a rainbow lookup table
//    vtkLookupTable lut = VTKUtils.getRainbowLookupTable();

    // Read the file
    File file = null;
    switch(IMAGE_TYPE) {
    case 0:
      // Get the file name using a JChooser
      file = Utils.getOpenFile(defaultPath);
      if(file == null) {
        Utils.errMsg("No file selected\nUse File|Reset Current to try again");
        return;
      }
      reader = new vtkTIFFReader();
      reader.SetFileName(file.getAbsolutePath());
      break;
    case 1:
      reader = new vtkTIFFReader();
      reader.SetFileName(TIF_FILE_NAME);
      break;
    case 2:
      reader = new vtkTIFFReader();
      reader.SetFileName(TIF_FILE_NAME2);
      scale = 10.0;
      break;
    case 3:
      reader = new vtkBMPReader();
      reader.SetFileName(TIF_FILE_NAME);
      break;
    }
    reader.Update();
    if(DEBUG_LEVEL > 0) {
      System.out.println("reader");
      System.out.println(reader);
      System.out.println("reader output");
      vtkImageData output = reader.GetOutput();
      int[] ival = output.GetDimensions();
      System.out.println("GetDimensions: " + ival[0] + "," + ival[1] + "," + ival[2]);
      System.out.println(output);
    }
        
    // Define the current image
    vtkImageData current = reader.GetOutput();
    if(DEBUG_LEVEL >= 0) {
      System.out.println("current:");
      double[] bounds = current.GetBounds();
      System.out.println("Bounds");
      for(int i = 0; i < bounds.length; i++) {
        System.out.println("  " + i + " " + bounds[i]);
      }
      System.out.print(current);
    }

    // Get the dimension
    double imageMaxDimension = 1;
    int[] dimensions = current.GetDimensions();
    for(int i = 0; i < dimensions.length; i++) {
      int dimension = dimensions[i];
      if(imageMaxDimension < dimension) imageMaxDimension = dimension;
    }
    
    // Smooth it
    vtkImageGaussianSmooth smooth = null;
    if(DO_SMOOTH) {
      smooth = new vtkImageGaussianSmooth();
      smooth.SetInput(current);
      smooth.SetStandardDeviations(3, 3, 3);
      current = smooth.GetOutput();
    }

    // Flip it
    vtkImageFlip flip = null;
    if(DO_FLIP) {
      flip = new vtkImageFlip();
      flip.SetInput(current);
      flip.SetFilteredAxis(1);
      current = flip.GetOutput();
    }

    // Scale it
    if(DO_SCALE) {
      shiftScale = new vtkImageShiftScale();
      shiftScale.SetInput(current);
      shiftScale.SetShift(shift);
      shiftScale.SetScale(scale);
      shiftScale.SetOutputScalarTypeToUnsignedChar();
      current = shiftScale.GetOutput();
    }

    // Set up the actor
    imageActor = new vtkImageActor();
    imageActor.SetInput(current);
    imageActor.SetVisibility(doFlat ? 1 : 0);
    renderer.AddActor(imageActor);

//    // Create a mapper for flat
//    vtkDataSetMapper flatMapper = new vtkDataSetMapper();
////    flatMapper.SetLookupTable(lut);
//    flatMapper.SetInput(current);
////    flatMapper.SetScalarRange(zMin, zMax);
//
//    // Set the actor for the flat mapper
//    flatActor = new vtkActor();
//    flatActor.SetMapper(flatMapper);
//    flatActor.SetVisibility(doFlat ? 1 : 0);
//    renderer.AddActor(flatActor);

    // // Set up the viewer
    // vtkImageViewer viewer = new vtkImageViewer();
    // if(flip != null) {
    // viewer.SetInput(flip.GetOutput());
    // } else if(smooth != null) {
    // viewer.SetInput(smooth.GetOutput());
    // } else {
    // viewer.SetInput(reader.GetOutput());
    // }
    // viewer.SetZSlice(0);
    // viewer.SetColorLevel(128);
    // viewer.SetColorWindow(255);
    // viewer.Render();

//    // Use a geometry filter to extract the geometry from the vtkImageData
//    vtkImageDataGeometryFilter geometry = new vtkImageDataGeometryFilter();
//    geometry.SetInput(current);
//    
//    // Warp to a 2D surface
//    vtkWarpScalar warp = new vtkWarpScalar();
//    warp.SetInput(geometry.GetOutput());
////    double scale = (xMax - xMin) / (zMax - zMin);
////    warp.SetScaleFactor(scale);
//    
//    // Create a mapper for warp
//    vtkDataSetMapper warpMapper = new vtkDataSetMapper();
//    warpMapper.SetLookupTable(lut);
//    warpMapper.SetInput(warp.GetOutput());
////    warpMapper.SetScalarRange(zMin, zMax);
//
//    // Set the actor for the warp mapper
//    warpActor = new vtkActor();
//    warpActor.SetMapper(warpMapper);
//    warpActor.SetVisibility(doWarp ? 1 : 0);
//    renderer.AddActor(warpActor);

    // Set the camera
    vtkCamera camera = new vtkCamera();
    camera.SetClippingRange(1, 1000);
    camera.SetFocalPoint(0, 0, 0);
    camera.SetPosition(0, 0, 5);
    camera.SetViewUp(0, 1, 0);
    // Let the renderer compute a good position and focal point
    renderer.SetActiveCamera(camera);
    renderer.ResetCamera();
    // camera.Dolly(1.4);
    // camera.Zoom(.8);
    renderer.ResetCameraClippingRange();

    // Add axes
    axes = new Axes(1.1 * imageMaxDimension);
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
          if(file != null && reader != null && flatActor != null) {
            defaultPath = file.getParentFile().getPath();
            reader.SetFileName(file.getAbsolutePath());
            reader.Update();
            shiftScale();
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
        camera.SetViewUp(0, 1, 0);
        camera.SetPosition(0, 0, 1);
        camera.SetFocalPoint(0, 0, 0);
        camera.SetViewAngle(30);
        camera.SetClippingRange(.1, 1000);
        camera.ComputeViewPlaneNormal();
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
    
    JLabel label = new JLabel("Shift");
    controlPanel.add(label);
    
    shiftText = new JTextField();
    controlPanel.add(shiftText);
    shiftText.setColumns(8);
    shiftText.setText(Double.toString(shift));
    shiftText.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        shiftScale();
      }
    });

    label = new JLabel("Scale");
    controlPanel.add(label);
    
    scaleText = new JTextField();
    controlPanel.add(scaleText);
    scaleText.setColumns(8);
    scaleText.setText(Double.toString(scale));
    scaleText.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        shiftScale();
      }
    });

    // Flat
    final JCheckBox flatCheck = new JCheckBox();
    flatCheck.setText("2D");
    flatCheck.setSelected(doFlat);
    controlPanel.add(flatCheck);
    flatCheck.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doFlat = flatCheck.isSelected();
        if(flatActor != null) {
          flatActor.SetVisibility(doFlat ? 1 : 0);
          renderer.GetRenderWindow().Render();
        }
      }
    });

    // Warp
    final JCheckBox warpCheck = new JCheckBox();
    warpCheck.setText("3D");
    warpCheck.setSelected(doWarp);
    controlPanel.add(warpCheck);
    warpCheck.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        doWarp = warpCheck.isSelected();
        if(warpActor != null) {
          warpActor.SetVisibility(doWarp ? 1 : 0);
          renderer.GetRenderWindow().Render();
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
    
  private void shiftScale() {
    if(shiftScale != null) {
      shift = Double.valueOf(shiftText.getText());
      shiftScale.SetShift(shift);
      scale = Double.valueOf(scaleText.getText());
      shiftScale.SetScale(scale);
      renderer.GetRenderWindow().Render();
    }
  }
 
}
