#!/bin/bash

cd ../gateway
echo "Running Maven clean package..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Maven build failed. Exiting."
    exit 1
fi

echo "Building Docker image..."
docker build -t gateway:0.0.1 .


if [ $? -ne 0 ]; then
    echo "Docker build failed. Exiting."
    exit 1
fi

cd ../k8s

echo "Applying Kubernetes deployment..."
kubectl apply -f postgres-deployment.yaml
kubectl apply -f rabbitmq-deployment.yaml
kubectl apply -f redis-deployment.yaml
kubectl apply -f gateway-deployment.yaml

if [ $? -ne 0 ]; then
    echo "Kubernetes deployment failed. Exiting."
    exit 1
fi

echo "Waiting for the pod to be up and running..."
kubectl rollout status deployment/gateway-deployment

echo "Deployment completed successfully!"