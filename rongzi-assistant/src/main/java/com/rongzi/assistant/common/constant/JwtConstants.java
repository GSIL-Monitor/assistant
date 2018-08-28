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

    Long EXPIRATION = 604800L;

    String CLAIMS_DATA_KEY = "data";

}
