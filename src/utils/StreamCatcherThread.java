/*
 * Program to make a Thread that reads from a BufferedReader and writes
 * to a MessageConsoleStream and optionally a String
 * Created on Mar 12, 2006
 * By Kenneth Evans, Jr.
 */

package utils;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * StreamCatcherThread Reads from a BufferedReader and writes to a
 * MessageConsoleStream and optionally a String
 * 
 * @author Kenneth Evans, Jr.
 */
public class StreamCatcherThread extends Thread
{
  private BufferedReader reader;
  private String buffer = "";
  private boolean useBuffer = false;
  private boolean finished = false;

  /**
   * StreamCatcherThread constructor.
   * 
   * @param reader
   * @param out
   */
  public StreamCatcherThread(BufferedReader reader) {
    this.reader = reader;
  }

  /**
   * StreamCatcherThread constructor.
   * @param reader
   * @param out
   * @param useBuffer to enable writing to a String.
   */
  public StreamCatcherThread(BufferedReader reader, 
    boolean useBuffer) {
    this.reader = reader;
    this.useBuffer = useBuffer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  public void run() {
    try {
      String line = null;
      while((line = reader.readLine()) != null) {
        if(useBuffer) {
          buffer += line + "\n";
        }
      }
    } catch(IOException ex) {
      String msg = "\nStreamCatcherThread IOException: \n" + ex.getMessage();
      if(useBuffer) {
        buffer += msg;
      }
    } finally {
      finished = true;
    }
  }

  /**
   * @return Returns the buffer.
   */
  public String getBuffer() {
    return buffer;
  }

  /**
   * @return Returns if finished.
   */
  public boolean isFinished() {
    return finished;
  }

}
