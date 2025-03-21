package belousov.eu.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Matcher;

@FunctionalInterface
public interface RequestHandler {
    void handle(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws IOException;
}
