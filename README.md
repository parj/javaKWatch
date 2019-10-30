[![Known Vulnerabilities](https://snyk.io/test/github/parj/javaKWatch/badge.svg)](https://snyk.io/test/github/parj/javaKWatch
) [![CircleCI](https://circleci.com/gh/parj/javaKWatch.svg?style=svg)](https://circleci.com/gh/parj/javaKWatch) ![GitHub](https://img.shields.io/github/license/parj/javaKWatch)

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fparj%2FjavaKWatch.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fparj%2FjavaKWatch?ref=badge_large)

# What is this?

This is used for watching Kubernetes deployments and emitting a web hook event. This uses the Kubernetes java client.

## How to use this
To initiate listening to the cluster - hit the end point `/k8s/watch/start`. Example - `curl http://localhost:9999/k8s/watch/start`

To add a webhook - hit the end point `/k8s/watch/add`. Two params need to be passed `name` and `url`. Example: `curl -v -X POST http://localhost:9999/k8s/watch/add\?name\=Foo\&url\=http://127.0.0.1:5000/`

Initiate a change in Kubernetes - add/change/delete and you will see the webhooks being triggered

# Pre-requisites

If you want to deploy to Kubernetes. JSONNET is required for generating the files. To install jsonnet look at here -> https://github.com/google/jsonnet

Sample kuberentes files are in the directory `kuberbetes`.

Run `git submodule update --init --recursive` to set up the kubernetes template directory.
 
# To use this application

## Pre-requisites
Either minikube (https://kubernetes.io/docs/tasks/tools/install-minikube/) or Docker for Desktop (https://www.docker.com/products/docker-desktop)

## Running locally

1. Clone the repo
1. Run `mvn spring-boot:run`

See section [how to use this](#how-to-use-this)

## Running within Kubernetes

1. Clone the repo
1. Run `mvn clean package docker:build`
1. Run `kubectl apply -f kubernetes/readpods.yml`. This create a service accaunt called `java-watch-sv`. The service account is used to `get, watch and list` pods and namesapces.
1. If you do not have jsonnet - run `kubectl apply -f values.yml`. If you do have jsonnet - run `jsonnet javakwatch.jsonnet | kubectl apply -f -`

## To generate Kuberentes files
 1. If you haven't run this -> Run `git submodule update --init --recursive` 
 1. Run `k8s-template/createTemplates.sh -f kubernetes/javakwatch.jsonnet -k`.