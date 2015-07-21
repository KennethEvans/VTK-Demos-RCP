/*
 * Program to provide VTK utilities
 * Created on Sep 14, 2006
 * By Kenneth Evans, Jr.
 */

package utils;

import java.awt.Color;

import vtk.vtkDataObject;
import vtk.vtkDataSet;
import vtk.vtkLookupTable;

public class VTKUtils
{
  
  public static void printOutput(String name, vtkDataSet output) {
    System.out.println(name + " output:");
    System.out.println(output);
  }

  public static void printOutput(String name, vtkDataObject output) {
    System.out.println(name + " output:");
    System.out.println(output);
  }

  public static vtkLookupTable getRainbowLookupTable() {
    return getRainbowLookupTable(256);
  }
  
  public static vtkLookupTable getRainbowLookupTable(int size) {
    RainbowColorScheme rainbow = new RainbowColorScheme(size);
    Color[] colors = rainbow.defineColors();
    int nColors = rainbow.getNColors();
    
    vtkLookupTable lut = new vtkLookupTable();
    lut.SetNumberOfColors(nColors);
    lut.Build();
    for(int i = 0; i < nColors; ++i) {
      lut.SetTableValue(i, colors[i].getRed() / 255.0f,
        colors[i].getGreen() / 255.0f, colors[i].getBlue() / 255.0f, 1);
    }

    return lut;
  }

}
