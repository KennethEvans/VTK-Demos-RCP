package utils;

public class Histogram1D
{
  private static final double DEFAULT_FILL_VAL = 1.0;
  private String name = "Histogram 1D";
  private int nCells = 0;
  private double start = 0.0;
  private double end = 0.0;
  private double cellWidth = 0.0;
  private double[] cellVals = null;
  
  /**
   * Histogram1D constructor
   * @param name
   * @param nCells
   * @param start
   * @param end
   */
  public Histogram1D(String name, int nCells, double start, double end) {
    this.name = name;
    this.nCells = nCells;
    this.start = start;
    this.end = end;
    if(nCells == 0) return;
    cellWidth = (end - start) / nCells;
    cellVals = new double[nCells];
    for(int i = 0; i < nCells; i++) {
      cellVals[i] = 0.0;
    }
  }
  
  /**
   * Adds the default fill value to the appropriate cell corresponding to val.
   * @param val
   */
  public void fill(double val) {
    fill(val, DEFAULT_FILL_VAL);
  }

  /**
   * Adds the specified fillVal to the appropriate cell corresponding to val.
   * @param val
   * @param fillVal
   */
  public void fill(double val, double fillVal) {
    if(val < start || val > end) return;
    double scale = 1.0 / cellWidth;
    double y = scale * (val - start);
    int i = (int)Math.floor(y);
    if(i < 0) i = 0;
    if( i > nCells - 1) i = nCells - 1;
    cellVals[i] += fillVal;
  }

  /**
   * @return Returns the default fill value.
   */
  public static double getDEFAULT_FILL_VAL() {
    return DEFAULT_FILL_VAL;
  }

  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The name to set.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return Returns the array of cell values.
   */
  public double[] getCellVals() {
    return cellVals;
  }

  /**
   * @return Returns the cell width.
   */
  public double getCellWidth() {
    return cellWidth;
  }

  /**
   * @return Returns the end of the range.
   */
  public double getEnd() {
    return end;
  }

  /**
   * @return Returns the number of cells.
   */
  public int getNCells() {
    return nCells;
  }

  /**
   * @return Returns the start of the range.
   */
  public double getStart() {
    return start;
  }

}
