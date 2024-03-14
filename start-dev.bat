@echo off
set env_file=./.env.dev
docker-compose --env-file %env_file% up --build
pause
