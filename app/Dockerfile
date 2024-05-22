#############docke
# frontend  #
#############
FROM node:20.12.2 AS build

WORKDIR /app
COPY package*.json ./

COPY . .

EXPOSE 3000
# RUN npm install && npm run build

CMD ["npm", "start"]