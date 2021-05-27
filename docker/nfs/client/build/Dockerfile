FROM fuzzle/docker-nfs-server:latest

ARG	DIRPATH=/home
WORKDIR	$DIRPATH
COPY start-container.bash /
RUN chmod +x /start-container.bash
CMD ["mount","192.168.25.131:/data/fx/worker2","/data/nfs"]
