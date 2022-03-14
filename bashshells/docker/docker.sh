# 停止docker
systemctl stop docker.socket
systemctl stop docker.service

systemctl start docker
systemctl daemon-reload