FROM clojure:latest
MAINTAINER rxacevedo@fastmail.com
ADD . /app
WORKDIR /app
CMD ["lein", "run", "3000"]
