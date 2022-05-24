FROM python:3.8

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

RUN ["chmod", "+x", "docker_entrypoint.sh"]

EXPOSE 5000

CMD [ "./docker_entrypoint.sh" ]
