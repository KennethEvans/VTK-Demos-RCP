/*
 * Program to 
 * Created on Sep 7, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

public class ProgrammableFilterDemo1 extends ProgrammableFilterDemo
{
  public ProgrammableFilterDemo1() {
    super();
    MODE = 1;
    
    setName("Programmable Filter 1");
  }

  public String getInfo() {
    String info = "This demo illustrates a function calculated\n"
      + "by a programmable filter using a Gaussian function.\n";
    return info;
  }

}
