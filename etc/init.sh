#!/bin/bash

# 
# This script is meant to help setup a development environment. As such
# it will sym-link the default matcher-config to the default location.
#

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROUTER_FILE="/usr/local/etc/router-matchers.json"

echo "Installing ${ROUTER_FILE}"
if [ ! -e $ROUTER_FILE ]; then
  ln -s "${DIR}/router-matchers.json" $ROUTER_FILE
else
  echo "Error: file already exists"
  exit 1
fi
