package common.utils;

import cn.hutool.core.util.ObjectUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * @author ：fengxi
 * @date ：Created in 2020-06-17 12:36
 * @description：
 * @modified By：
 */
public class IpUtil {

    private static final String ANYHOST_VALUE = "0.0.0.0";
    private static final String LOCALHOST_VALUE = "127.0.0.1";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");



    private static volatile InetAddress LOCAL_ADDRESS = null;

    // ---------------------- valid ----------------------

    private static InetAddress toValidAddress(InetAddress address) {
        if (address instanceof Inet6Address) {
            Inet6Address v6Address = (Inet6Address) address;
            if (isPreferIPV6Address()) {
                return normalizeV6Address(v6Address);
            }
        }
        if (isValidV4Address(address)) {
            return address;
        }
        return null;
    }

    private static boolean isPreferIPV6Address() {
        return Boolean.getBoolean("java.net.preferIPv6Addresses");
    }

    /**
     * valid Inet4Address
     *
     * @param address
     * @return
     */
    private static boolean isValidV4Address(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        boolean result = (name != null
                && IP_PATTERN.matcher(name).matches()
                && !ANYHOST_VALUE.equals(name)
                && !LOCALHOST_VALUE.equals(name));
        return result;
    }


    /**
     * normalize the ipv6 Address, convert scope name to scope id.
     * e.g.
     * convert
     * fe80:0:0:0:894:aeec:f37d:23e1%en0
     * to
     * fe80:0:0:0:894:aeec:f37d:23e1%5
     * <p>
     * The %5 after ipv6 address is called scope id.
     * see java doc of {@link Inet6Address} for more details.
     *
     * @param address the input address
     * @return the normalized address, with scope id converted to int
     */
    private static InetAddress normalizeV6Address(Inet6Address address) {
        String addr = address.getHostAddress();
        int i = addr.lastIndexOf('%');
        if (i > 0) {
            try {
                return InetAddress.getByName(addr.substring(0, i) + '%' + address.getScopeId());
            } catch (UnknownHostException e) {
                // ignore
                e.printStackTrace();
            }
        }
        return address;
    }

    // ---------------------- find ip ----------------------


    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            InetAddress addressItem = toValidAddress(localAddress);
            if (addressItem != null) {
                return addressItem;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (null == interfaces) {
                return localAddress;
            }
            while (interfaces.hasMoreElements()) {
                try {
                    NetworkInterface network = interfaces.nextElement();
                    if (network.isLoopback() || network.isVirtual() || !network.isUp()) {
                        continue;
                    }
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        try {
                            InetAddress addressItem = toValidAddress(addresses.nextElement());
                            if (addressItem != null) {
                                try {
                                    if(addressItem.isReachable(100)){
                                        return addressItem;
                                    }
                                } catch (IOException e) {
                                    // ignore
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return localAddress;
    }


    // ---------------------- tool ----------------------

    /**
     * Find first valid IP from local network card
     *
     * @return first valid local IP
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    /**
     * get ip address
     *
     * @return String
     */
    public static String getIp(){
        return getLocalAddress().getHostAddress();
    }

    /**
     * get ip:port
     *
     * @param port
     * @return String
     */
    public static String getIpPort(int port){
        String ip = getIp();
        return getIpPort(ip, port);
    }

    public static String getIpPort(String ip, int port){
        if (ip==null) {
            return null;
        }
        return ip.concat(":").concat(String.valueOf(port));
    }

    public static Object[] parseIpPort(String address){
        String[] array = address.split(":");

        String host = array[0];
        int port = Integer.parseInt(array[1]);

        return new Object[]{host, port};
    }

    /***
     * 获取客户端IP地址;这里通过了Nginx获取;X-Real-IP
     * nginx如下配置
     * 方式一，针对单个Url
     * location ^~ /test-dbapi {
     *                 #保留代理之前的host 包含客户端真实的域名和端口号
     *                 proxy_set_header    Host  $host;
     *                 #保留代理之前的真实客户端ip
     *                 proxy_set_header    X-Real-IP  $remote_addr;
     *                 #这个Header和X-Real-IP类似，但它在多级代理时会包含真实客户端及中间每个代理服务器的IP
     *                 proxy_set_header    X-Forwarded-For  $proxy_add_x_forwarded_for;
     *                 #表示客户端真实的协议（http还是https）
     *                 proxy_set_header X-Forwarded-Proto $scheme;
     *
     *                 proxy_pass http://10.32.187.47:8013/;
     *                 proxy_cookie_domain 10.32.112.80:8201 220.202.55.101:80;
     * }
     * 方式二，针对整个server
     * server {
     *         listen      8181;
     *
     *         #保留代理之前的host 包含客户端真实的域名和端口号
     *         proxy_set_header    Host  $host;
     *         #保留代理之前的真实客户端ip
     *         proxy_set_header    X-Real-IP  $remote_addr;
     *         #这个Header和X-Real-IP类似，但它在多级代理时会包含真实客户端及中间每个代理服务器的IP
     *         proxy_set_header    X-Forwarded-For  $proxy_add_x_forwarded_for;
     *         #表示客户端真实的协议（http还是https）
     *         proxy_set_header X-Forwarded-Proto $scheme;
     *
     * }
     */
    public static String getRealIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("X-Real-IP");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
            if (!ObjectUtil.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个IP值，第一个为真实IP。
                int index = ip.indexOf(',');
                if (index != -1) {
                    ip =  ip.substring(0, index);
                }
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if("0:0:0:0:0:0:0:1".equals(ip)){
            return "127.0.0.1";
        }else {
            if(ip.equals("127.0.0.1") || ip.equalsIgnoreCase("localhost") && ObjectUtil.isEmpty(request.getRemoteAddr())){
                ip = request.getRemoteAddr();
            }
        }
        return ip;
    }

    public static void main(String[] args) {
        System.out.printf(getIp());
    }
}
