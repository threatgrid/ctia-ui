# ctia-ui

A user interface for managing a [CTIA] instance.

## Development Setup

Install [Leiningen] and [Node.js]. Then from the repository folder on the
command line:

```sh
# install node_modules (one-time)
npm install

# optional one-time step to install Grunt command line tool
# NOTE: you may have to "sudo npm install -g grunt-cli" depending on your system
npm install -g grunt-cli

# you may wish the run the following commands in separate console tabs / windows

# build CLJS files
lein clean && lein cljsbuild auto

# compile LESS into CSS
grunt watch

# run a local web server out of public/ on port 6886
node server.js 6886
```

## Development environment with docker-compose

A docker-compose development environment, including ES, Redis, and CTIA is available:

1. checkout CTIA on github:

   `git clone git@github.com:threatgrid/ctia.git`

2. build the jar:

   `cd ctia && lein uberjar`

3. build CTIA docker container:

   `docker build -t threatgrid/ctia:latest .`

4. launch the compose receipe:

   ```bash
      cd <ctia-ui-project-path>
      docker-compose -f containers/dev/docker-compose.yml up
   ```

5. change `tenzin-base-uri` to `http://127.0.0.1:3000/ctia` 


## Tenzin Docker Demo

A simple Docker container is available for testing:

### Build

`docker build -t threatgrid/ctia-ui:latest .`

### Run

`docker run -t -p 8080:8080 threatgrid/ctia-ui`

## License (TBD)

Copyright (c) Cisco Systems. All rights reserved.

[CTIA]:https://github.com/threatgrid/ctia/
[Leiningen]:http://leiningen.org
[Node.js]:http://nodejs.org
