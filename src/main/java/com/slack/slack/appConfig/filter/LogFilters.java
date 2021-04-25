package com.slack.slack.appConfig.filter;

/**
 * 로그 필터 인터페이스
 * */
public interface LogFilters {
    // 필터 로직
    //void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException;

    // 필터에서 제외되어야 할 url 정의. request으로부터 가져와서 제외 대상일 때 true 리턴
    //boolean shouldNotFilter(HttpServletRequest request) throws ServletException;
}
