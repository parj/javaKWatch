# What is this?

This is used for watching Kubernetes deployments and emitting events. This uses the Kubernetes java client.

# Pre-requisites

If you want to deploy to Kubernetes. JSONNET is required for generating the files. To install jsonnet look at here -> https://github.com/google/jsonnet

Sample kuberentes files are in the directory `kuberbetes`.

# To generate Kuberentes files
 1. clone the reposiory -> https://github.com/parj/jsonnet-service-ingress-deployment. This repo contains the master template and then 
 1. Run `createTemplates.sh -f javakwatch.jsonnet -k` in the directory `kuberenetes`.
 
 If you want to do the generation manually, download the file -> https://raw.githubusercontent.com/parj/jsonnet-service-ingress-deployment/master/service-deployment-ingress.jsonnet.template and place in the directory `kubernetes` and then run `jsonnet javakwatch.jsonnet`
 
  