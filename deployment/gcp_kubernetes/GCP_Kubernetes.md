# Deploying to Kubernetes in GCP

**WARNING: UNSAFE!** The information below is provided to showcase how we can deploy the application to a given platform.
Be aware what it does is probably unsafe for a proper production application, in many cases. Double check the documentation
of your platform of choice to ensure you follow best practices to secure your application.

If you want to deploy to [Kubernetes in Google Cloud Platform](https://console.cloud.google.com/kubernetes/list), this document 
contains steps to achieve that. Please note that these steps are just for a basic deployment, without many of the guarantees 
(redundancy, backups, etc) you would expect in production


## Requirements

We use [Sbt Native Packager](https://github.com/sbt/sbt-native-packager) and [Sbt Docker](https://github.com/marcuslonnberg/sbt-docker) 
to generate docker files with the application ready to run.

The command to create a docker image is:

```
$ sbt clean docker  
```

this creates a new image in your local docker repository. For more information on how to write good docker files we recommend
[this article](https://rock-it.pl/how-to-write-excellent-dockerfiles/)

You also want to install [Google Could SDK](https://cloud.google.com/sdk/) to be able to follow the tutorials. You will need to run
the following commands to set up the SDK:

```
# Installs kubernetes console module
$ gcloud components install kubectl
  
# Set region and your project as default params. Chose your preferred zone
$ gcloud config set compute/zone us-central1-b
$ gcloud config set project PROJECT_ID
$ gcloud config list
    
# Authenticate
$ gcloud auth application-default login

```

## Becoming familiar with Kubernetes in GCP

The best way to become familiar with Kubernetes in GCP is to follow their [tutorials](https://cloud.google.com/container-engine/docs/tutorials).

Start with their basic [sample app](https://cloud.google.com/container-engine/docs/tutorials/hello-node) to understand Kubernetes concepts.
Then proceed with the following tutorials, you won't spend more than a couple of hours in total and it will save you from basic errors later on.

## How to deploy your application

This section covers several ways to deploy the application. They are incremental steps, with comments on relevant info found during the steps.

### Running the application locally via the docker image 

We already have a script that runs a default [PostgreSQL](https://www.postgresql.org/) image, located at `bin/psql/setupPostgresDocker.sh`.
The script mounts a local volume (so data is not lost if container is shut down) and runs the database in port 5432 with user 'postgres' and 
password 'passw0rd'.

The script also creates a new local docker network, called 'local-docker', and runs Postgres over that network. This is necessary to be able
to access the database from another container, without a custom network the other container won't see the POstgres database, even with the 
port exposed. You can read more about this in the [docker network documentation](https://docs.docker.com/engine/userguide/networking/work-with-networks/).

With Postgres running, you can create a docker image of the application by leveraging [Sbt Docker](https://github.com/marcuslonnberg/sbt-docker):

```
$ sbt clean docker
```
 
This will create an image `com.aracon/greenscreen` based on configuration in `build.sbt`. Please note that as of writing this document the script
contains configuration that sets some environment variables like `JDBC_DATABASE_URL` to point the application to the correct database or to load
the correct config. This may change slightly as code evolves.

To execute the application, run:

```
$ docker run -p 8080:8080 --network=local-docker com.aracon/greenscreen
```

and connect to [http://localhost:8080/greenscreen/](http://localhost:8080/greenscreen/)

Another comment about the docker image: we are using the `AshScriptPlugin` plugin of Sbt Docker, and we provide a `ash-template` template to 
override the default one. The reason is that the original template is not passing `JAVA_OPTS` and we need that variable to pass certain configuration
keys, like the one that allows the application to decide which configuration file to load initially. 

### Running the application in GCP using our docker images and Kubernetes descriptor files 
  
This step allows us to run the application in Kubernetes, using our custom docker image, and hardcoded Kubernetes descriptors. 

First of all, a warning: this uses a container to run Postgres within Kubernetes itself. Unfortunately Kubernetes (v 1.5) doesn't allow
several pods to share a volume. This means we can only deploy postgres as a master-slave which may have downtime while a master pod is 
recreated. It is probably better to use Cloud SQL for database purposes as it is better suited for the task.
     
   
We will use [Google Container Registry](https://cloud.google.com/container-registry/docs/) to store our docker images. This means tagging our
local images and then pushing them (check your project ID first!):
   
```
# Tag and upload postgres to USA servers
$ docker tag postgres:9.6.2 us.gcr.io/PROJECT_ID/postgres962
$ gcloud docker -- push us.gcr.io/PROJECT_ID/postgres962
        
# Tag and upload greenscreen app to USA servers
$ docker tag com.aracon/greenscreen us.gcr.io/PROJECT_ID/server
$ gcloud docker -- push us.gcr.io/PROJECT_ID/server
```     

NOTE: for this exercise, make sure your app references a database at `postgres-master:5432` in its config as otherwise it won't connect.

Then you can configure your Kubernetes cluster for deployment:

```
# Create a cluster with a few nodes
$ gcloud container clusters create greenscreen --num-nodes=3
      
# See info about the cluster
$ gcloud container clusters list
$ gcloud container clusters describe greenscreen
         
# Create a persistent disk for postgresql db
gcloud compute disks create --size 200GB postgres-disk
      
# Create a secret in kubernetes to store the database password
$ kubectl create secret generic postgres --from-literal=password=passw0rd
```

With these steps done we have a cluster, a volume to store the database data, and secure configuration for the database password.
The next step is to do the deployment. For this we use the scripts at `deployment/gcp_kubernetes/basic_kubernetes` folder. Read them
and update the PROJECT_ID as needed before executing them:
 
```
# Deploy instance to a pod and check process in logs 
$ kubectl create -f postgres-master-deployment.yaml
$ kubectl get pod -l app=postgres
$ kubectl logs -f POD_NAME
  
# Set a service to run postgres so the port is accessible by other pods      
$ kubectl create -f postgres-master-service.yaml
$ kubectl get service

# Deploy ohur app      
$ kubectl create -f greenscreen-deployment.yaml
$ kubectl get pod -l app=greenscreen
$ kubectl logs -f POD_NAME

# Set a service to run the app. The service is a Load_Balancer service so it provides external IP access
$ kubectl create -f greenscreen-service.yaml
 
# You can find the external ip with the `get` command and access teh app on port 8080
$ kubectl get service

# To delete a deployment (service) run:  
$ kubectl delete deployment <name>
```      



TBD

3b) Add https://github.com/albuch/sbt-dependency-check for security! force trigger on compile and add to readme note about plugin

4) use ansible for deployment
4a) Enable CI via drone.io to deploy to kubernetes
  -- how to create different docker instances for different environments in CI? create for uat with one config, prod another
  -- needs sbt to read some environment variable to set the docker file params appropiately (env values)
  -- or kubernetes config prepares the env variables docker will use and we do nothing in sbt
      http://terrenceryan.com/blog/index.php/kubernetes-secrets-directly-to-environment-variables/

--> do commit to tracer bullet for deployment to kubernetes, we need to configure target server to be something else 
than GPC

4b) user letsencrypt and custom domain for url!
5) replace postgresql db from container to GCP managed postgres and enable backups
6) other security features for kubernetes?
7) Other greenscreen stuff
8) websockets? https://medium.com/google-cloud/deploying-websockets-cluster-to-gcp-with-lets-encrypt-certificates-5ebb7fc1e245

set domain & get free ssl cert (let's encrypt)
  ???

Logging and monitoring?
  ???

Docker image advise
  https://rock-it.pl/how-to-write-excellent-dockerfiles/

Docker + ansible
https://docs.docker.com/engine/admin/ansible/#installation


Ansible + kubernetes??
 ???

Env variables in Kubernetes?
 ???

Kubernetes and postgresql
 http://blog.kubernetes.io/2017/02/postgresql-clusters-kubernetes-statefulsets.html
 https://github.com/CrunchyData/crunchy-containers


Preemptible machines for nodes?
 https://cloud.google.com/preemptible-vms/


Q: How to secure, etc Kubernetes? 
  https://kubernetes.io/docs/concepts/configuration/secret/

Q: how to do red/green deployments in kubernetes to avoid issues

Q: set up CI with drone.io