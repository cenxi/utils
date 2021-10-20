######NTP服务端配置，基于Centos7.4#########
if [ ! ntpd ];then
  ####安装一个包ntp-4.2.6p5-29.el7.centos.2.x86_64.rpm####
  yum install -y ntp
fi

\cp -f /etc/ntp.conf /etc/ntp.conf.bak

cat>>/etc/ntp.conf<<EOF
driftfile /var/lib/ntp/drift

restrict default nomodify notrap nopeer noquery

restrict 127.0.0.1
restrict ::1

server 127.127.1.0 prefer

includefile /etc/ntp/crypto/pw

keys /etc/ntp/keys

disable monitor
EOF

systemctl enable --now ntpd
systemctl restart ntpd
systemctl status ntpd
