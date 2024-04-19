## Push to docker on Ubuntu:

- chú ý: ko để thông số #server.port=8888 trong application.properties
- mvn clean package
- copy file `target/Project3-0.0.1-SNAPSHOT.jar` to host: `/home/project_name/target/`
- copy file `Dockerfile`, `docker-compose.yml` (if changes) to host: `/home/project_name/`

```sh
clear;
```
```bash
mvn clean package
```
```bash 
docker compose up -d shop-clothes_db
```
```bash 
docker compose up -d shop-clothes_backend
```
```bash 
docker compose down shop-clothes_redis; docker compose up -d shop-clothes_redis;
```
```bash
docker compose build
```

docker compose build shop-clothes_backend; docker compose up -d shop-clothes_backend;

## Update image and rebuild container

```
mvn clean package
```

```bash
docker compose down shop-clothes_backend
```
```bash
docker image rm shop-clothes_shop-clothes_backend:latest
```
```bash
docker volume rm shop-clothes_shop-clothes_backend
```
```bash
docker compose up -d shop-clothes_backend
```

```bash
#docker compose down shop-clothes_db
#docker compose down shop-clothes_backend
#docker image rm shop-clothes_backend:latest
#docker volume rm shop-clothes_backend
#docker compose up -d shop-clothes_backend
```

### Info
```bash
docker logs backend; #logs container backend
docker ps; # list container running
docker image ls; # list all image
docker volume ls; # list all volume
```
```bash
docker exec -it mysqldb mysql -uroot -p; # login to mysql container
```

```bash
docker rm -vf $(docker ps -aq); docker rmi -f $(docker images -aq);
```
```bash
docker volume rm $(docker volume ls -q --filter dangling=true);
```
