FROM clojure:alpine
RUN apk update \
  && apk add nodejs=6.7.0-r0
RUN mkdir -p /usr/src/app

COPY / /usr/src/app/
WORKDIR /usr/src/app

RUN ["npm", "install"]
RUN ["npm", "install", "-g", "grunt-cli"]
RUN ["lein", "clean"]
RUN ["lein", "cljsbuild", "once"]
RUN ["grunt", "less"]

CMD ["node", "server.js", "8080"]
EXPOSE 8080
