package annotation.mapping.platform.servlet;

import annotation.mapping.platform.exception.InternalServerErrorException;
import annotation.mapping.platform.exception.NotFoundException;
import annotation.mapping.platform.exception.WrongParameterException;
import annotation.mapping.platform.resolver.AnnotationResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class AnnotationServlet extends HttpServlet {
	private AnnotationResolver resolver;

	public AnnotationServlet() {
		resolver = new AnnotationResolver();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
		processRequest(req, response);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		processRequest(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		processRequest(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		processRequest(req, resp);
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse response)
			throws IOException {
		try {
			String result = (String) resolver.resolve(req, response);
			writeData(response, result, 200);
		} catch (InternalServerErrorException | IOException ex) {
			writeData(response, getExceptionalResponse("Internal server error", ex.getMessage()), 500);
		} catch (WrongParameterException ex) {
			writeData(response, getExceptionalResponse("Validation error", ex.getMessage()), 400);
		} catch (NotFoundException ex) {
			writeData(response, getExceptionalResponse("Not found", ex.getMessage()), 404);
		}
	}

	private String getExceptionalResponse(String message, String cause) {
		Map<String, Object> dataMap = Map.of("message", message, "cause", cause);
		try {
			return new ObjectMapper().writeValueAsString(dataMap);
		} catch (JsonProcessingException e) {
			return "{\n" +
					"\t\"message\": \"Internal server error\"\n" +
					"}";
		}
	}

	private void writeData(HttpServletResponse response, String result, int status) throws IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(status);
		out.print(result);
		out.flush();
	}
}
