package com.joopc.core.logics;

import com.joopc.core.Database;
import com.joopc.core.db.tables.Users;
import com.joopc.core.db.tables.records.UsersRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserLogicTest {

    @BeforeAll
    static void cleanDatabase() {
        Database.getContext().truncate(Users.USERS).execute();
    }

    @Test
    @DisplayName("Check user creation process")
    void userCreationTest() throws SQLException {
        var createdUser = UserLogic.create("00981111111111");
        var fetchedUser = UserLogic.fetch(createdUser.getId());
        assertTrue(fetchedUser.isPresent());
        assertEquals(
                createdUser.getId(),
                fetchedUser.map(UsersRecord::getId).orElse("")
        );
    }

}
