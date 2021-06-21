package com.mini.rpc.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://blog.csdn.net/weixin_43820352/article/details/110959008
 *
 * @author NNroc
 * @date 2020/6/20 14:40
 */
public class IPUtil {

  /**
   * 获取 ip
   *
   * @return
   * @throws SocketException
   * @throws UnknownHostException
   */
  public static String getIpAddress() throws UnknownHostException {
    if (isWindowsOS()) {
      return getWindowsLocalIp();
    } else {
      return getSelfPublicIp();
    }
  }

  /**
   * 判断当前操作系统
   *
   * @return
   */
  private static boolean isWindowsOS() {
    boolean isWindowsOS = false;
    String osName = System.getProperty("os.name");
    if (osName.toLowerCase().contains("windows")) {
      isWindowsOS = true;
    }
    return isWindowsOS;
  }

  private static String getWindowsLocalIp() throws UnknownHostException {
    return InetAddress.getLocalHost().getHostAddress();
  }

  /**
   * 获取 Linux 下的 内网 IP 地址
   *
   * @return IP地址
   * @throws SocketException
   */
  public static String getLinuxLocalIp() throws SocketException {
    String ip = "";
    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
      NetworkInterface intf = en.nextElement();
      String name = intf.getName();
      if (!name.contains("docker") && !name.contains("lo")) {
        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
          InetAddress inetAddress = enumIpAddr.nextElement();
          if (!inetAddress.isLoopbackAddress()) {
            String ipaddress = inetAddress.getHostAddress();
            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
              ip = ipaddress;
              System.out.println(ipaddress);
            }
          }
        }
      }
    }
    return ip;
  }

  /**
   * 获取用户真实 IP 地址（公网），不使用 request.getRemoteAddr(); 的原因是有可能用户使用了代理软件方式避免真实IP地址,
   * <p>
   * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
   * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
   * <p>
   * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
   * 192.168.1.100
   *
   * @param request
   * @return
   */
  //private static String getIpRealAddress(HttpServletRequest request) {
  //  String ip = request.getHeader("x-forwarded-for");
  //  if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
  //    ip = request.getHeader("Proxy-Client-IP");
  //  }
  //  if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
  //    ip = request.getHeader("WL-Proxy-Client-IP");
  //  }
  //  if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
  //    ip = request.getHeader("HTTP_CLIENT_IP");
  //  }
  //  if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
  //    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
  //  }
  //  if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
  //    ip = request.getRemoteAddr();
  //  }
  //  return ip;
  //}

  //    ----------------- 获取公网 ip -----------------
  /**
   * 外网ip地址
   */
  private static String publicIp;

  /**
   * 下面url返回地址都包含ip地址，为防止某个url失效，
   * 遍历url获取ip地址，有一个能成功获取就返回
   */
  private static String[] urls = {
      "http://whatismyip.akamai.com",
      "http://icanhazip.com",
      "http://members.3322.org/dyndns/getip",
      "http://checkip.dyndns.com/",
      "http://pv.sohu.com/cityjson",
      "http://ip.taobao.com/service/getIpInfo.php?ip=myip",
      "http://www.ip168.com/json.do?view=myipaddress",
      "http://www.net.cn/static/customercare/yourip.asp",
      "http://ipecho.net/plain",
      "http://myip.dnsomatic.com",
      "http://tnx.nl/ip",
      "http://ifconfig.me"
  };

  /**
   * ip地址的匹配正则表达式
   */
  private static String regEx = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

  private static Pattern pattern = Pattern.compile(regEx);

  /**
   * 获取本机外网地址
   *
   * @return
   */
  public static String getSelfPublicIp() {
    if (publicIp != null && !"".equals(publicIp.trim())) {
      return publicIp;
    }
    for (String url : urls) {
      //http访问url获取带ip的信息
      String result = getUrlResult(url);
      //正则匹配查找ip地址
      Matcher m = pattern.matcher(result);
      while (m.find()) {
        publicIp = m.group();
        //                System.out.println(url + " ==> " + publicIp);
        //只获取匹配到的第一个IP地址
        return publicIp;
      }
    }
    return null;
  }

  /**
   * http访问url
   */
  private static String getUrlResult(String url) {
    StringBuilder sb = new StringBuilder();
    BufferedReader in = null;
    try {
      URL realUrl = new URL(url);
      URLConnection connection = realUrl.openConnection();
      connection.setConnectTimeout(1000);
      connection.setReadTimeout(1000);
      in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        sb.append(line);
      }
    } catch (Exception e) {
      //            System.out.println(e.getMessage());
      return "";
    }
    return sb.toString();
  }
}