# Release - currently disabled as it can't run sbt commands - See https://devcenter.heroku.com/articles/release-phase
# release: sbt -Dflyway.url=$JDBC_DATABASE_URL flywayMigrate

# Procfile - See https://devcenter.heroku.com/articles/procfile
# JAVA_OPTS - See https://devcenter.heroku.com/articles/java-support for Heroku limitations imposed on Heroku processes
web: target/universal/stage/bin/greenscreen -Dhttp.port=$PORT


