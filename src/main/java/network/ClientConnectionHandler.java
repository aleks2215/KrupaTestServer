package network;

import db.DbWriter;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import model.Good;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ClientConnectionHandler implements Runnable {
  private static Logger log = Logger.getLogger(ClientConnectionHandler.class.getName());

  @Override
  public void run() {
    try (ServerSocket serverSocket = new ServerSocket(4567)) {
      while (true) {
        log.log(Level.INFO,"Ожидание подключения нового клиента...");
        Socket receivedConnectionSocket = serverSocket.accept();
        log.log(Level.INFO,"Соединение с клиентом установлено");

        ObjectInputStream objectInputStream = new ObjectInputStream(
            receivedConnectionSocket.getInputStream());
        List<Good> goods = (ArrayList<Good>) objectInputStream.readObject();

        DbWriter dbWriter = new DbWriter(goods);
        dbWriter.saveGoods();

        log.log(Level.INFO,"Работа с клиентом окончена.");
      }
    } catch (Exception e) {
     log.log(Level.ERROR,"Ошибка при обработке клиентского сообщения", e);
    }
  }
}
