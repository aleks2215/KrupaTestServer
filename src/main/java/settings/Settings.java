package settings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Settings {

  private static Logger log = Logger.getLogger(Settings.class.getName());
  private static Settings settings;
  private File propFile;
  private Properties properties;

  private Settings() {
    initProperties();
  }

  public static Settings getInstance() {
    if (settings == null) {
      settings = new Settings();
    }

    return settings;
  }

  private void initProperties() {
    properties = new Properties();
    try {
      String propPath = System.getProperty("user.dir") + "\\options.txt";
      propFile = new File(propPath);
      if (!propFile.exists()) {
        propFile.createNewFile();
        properties.setProperty(SettingsTypes.PORT.getName(), "4567");
        try (FileWriter fileWriter = new FileWriter(propFile)) {
          properties.store(fileWriter, "Server options");
        }
      } else {
        properties.load(new FileReader(propFile));
      }
    } catch (IOException e) {
      log.log(Level.WARN, "Не удалось загрузить файл с настройками", e);
    }
  }

  public String getProperty(SettingsTypes type) {
    String result = null;
    if (type == SettingsTypes.PORT) {
      result = properties.getProperty(SettingsTypes.PORT.getName());
    }
    return result;
  }

  public void setProperty(SettingsTypes type, String value) {
    properties.setProperty(type.getName(), value);
  }

  public void save() {
    try (FileWriter fileWriter = new FileWriter(propFile)) {
      properties.store(fileWriter, "Server options");
      log.log(Level.INFO, "Настройки успешно сохранены");
    } catch (IOException e) {
      log.log(Level.WARN, "Ошибка при сохранении настроек в файл", e);
    }
  }

  public enum SettingsTypes {

    PORT("Port");

    private final String name;

    SettingsTypes(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }
}
