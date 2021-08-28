package com.wenower.core.logics;

import com.wenower.core.Database;
import com.wenower.core.db.tables.Users;
import com.wenower.core.db.tables.records.UsersRecord;
import java.sql.SQLException;
import java.util.UUID;

public class UserLogic {

  public static UsersRecord fetch(String id) throws SQLException {
    return Database.getContext().select().from(Users.USERS).where(Users.USERS.ID.eq(id))
        .fetchOneInto(UsersRecord.class);
  }

  public static void delete(String id) throws SQLException {
    Database.getContext().deleteFrom(Users.USERS).where(Users.USERS.ID.eq(id))
        .execute();
  }

  public static UsersRecord create(String phone) throws SQLException {
    var user = Database.getContext().newRecord(Users.USERS);
    user.setId(UUID.randomUUID().toString());
    user.setPhone(phone);
    user.store();
    return user;
  }
}
