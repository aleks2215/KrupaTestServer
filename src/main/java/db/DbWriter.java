package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import model.Good;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class DbWriter {

  private static Logger log = Logger.getLogger(DbWriter.class.getName());
  private List<Good> goods;

  public DbWriter(List<Good> goods) {
    this.goods = goods;
  }

  public void saveGoods() {
    log.log(Level.INFO,"Подключение к базе данных...");
    try (Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:test", "sa", "");
        Statement statement = connection.createStatement()) {
      log.log(Level.INFO,"Подключение к базе установлено");

      createTable(statement);
      insertGoods(statement);
      showGoodsFromDb(statement);

    } catch (SQLException e) {
      log.log(Level.ERROR, "Ошибка при подключении к базе данных", e);
    }
  }

  private void createTable(Statement statement) {
    try {
      String sqlStr = "DROP TABLE IF EXISTS goods CREATE TABLE goods " +
          "(code INTEGER not NULL, " +
          " title VARCHAR(255), " +
          " PRIMARY KEY (code))";

      statement.executeQuery(sqlStr);
      log.log(Level.INFO,"Таблица goods успешно создана");
    } catch (SQLException e) {
      log.log(Level.ERROR, "Ошибка при создании таблицы goods", e);
    }
  }

  private void insertGoods(Statement statement) {
    try {
      for (Good good : goods) {
        String sqlStr = "INSERT INTO goods VALUES (" + good.getCode() + ",'" + good.getTitle() + "')";
        statement.executeUpdate(sqlStr);
      }
      log.log(Level.INFO,"Данные из xml успешно загружены в таблицу goods");
    } catch (SQLException e) {
      log.log(Level.ERROR, "Ошибка при создании таблицы goods", e);
    }
  }

  private void showGoodsFromDb(Statement statement) {
    try {
      System.out.println("Выгрузка данных о товарах из базы данных:");

      String sqlStr = "SELECT * FROM goods";
      ResultSet result = statement.executeQuery(sqlStr);

      while (result.next()) {
        int code = result.getInt(1);
        String title = result.getString(2);
        System.out.printf("Good (code=%d, title=%s)%n", code, title);
      }
      log.log(Level.INFO,"Данные о товарах успешно выгружены из базы данных");
    } catch (SQLException e) {
      log.log(Level.ERROR, "Ошибка при выгрузке товаров из базы данных", e);
    }
  }

}
