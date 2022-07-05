package vip.aquan.generatebarcode.shared.util;


import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

/**
 * RSA Encryption Utilities
 */
public class RSAUtils {
    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static KeyPairGenerator keyPairGenerator = null;
    private static KeyFactory keyFactory = null;
    private static KeyPair keyPair = null;
    private static Base64.Decoder decoder = Base64.getDecoder();
    private static Base64.Encoder encoder = Base64.getEncoder();

    static {
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyFactory = KeyFactory.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private RSAUtils() {
    }

    public static synchronized String[] generateKeyPair() {
        try {
            keyPairGenerator.initialize(KEY_SIZE, new SecureRandom(UUID.randomUUID().toString().replaceAll("-", "").getBytes()));
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicKeyString = encoder.encodeToString(rsaPublicKey.getEncoded());
        String privateKeyString = encoder.encodeToString(rsaPrivateKey.getEncoded());
        return new String[]{publicKeyString, privateKeyString};
    }

    private static Cipher getRSACipher() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        return cipher;
    }

    public static String encryptByPublic(byte[] content, PublicKey publicKey) {
        try {
            Cipher cipher = getRSACipher();
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
            int splitLength = ((RSAPublicKey) publicKey).getModulus().bitLength() / 8 - 11;
            byte[][] arrays = splitBytes(content, splitLength);
            StringBuffer stringBuffer = new StringBuffer();
            for (byte[] array : arrays) {
                stringBuffer.append(bytesToHexString(cipher.doFinal(array)));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encryptByPrivate(byte[] content, PrivateKey privateKey) {
        try {
            Cipher cipher = getRSACipher();
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            int splitLength = ((RSAPrivateKey) privateKey).getModulus().bitLength() / 8 - 11;
            byte[][] arrays = splitBytes(content, splitLength);
            StringBuffer stringBuffer = new StringBuffer();
            for (byte[] array : arrays) {
                stringBuffer.append(bytesToHexString(cipher.doFinal(array)));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptByPrivate(String content, PrivateKey privateKey) {
        try {
            Cipher cipher = getRSACipher();
            cipher.init(Cipher.DECRYPT_MODE, privateKey,
                    new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
            int splitLength = ((RSAPrivateKey) privateKey).getModulus().bitLength() / 8;
            byte[] contentBytes = hexStringToBytes(content);
            byte[][] arrays = splitBytes(contentBytes, splitLength);
            StringBuffer stringBuffer = new StringBuffer();
            for (byte[] array : arrays) {
                stringBuffer.append(new String(cipher.doFinal(array), "UTF-8"));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptByPublic(String content, PublicKey publicKey) {
        try {
            Cipher cipher = getRSACipher();
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            int splitLength = ((RSAPublicKey) publicKey).getModulus().bitLength() / 8;
            byte[] contentBytes = hexStringToBytes(content);
            byte[][] arrays = splitBytes(contentBytes, splitLength);
            StringBuffer stringBuffer = new StringBuffer();
            for (byte[] array : arrays) {
                stringBuffer.append(new String(cipher.doFinal(array), "UTF-8"));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAPublicKey getPublicKey(String publicKeyDataBase64) {
        try {
            byte[] keyBytes = decoder.decode(publicKeyDataBase64);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
            return (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAPrivateKey getPrivateKey(String privateKeyDataBase64) {
        try {
            byte[] keyBytes = decoder.decode(privateKeyDataBase64);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
            return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[][] splitBytes(byte[] bytes, int splitLength) {
        int remainder = bytes.length % splitLength;
        int quotient = remainder != 0 ? bytes.length / splitLength + 1 : bytes.length / splitLength;
        byte[][] arrays = new byte[quotient][];
        byte[] array = null;
        for (int i = 0; i < quotient; i++) {
            if (i == quotient - 1 && remainder != 0) {
                array = new byte[remainder];
                System.arraycopy(bytes, i * splitLength, array, 0, remainder);
            } else {
                array = new byte[splitLength];
                System.arraycopy(bytes, i * splitLength, array, 0, splitLength);
            }
            arrays[i] = array;
        }
        return arrays;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(0xFF & bytes[i]);
            if (temp.length() < 2) {
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    public static byte[] hexStringToBytes(String hex) {
        int len = (hex.length() / 2);
        hex = hex.toUpperCase();
        byte[] result = new byte[len];
        char[] chars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(chars[pos]) << 4 | toByte(chars[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}