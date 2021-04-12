package au.com.dius.pactworkshop.provider;

import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authHeader = ((HttpServletRequest) request).getHeader("Authorization");
        if (authHeader == null) {
            ((HttpServletResponse) response).sendError(401, "Unauthorized");
            return;
        }
        authHeader = authHeader.replaceAll("Bearer ", "");
        if (!isValidAuthTimestamp(authHeader)) {
            ((HttpServletResponse) response).sendError(401, "Unauthorized");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isValidAuthTimestamp(String timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            Date headerDate = formatter.parse(timestamp);
            long diff = (System.currentTimeMillis() - headerDate.getTime()) / 1000;
            return diff >= 0 && diff <= 3600;
        } catch (ParseException e) {
            e.printStackTrace();
        }
            return false;
    }
}
