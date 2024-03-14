@echo off
set env_file=./.env.qa
docker-compose --env-file %env_file% up --build
pause
