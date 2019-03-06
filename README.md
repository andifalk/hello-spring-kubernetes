# Hello Spring Kubernetes

This is a demo for using [spring cloud kubernetes](https://spring.io/projects/spring-cloud-kubernetes) to map [Kubernetes](https://kubernetes.io) ConfigMaps and Secrets to Spring property sources.

## Demo application

It contains a simple demo spring boot web application that prints out a hello world message that can be configured via a [Kubernetes ConfigMap](https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/). The ConfigMap is read using the Kubernetes client API.

The config map defines these properties:

* hello.message=k8s
* hello.prefix=Hi

As every web application must be secure all endpoints require authentication.
The user credentials are configured using [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/) mounted as volumes. This is recommended in favour of environment variables as these can easily be leaked into logs or via ```kubectl describe pod``` command.

You may read the secrets using the Kubernetes client API as well. But this is not recommended as you have to give
you deployed application rights to read all secrets in same namespace which might be a high security risk.

The user credentials are configuraed via these properties:

* user.username
* user.password
* admin.username
* admin.password

The contained demo spring boot application exposes a simple RESTful API:

* http://localhost:8080 - GET a configured hello world message
* http://localhost:8080 - POST with a custom message in request body
* http://localhost:8080/actuator - Actuator endpoints for monitoring

## Deploy the application

The corresponding container image for this application is already 
published on docker hub.
You can pull it from there using:

```
docker pull andifalk/hello-spring-kubernetes:latest
```

To deploy the container image to a Kubernetes cluster you first have to create the corresponding ConfigMap and Secrets:

```
kubectl create -f k8s/configmap.yaml
kubectl create -f k8s/secrets.yaml
```

When using the spring cloud kubernetes project in a Kubernetes cluster with RBAC enabled it requires configuring a service account for the application. Otherwise the application cannot access the ConfigMaps.
The following commands create the required role for read access to Pods, Services, ConfigMaps, Namespaces and Endpoints. A service account is created (is used by the later app deployment manifest). Finally a role binding between the service account and the role for read access is created.

```
kubectl create -f k8s/read-access-role.yaml
kubectl create -f k8s/serviceaccount.yaml
kubectl create -f k8s/rolebinding-read-access.yaml
```

Now we are set up to deploy our spring boot application using:

```
kubectl create -f k8s/deployment.yaml
```

Please note that the deployment also enforces a pod context security policy that prevents root priviliges on the pod for the docker container.

```
...
securityContext:
    allowPrivilegeEscalation: false
    runAsNonRoot: true 
...    
```            

To access the deployed web application you need to expose it as a service or use port forwarding to the Pod like this:

```
kubectl port-forward pod/[Pod name] 8080:8080
```

You have to repace [Pod name] with your concrete Pod name. You can get the name using:

```
kubectl get pods
```


