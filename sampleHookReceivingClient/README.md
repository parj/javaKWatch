# What is this?

Simple go client to listen to a receive hook events

# How to use this

Steps
1. Build the docker container - `docker build -t parjanya/simplego .`
1. Upload to Kubernetes - `kubectl apply -f values.yml`
1. Ensure javaKWatch is running in K8S.
1. Add the hook end point `curl -X POST 'http://{YOUR_KUBERNETES_INGRESS_IP}/k8s/watch/add?name=Python&url=http://simpleserver-service.default:9090/listen/hook' `
1. Start the listener `curl -X GET http://{YOUR_KUBERNETES_INGRESS_IP}/k8s/watch/start `