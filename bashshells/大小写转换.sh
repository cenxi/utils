# ${parameter^^} 表达式基于 parameter 变量值，把所有字符转成大写，得到新的值
# ${parameter,,} 表达式基于 parameter 变量值，把所有字符转成小写，得到新的值

kibana_vars=(
    console.enabled
    console.proxyConfig
    console.proxyFilter
    elasticsearch.customHeaders
    elasticsearch.hosts
)

longopts=''
for kibana_var in ${kibana_vars[*]}; do
    # 'elasticsearch.hosts' -> 'ELASTICSEARCH_HOSTS'
    echo ${kibana_var}
    echo ${kibana_var^^}
    # tr命令将点号转成下划线
    env_var=$(echo ${kibana_var^^} | tr . _)
    echo ${env_var}

done