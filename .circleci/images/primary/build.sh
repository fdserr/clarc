#!/bin/bash

# Build the CircleCI primary image and push it to DockerHHub

# Always increase build tag before pushing changed image to dockerhub
TAG=0.0.1

# Build
docker build -t fdserr/clarc-cci-primary:${TAG} ./

if [ $? ]
then
  # Push
  if docker login; then
    docker push fdserr/clarc-cci-primary:${TAG}
  fi
fi

exit $?
