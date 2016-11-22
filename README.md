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

## Launch a CTIA-UI demo via docker-compose

A docker-compose demo environment, including ES, Redis, CTIA, and the CTIA-UI is available:

1. Clone into the CTIA repository on github:

   `git clone git@github.com:threatgrid/ctia.git`

2. Build the CTIA uberjar:

   `cd ctia && lein uberjar`

3. Build CTIA docker container:

   `docker build -t threatgrid/ctia:latest .`

4. Launch the CTIA-UI via docker-compose:

   ```bash
      cd <ctia-ui-project-path>
      docker-compose -f containers/demo/docker-compose.yml up
   ```

## Launch dev container via docker-compose

A docker-compose development environment, including ES, Redis, and CTIA is available:

1. Clone into the CTIA repository on github:

   `git clone git@github.com:threatgrid/ctia.git`

2. Build the CTIA uberjar:

   `cd ctia && lein uberjar`

3. Build CTIA docker container:

   `docker build -t threatgrid/ctia:latest .`

4. Launch the CTIA-UI via docker-compose:

   ```bash
      cd <ctia-ui-project-path>
      docker-compose -f containers/dev/docker-compose.yml up
   ```

5. Launch CTIA-UI in your development environment and use http://localhost:3000 as your CTIA server.

## License (TBD)

Copyright (c) Cisco Systems. All rights reserved.

[CTIA]:https://github.com/threatgrid/ctia/
[Leiningen]:http://leiningen.org
[Node.js]:http://nodejs.org
