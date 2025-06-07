# frontend/Dockerfile

# ---- Build Stage ----
FROM node:18-alpine AS build

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json (or yarn.lock)
COPY package*.json ./
# If you use yarn, uncomment the next line and comment out the npm ci line
# COPY yarn.lock ./

# Install dependencies
# If you use yarn, use: RUN yarn install --frozen-lockfile
RUN npm ci

# Copy the rest of the application code
COPY . .

# Build the React application for production
# Replace 'build' with your actual build script if it's different (e.g., 'npm run build:prod')
RUN npm run build

# ---- Serve Stage ----
FROM nginx:1.25-alpine

# Remove default Nginx server configuration
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the build output from the 'build' stage to Nginx's html directory
COPY --from=build /app/build /usr/share/nginx/html

# Expose port 80 for Nginx
EXPOSE 80

# Command to run Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]
