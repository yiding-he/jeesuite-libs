package com.jeesuite.security;

import com.jeesuite.cache.redis.JedisProviderFactory;
import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.common.util.TokenGenerator;
import com.jeesuite.security.SecurityConstants.CacheType;
import com.jeesuite.security.cache.LocalCache;
import com.jeesuite.security.cache.RedisCache;
import com.jeesuite.security.model.AccessToken;
import com.jeesuite.security.model.BaseUserInfo;

import java.io.Serializable;

public class SecurityOauth2Manager {

    private static final Integer TOKEN_EXPIRED_SECONDS = ResourceUtils.getInt(SecurityConstants.CONFIG_OAUTH2_TOKEN_EXPIRE_IN, 3600 * 24);

    private Cache cache;

    private Cache tokenCache;

    public SecurityOauth2Manager(SecurityDecisionProvider decisionProvider) {
        if (CacheType.redis == decisionProvider.cacheType()) {
            JedisProviderFactory.addGroupProvider("auth");
            this.cache = new RedisCache("security.oauth2.authCode", 180);
            this.tokenCache = new RedisCache("security.oauth2.token", TOKEN_EXPIRED_SECONDS);
        } else {
            this.cache = new LocalCache(180);
            this.tokenCache = new LocalCache(TOKEN_EXPIRED_SECONDS);
        }
    }

    public String createOauth2AuthCode(Serializable userId) {
        String authCode = TokenGenerator.generateWithSign();
        cache.setString(authCode, userId.toString());
        return authCode;
    }

    public String authCode2UserId(String authCode) {
        return cache.getString(authCode);
    }

    public AccessToken createAccessToken(BaseUserInfo user) {
        AccessToken accessToken = new AccessToken();
        accessToken.setAccess_token(TokenGenerator.generate());
        accessToken.setRefresh_token(TokenGenerator.generate());
        accessToken.setExpires_in(TOKEN_EXPIRED_SECONDS);
        tokenCache.setObject(accessToken.getAccess_token(), accessToken);
        return accessToken;
    }
}
