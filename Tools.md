# Tools used

This file describe libraries and other tools used in the project

## Sbt plugins

Some relevant plugins. Please check `plugins.sbt` for a comprehensive list along links to their websites.

### Coursier

This project uses [Coursier](https://github.com/alexarchambault/coursier) to fetch dependencies. Unfortunately [Scala-Fmt]()

### WartRemover

[WartRemover](http://www.wartremover.org/doc/install-setup.html) is a Scala linter that will make the compile step fail if
we are using *bad code*. To understand what we mean by *bad code*, we are talking about things like using `null` or throwing
exceptions. This will help keep a higher quality standard.

### SCoverage

[Sbt-Scoverage](https://github.com/scoverage/sbt-scoverage) is a Sbt plugin that generates test coverage reports for your project.
You can generate a report via `bin/checkCoverage.sh`. The build is configured to fail if coverage is checked and it goes below
a certain ratio (currently `80%`). This can be used as a build step in CI, before packaging/deployment, to ensure the code is
properly tested.

We don't enable coverage by default in our build, but require the above script to run it, as to avoid instrumentation impacting
the packaged binaries.

### Native packager

[Sbt Native Packager](https://github.com/sbt/sbt-native-packager) provides tooling to create deployables for many release environments.
We currently use it to generate a simple zip package, but it includes support for Docker, Heroku, and many other platforms.

### OWASP Dependency Check
 
[OWASP DependencyCheck library](https://github.com/albuch/sbt-dependency-check) for Scala allows projects to monitor dependent libraries
for known, published vulnerabilities (e.g. CVEs).


## Relevant libraries

Besides [Cats](http://typelevel.org/cats/) and similar [Typelevel libraries](http://typelevel.org/projects/), we use the following

### Http4s

The server we use for the application is [Http4s](http://http4s.org/)

### Pureconfig and Refined

We use [PureConfig](https://github.com/melrief/pureconfig) to load our configuration. There is a test, `ConfigSpec`, that 
ensures our configuration files are valid.

We also use [Refined](https://github.com/fthomas/refined) to enforce constraints for types, initially on the configuration 
we load but we will expand to other
areas of the application.

### FreeStyle

[FreeStyle](http://frees.io/) is a 47 Degrees library/framework to build applications following FP principles. It facilitates
building an application based on Free Monads and provides integrations with libraries like Cats and Http4s.

## Database

### Postgres

The project is configured to use [Postgres](https://www.postgresql.org/).

You can set up one local instance or if you use [Docker](https://www.docker.com/) the script `bin/psql/startPostgresDocker.sh`
can set up one container running postgres for you.

Folder `bin/psql/` includes some utility scripts to help removing unused docker volumes

### Doobie

We use [Doobie](https://github.com/tpolecat/doobie) as the JDBC layer. The Transactor is using the default `IOLite` monad
for IO operations. This may need to be changed later on if performance becomes a concern.

We also included some implicits on the main package object for `greenscreen` which allow Doobie to use Refined types. This
should become part of Doobie in 0.4.2/0.4.3, but in the meantime we need it.

### DB Migrations

We use [FlyWay](https://flywaydb.org/getstarted/why) to manage database migrations.

To run migrations locally against your local dev database use script `bin/psql/runMigrations.sh`

## Server stuff

### Metrics and health checks

We use Dropwizard metrics for [metrics](http://metrics.dropwizard.io/3.2.0/manual/core.html) and [health checks](http://metrics.dropwizard.io/3.2.0/manual/healthchecks.html)

You can access the metrics at `http://127.0.0.1:8080/greenscreen/metrics`

### Websockets and TLS

Unfortunately Firefox doesn't allow you to connect to websockets that use a self-signed certificate, even if you added
an exception for the Certificate for normal Https requests. This means that we need to run the server without TLS in dev 
mode, and enable TLS for prod. 

### Http/2

Java 8 doesn't have the TLS keys required for Http/2. For that reason we need an external library, [ALPN](https://eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-versions). 
Be aware the ALPN version is dependant on the JVM version so you may need to keep this up to date, manually,
until Java 9 is released.

ALPN dependency is managed in `build.sbt` and there is a piece of code for `javaOptions` to load the file. 


## Deployment

**WARNING: UNSAFE!** The information below is provided to showcase how we can deploy the application to a given platform.
Be aware what it does is probably unsafe for a proper production application, in many cases. Double check the documentation
of your platform of choice to ensure you follow best practices to secure your application.

###  Deploying To Google Cloud Platform

Please see [GCP_Readme](deployment/google_compute_engine/GCP_Readme.md) on `deployment` folder for more information

###  Deploying To Google Container Platform

Please see [Kubernetes_Readme](deployment/gcp_kubernetes/GCP_Kubernetes.md) on `deployment` folder for more information

Includes instructions on how to setup [Concourse](https://concourse.ci/index.html) for CI tasks

###  Deploying To Heroku

Please see [Heroku's Readme](deployment/heroku/Heroku.md) on `deployment` folder for more information

NOTE: As of 27th May 2017 Heroku doesn't support Http/2 yet. 