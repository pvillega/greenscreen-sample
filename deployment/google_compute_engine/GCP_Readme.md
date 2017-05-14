# Deploying to Google Cloud Platform

**WARNING: UNSAFE!** The information below is provided to showcase how we can deploy the application to a given platform.
Be aware what it does is probably unsafe for a proper production application, in many cases. Double check the documentation
of your platform of choice to ensure you follow best practices to secure your application.


If you want to deploy to [Google Cloud Platform](https://console.cloud.google.com/compute/), this file contains a basic summary of steps
that allow that. Please note that these steps are just for a basic deployment to one instance, totally unsafe for production. 

Just an experiment.

## Requirements

We use [Sbt Native Packager](https://github.com/sbt/sbt-native-packager) to generate a zip file with the application ready to run.

The command to create this package is:

```
$ sbt clean universal:packageBin  
```

this creates a zip file under `target/universal/` that you can use to deploy to the server.

You also want to install [Google Could SDK](https://cloud.google.com/sdk/) to be able to follow the tutorials.

## How to deploy your application

You want to read the [tutorial](https://cloud.google.com/java/tutorials/bookshelf-on-compute-engine) for Java applications and adapt accordingly.
  
### Database

The application relies in Postgres, so we need to create a database to that end. Check the [documentation](https://cloud.google.com/sql/docs/postgres/)
for more details.

We create a PostgreSQL 9.6 instance with a micro tier so it's free. We also set the password for our user:

```
gcloud beta sql instances create mypsqlinstance --tier db-f1-micro --database-version=POSTGRES_9_6

gcloud beta sql users set-password postgres no-host --instance=mypsqlinstance --password=passw0rd
```

Please note that to be able to connect to the database, you will need to edit access controls for the database (via the UI) and 
authorise the networks that can connect ot it. This means we will need to assign a fix public IP to the server that runs the application
so we can authorise it. 

Also note that there are other ways to connect to the database, for example using proxies. Check the [documentation](https://cloud.google.com/sql/docs/postgres/) 
for more details.

To drop the database instance, losing all the data, run:

```
gcloud sql instances delete mypsqlinstance
```
 
### Upload artefact
 
Once you have created the artefact you can create a bucket (similar to Amazon S3) to copy it to. 

```
gsutil mb gs://greenscreen

gsutil defacl set public-read gs://greenscreen

``` 
 
Copy the artefact using:

```
gsutil cp target/universal/greenscreen-c7ef5a96cf1f672aa90716c8bae0291605470fd2-SNAPSHOT.zip gs://greenscreen/gce/  
```
 
### Firewalls

We want to create 2 entries in our firewall to allow access to ports 8080 and 8443. To do that execute:
 
```
gcloud compute firewall-rules create allow-http \
  --allow tcp:8080 \
  --source-ranges 0.0.0.0/0 \
  --target-tags http-server \
  --description "Allow port 8080 access to instances tagged with http-server"

gcloud compute firewall-rules create allow-https \
  --allow tcp:8443 \
  --source-ranges 0.0.0.0/0 \
  --target-tags http-server \
  --description "Allow port 8443 access to instances tagged with http-server"
``` 
  
### Startup script
 
When creating a new instance in GCP you can indicate a script in your local machine that will be executed to configure
the new instance. Please edit the script and adapt it as required. Note that it:

* installs Java 8 and some other tooling for the service
* uses a hardcoded name for the artefact uploaded to GCP that will be used for deployment
* sets up several environment variables with DB configuration the application will use to connect to the DB
* expects the path to a TLS certificate to be used by the server (you may want to upload that to GCP, too)


### Deployment 

Deployment uses a command that will instantiate the instance and then run a script to configure it. This is meant to be used
to test the instance, not for a final prod script.

```
gcloud compute instances create my-app-instance \
  --machine-type=f1-micro \
  --address 108.59.85.68 \
  --scopes="datastore,userinfo-email,logging-write,storage-full,cloud-platform" \
  --metadata-from-file startup-script=./startup.sh \
  --zone=us-central1-f \
  --tags=http-server \
  --image "ubuntu-1704-zesty-v20170413" \
  --image-project "ubuntu-os-cloud" \
  --metadata BUCKET=greenscreen
```

Please note you will need to register a fixed IP in GCP for your container and update the command above as required. Also,
make sure the path to the script is correct and it point to your local copy of `startup.sh`


## Logging

Logging in GCP relies in [Fluentd](http://www.fluentd.org/). There is a Scala [library](https://github.com/fluent/fluent-logger-scala) 
to integrate the client for your usage. GCP also provides [StackDriver](https://app.google.stackdriver.com/account/login/) which aggregates
your logs across a project and allows you to set up alerts and healthchecks for your application.

The `startup.sh` script installs a client that will integrate with Stackdriver. More information in [the documentaiton](https://cloud.google.com/monitoring/quickstart-lamp)
 
The formats GCP expect are defined at [https://cloud.google.com/error-reporting/docs/formatting-error-messages]() 
and [https://cloud.google.com/error-reporting/docs/setup/compute-engine#log_exceptions]()
 

## Betting on GCP

If you want to go this path and deploy the application on GCP you may want to familiarise yourself with creation of images in GCP
to avoid rebuilding the same each time you deploy. Please check the [documentation](https://cloud.google.com/compute/docs/how-to)

You may also want to read more about [Ansible](http://docs.ansible.com/) and their [GCP Demo](https://github.com/GoogleCloudPlatform/compute-video-demo-ansible)

Lastly, please read the [best practices](https://cloud.google.com/compute/docs/tutorials/robustsystems) documentation and ensure the system 
is secure!
