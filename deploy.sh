#!/bin/bash

set -e  # Останавливаем выполнение при любой ошибке

# Проверка текущей директории
if [ ! -d "./k8/helm" ]; then
  echo "❌ Error: Directory './k8/helm' not found. Please run this script from the project root directory."
  exit 1
fi

echo "✅ Starting deployment..."

# Добавление репозиториев Helm
echo "➕ Adding Helm repositories..."
helm repo add jetstack https://charts.jetstack.io || echo "jetstack repo already added"
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx || echo "ingress-nginx repo already added"
helm repo update

# Установка cert-manager
echo "🔧 Installing/Upgrading cert-manager..."
helm upgrade --install cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --set installCRDs=true

# Применение ClusterIssuer
echo "📄 Applying ClusterIssuer (letsencrypt-prod.yaml)..."
kubectl apply -f cluster-resources/letsencrypt-prod.yaml

# Установка ingress-nginx
echo "🌐 Installing/Upgrading ingress-nginx..."
helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx \
  --create-namespace \
  -f infrastructure/ingress-nginx-values.yaml

# Проверка готовности webhook'а ingress-nginx
echo "⏳ Waiting for ingress-nginx admission webhook to become ready..."
kubectl rollout status deployment ingress-nginx-controller -n ingress-nginx

# Обновление зависимостей чарта
echo "📦 Updating Helm dependencies..."
helm dependency update ./k8/helm

# Установка/обновление твоего приложения
echo "🚀 Installing/Upgrading your Helm chart (secret-agency)..."
helm upgrade --install secret-agency ./k8/helm \
  --namespace secret-agency \
  --create-namespace

echo "✅ Deployment finished!"
