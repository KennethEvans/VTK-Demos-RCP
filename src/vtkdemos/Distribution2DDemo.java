/*
 * Program to 
 * Created on Sep 16, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;


public class Distribution2DDemo extends Surface2DDemo
{

  public Distribution2DDemo() {
    super();
    setName("Distribution 2D");
    functionType = 1;
  }

  public String getInfo() {
    String info = "This demo illustrates a 2D Gaussian\n"
                + "from a distribution using a vtkImageData.\n"
                + "It is the same as Surface2D with a\n"
                + "different function.";
    return info;
  }

}
