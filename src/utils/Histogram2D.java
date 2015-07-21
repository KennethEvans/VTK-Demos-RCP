package utils;

public class Histogram2D
{
  private static final double DEFAULT_FILL_VAL = 1.0;
  private String name = "Histogram 1D";
  private int nCells1 = 0;
  private double start1 = 0.0;
  private double end1 = 0.0;
  private double cellWidth1 = 0.0;
  private int nCells2 = 0;
  private double start2 = 0.0;
  private double end2 = 0.0;
  private double cellWidth2 = 0.0;
  private double[][] cellVals = null;
  
  /**
   * Histogram1D constructor
   * @param name
   * @param nCells1
   * @param start1
   * @param end1
   */
  public Histogram2D(String name, int nCells1, double start1, double end1,
    int nCells2, double start2, double end2) {
    this.name = name;
    this.nCells1 = nCells1;
    this.start1 = start1;
    this.end1 = end1;
    this.nCells2 = nCells2;
    this.start2 = start2;
    this.end2 = end2;
    if(nCells1 == 0 || nCells2 == 0) return;
    cellWidth1 = (end1 - start1) / nCells1;
    cellWidth2 = (end2 - start2) / nCells2;
    cellVals = new double[nCells1][nCells2];
    for(int i = 0; i < nCells1; i++) {
      for(int j = 0; j < nCells1; j++) {
        cellVals[i][j] = 0.0;
      }
    }
  }
  
  /**
   * Adds the default fill value to the appropriate cell corresponding to val1
   * and val2.
   * @param val1
   * @param val2
   */
  public void fill(double val1, double val2) {
    fill(val1, val2, DEFAULT_FILL_VAL);
  }

  /**
   * Adds the specified fillVal to the appropriate cell corresponding to val1
   * and val2.
   * @param val1
   * @param val2
   * @param fillVal
   */
  public void fill(double val1, double val2, double fillVal) {
    if(val1 < start1 || val1 > end1) return;
    if(val2 < start1 || val2 > end2) return;

    double scale1 = 1./ cellWidth1;
    double y1 = scale1 * (val1 - start1);
    int i = (int)Math.floor(y1);
    if(i < 0) i = 0;
    if( i > nCells1 - 1) i = nCells1 - 1;

    double scale2 = 1./ cellWidth2;
    double y2 = scale2 * (val2 - start2);
    int j = (int)Math.floor(y2);
    if(i < 0) i = 0;
    if( i > nCells2 - 1) i = nCells2 - 1;
    
    cellVals[i][j] += fillVal;
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
   * @return Returns the cell values.
   */
  public double[][] getCellVals() {
    return cellVals;
  }

  /**
   * @return Returns the cell width for dimension 1.
   */
  public double getCellWidth1() {
    return cellWidth1;
  }

  /**
   * @return Returns the cell width for dimension 2.
   */
  public double getCellWidth2() {
    return cellWidth2;
  }

  /**
   * @return Returns the end for dimension 1.
   */
  public double getEnd1() {
    return end1;
  }

  /**
   * @return Returns the end for dimension 2.
   */
  public double getEnd2() {
    return end2;
  }

  /**
   * @return Returns the number of cells for dimension 1.
   */
  public int getNCells1() {
    return nCells1;
  }

  /**
   * @return Returns the number of cells for dimension 2.
   */
  public int getNCells2() {
    return nCells2;
  }

  /**
   * @return Returns the start for dimension 1.
   */
  public double getStart1() {
    return start1;
  }

  /**
   * @return Returns the start for dimension 2.
   */
  public double getStart2() {
    return start2;
  }
  
}
