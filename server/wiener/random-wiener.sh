#!/bin/bash

file=$(find tf_files/data/wiener_images/ | shuf | head -n 1)
echo "Analyzing $file"
./guess.sh "$file"
