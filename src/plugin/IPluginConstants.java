/*
 * Interface to store constants
 * Created on March 10, 2006
 * By Kenneth Evans, Jr.
 */

package plugin;

/**
 * IPluginConstants * Constants for VTK Demos RCP
 * 
 * @author evans
 */
public interface IPluginConstants
{
  public static final String PLUGIN_ID = "VTKDemo";
  public static final String INFO_ACTION_ID = PLUGIN_ID + ".infoAction";
  public static final String SCROLLED_INFO_ACTION_ID = PLUGIN_ID
  + ".scrolledInfoAction";
  public static final String DISPLAY_INFO_ACTION_ID = PLUGIN_ID
  + ".displayInfoAction";
  public static final String DISPLAY_INFO_JAVA_ACTION_ID = PLUGIN_ID
    + ".displayInfoJavaAction";
  public static final String DEBUG_INFO_ACTION_ID = PLUGIN_ID
    + ".debugInfoAction";
}
