#!/bin/bash

if [[ $# -lt 1 ]]; then
	echo "Usage: $0 <guess-img.jpg>"
	exit 1
fi

docker run --rm -v $PWD/tf_files:/tf_files -v "$(realpath "$1")":/img/guess.jpg wiener bash ./guess.sh 2>/dev/null \
    | awk '
    /wiener images/ {
        gsub(/^wiener images \(score = /, "")
        gsub(/\).*$/, "")
        if ($0 >= 0.5) {
            print "Wiener"
        } else {
            print "Not Wiener"
        }
    }'
