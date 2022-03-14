# 查看iptables规则
iptables -L -n

# 删除所有链，并接受input/output/forward流量
iptables -F
iptables -X
iptables -P INPUT ACCEPT
iptables -P OUTPUT ACCEPT
iptables -P FORWARD ACCEPT