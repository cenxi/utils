rpm -Uvh --force --nodeps /opt/ai_deploy/packages/rpm/ansible/*.rpm

sed -ri  's/.+host_key_checking = True.*/host_key_checking = False/g' /etc/ansible/ansible.cfg
sed -ri  's/.+host_key_checking = False.*/host_key_checking = False/g' /etc/ansible/ansible.cfg