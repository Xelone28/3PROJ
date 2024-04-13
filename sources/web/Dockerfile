# Use Node.js LTS version as base image
FROM node:21-alpine3.18 as build

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json to container
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application
COPY . .

# Start the React development server with hot reloading
CMD ["npm", "start"]
