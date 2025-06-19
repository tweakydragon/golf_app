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