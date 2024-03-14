@echo off
set env_file=./.env.prod
docker-compose --env-file %env_file% up --build
pause
