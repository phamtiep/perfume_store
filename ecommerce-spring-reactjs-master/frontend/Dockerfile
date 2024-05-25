FROM  node:17.5.0-alpine3.15

COPY package.json package-locj.json

RUN npm install

COPY . .

RUN npm build