@echo off

IF "%1"=="up" (
    docker-compose up --build
)

IF "%1"=="down" (
    docker-compose down
)

IF "%1"=="logs" (
    docker-compose logs -f
)