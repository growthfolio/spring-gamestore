FROM ubuntu:latest
LABEL authors="felipe-macedo"

ENTRYPOINT ["top", "-b"]