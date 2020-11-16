package main;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;
import network.ClientConnectionHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import settings.Settings;
import settings.Settings.SettingsTypes;
import settings.SettingsChecker;

public class ConsoleReader {

  private static Logger log = Logger.getLogger(ConsoleReader.class.getName());

  public static void main(String[] args) {

    log.log(Level.INFO, "Сервер для приема данных из .xml файла и записи из в базу запущен");
    System.out.println("Для выхода из программы введите команду exit");
    System.out.println("Активные ip адреса сервера:");
    showIP();

    try (Scanner scanner = new Scanner(System.in)) {

      Settings settings = Settings.getInstance();
      SettingsChecker settingsChecker = new SettingsChecker();
      int port;

      if (!settingsChecker.checkPort(settings.getProperty(SettingsTypes.PORT))) {
        log.log(Level.WARN, "Не удалось прочитать корректный порт из файла options.txt");
        while (true) {
          System.out.println("Введите корректный порт:");
          String string = scanner.nextLine();

          if (string.equals("exit")) {
            System.exit(0);
          }

          if (settingsChecker.checkPort(string)) {
            port = Integer.parseInt(string);
            settings.setProperty(SettingsTypes.PORT, String.valueOf(port));
            settings.save();
            break;
          }
        }
      } else {
        port = Integer.parseInt(settings.getProperty(SettingsTypes.PORT));
      }

      Thread clientConnectionHandlerThread = new Thread(new ClientConnectionHandler(port));
      clientConnectionHandlerThread.setDaemon(true);
      clientConnectionHandlerThread.start();

      while (true) {
        String string = scanner.nextLine();

        if (string.equals("exit")) {
          return;
        }
      }
    }
  }

  public static void showIP() {
    String ip;
    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface iface = interfaces.nextElement();
        // filters out 127.0.0.1 and inactive interfaces
        if (iface.isLoopback() || !iface.isUp()) {
          continue;
        }

        Enumeration<InetAddress> addresses = iface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          InetAddress addr = addresses.nextElement();
          if (addr.getClass() == Inet4Address.class) {
            ip = addr.getHostAddress();
            System.out.println(iface.getDisplayName() + " " + ip);
          }
        }
      }
    } catch (SocketException e) {
      log.log(Level.WARN, "Неудалось получить сетевые интерфейсы", e);
    }
  }
}
