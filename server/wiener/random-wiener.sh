#!/bin/bash
#This script finds a random picture in the directory by shuffling the files and then selecting the first one.

file=$(find tf_files/data/wiener_images/ | shuf | head -n 1)
echo "Analyzing $file"
./guess.sh "$file"
