# 安装yum-utils
$ yum -y install yum-utils

# repotrack、yumdownloader --resolve两种方式下载

# 方式一(推荐)
$ repotrack ansible

# 方式二(下载的包放在同样的操作系统，其他机器上可能无法正常安装，提示依赖问题)
# 仅会将主软件包和基于你现在的操作系统所缺少的依赖关系包一并下载
$ yumdownloader --resolve --destdir=/tmp ansible


# 安装rpm包
$ yum -y localinstall /opt/*.rpm --skip-broken