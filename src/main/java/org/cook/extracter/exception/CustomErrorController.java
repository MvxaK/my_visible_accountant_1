package org.cook.extracter.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {

        String path = (String) request.getAttribute(jakarta.servlet.RequestDispatcher.ERROR_REQUEST_URI);
        if(path != null && path.equals("/error/403")){
            return new ModelAndView("error/403");
        }

        Object status = request.getAttribute(jakarta.servlet.RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return new ModelAndView("error/404");
            }

            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return new ModelAndView("error/403");
            }
        }

        return new ModelAndView("error/general");
    }
}
