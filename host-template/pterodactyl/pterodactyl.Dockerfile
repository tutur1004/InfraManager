# ----------------------------------
# Pterodactyl Core Dockerfile
# Environment: Java
# Minimum Panel Version: 0.6.0
# ----------------------------------
FROM eclipse-temurin:8-jre-alpine

MAINTAINER MileKat => tutur1004#7091

RUN adduser --disabled-password container
RUN chown -R container:container /home/container

USER container
ENV USER=container HOME=/home/container

COPY --chown=container:container container /home/data
RUN chown -R container:container /home/data

WORKDIR /home/container

COPY --chown=container:container entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

CMD ["/bin/ash", "/entrypoint.sh"]