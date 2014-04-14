### Setting Up a Dev Environment

This folder contains what you need to get up and running with a dev
environment. The `init.sh` file will attempt to setup your local environment
and get you jump-started. Once you are setup, you can start a bunch
of test servers (as specified in the deployed default configuration)
but running `run.sh`. 

Of course you are more than welcome to setup your own dev environment if you
would like to test something more specific. This is just to get you jump-started.
Feel free to peruse the `init.sh` and `run.sh` to see what they are doing
exactly.

I will note that the test servers have a dependency on NodeJS (read more
below).


### test-servers/
This folder contains a bunch of tests servers that respond in various
ways (simple, chunked, large responses, etc). Since we just need some
simple test services the following have been written in NodeJS (v0.10.26).

The names of the files should be pretty representative of how the server
behaves. To run a file, just `node FILENAME.js`.
