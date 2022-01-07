package com.joopc.core.logics;

import com.joopc.core.Database;
import com.joopc.core.db.tables.Users;
import com.joopc.core.db.tables.records.UsersRecord;
import org.jooq.DSLContext;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserLogic {

    private static Optional<DSLContext> getContext() {
        try {
            return Optional.of(Database.getContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static Optional<UsersRecord> fetch(String id) {
        return getContext().flatMap(ctx -> ctx
                .select()
                .from(Users.USERS)
                .where(Users.USERS.ID.eq(id))
                .fetchOptionalInto(UsersRecord.class)
        );
    }

    public static Optional<UsersRecord> fetch(String username, String password) {
        return getContext().flatMap(ctx -> ctx
                .select()
                .from(Users.USERS)
                .where(Users.USERS.USERNAME.eq(username))
                .and(Users.USERS.PASSWORD.eq(password))
                .fetchOptionalInto(UsersRecord.class)
        );
    }

    public static void delete(String id) {
        getContext().map(ctx -> ctx.deleteFrom(Users.USERS).where(Users.USERS.ID.eq(id)).execute());
    }

    public static Optional<UsersRecord> create(String phone) {
        return getContext().map(ctx -> {
                    var user = ctx.newRecord(Users.USERS);
                    user.setId(UUID.randomUUID().toString());
                    user.setUsername(UUID.randomUUID().toString());
                    user.setPassword(UUID.randomUUID().toString());
                    user.setPhone(phone);
                    user.store();
                    return user;
                }
        );
    }
}
