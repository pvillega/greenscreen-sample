# Deploying to Heroku

**WARNING: UNSAFE!** The information below is provided to showcase how we can deploy the application to a given platform.
Be aware what it does is probably unsafe for a proper production application, in many cases. Double check the documentation
of your platform of choice to ensure you follow best practices to secure your application.

This document describes how to deploy the application to [Heroku](https://help.heroku.com/). Note that Heroku is an ideal
environment if you don't have much experience in DevOps or you want to focus on functionality, not on sysadmin tasks. You
pay a premium for having all this abstracted from you, both in cash and in some delay getting things like Http/2, but in 
exchange you can focus on business value.

We recommend to read [Heroku's reference](https://devcenter.heroku.com/categories/reference) as it provides a lot of information
in relevant subjects like security or platform limitations. This includes explanation on how to create a new application in 
Heroku, which is not covered in this readme.

## Configuring the application for Heroku deployment

Due to the way Heroku works, there are thing to consider when building the application. This section explains common
changes that are required.

### Environment variables and Configuration

Following the [12 factor](https://12factor.net/config) approach, Heroku stores configuration as environment variables at 
the application level. This means we can use environment variable substitution to set up different environments, without
having to create independent `dev` and `prod` configurations.

See more information on how to add env variables to your nodes in [the documentation](https://devcenter.heroku.com/articles/config-vars)

### TLS

Heroku offers [free certificates](https://devcenter.heroku.com/articles/automated-certificate-management) using 
[Let's Encrypt](https://letsencrypt.org/) for paid applications. That includes managing renewal of certs. Free applications
can be accessed via https with a Heroku-owned certificate.

There is a known TLS issue with Secure WebSockets and custom signed TLS certificates in which some browsers, like Firefox,
reject these type of connections. This means we can't use TLS connections in our local computer for development.
It's common to fix this issue by providing *protocol agnostic paths* when referring to an url, that is `//path` vs 
`https://path`. The issue is that those protocols are not secure, as they allow a potential attacker to hijack connections
by using a non-TLS path in production.

To avoid this we set the paths to use for Http and Websockets in config, based on a flag `DEV_ENV` that is false by default 
and we enable specifically in the `bin/startDev.sh` script. It's a crude workaround but solves the issue.

### External Url

When creating url's for Websockets or similar protocols we need the externally accessible url of the application. This can
be provided via the env variable `EXTERNAL_URL` which defaults to `0.0.0.0` if not set.

The configuration class has a `serverPath` value which uses the external url, application port, and server context to build
the url path for external requests.

### Logging

Heroku will, by default, aggregate all logs from `Console` across all nodes of an application. To do so, our logback configuration
*must* output to console.

Please note the default Heroku log system doesn't store logs, it just aggregates logs across instances and has a tiny cache.
To store log history you need an external [logging addon](https://elements.heroku.com/addons) for your application.

For this example we integrated with [LogDNA](https://elements.heroku.com/addons/logdna)

### Database

Heroku provides [PostgreSQL](https://devcenter.heroku.com/articles/heroku-postgresql). The PostgreSQL addons adds
a set of [environment variables](https://devcenter.heroku.com/articles/heroku-postgresql#connecting-in-java) to the nodes,
which can be retrieved by the configuration files to set up your JDBC connection. 

For example to have a default `dev` configuration that can be overridden via an env variable use:

```hocon
db.url = "jdbc:postgresql://localhost:5432/greenscreen"
db.url = ${?JDBC_DATABASE_URL}
```

The key `db.url` will be overriden if we set the environment var `JDBC_DATABASE_URL`

[Doobie](https://github.com/tpolecat/doobie) works great with PostgreSQL, and requires no extra configuration.


### Database Migrations

As mentioned we use [Flyway](https://flywaydb.org/documentation/sbt/) for database migration. We run migrations on application
start to ensure the database is in the right state before starting. If a migration fails, we abort startup.

To run migrations manually against your local dev database use script `bin/psql/runMigrations.sh`

Heroku provides a [release phase](https://devcenter.heroku.com/articles/release-phase) mechanism that allows you to run
commands before deploying the application. Although this would be the ideal place to run the migrations, currently we can't
run `sbt` commands in this step, so we can't use it correctly. It also didn't stop deployment on error, so it needs more
testing before adopting.

### Metrics

We want to use application metrics to monitor application performance, detect issues, and alert on thresholds. 
  
Heroku offers several addons, like [Librato](https://elements.heroku.com/addons/librato) which provides a
[library](https://github.com/librato/metrics-librato) we can integrate with [Dropwizard's metrics](https://github.com/dropwizard/metrics)
which in turn is a metrics library supported by Http4s.

In development we provide an endpoint (`metrics.json`) that allows us to check metrics directly. We disable this in production
to avoid exposing potentially sensitive data to the internet. In prod, tools like Librato will amalgamate the data.

Heroku also provides [metrics](https://devcenter.heroku.com/articles/application-metrics-beta) for paid plans, which may be good
enough without requiring extra metrics addons.

### HealthChecks and Status page

Ideally we would like to provide a detailed [Status page format](https://github.com/pvillega/SE4/blob/master/SE4.md) complemented
by [Dropwizard Health checks](http://metrics.dropwizard.io/3.2.2/manual/healthchecks.html).

Unfortunately in environments like Heroku where you can't configure the firewalls, that would expose too much information 
to internet. So we are restricted to whatever is offered by the metrics plugin we use, which may be enough.

The application includes a `StatusService` that provides some information about the application, like version number, by 
taking advantage of [sbt-buildinfo](https://github.com/sbt/sbt-buildinfo) and [sbt-git](https://github.com/sbt/sbt-git)
to generate [information about the release](http://blog.byjean.eu/2015/07/10/painless-release-with-sbt.html).

### Feature flags
 
Something often requested is the capability of toggling a feature on or off via a flag, so new functionality can be hidden
in production until it is ready or just to allow disabling unstable components.

Thankfully using Heroku this is quite simple, as modifying an environment variable causes the nodes to restart, which means
we can just use standard configuration in `application.conf` and override the defaults with values from environment variables.

There is an example of such flags being exposed in `HelloWorldService` at `flags` endpoint.

### Local testing

Heroku CLI provides a tool to run the application [locally](https://devcenter.heroku.com/articles/heroku-local) using the 
same tooling Heroku uses, like the `Procfile`. This allows you to test the application using a replica of a Heroku 
environment, which can help finding issues with config.

Any environment variables to be set are added to file `.env` in root.

To launch the application this way, use `bin/startLocal.sh`. This will create a new deployable via `sbt stage` and run it.

### Deployment to Heroku development/staging

There is an [Sbt-Heroku](https://github.com/heroku/sbt-heroku) plugin that facilitates the process of deploying the 
application to Heroku. It depends on [sbt-native-packager](https://github.com/sbt/sbt-native-packager).

When we create a Heroku app (independent or as part of a pipeline) we get instructions to connect that app to Github or to 
push directly via the Heroku CLI. Sbt-Heroku helps in both cases to generate the proper artefact via a `stage` phase.

NOTE: when in a pipeline, make sure you don't link your repository to a production instance, only to the start of the
 pipeline!
 
If you are using the direct push via Heroku CLI, you can run:
 
```bash
$ bin/deployHeroku.sh
```

to deploy to Heroku.


## Pipelines and CI

Heroku provides a [pipelines](https://devcenter.heroku.com/articles/pipelines) mechanism that allows you to promote
applications across several environments. You can add existing apps to a pipeline (tagged as staging, prod, etc) and move
 the Heroku slug across them. Env vars are independent for each app.

A pipeline can be integrated with Github to provide automatic unit testing (basic sbt test, configurable) and 1-use instances to test 
changes from PR and master. This enables [CD](https://www.heroku.com/continuous-delivery/on-heroku) within the platform.

There are 3rd party tools that provide addons for Heroku, like Codeship. They may be worth checking as they may provide
 extended functionality.
 

## Profiling and memory issues

Heroku provides [some mechanisms](https://devcenter.heroku.com/articles/java-memory-issues) to obtian head dumps or link
visual jvm to running processes.