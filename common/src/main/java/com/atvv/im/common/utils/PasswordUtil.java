package com.atvv.im.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.Digester;


/**
 * @author hjq
 * @date 2023/10/27 11:07
 */
public class PasswordUtil {
    /**
     * 私钥
     */
    private static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKYLuEMEWmDCNmnVwQ65mvz6mQT5Hf+a4hbemPmDxYKd2ZcCclaIP9hYAkn+gnp2p/NsB+Kt33afW5gzVSSW/nXeuYomghdduMguC4Ctip1k/+Cdfnl642p8WclAnrnT+i3OHx7c1chLWBVQQLm23DXFcgUzO55CGfLUxRSBptqbAgMBAAECgYAC2jaBkyAdPcR9KgkMzBH9hoq3fJuy9AS55ap7Ey49DUTD/vg325mC/ejGVtAyoaLSa6580JFJEpDUmw0kHH+OKGdpJpfkT+FUZO5aI/vnAQaHZ0R6+n3m4QZxc9diNIPmfkinzCffxGFFf4r8Z8oc6g+Wzg+x164TXg+isYKXAQJBAOaoYsncZJGwyp22g75qISTaN6xbGc2zzAqCqDhMCQ0cddZZ3LH5opYBFHDqBi4OJ9ENh2H1lzB095OowAvDWN8CQQC4SgUWSpPsfBqRsSW/HfyeT2b2X7iNEYoupUiy9wVjk3FaMliOfXziv0Is9GXWs9kT8oROJFAkL15diKTnIGnFAkEArnq71+T+snKgonLY76ZKkhz8NkXnnAIhG+ZAJ+3kfuWDgfUSDBNBWOVSOCPfLuGIXwwz1/c2OfKRAUKKm5tW0wJAPIUCWqmvHlA7IXE9Zh/g39RPicUxhBIogN0CnNMGUAcRiH9UGacYJhaNEtpHv1Rci3JvBIJkVx2/LZQ8IzK/yQJBAN1NVBpFohgNf87VdxDfjNL5BBD5+wq2MzTMHZykDkCxqwg7BHUlEWMMNniaoC4+sqjPLAPV0J2jTyfdiF4qpQs=";
    /**
     * 公钥
     */
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmC7hDBFpgwjZp1cEOuZr8+pkE+R3/muIW3pj5g8WCndmXAnJWiD/YWAJJ/oJ6dqfzbAfird92n1uYM1Uklv513rmKJoIXXbjILguArYqdZP/gnX55euNqfFnJQJ650/otzh8e3NXIS1gVUEC5ttw1xXIFMzueQhny1MUUgabamwIDAQAB";


    public static String RsaEncode(String text) {
        RSA rsa = new RSA(AsymmetricAlgorithm.RSA_ECB_PKCS1.getValue(), PRIVATE_KEY, PUBLIC_KEY);
        return rsa.encryptBase64(text, KeyType.PublicKey);
    }

    public static String RsaDecode(String text) {
        RSA rsa = new RSA(AsymmetricAlgorithm.RSA_ECB_PKCS1.getValue(), PRIVATE_KEY, PUBLIC_KEY);
        return rsa.decryptStr(text, KeyType.PrivateKey);
    }

    public static String md5Encode(String text){
        return DigestUtil.md5Hex(text);
    }
}
