# Model

This file describes the internal model of the application as well as choices made at some points.

## QandA

The questionnaire part is inspired by code of Channing Walton's project [Qanda](https://github.com/channingwalton/qanda).
We used the Shapeless implementation as reference.

## Algebras

Package `algebras` defines several algebras for the application, using [FreeStyle](http://frees.io). 
You can consider an algebra to be a connection point to some side-effect or external domain. Often, that will be a 
subsystem like a database or an API endpoint. But it could be a different bounded context, if using DDD.

### Storage

Package `algebra` contains algebra `Storage` which manages storage of the questionnaire to a database

We provide caching via in-memory [cache](http://frees.io/docs/effects/Cache/) integration from FreeStyle. This can be 
replaced by Redis-backep cache if needed, which an integration provided by FreeStyle itself. This can be seen defined
int he main `App` module


## Modules

Package `modules` defines several Modules for the application, using [FreeStyle](http://frees.io). A module groups
several algebras together so that we can use them to build programs.

The main `App` module includes FreeStyle Reader effect, Error effect, and Cache integration, to facilitate building
programs with these effects.

## Programs

Package `programs` contains all programs defined in the application. Currently a very basic set.

A `program` is the business logic of the application. For example, in an old-style application you would have a login method
that connect to a database, validates the login details, and returns a user. In the new style, a program would declare these
steps, of which connecting to the db will be done via an algebra and validation will be within the program itself.

This way we can test business logic with an interpreter that stubs the algebras that have high cost (db, 3rd party endpoints)
and use another interpreter to run the software in production environments.

## Handlers/Interpreters

Handlers (interpreters of programs in FreeStyle) are the way you change the behaviour of your program at runtime.

Be aware that when you run a program you must have an interpreter in scope for all your algebras, plus all the effects and patterns
used in your `module`. For example, if you use `cache` and `logging` you need interpreters for them, too! Otherwise compilation
will fail.
 
`SpecTrait` provides some default interpreters to use on tests. Override as required in your own tests.