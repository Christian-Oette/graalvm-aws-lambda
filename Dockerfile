FROM amazonlinux

RUN yum install -y wget \
	&& yum install -y tar \
	&& yum install -y gzip \
	&& wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-21.0.0.2/graalvm-ce-java11-linux-amd64-21.0.0.2.tar.gz \
	&& tar -xzvf graalvm-ce-java11-linux-amd64-21.0.0.2.tar.gz \
	&& rm graalvm-ce-java11-linux-amd64-21.0.0.2.tar.gz \
	&& mv graalvm-ce-java11-21.0.0.2 graalvm-ce-java11 \
	&& PATH=$PATH:/graalvm-ce-java11/bin \
	&& gu install native-image \
	&& wget https://archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz \
	&& tar -xzvf apache-maven-3.3.9-bin.tar.gz \
	&& yum group install -y "Development Tools"

ENV PATH="/graalvm-ce-java11/bin:/apache-maven-3.3.9/bin:${PATH}"
ENV JAVA_HOME "/graalvm-ce-java11"