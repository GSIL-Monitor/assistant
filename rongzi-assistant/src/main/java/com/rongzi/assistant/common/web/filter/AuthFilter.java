package com.rongzi.assistant.common.web.filter;

import com.rongzi.assistant.common.constant.FilterConstants;
import com.rongzi.assistant.common.exception.AssistantExceptionEnum;
import com.rongzi.assistant.common.tips.AssistantTip;
import com.rongzi.assistant.common.web.context.UserContextHolder;
import com.rongzi.assistant.manager.TokenManager;
import com.rongzi.assistant.model.UserInfo;
import com.rongzi.core.util.RenderUtil;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter extends OncePerRequestFilter {

    protected PathMatcher pathMatcher = new AntPathMatcher();

    private String[] excludesPattern = new String[]{
            "/api/account/login",
            "/favicon.ico",
            "/druid/**",
            "/swagger*",
            "/actuator/**",
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isExclusion(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get authorization from Http request
        final String authHeader = request.getHeader("Authorization");

        // If the Http request is OPTIONS then just return the status code 200
        // which is HttpServletResponse.SC_OK in this code
        if (FilterConstants.REQUEST_METHOD_OPTIONS.equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
        }
        // Except OPTIONS, other request should be checked by JWT
        else {

            // Check the authorization, check if the token is started by "Bearer "
            if (authHeader == null || !authHeader.startsWith(FilterConstants.AUTHHEADER)) {
                AssistantTip errorTip = AssistantTip.error(AssistantExceptionEnum.INVALID_TOKEN.getCode(), AssistantExceptionEnum.INVALID_TOKEN.getMessage());
                RenderUtil.renderJson(response, errorTip);
                return;
            }

            try {
                UserInfo userInfo = TokenManager.getUserInfoFromToken(authHeader);
                // Add the userInfo to request
                UserContextHolder.setCurrentUserInfo(userInfo);
            } catch (Exception e) {
                AssistantTip errorTip = AssistantTip.error(AssistantExceptionEnum.INVALID_TOKEN.getCode(), AssistantExceptionEnum.INVALID_TOKEN.getMessage());
                RenderUtil.renderJson(response, errorTip);
                return;
            }

            filterChain.doFilter(request, response);
        }
    }

    private boolean isExclusion(String requestURI) {
        if (excludesPattern == null || requestURI == null) {
            return false;
        }

        for (String pattern : excludesPattern) {
            if (pathMatcher.match(pattern, requestURI)) {
                return true;
            }
        }

        return false;
    }
}
