package pe.gob.hospitalcayetano.msseguridad.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pe.gob.hospitalcayetano.msseguridad.servletloggin.CacheBodyHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LoggerFilter extends OncePerRequestFilter {

    @Value("${lista.data.sensible:#{T(java.util.Collections).emptyList()}}")
    private List<String> listaDataSensible;

    @Value("${lista.data.extensa:#{T(java.util.Collections).emptyList()}}")
    private List<String> listaDataExtensa;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURL().toString().contains("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        long tiempoInicio = System.currentTimeMillis();

        StringBuilder informacionEndpoint = new StringBuilder()
                .append(request.getMethod())
                .append(" ")
                .append(request.getRequestURL());

        if (request.getQueryString() != null) {
            informacionEndpoint.append("?").append(ocultarDataSensibleQueryParam(request.getQueryString()));
        }

        logger.info("=> " + informacionEndpoint);

        //ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        CacheBodyHttpServletRequest wrappedRequest = new CacheBodyHttpServletRequest(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        imprimirRequestBody(wrappedRequest, request);

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        //imprimirRequestBody(wrappedRequest, request);

        imprimirResponseBody(wrappedResponse, response);

        logger.info("<= " + informacionEndpoint + " - response HTTP=" + response.getStatus() + " en " + (tiempoTranscurrido/1000.0) + "s");

        wrappedResponse.copyBodyToResponse();
    }

    private void imprimirRequestBody(CacheBodyHttpServletRequest wrappedRequest, HttpServletRequest request) {
        //String requestBodyString = this.obtenerDataString(wrappedRequest.getContentAsByteArray(), request.getCharacterEncoding());
        String requestBodyString = wrappedRequest.getReader().lines().collect(Collectors.joining());
        if (requestBodyString == null) return;
        StringBuilder requestBody = new StringBuilder();
        requestBody.append(limpiarEspciosBlancoYSantoLinea(requestBodyString));

        if (listaDataSensible != null && !listaDataSensible.isEmpty()) {
            String informacionOcultada = ocultarDataSensible(requestBody.toString());

            requestBody.setLength(0);
            requestBody.append(informacionOcultada);
        }

        if (listaDataExtensa != null && !listaDataExtensa.isEmpty()) {
            String informacionRecortada = reducirDataExtensa(requestBody.toString());

            requestBody.setLength(0);
            requestBody.append(informacionRecortada);
        }

        logger.info("Request body:\n" + requestBody);
    }

    private void imprimirResponseBody(ContentCachingResponseWrapper wrappedResponse, HttpServletResponse response) {
        String responseBodyString = this.obtenerDataString(wrappedResponse.getContentAsByteArray(), response.getCharacterEncoding());
        StringBuilder responseBody = new StringBuilder();
        responseBody.append(limpiarEspciosBlancoYSantoLinea(responseBodyString));

        if (listaDataSensible != null && !listaDataSensible.isEmpty()) {
            String informacionOcultada =  ocultarDataSensible(responseBody.toString());

            responseBody.setLength(0);
            responseBody.append(informacionOcultada);
        }

        if (listaDataExtensa != null && !listaDataExtensa.isEmpty()) {
            String informacionRecortada = reducirDataExtensa(responseBody.toString());

            responseBody.setLength(0);
            responseBody.append(informacionRecortada);
        }

        logger.info("Response body:\n" + responseBody);
    }

    private String obtenerDataString(byte[] buf, String charsetName) {
        if (buf == null || buf.length == 0) return null;
        try {
            return new String(buf, charsetName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static String limpiarEspciosBlancoYSantoLinea(String json) {
        String jsonATratar = json.replaceAll("(\n|\r)", "");
        boolean quoted = false;

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < jsonATratar.length(); i++) {
            char c = jsonATratar.charAt(i);

            if (c == '\"') quoted = !quoted;

            if (quoted || !Character.isWhitespace(c)) builder.append(c);
        }

        return builder.toString();
    }

    private String ocultarDataSensibleQueryParam(String data) {
        if (listaDataSensible == null || listaDataSensible.isEmpty()) return data;

        String dataSensibleRegex = listaDataSensible.stream()
                .map(field -> "(?<=" + field + "=)([^&]*)")
                .reduce((a, b) -> a + "|" + b)
                .orElse("");

        return data.replaceAll(dataSensibleRegex, "*****");
    }

    private String ocultarDataSensible(String data) {
        String dataSensibleRegex = listaDataSensible.stream()
                .map(field -> "(?<=\\\"" + field + "\\\":\\\")[^\"]+?(?=\\\")")
                .reduce((a, b) -> a + "|" + b)
                .orElse("");

        return data.replaceAll(dataSensibleRegex, "*****");
    }

    private String reducirDataExtensa(String data) {
        String dataExtensaRegex = listaDataExtensa.stream()
                .map(field -> "(?<=\\\"" + field + "\\\":\\\")[^\"]+?(?=\\\")")
                .reduce((a, b) -> a + "|" + b)
                .orElse("");

        Pattern pattern = Pattern.compile(dataExtensaRegex);
        Matcher matcher = pattern.matcher(data);

        StringBuilder updatedRequestBody = new StringBuilder();

        while (matcher.find()) {
            String sensitiveValue = matcher.group();
            String truncatedValue = sensitiveValue.length() > 10
                    ? sensitiveValue.substring(0, 10) + "..."
                    : sensitiveValue;
            matcher.appendReplacement(updatedRequestBody, truncatedValue);
        }

        matcher.appendTail(updatedRequestBody);

        return updatedRequestBody.toString();
    }

}
