package xyz.kbws.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSignerUtil;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.exception.ThrowUtils;

/**
 * @author kbws
 * @date 2024/11/25
 * @description:
 */
public class JwtUtil {

    public static String createToken(String userId, String userRole) {
        return JWT.create()
                .setPayload("userId", userId)
                .setPayload("userRole", userRole)
                .setSigner(JWTSignerUtil.none())
                .sign();
    }

    public static String getUserId(String token) {
        verifyToken(token);
        JWT jwt = JWT.of(token);
        return (String) jwt.getPayload("userId");
    }

    public static String getUserRole(String token) {
        verifyToken(token);
        JWT jwt = JWT.of(token);
        return (String) jwt.getPayload("userRole");
    }

    public static void verifyToken(String token) {
        boolean verify = JWT.of(token).verify();
        ThrowUtils.throwIf(!verify, ErrorCode.TOKEN_ERROR);
    }
}
