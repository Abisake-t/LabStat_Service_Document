# Service_Document

Document storage microservice

## Liquibase

### Generating the Initial Database Schema

Liquibase provides database schema versioning via scripts that are stored in the project and applied in sequence on
startup. It is configured to be run via Spring Boot. For initial database schema initialization, running the
follow command is necessary. This is unlikely something one will need to do as it should have already been generated at
this point but still good to have noted and known.

*Note:* For some reason this only works in Command Prompt and not Powershell. I have not tested it on macOS or Linux.

```
mvn liquibase:generateChangeLog -Dliquibase.url=jdbc:postgresql://localhost:5432/document -Dliquibase.username=<username> -Dliquibase.password=<password> -Dliquibase.referenceUrl=hibernate:spring:com.scube.ai.database.entity?dialect=org.hibernate.dialect.PostgreSQLDialect -Dliquibase.outputChangeLogFile=test.xml
```

With that in place, scripts to update the database can be places in `src/main/resources/db/changelog/scripts` to be
executed and applied to the database on application startup (aka automatically on deployment). Head on over to
the [documentation](https://docs.liquibase.com/concepts/introduction-to-liquibase.html) for more information on using
Liquibase.

### Updating the Database Schema

Liquibase recommends creating the changelog scripts manually. However, if you are lazy and want to generate
automatically, you can follow the below to do that. I couldn't find a better way to do this but if I do find one, we'll
update this file.

- Step 1: create a new database called `document2` in the postgres server
- Step 2: update `spring.jpa.hibernate.ddl-auto` to `update` in `application.yml` file.
- Step 3: update the `spring.liquibase.enabled` to `false` in `application.yml` file.
- Step 4: update the `spring.datasource.url` to `jdbc:postgresql://localhost:5432/document2` in `application.yml`
  file.
- Step 5: run the application. This will create the database schema in the `document2` database.
- Step 6: run the below command to generate the changelog file.
- Step 7: you should now have a new changelog file in `src/main/resources/db/changelog/scripts` directory. Verify that
  the file is not doing something unexpected.
- Step 8: update hte `db.changelog-master.xml` file to include the new changelog file.
- Step 9: revert the changes made in steps 2, 3, and 4.

```
mvn liquibase:diff -Dliquibase.url=jdbc:postgresql://localhost:5432/document -Dliquibase.username=<username> -Dliquibase.password=<password> -Dliquibase.referenceUrl=jdbc:postgresql://localhost:5432/document2 -Dliquibase.referenceUsername=<username> -Dliquibase.referencePassword=<password> -Dliquibase.changeLogFile=src\main\resources\db\changelog\db.changelog-master.xml -Dliquibase.diffChangeLogFile=src\main\resources\db\changelog\scripts\test.xml
```

# Permissions

https://scubeenterprise.atlassian.net/wiki/spaces/SD/pages/71073793/Adding+Keycloak+Permission