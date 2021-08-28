package com.wenower.core.logics;

import com.wenower.core.Database;
import com.wenower.core.db.tables.records.UsersRecord;
import java.sql.SQLException;
import org.jooq.TruncateIdentityStep;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.wenower.core.db.tables.Users;
public class UserLogicTest {

  @BeforeAll
  static void cleanDatabase() throws SQLException {
    Database.getContext().truncate(Users.USERS).execute();
  }

  @Test
  @DisplayName("Check user creation process")
  void userCreationTest() throws SQLException {
    var createdUser = UserLogic.create("00981111111111");
    var fetchedUser = UserLogic.fetch(createdUser.getId());
    assertEquals(createdUser.getId(), fetchedUser.getId());
  }

}
