FROM python:3.6.9

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD [ "./docker_entrypoint.sh" ]
