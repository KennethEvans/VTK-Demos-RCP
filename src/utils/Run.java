/*
 * Program to run a command with output to a Console
 * Created on Mar 12, 2006
 * By Kenneth Evans, Jr.
 */

package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class Run
{
  private boolean useBufferDefault = false;
  private long timeoutInterval = 100;  // ms
  private long timeout = 10000;  // ms
  Process proc = null;
  private volatile boolean finished = false;
  private volatile boolean timedOut = false;
  private StreamCatcherThread errStreamThread = null;
  private StreamCatcherThread outStreamThread = null;
  private String output = "";
  private String error = "";

  public int run(String[] cmdArray) throws IOException {
    return run(cmdArray, null, null, useBufferDefault);
  }

  public int run(String[] cmdArray, boolean useBuffer) throws IOException {
    return run(cmdArray, null, null, useBuffer);
  }

  public int run(String[] cmdArray, String[] envp) throws IOException {
    return run(cmdArray, envp, null, useBufferDefault);
  }

  public int run(String[] cmdArray, String[] envp, boolean useBuffer)
    throws IOException {
    return run(cmdArray, envp, null, useBuffer);
  }

  public int run(String[] cmdArray, String[] envp, File dir)
    throws IOException {
    return run(cmdArray, envp, dir, useBufferDefault);
  }

  synchronized public int run(String[] cmdArray, String[] envp, File dir, boolean useBuffer)
    throws IOException {
    // Exec the command
    proc = Runtime.getRuntime().exec(cmdArray, envp, dir);

    // Start stderr and stdout streams
    startInputStreams(proc, useBuffer);

    int exitValue = -1;
    if(timeout == 0) {
      // Wait for the process to end
      try {
        exitValue = proc.waitFor();
        finished = true;
      } catch(InterruptedException x) {
        // TO DO
      }
    } else {
      // Note that run needs to be synchronized to use wait but not to
      // use waitFor
      long elapsed = 0;
      while(!finished && elapsed < timeout) {
        try {
          wait(timeoutInterval);
          elapsed += timeoutInterval;
          // Check if finished yet
          try {
            exitValue = proc.exitValue();
            finished = true;
          } catch(IllegalThreadStateException ex) {
            // Is not finished, continue
          }
        } catch(InterruptedException x) {
          // Continue
        }
      }
      if(!finished) {
        timedOut = true;
        proc.destroy();
      }
    }
    error = errStreamThread.getBuffer();
    output = outStreamThread.getBuffer();
    return exitValue;
  }

  private void startInputStreams(Process proc, boolean useBuffer) {
    // Start the stderr input stream  
    BufferedReader errorReader = new BufferedReader(new InputStreamReader(proc
      .getErrorStream()));
    errStreamThread = new StreamCatcherThread(errorReader, useBuffer);
    errStreamThread.start();

    // Start the stdout input stream  
    BufferedReader outReader = new BufferedReader(new InputStreamReader(proc
      .getInputStream()));
    outStreamThread = new StreamCatcherThread(outReader, useBuffer);
    outStreamThread.start();
  }
  
  public void abort() {
    if(proc != null && !finished) proc.destroy();
  }

  /**
   * @return Returns the output from the process.
   */
  public String getOutput() {
    return output;
  }

  /**
   * @return Returns the error from the process.
   */
  public String getError() {
    return error;
  }

  /**
   * @return Returns the proc.
   */
  public Process getProc() {
    return proc;
  }

  /**
   * @return Returns the timeout.
   */
  public long getTimeout() {
    return timeout;
  }

  /**
   * @param timeout The timeout to set.
   */
  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  /**
   * @return Returns the timeoutInterval.
   */
  public long getTimeoutInterval() {
    return timeoutInterval;
  }

  /**
   * @param timeoutInterval The timeoutInterval to set.
   */
  public void setTimeoutInterval(long timeoutInterval) {
    this.timeoutInterval = timeoutInterval;
  }

  /**
   * @return Returns if process timed out.
   */
  public boolean isTimedOut() {
    return timedOut;
  }

  /**
   * @return Returns if process finished.
   */
  public boolean isFinished() {
    return finished;
  }

}
