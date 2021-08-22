package com.wenower.core.logics;

import com.wenower.core.Database;
import com.wenower.core.db.tables.Users;
import java.sql.SQLException;
import java.util.UUID;
import org.jooq.Record;
import org.jooq.Result;

public class UserLogic {

  public static Result<Record> select(String id) throws SQLException {
    return Database.getContext().select().from(Users.USERS).where(Users.USERS.ID.eq(id)).fetch();
  }

  public static void delete(String id) throws SQLException {
    Database.getContext().deleteFrom(Users.USERS).where(Users.USERS.ID.eq(id))
        .execute();
  }

  public static void create(String phone) throws SQLException {
    var user = Database.getContext().newRecord(Users.USERS);
    user.setId(UUID.randomUUID().toString());
    user.setPhone(phone);
    user.store();
  }
}
