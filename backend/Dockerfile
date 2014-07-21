# Pull base image.
FROM dockerfile/mongodb

ENV SBT_VERSION 0.13.5
ENV DEBIAN_FRONTEND noninteractive

# Install Java 8
RUN \
  echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer

WORKDIR /tmp

RUN \
  wget http://dl.bintray.com/sbt/native-packages/sbt/$SBT_VERSION/sbt-$SBT_VERSION.zip && \
  unzip sbt-$SBT_VERSION.zip -d /usr/local

# COMMIT PROJECT FILES
ADD app /root/app
ADD conf /root/conf
ADD logs /root/logs
ADD project/plugins.sbt /root/project/
ADD public /root/public
ADD test /root/test
ADD build.sbt /root/

# TEST AND BUILD THE PROJECT -- FAILURE WILL HALT IMAGE CREATION
RUN cd /root; /usr/local/sbt/bin/sbt test stage
RUN rm /root/target/universal/stage/bin/*.bat

# TESTS PASSED -- CONFIGURE IMAGE
WORKDIR /root
ENTRYPOINT target/universal/stage/bin/$(ls target/universal/stage/bin)
EXPOSE 9000

