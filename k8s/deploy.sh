#!/bin/bash

cd ../common
echo "Running Common Maven clean package..."
mvn clean package -DskipTests -q

cd ../gateway
echo "Running Gateway Maven clean package..."
mvn clean package -DskipTests -q

if [ $? -ne 0 ]; then
    echo "Maven build failed. Exiting."
    exit 1
fi

echo "Building Gateway Docker image..."
docker build -t gateway:0.0.1 .


if [ $? -ne 0 ]; then
    echo "Docker build failed. Exiting."
    exit 1
fi

cd ../reservation
echo "Running Reservation Maven clean package..."
mvn clean package -DskipTests -q

if [ $? -ne 0 ]; then
    echo "Maven build failed. Exiting."
    exit 1
fi

echo "Building Reservation Docker image..."
docker build -t reservation:0.0.1 .

cd ../k8s
echo "Removing existing Kubernetes deployments..."
kubectl delete -f postgres-deployment.yaml
kubectl delete -f rabbitmq-deployment.yaml
kubectl delete -f redis-deployment.yaml
kubectl delete -f gateway-deployment.yaml
kubectl delete -f reservation-deployment.yaml

echo "Applying Kubernetes deployment..."
kubectl apply -f postgres-deployment.yaml
kubectl apply -f rabbitmq-deployment.yaml
kubectl apply -f redis-deployment.yaml
kubectl apply -f gateway-deployment.yaml
kubectl apply -f reservation-deployment.yaml
if [ $? -ne 0 ]; then
    echo "Kubernetes deployment failed. Exiting."
    exit 1
fi

echo "Waiting for the pod to be up and running..."
kubectl rollout status deployment/rabbitmq-deployment
kubectl rollout status deployment/redis-deployment
kubectl rollout status deployment/postgres-deployment
kubectl rollout status deployment/gateway-deployment
kubectl rollout status deployment/reservation-deployment

echo "Deployment completed successfully!"