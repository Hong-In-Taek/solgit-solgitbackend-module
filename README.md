# solgit-solgitbackend-module
solgit-solgitbackend-module



docker build -t hit1414/solgit-backend-module:1.0.0 .

docker run -d \ 
  --name solgit-backend \
  -p 8085:8085 \
  -e SPRING_RABBITMQ_HOST=host.docker.internal \
  -e SPRING_RABBITMQ_PORT=5672 \
  -e SPRING_RABBITMQ_USERNAME=admin \
  -e SPRING_RABBITMQ_PASSWORD=password \
  hit1414/solgit-backend-module:1.0.0