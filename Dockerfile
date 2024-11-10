FROM alpine:3.12 as builder

RUN mkdir -p /opt/cprof && \
  wget -q -O- https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent.tar.gz \
  | tar xzv -C /opt/cprof

# FROM cgr.dev/chainguard/jre:latest
FROM openjdk:21-jdk-slim

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

COPY --from=builder /opt/cprof /opt/cprof
COPY build/libs/*-all.jar /app.jar

CMD ["java", "-agentpath:/opt/cprof/profiler_java_agent.so=-cprof_service=dp-audit-logger,-cprof_service_version=1.0.0,-cprof_enable_heap_sampling=true",  "-jar", "/app.jar" ]