// Added by sbt-fresh - source code formatting - see https://github.com/olafurpg/scalafmt
addSbtPlugin("com.geirsson"      % "sbt-scalafmt" % "0.6.6")
// Added by sbt-fresh - allows running git commands from within sbt - see https://github.com/sbt/sbt-git
addSbtPlugin("com.typesafe.sbt"  % "sbt-git"      % "0.9.3")
// Added by sbt-fresh - adds license header to source files - see https://github.com/sbt/sbt-header
addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "1.8.0")

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25" // Needed by sbt-git

// Used to find outdated dependencies - See https://github.com/rtimush/sbt-updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.0")

// Faster fetching of artefacts in Sbt - See https://github.com/alexarchambault/coursier - version enforced by scalafmt (run `scalafmt` on sbt console)
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC1")

// Visualize your project's dependencies, helps with ocassional classpath issues due to project dependencies - See https://github.com/jrudolph/sbt-dependency-graph
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

// Twirl is the Play template engine, used for rendering html pages - See https://github.com/playframework/twirl
addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.3.3")

// Flyway is a tool for database migrations - See https://flywaydb.org/getstarted/firststeps/sbt
addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.1.2")

resolvers += "Flyway" at "https://flywaydb.org/repo"

// WartRemover is a scala linter - See http://www.wartremover.org/doc/install-setup.html
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.1.1")

// sbt-scoverage is a plugin for SBT that integrates the scoverage code coverage library - See https://github.com/scoverage/sbt-scoverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

// sbt-native-packager lets you build application packages in native formats - See https://github.com/sbt/sbt-native-packager
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M9")

// sbt-docker let's you create docker images for your application. Requires sbt-native-package - See https://github.com/marcuslonnberg/sbt-docker
// Commented as we don't deploy to GKE by now
//addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.4.1")

// sbt-heroku facilitates deploying apps to Heroku - See https://github.com/heroku/sbt-heroku
addSbtPlugin("com.heroku" % "sbt-heroku" % "1.0.0")

// sbt-dependency-check plugin allows projects to monitor dependent libraries for known, published vulnerabilities (e.g. CVEs) - See https://github.com/albuch/sbt-dependency-check
addSbtPlugin("net.vonbuchholtz" % "sbt-dependency-check" % "0.1.7")

// sbt-errors-summary sumamrises all errors in sbt in a nicer interface - See https://github.com/Duhemm/sbt-errors-summary
resolvers += Resolver.bintrayIvyRepo("duhemm", "sbt-plugins")
addSbtPlugin("org.duhemm" % "sbt-errors-summary" % "0.2.0")

// sbt-buildinfo generates Scala source from your build definitions - See https://github.com/sbt/sbt-buildinfo
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")