/*
 * Program to 
 * Created on Sep 7, 2006
 * By Kenneth Evans, Jr.
 */

package vtkdemos;

public class ProgrammableFilterDemo2 extends ProgrammableFilterDemo
{
  public ProgrammableFilterDemo2() {
    super();
    MODE = 2;
    
    setName("Programmable Filter 2");
  }

  public String getInfo() {
    String info = "This demo illustrates a function calculated\n"
      + "by a programmable filter using a UVW function.\n";
    return info;
  }

}
