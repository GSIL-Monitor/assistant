package com.rongzi.assistant.common.constant;

/**
 * jwt相关配置
 *
 * @author fengshuonan
 * @date 2017-08-23 9:23
 */
public interface JwtConstants {

    String AUTH_HEADER = "Authorization";

    String SECRET = "dfssrzSecret";

    // token过期时间一年
    Long EXPIRATION = 3600 * 24 * 365L;

    String CLAIMS_DATA_KEY = "data";

}

