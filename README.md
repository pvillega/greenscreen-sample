# Green Screen 

Welcome to Green Screen! At its core, Green Screen is a lab project, that is, a place where you can test new 
libraries and techniques. 

The excuse for the existence of this project is to bring back to life the user experience of green screens within 
the context of modern web frameworks.


## Starting the app

You can use the script `bin/startDev.sh` to launch the application.

Alternatively, within `sbt` console execute:

```bash
[greenscreen]> run
```

App starts by default on port `8080`, and it's accessible via `http://127.0.0.1:8080/greenscreen/<path>`

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

## Database

### H2 and Postgres

The project is configured to use 2 databases:

* H2 in development, using a physical file (not in-memory), which can be found at `./devDB.h2db.mv.db` 
* PostgreSQL in production

We use H2 in development to avoid the need of setting a local Postgres. Of course, that can be done if desired.
We use a physical file instead of in-memory so we don't have to apply db migrations on each restart, as they can be slow.

### Doobie

We use [Doobie](https://github.com/tpolecat/doobie) as the JDBC layer. The Transactor is using the default `IOLite` monad
for IO operations. This may need to be changed later on if performance becomes a concern.

We also included some implicits on the main package object for `greenscreen` which allow Doobie to use Refined types. This
should become part of Doobie in 0.4.2/0.4.3, but in the meantime we need it.

### DB Migrations

We use [FlyWay](https://flywaydb.org/getstarted/why) to manage database migrations.


## Server stuff

## Metrics and health checks

We use Dropwizard metrics for [metrics](http://metrics.dropwizard.io/3.2.0/manual/core.html) and [health checks](http://metrics.dropwizard.io/3.2.0/manual/healthchecks.html)

You can access the metrics at `http://127.0.0.1:8080/greenscreen/metrics`

## Websockets and TLS

Unfortunately Firefox doesn't allow you to connect to websockets that use a self-signed certificate, even if you added
an exception for the Certificate for normal Https requests. This means that we need to run the server without TLS in dev 
mode, and enable TLS for prod. 

## Http/2

Java 8 doesn't have the TLS keys required for Http/2. For that reason we need an external library, [ALPN](https://eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-versions). 
Be aware the ALPN version is dependant on the JVM version so you may need to keep this up to date, manually,
until Java 9 is released.

ALPN dependency is managed in `build.sbt` and there is a piece of code for `javaOptions` to load the file. 


## Deployment

TODO - GCP

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with
any pull requests, please state that the contribution is your original work and that you license
the work to the project under the project's open source license. Whether or not you state this
explicitly, by submitting any copyrighted material via pull request, email, or other means you
agree to license the material under the project's open source license and warrant that you have the
legal authority to do so.

## License ##

This code is open source software licensed under the
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0) license.
