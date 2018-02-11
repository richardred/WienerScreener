FROM tensorflow/tensorflow:0.9.0-devel

MAINTAINER Jerome WAX "xblaster@lo2k.net"

WORKDIR /tensorflow

RUN git pull

ADD src .
CMD cd /tensorflow && ./train.sh
