# Primary Docker image for elvn CircleCI

FROM circleci/clojure:lein-2.7.1-browsers

RUN sudo apt-get update \
  && sudo apt-get install -y python-pip \
  && pip install awscli --upgrade --user

ENV PATH=/home/circleci/.local/bin:$PATH
