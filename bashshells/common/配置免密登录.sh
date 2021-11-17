echo 'y'|ssh-keygen -t rsa -P "" -f ~/.ssh/id_rsa

# 安装expect
# yum install expect -y
# -o StrictHostKeyChecking=no不需要提示yes/no
expect <<EOF
spawn ssh-copy-id -o StrictHostKeyChecking=no root@1.1.1.1
expect {
    "yes/no" { send "yes\n";exp_continue }
    "password" { send "aaa_123\n" }
}
expect eof
EOF

