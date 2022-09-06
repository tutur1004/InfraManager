# ----------------------------------
# Pterodactyl Core Dockerfile
# Environment: Java
# Minimum Panel Version: 0.6.0
# ----------------------------------
FROM eclipse-temurin:8-jre-alpine

MAINTAINER MileKat => tutur1004#7091

RUN adduser --disabled-password minecraft -u 1004
USER minecraft
ENV USER=minecraft HOME=/home/minecraft

COPY --chown=minecraft:minecraft container /home/minecraft
RUN chown -R minecraft:minecraft /home/minecraft

WORKDIR /home/minecraft

COPY --chown=minecraft:minecraft entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

CMD ["/bin/ash", "/entrypoint.sh"]
