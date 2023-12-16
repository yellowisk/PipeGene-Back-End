FROM postgres:14.2

RUN echo "Building Dockerfile..."

RUN mkdir -p /docker-entrypoint-initdb.d
COPY ./sql/*.sql /docker-entrypoint-initdb.d/

COPY *.sh /bootstrap/
RUN chmod 755 /bootstrap/*.sh

ENV POSTGRES_PASSWORD=password

HEALTHCHECK --start-period=30s --interval=1s --timeout=60s --retries=30\
  CMD /bootstrap/init.sh
