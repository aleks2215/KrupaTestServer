package main;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;

import network.ClientConnectionHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ConsoleReader {
  private static Logger log = Logger.getLogger(ConsoleReader.class.getName());

  public static void main(String[] args) {

    log.log(Level.INFO,"Сервер для приема данных из .xml файла и записи из в базу запущен");
    System.out.println("Для выхода из программы введите команду exit");
    System.out.println("Активные ip адреса сервера:");
    showIP();

    Thread clientConnectionHandlerThread = new Thread(new ClientConnectionHandler());
    clientConnectionHandlerThread.setDaemon(true);
    clientConnectionHandlerThread.start();

    try (Scanner scanner = new Scanner(System.in)) {
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
