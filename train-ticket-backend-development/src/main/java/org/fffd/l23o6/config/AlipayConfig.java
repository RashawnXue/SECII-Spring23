package org.fffd.l23o6.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    private String appId;
    private String alipayPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;
    private String returnUrl;
    private String signType;
    private String charset;
    private String gatewayUrl;
    public AlipayConfig() {
        appId = "9021000122699481";
        notifyUrl = "http://dpbres.natappfree.cc/v1/alipay/notify";
        returnUrl = "http://localhost:5173/";
        signType = "RSA2";
        charset = "utf-8";
        gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
        alipayPrivateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCRBwBhCn6EMmUJLDQZWs7fc/M/cLp+FzPPDfxPySNhJ5xcWlypTcHScRqexr5TvfXouGFXZWdhW7o3XH4Vv2G1qp6hGCDrjlP6qmXos3FoXRsxZrbl6jThMHJhdDsBQAhy2Z1v1sDI3VuYr7c7mORJeo1+Ih5Fv1RTZp8uvmViAxAxe7YiRBTkxACC2PwOmrASN0V/WKNmZfG4xWyYU0CL5W/Iv7WjOpGBWMKhRDmqWYgmvPTZ9Z1j5d2Zpo/SsfSq+QFV+SdStyRQGurilVb3u4flf9EGy4sTbVJLkorkNTdwGDgWCMAJ3d684yEwO8PVk55h+KLltuDLQ7kXCrbfAgMBAAECggEAYO+6HCLkj74TdH8cT7o3vnoFI+lYbgFaOtKRTE8YG89bHwuzFeMIY34AGhKjktvM64GaZs07vYKPRIPJLwYd7Eyf/PNgxbH7hfexaEnQyr4499s88w6Jy98TBPqomdxQCVxVdDSp5Vi36aXBps85/7cvrhhl/Z62YYydZ6QRpeF8DlVZ3uVZsw24S1SCdfxMPptD83CDkPao6EqYHpf1wM8SSrGDcgcSEkdWSflIIjLgNSTyZQvc8TimyLjHYhtml3413QUOcAUZVFZMhQQDeqswUvqv435ERiFByvfhC6hyrwowsRVVCzC0nokuiOjFSBVwWnr14AWXbhWBoIHyuQKBgQDeVW8DSCb+rQvjMFkQP78y5hJu7oW+88w/2ytYCbj9zlxDAA198w4vCImb1k8pXlGE64GxEUI+FoLlXmyerQL1WCZoFZu1qsy7iapui9Ow8hR8uRYyT/vvhywuZ0AaIeFeEhMFYPensUN5revajqHbm55wA1Uq4iHm/MxB1ayDawKBgQCm/NscRB+8jK8Q3MWVc/c9vEkCf1XpKrkCFO/CCxk9J5/d7l/EtuKdPmmo7s6w18HkmLlmeXiQN32/SND+TAufOVxz3ZDAmOqnFophtKB2gvUY5Pf3DVxklPt/KdbchtN4W8bj2fvT5yvC9bKAmSGfhWISRBZJEA/qRNy+1uQrXQKBgAmTtod2qS9ZNuTfZUa86DiCDpM2tQ4npEmv4CsXqB9Or0y3iN0/BgJipMLQtXI7vNkz588/xhG0mIjWy2j+XN1Coczj+WQW6mC9qli+ryy1EMrakikyptCHixCoo+s8QF/z+VvG9IQFr7ljE4DDmhdCmc2LZzTErt8rToUbdiXnAoGAH/ZF+iMP8Ab9ezzGlNSdfjOLxe6ZgweqYbugKIP3UL1E3DXEHuLifTZ1AuCyryac9iRFWFfnu7LjXrmRRxtHHjLcf1DpY/Pg9hpaYlkwXX6zaxBoZeEozhe2C6D2fz9POrpWV0Zh7S1SMv7UK8kjpmThYJ2nplRst2p21hBLnAECgYBimfA7I2vX8ZiKh6uVmFtBD9u2HXE8DsDosc6mrPqxWNdwTjluo6ZMjY+UZIgJxwezoG8nCbp/xBqrScVzXe4U/g9PiTW1WHXQxJDSi57dzb5odf/hShixjKbUTthwODf0JMJzuqQsMXGqqV6TwXStQJKKS9aLrM/eA8LtnphNrg==";
        alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkQcAYQp+hDJlCSw0GVrO33PzP3C6fhczzw38T8kjYSecXFpcqU3B0nEansa+U7316LhhV2VnYVu6N1x+Fb9htaqeoRgg645T+qpl6LNxaF0bMWa25eo04TByYXQ7AUAIctmdb9bAyN1bmK+3O5jkSXqNfiIeRb9UU2afLr5lYgMQMXu2IkQU5MQAgtj8DpqwEjdFf1ijZmXxuMVsmFNAi+VvyL+1ozqRgVjCoUQ5qlmIJrz02fWdY+XdmaaP0rH0qvkBVfknUrckUBrq4pVW97uH5X/RBsuLE21SS5KK5DU3cBg4FgjACd3evOMhMDvD1ZOeYfii5bbgy0O5Fwq23wIDAQAB";
//        alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn0zZu0uG1u/AG7VHBqTMu1vcWWBIz2ZS/Kml0Qvo1SpEy6oOl5SySA/gU5QV6jpyyso/DqUJOzjbd5qbr/IOIzFG3+/wuGzEN1aBhxM58Gd9Cui3rbZG1XsfDdtOpXKNZg8SXrJtewr0eJSobv7C4JyeJXtjzIqBLIRUyRuHswYyWEPYh6z7OWZsQ6U6xSreCG/LX7trzHWjG7+z4r/QJEaPYx/CuY7ba8c26z3fR/2+HC61MnG7dXBpSmP2ZlqnMf38sob4AYBI9Zc0z29t4OVG7VLrEeYKwPJ2Zs+ki79HiFZrqVUXTnTW+unlCdEFpI3yfHFc/62VseHaRShZWwIDAQAB";
    }
}