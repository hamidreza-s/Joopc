## Wenower Core



### OSX local installation

1. Install Protocol Buffer

```
$ brew install protobuf
```

2. Install Postgresql and `joopc` database and user

```
$ brew install postgresql
$ psql postgres
postgres=# CREATE DATABASE joopc;
postgres=# CREATE USER joopc WITH ENCRYPTED PASSWORD 'secret';
postgres=# ALTER database joopc OWNER TO joopc;
postgres=# GRANT ALL PRIVILEGES ON DATABASE joopc TO joopc;
postgres=# QUIT
$ psql joopc -U joopc
wenower=> \i ./src/main/resources/schema.sql
```

3. Compile and run tests

```
$ mvn compile
$ mvn test
```

4. Run the service
```
$ java package
$ java -jar ./target/joopc-core-1.0-SNAPSHOT-jar-with-dependencies.jar
Jan 06, 2022 3:46:46 PM com.joopc.core.App main
INFO: Server started, listening on 9090
```