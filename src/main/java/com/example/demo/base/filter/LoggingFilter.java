package com.example.demo.base.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.UUID;

@Component
@Order(1)
public class LoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String DEFAULT_REQUEST_ID_HEADER = "X-Request-ID";
    private static final String MDC_REQUEST_ID = "requestId";
    private static final String DEFAULT_ERROR_REQUEST_PATH = "C:\\temp\\error-request";

    private final String requestIdHeaderName;
    private final String errorRequestPath;

    public LoggingFilter(
            @Value("${base.request-id-header:" + DEFAULT_REQUEST_ID_HEADER + "}") String requestIdHeaderName,
            @Value("${base.error-request-savepath:" + DEFAULT_ERROR_REQUEST_PATH + "}") String errorRequestPath) {
        this.requestIdHeaderName = requestIdHeaderName;
        this.errorRequestPath = errorRequestPath;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        String requestId = getRequestId(wrappedRequest);
        MDC.put(MDC_REQUEST_ID, requestId);

        logger.info("Request start requestId={} method={} path={}", requestId, wrappedRequest.getMethod(), wrappedRequest.getRequestURI());

        long startNanos = System.nanoTime();
        try {
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            try {
                long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
                logger.info("Request end requestId={} path={} status={} durationMs={}",
                        requestId, wrappedRequest.getRequestURI(), response.getStatus(), durationMs);

                if (response.getStatus() >= 400) {
                    preserveRequest(wrappedRequest, response.getStatus(), requestId);
                }
            } finally {
                MDC.remove(MDC_REQUEST_ID);
            }
        }
    }
    // エラーリクエストの内容をファイルに保存する（失敗した場合はログに出力）
    private void preserveRequest(ContentCachingRequestWrapper request, int status, String requestId) {
        String requestSummary = buildRequestSummary(request, status, requestId);
        Path outputDir = Paths.get(errorRequestPath);

        if (Files.exists(outputDir) && Files.isDirectory(outputDir)) {
            try {
                Files.createDirectories(outputDir);
                Path outFile = outputDir.resolve(requestId + ".req");
                Files.writeString(outFile, requestSummary, StandardCharsets.UTF_8);
                logger.info("Preserved error request to {}", outFile.toAbsolutePath());
                return;
            } catch (IOException e) {
                logger.warn("Failed to write preserved request file, fallback to log", e);
            }
        } else {
            logger.info("Error request path does not exist or is not directory: {}", outputDir);
        }

        logger.info("Preserved request fallback log (status={}, requestId={}): {}", status, requestId, requestSummary);
    }

    // リクエストの内容を文字列としてまとめる
    private String buildRequestSummary(ContentCachingRequestWrapper request, int status, String requestId) {
        StringBuilder sb = new StringBuilder();
        sb.append("requestId:").append(requestId).append("\n");
        sb.append("status:").append(status).append("\n");
        sb.append("method:").append(request.getMethod()).append("\n");
        sb.append("uri:").append(request.getRequestURI()).append("\n");
        sb.append("query:").append(request.getQueryString()).append("\n");
        sb.append("headers:\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                sb.append("  ").append(name).append(": ").append(request.getHeader(name)).append("\n");
            }
        }
        sb.append("body:\n");
        byte[] body = request.getContentAsByteArray();
        if (body != null && body.length > 0) {
            sb.append(new String(body, StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    // リクエストIDをヘッダーから取得、なければ新規生成　
    private String getRequestId(HttpServletRequest request) {
        String requestIdFromHeader = request.getHeader(requestIdHeaderName);
        if (requestIdFromHeader != null && !requestIdFromHeader.isBlank()) {
            return requestIdFromHeader;
        }
        return UUID.randomUUID().toString();
    }
}
