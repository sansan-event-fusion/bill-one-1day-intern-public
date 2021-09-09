FROM python:3.7

RUN pip install gcloud-tasks-emulator==0.5.0

ENV PORT 9090
EXPOSE 9090
STOPSIGNAL SIGINT

ENTRYPOINT ["gcloud-tasks-emulator"]
CMD ["start", "--port=$PORT"]
