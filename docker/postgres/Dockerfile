FROM postgres:latest

RUN mkdir -p /docker-entrypoint-initdb.d
COPY ./sql/*.sql /docker-entrypoint-initdb.d/

COPY *.sh /
RUN chmod 755 /*.sh

HEALTHCHECK --start-period=30s --interval=1s --timeout=60s --retries=30\
  CMD /bootstrap/init.sh