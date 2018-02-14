#!/bin/bash
#This script uses Docker to run the TensorFlow training algorithm with the directory of hot dog pictures.

docker run -it --rm -v $PWD/tf_files:/tf_files wiener
