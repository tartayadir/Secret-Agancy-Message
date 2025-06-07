#!/bin/bash
set -e  # Fast fail flag

# --- Configuration ---
FRONTEND_IMAGE_NAME="secret-agency-message-service-ui-image"
FRONTEND_IMAGE_TAG="latest" # Or use a git commit hash, version number, etc.
FRONTEND_DOCKERFILE_PATH="./frontend/Dockerfile"
FRONTEND_CONTEXT_PATH="./frontend"

if [ ! -d "./k8s/helm" ]; then
  echo "❌ Error: Directory './k8s/helm' not found. Please run this script from the project root directory."
  exit 1
fi
if [ ! -f "$FRONTEND_DOCKERFILE_PATH" ]; then
  echo "❌ Error: Frontend Dockerfile '$FRONTEND_DOCKERFILE_PATH' not found."
  exit 1
fi

for file in infrastructure/ingress-nginx-values.yaml cluster-resources/letsencrypt-prod.yaml infrastructure/nginx-tcp-configmap.yaml; do
  if [ ! -f "$file" ]; then
    echo "❌ Error: File '$file' not found."
    exit 1
  fi
done

echo "✅ Starting deployment..."

# --- Build and Push Frontend Docker Image ---
echo "🏗️ Building frontend Docker image: $FRONTEND_IMAGE_NAME:$FRONTEND_IMAGE_TAG..."
if ! docker build -t "$FRONTEND_IMAGE_NAME:$FRONTEND_IMAGE_TAG" -f "$FRONTEND_DOCKERFILE_PATH" "$FRONTEND_CONTEXT_PATH"; then
  echo "❌ Error: Failed to build frontend Docker image."
  exit 1
fi

echo "📤 Pushing frontend Docker image: $FRONTEND_IMAGE_NAME:$FRONTEND_IMAGE_TAG..."
# Make sure you are logged into your Docker registry before running this script
# e.g., docker login your-docker-registry
if ! minikube.exe image load "$FRONTEND_IMAGE_NAME:$FRONTEND_IMAGE_TAG"; then
  echo "❌ Error: Failed to push frontend Docker image."
  exit 1
fi
echo "✅ Frontend Docker image built and pushed successfully."
# --- End Build and Push Frontend Docker Image ---

# Adding Helm repositories
echo "➕ Adding Helm repositories..."
helm repo add jetstack https://charts.jetstack.io || echo "jetstack repo already added"
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx || echo "ingress-nginx repo already added"
if ! helm repo update; then
  echo "❌ Error: Failed to update Helm repositories."
  exit 1
fi

# Installing cert-manager
echo "🔧 Installing/Upgrading cert-manager..."
helm upgrade --install cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --set installCRDs=true
echo "⏳ Waiting for cert-manager to become ready..."
kubectl rollout status deployment cert-manager -n cert-manager --timeout=5m
kubectl rollout status deployment cert-manager-webhook -n cert-manager --timeout=5m

# Applying ClusterIssuer
echo "📄 Applying ClusterIssuer (letsencrypt-prod.yaml)..."
kubectl apply -f cluster-resources/letsencrypt-prod.yaml

# Applying ConfigMap для TCP
echo "📄 Applying TCP ConfigMap for ingress-nginx..."
kubectl apply -f infrastructure/nginx-tcp-configmap.yaml

# Installing ingress-nginx
echo "🌐 Installing/Upgrading ingress-nginx..."
helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace \
  -f infrastructure/ingress-nginx-values.yaml

# Readiness check webhook'а ingress-nginx
echo "⏳ Waiting for ingress-nginx admission webhook to become ready..."
# It might be ingress-nginx-controller or ingress-nginx-admission, check your deployment name
# Trying with ingress-nginx-controller first as in original script
if ! kubectl rollout status deployment ingress-nginx-controller -n ingress-nginx --timeout=5m 2>/dev/null; then
  echo "⏳ ingress-nginx-controller not found or timed out, trying ingress-nginx-admission..."
  # Fallback to common alternative names if the first one fails
  if ! kubectl rollout status deployment ingress-nginx-admission -n ingress-nginx --timeout=5m 2>/dev/null && \
     ! kubectl rollout status deployment ingress-nginx-ingress-nginx-controller -n ingress-nginx --timeout=5m 2>/dev/null; then
       echo "⚠️ Warning: Could not confirm readiness of ingress-nginx admission webhook via common deployment names. Proceeding with caution."
  fi
fi


# Updating chart dependencies
echo "📦 Updating Helm dependencies for ./k8s/helm..."
if ! helm dependency update ./k8s/helm; then
  echo "❌ Error: Failed to update Helm dependencies for ./k8s/helm."
  exit 1
fi

# Installing/updating application
echo "🚀 Installing/Upgrading your Helm chart (secret-agency umbrella chart)..."
# You might want to pass values to your frontend subchart here
# For example, to set the image tag dynamically:
# --set frontend.image.tag=$FRONTEND_IMAGE_TAG \
# --set frontend.image.repository=$FRONTEND_IMAGE_NAME \
# --set frontend.ingress.hosts[0].host=your.actual.frontend.domain \
helm upgrade --install secret-agency ./k8s/helm \
  --namespace secret-agency \
  --create-namespace \
  --set frontend.image.repository="$FRONTEND_IMAGE_NAME" \
  --set frontend.image.tag="$FRONTEND_IMAGE_TAG"
  # Add other --set commands as needed, especially for ingress host

echo "✅ Deployment finished!"

# Start port forwarding for required services
echo "🔌 Setting up port forwarding..."

# Function to start port forwarding in background
start_port_forward() {
    local service=$1
    local ports=$2
    local namespace=$3
    local display_name=${4:-$service} # Optional display name
    echo "📡 Starting port forward for $display_name ($service in $namespace)..."
    kubectl port-forward -n $namespace svc/$service $ports >/tmp/pf-$service.log 2>&1 &
    # Give it a moment to establish or fail
    sleep 3
    if ! pgrep -f "kubectl port-forward -n $namespace svc/$service $ports" > /dev/null; then
        echo "❌ Failed to start port-forward for $display_name. Check /tmp/pf-$service.log"
    else
        echo "✅ Port-forward for $display_name seems to be running."
    fi
}

# Kill existing port forwards
echo "🧹 Cleaning up existing port forwards..."
pkill -f "kubectl port-forward" || true
sleep 2  # Wait for processes to clean up

# Start port forwarding for each service
echo "🚀 Starting port forwards..."

start_port_forward "secret-agency-mysql-service" "3306:3306" "secret-agency" "MySQL"
start_port_forward "secret-agency-nats-service" "4222:4222 8222:8222 9222:9222" "secret-agency" "NATS"
start_port_forward "secret-agency-secret-agency-service" "8080:8080" "secret-agency" "Backend API (secret-agency)"
start_port_forward "secret-agency-frontend" "8081:80" "secret-agency" "Frontend (via port 8081)"


echo "✅ Port forwarding setup complete!"
echo "
🔗 Available services (approximate local URLs):
   MySQL: localhost:3306
   NATS: localhost:4222 (main), localhost:8222 (monitoring), localhost:9222 (routing)
   Backend API (secret-agency): localhost:8080
   Frontend (secret-agency): localhost:8081 (or access via Ingress if configured with a domain)

🌐 Your frontend should also be accessible via Ingress if DNS is set up for:
   Host: (check your frontend.ingress.hosts[0].host in values.yaml or override)

⚠️  Port forwarding is running in the background. To stop it, run:
   pkill -f \"kubectl port-forward\"
"

# helm uninstall secret-agency --namespace secret-agency
