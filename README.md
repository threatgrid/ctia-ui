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

## License (TBD)

Copyright (c) Cisco Systems. All rights reserved.

[CTIA]:https://github.com/threatgrid/ctia/
[Leiningen]:http://leiningen.org
[Node.js]:http://nodejs.org
