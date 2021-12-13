# ansible在主机执行操作
ansible all -i "localhost," -c local -m shell -a 'touch /tmp/11;echo "22">/tmp/11'