#!/usr/bin/bash
# build docker image for server hosting weblet produced in target folder
lein uberjar
docker build -t gamelist/jserv .
