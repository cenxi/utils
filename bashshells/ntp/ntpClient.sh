######NTP服务端配置，基于Centos7.4#########
if [ ! ntpd ];then
  yum install -y ntp
fi

#####入参传ntp服务端的ip####
ntpdate $1