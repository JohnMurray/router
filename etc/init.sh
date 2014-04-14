#!/bin/bash

# 
# This script is meant to help setup a development environment. As such
# it will sym-link the default routes-config to the default location.
#

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROUTER_DIR="/usr/local/etc/router"
ROUTER_FILE="routes.json"

#
# Install the router file
#
echo "Installing ${ROUTER_FILE}"
if [ ! -e $ROUTER_FILE ]; then
  mkdir -p $ROUTER_FILE/$ROUTER_FILE
  ln -s $DIR/$ROUTER_FILE $ROUTER_FILE/$ROUTER_FILE
else
  echo "Warning: file already exists"
fi


#
# Ensure that NodeJs is installed and available
#
if [[ `which node` ]] ; then
  echo -n "Node version found: " && node -v
  if [[ `which npm` ]] ; then
    echo "Installing foreman"
    npm install -g foreman
  else
    echo "Warning: 'npm' not found in path. Will not be able to install foreman";
  fi
else
  echo "Warning: 'node' command not found in path. Will not be able to run test-servers";
fi
