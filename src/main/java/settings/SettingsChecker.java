package settings;

public class SettingsChecker {

  public boolean checkPort(String strPort) {
    try {
      int port = Integer.parseInt(strPort);
      if (port < 0 || port > 65535) {
        return false;
      }
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
