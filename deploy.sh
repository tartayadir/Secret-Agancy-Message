#!/bin/bash

set -e  # Останавливаем выполнение при любой ошибке

# Проверка текущей директории
if [ ! -d "./k8/helm" ]; then
  echo "❌ Error: Directory './k8/helm' not found. Please run this script from the project root directory."
  exit 1
fi

for file in infrastructure/ingress-nginx-values.yaml cluster-resources/letsencrypt-prod.yaml infrastructure/nginx-tcp-configmap.yaml; do
  if [ ! -f "$file" ]; then
    echo "❌ Error: File '$file' not found."
    exit 1
  fi
done

echo "✅ Starting deployment..."

# Добавление репозиториев Helm
echo "➕ Adding Helm repositories..."
helm repo add jetstack https://charts.jetstack.io || echo "jetstack repo already added"
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx || echo "ingress-nginx repo already added"
if ! helm repo update; then
  echo "❌ Error: Failed to update Helm repositories."
  exit 1
fi

# Установка cert-manager
echo "🔧 Installing/Upgrading cert-manager..."
helm upgrade --install cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --set installCRDs=true
echo "⏳ Waiting for cert-manager to become ready..."
kubectl rollout status deployment cert-manager -n cert-manager --timeout=5m
kubectl rollout status deployment cert-manager-webhook -n cert-manager --timeout=5m

# Применение ClusterIssuer
echo "📄 Applying ClusterIssuer (letsencrypt-prod.yaml)..."
kubectl apply -f cluster-resources/letsencrypt-prod.yaml

# Применение ConfigMap для TCP
echo "📄 Applying TCP ConfigMap for ingress-nginx..."
kubectl apply -f infrastructure/nginx-tcp-configmap.yaml

# Установка ingress-nginx
echo "🌐 Installing/Upgrading ingress-nginx..."
helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace \
  -f infrastructure/ingress-nginx-values.yaml

# Проверка готовности webhook'а ingress-nginx
echo "⏳ Waiting for ingress-nginx admission webhook to become ready..."
kubectl rollout status deployment ingress-nginx-controller -n ingress-nginx --timeout=5m

# Обновление зависимостей чарта
echo "📦 Updating Helm dependencies..."
if ! helm dependency update ./k8/helm; then
  echo "❌ Error: Failed to update Helm dependencies."
  exit 1
fi

# Установка/обновление приложения
echo "🚀 Installing/Upgrading your Helm chart (secret-agency)..."
helm upgrade --install secret-agency ./k8/helm \
  --namespace secret-agency \
  --create-namespace

echo "✅ Deployment finished!"

# Start port forwarding for required services
echo "🔌 Setting up port forwarding..."

# Function to start port forwarding in background
start_port_forward() {
    local service=$1
    local ports=$2
    local namespace=$3
    echo "📡 Starting port forward for $service..."
    kubectl port-forward -n $namespace svc/$service $ports >/dev/null 2>&1 &
    sleep 2  # Wait for port forward to establish
}

# Kill existing port forwards
echo "🧹 Cleaning up existing port forwards..."
pkill -f "kubectl port-forward" || true
sleep 2  # Wait for processes to clean up

# Start port forwarding for each service
echo "🚀 Starting port forwards..."
start_port_forward "secret-agency-mysql-service" "3306:3306" "secret-agency"
start_port_forward "secret-agency-nats-service" "4222:4222 8222:8222 9222:9222" "secret-agency"
start_port_forward "secret-agency-secret-agency-service" "8080:8080" "secret-agency"

echo "✅ Port forwarding setup complete!"
echo "
🔗 Available services:
   MySQL: localhost:3306
   NATS: localhost:4222 (main), localhost:8222 (monitoring), localhost:9222 (routing)
   Secret Agency Service: localhost:8080

⚠️  Port forwarding is running in the background. To stop it, run:
   pkill -f \"kubectl port-forward\"
"

## Check if port forwards are running
#echo "🔍 Verifying port forwards..."
#sleep 2
#if pgrep -f "kubectl port-forward" > /dev/null; then
#    echo "✅ Port forwarding is running"
#else
#    echo "❌ Port forwarding failed to start"
#    exit 1
#fi