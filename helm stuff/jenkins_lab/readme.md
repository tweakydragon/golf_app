# Create the Helm Chart
helm create jenkins_lab


# Delete 
helm delete jenkins-lab-release 
# Install the first time
helm install jenkins-lab-release jekins_lab/

# Upgrade after templating
helm upgrade jenkins-lab jenkins_lab/ --values jenkins_lab/values.yaml

# Access the pods
minikube tunnel

# Get pod info
kubectl describe pod jenkins-lab-release-jenkins-777854d696-5pfb2 

# Setup Namespaces
kubectl create namespace dev
kubectl create namespace testing

# Run Job Templates
# Scripts are now stored in values.yaml and mounted as ConfigMaps
# For path_a (Hello World):
helm upgrade jenkins-lab jenkins_lab/ --values jenkins_lab/values.yaml --set job.path=path_a

# For path_b (Goodbye World):
helm upgrade jenkins-lab jenkins_lab/ --values jenkins_lab/values.yaml --set job.path=path_b

# You can also override scripts at runtime:
helm upgrade jenkins-lab jenkins_lab/ --values jenkins_lab/values.yaml \
  --set job.path=path_a \
  --set-string job.scripts.path_a="#!/bin/bash\necho \"Custom Hello World\""

# Check job status:
kubectl get jobs
kubectl logs job/jenkins-lab-job-path_a
kubectl logs job/jenkins-lab-job-path_b

# View the ConfigMap with scripts:
kubectl get configmap jenkins-lab-job-scripts -o yaml