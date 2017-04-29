// Added by sbt-fresh - source code formatting - see https://github.com/olafurpg/scalafmt
addSbtPlugin("com.geirsson"      % "sbt-scalafmt" % "0.6.6")
// Added by sbt-fresh - allows running git commands from within sbt - see https://github.com/sbt/sbt-git
addSbtPlugin("com.typesafe.sbt"  % "sbt-git"      % "0.9.2")
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
addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.3.0")

// Flyway is a tool for database migrations - See https://flywaydb.org/getstarted/firststeps/sbt
addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.1.2")

resolvers += "Flyway" at "https://flywaydb.org/repo"

// WartRemover is a scala linter - See http://www.wartremover.org/doc/install-setup.html
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.0.3")

// sbt-scoverage is a plugin for SBT that integrates the scoverage code coverage library - See https://github.com/scoverage/sbt-scoverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")

// sbt-native-packager lets you build application packages in native formats - See https://github.com/sbt/sbt-native-packager
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M8")