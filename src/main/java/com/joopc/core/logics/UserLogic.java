package com.joopc.core.logics;

import com.joopc.core.Database;
import com.joopc.core.db.tables.Users;
import com.joopc.core.db.tables.records.UsersRecord;

import java.util.Optional;
import java.util.UUID;

public class UserLogic {

    public static Optional<UsersRecord> fetch(String id) {
        return Database.getContext()
                .select()
                .from(Users.USERS)
                .where(Users.USERS.ID.eq(id))
                .fetchOptionalInto(UsersRecord.class);
    }

    public static Optional<UsersRecord> fetch(String username, String password) {
        return Database.getContext()
                .select()
                .from(Users.USERS)
                .where(Users.USERS.USERNAME.eq(username))
                .and(Users.USERS.PASSWORD.eq(password))
                .fetchOptionalInto(UsersRecord.class);
    }

    public static void delete(String id) {
        Database.getContext().deleteFrom(Users.USERS).where(Users.USERS.ID.eq(id)).execute();
    }

    public static UsersRecord create(String phone) {
        var user = Database.getContext().newRecord(Users.USERS);
        user.setId(UUID.randomUUID().toString());
        user.setUsername(UUID.randomUUID().toString());
        user.setPassword(UUID.randomUUID().toString());
        user.setPhone(phone);
        user.store();
        return user;
    }
}
