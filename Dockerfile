FROM clojure:latest
MAINTAINER rxacevedo@fastmail.com
RUN mkdir /code
ADD . /code
WORKDIR /code
RUN lein deps
CMD ["lein", "run", "80"]
