#!/bin/bash

if [[ $# -lt 1 ]]; then
	echo "Usage: $0 <folder-to-clean>"
	exit 1
fi

find "$1" -type f \
    | xargs file \
    | grep -v JPEG \
    | awk '{ print $1 }' \
    | sed s/:$//g \
    | xargs rm -v
