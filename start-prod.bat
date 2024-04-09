@echo off
set env_file=./.env.prod
set env=./.env

Rem merge .env.dev and .env
for /f "tokens=*" %%a in (%env_file%) do (
    set %%a
)

for /f "tokens=*" %%a in (%env%) do (
    set %%a
)

docker-compose --env-file %env_file% up --build
pause
