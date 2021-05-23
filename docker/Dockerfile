FROM clojure:lein-2.9.1

RUN apt update
RUN apt install --no-install-recommends -yy curl unzip build-essential zlib1g-dev

WORKDIR "/opt"

RUN curl -sLO https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-20.3.0/graalvm-ce-java11-linux-amd64-20.3.0.tar.gz
RUN tar -xzf graalvm-ce-java11-linux-amd64-20.3.0.tar.gz

RUN apt install -yy apache2

RUN a2enmod rewrite
RUN a2enmod cgi

COPY ./docker/conf/apache2.conf /etc/apache2/apache2.conf
COPY ./docker/conf/conf-available/serve-cgi-bin.conf /etc/apache2/conf-available/serve-cgi-bin.conf
COPY ./docker/conf/mods-available/mime.conf /etc/apache2/mods-available/mime.conf
COPY ./docker/conf/sites-available /etc/apache2/sites-available

WORKDIR /

RUN curl -s https://raw.githubusercontent.com/babashka/babashka/master/install  > ./install \
    && chmod +x ./install \
    && ./install --version "0.4.1"


EXPOSE 80
VOLUME /var/www/public_html
WORKDIR /var/www/public_html
CMD /usr/sbin/apache2ctl -D FOREGROUND
