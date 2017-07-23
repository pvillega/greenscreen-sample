# Green Screen 

Welcome to Green Screen! At its core, Green Screen is a lab project, that is, a place where you can test new 
libraries and techniques. 

The excuse for the existence of this project is to bring back to life the user experience of green screens within 
the context of modern web frameworks.


## Starting the app

The app relies on [Postgres](https://www.postgresql.org/) as the database. You can set up one local instance or, recommended,
use [Docker](https://www.docker.com/) and run:

```bash
$ bin/psql/startPostgresDocker.sh
```

You can use the script `bin/startDev.sh` to launch the application.

Alternatively, within `sbt` console execute:

```bash
[greenscreen]> run
```

App starts by default on port `8080`, and it's accessible via `http://127.0.0.1:8080/greenscreen/<path>`

## Tooling

See file [Tools.md](./Tools.md) for more information on libraries and other tooling used in the application

## Model

See file [Model.md](./Model.md) for more information on the internal structure of the application


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
