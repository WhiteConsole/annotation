package annotation.mapping.platform.resolver;

import annotation.mapping.annotation.Controller;
import annotation.mapping.annotation.arguments.*;
import annotation.mapping.annotation.httpmethod.DeleteMapping;
import annotation.mapping.annotation.httpmethod.GetMapping;
import annotation.mapping.annotation.httpmethod.PostMapping;
import annotation.mapping.annotation.httpmethod.PutMapping;
import annotation.mapping.platform.exception.InternalServerErrorException;
import annotation.mapping.platform.exception.NotFoundException;
import annotation.mapping.platform.exception.WrongParameterException;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationResolver {

	private String buildPath(String[] paths, int start) {
		String path = "";
		if (paths.length > 1 && start <= paths.length) {
			for (int x = start; x < paths.length; x++) {
				path += "/" + paths[x];
			}
		}
		return path;
	}

	public Object resolve(HttpServletRequest servletRequest, HttpServletResponse response) {
		String requestedPath = servletRequest.getRequestURI();
		Object result = null;
		Reflections reflections = new Reflections("annotation.mapping");
		Set<Class<?>> annotatedCassList = reflections.getTypesAnnotatedWith(Controller.class);
		for (Class clazz : annotatedCassList) {
			Controller controller = (Controller) clazz.getAnnotation(Controller.class);
			if (StringUtils.isBlank(controller.value())) {
				result = resolveMethod(clazz, requestedPath, servletRequest, response);
			} else {
				String [] paths = requestedPath.substring(1).split("/");
				String classPath = "";
				for (int index = 0; index < paths.length; index++) {
					classPath += "/" + paths[index];
					if (controller.value().equals(classPath)) {
						result = resolveMethod(clazz, buildPath(paths, index + 1), servletRequest, response);
						if (result != null) {
							return result;
						}
					}
				}
			}

			if (result != null) {
				return result;
			}
		}
		throw new NotFoundException("Resource path doesn't exist");
	}

	private Object resolveMethod(Class clazz, String methodPath, HttpServletRequest servletRequest,
								 HttpServletResponse response) {
		try {
			String httpMethod = servletRequest.getMethod();
			final Object newInstance = clazz.newInstance();
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				if ("GET".equalsIgnoreCase(httpMethod) && method.getAnnotation(GetMapping.class) != null &&
						method.getAnnotation(GetMapping.class).value().equals(methodPath)) {
					return invokeMethod(newInstance, method, servletRequest, response);
				} else if ("POST".equalsIgnoreCase(httpMethod) && method.getAnnotation(PostMapping.class) != null &&
						method.getAnnotation(PostMapping.class).value().equals(methodPath)) {
					return invokeMethod(newInstance, method, servletRequest, response);
				} else if ("PUT".equalsIgnoreCase(httpMethod) && method.getAnnotation(PutMapping.class) != null &&
						method.getAnnotation(PutMapping.class).value().equals(methodPath)) {
					return invokeMethod(newInstance, method, servletRequest, response);
				} else if ("DELETE".equalsIgnoreCase(httpMethod) && method.getAnnotation(DeleteMapping.class) != null &&
						method.getAnnotation(DeleteMapping.class).value().equals(methodPath)) {
					return invokeMethod(newInstance, method, servletRequest, response);
				}
			}
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException ex) {
			if (ex.getCause() instanceof WrongParameterException || ex.getCause() instanceof NotFoundException ||
					ex.getCause() instanceof InternalServerErrorException) {
				throw (RuntimeException) ex.getCause();
			} else {
				throw new InternalServerErrorException("Internal server error");
			}
		}
		return null;
	}

	private Object invokeMethod(final Object newInstance, final Method method, HttpServletRequest servletRequest,
								HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {
		List<Object> invokeRequest = buildMethodArguments(method, servletRequest, response);
		return method.invoke(newInstance, invokeRequest.toArray());
	}

	private void resolveAnnotatedParams(Annotation annotation, HttpServletRequest servletRequest,
										HttpServletResponse response, List<Object> invokeRequest) {
		if (annotation instanceof HttpHeader) {
			invokeRequest.add(servletRequest.getHeader(((HttpHeader) annotation).name()));
		} else if (annotation instanceof RequestPath) {
			invokeRequest.add(servletRequest.getRequestURI());
		} else if (annotation instanceof RequestMap) {
			invokeRequest.add(servletRequest.getParameterMap());
		} else if (annotation instanceof HttpRequest) {
			invokeRequest.add(servletRequest);
		} else if (annotation instanceof HttpResponse) {
			invokeRequest.add(response);
		} else if (annotation instanceof Body) {
			try {
				if ("application/json".equalsIgnoreCase(servletRequest.getContentType())) {
					BufferedReader reader = servletRequest.getReader();
					invokeRequest.add(reader.lines().collect(Collectors.joining()));
				} else {
					throw new WrongParameterException("Body should be in JSON format");
				}
			} catch (IOException e) {
				throw new WrongParameterException("Invalid request body");
			}
		} else {
			invokeRequest.add(null);
		}
	}

	private void resolveParams(Class clazz, List<Object> invokeRequest, HttpServletRequest servletRequest,
							   HttpServletResponse response) {
		if ("HttpServletRequest".equals(clazz.getSimpleName())) {
			invokeRequest.add(servletRequest);
		} else if ("HttpServletResponse".equals(clazz.getSimpleName())) {
			invokeRequest.add(response);
		} else {
			invokeRequest.add(null);
		}
	}

	private List<Object> buildMethodArguments(Method method, HttpServletRequest servletRequest,
											  HttpServletResponse response) {
		List<Object> invokeRequest = new ArrayList<>();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Class<?>[] paramsType = method.getParameterTypes();
		for (int index = 0; index < parameterAnnotations.length; index++) {
			Annotation[] annotations = parameterAnnotations[index];
			if (annotations.length > 0) {
				resolveAnnotatedParams(annotations[0], servletRequest, response, invokeRequest);
			} else {
				resolveParams(paramsType[index], invokeRequest, servletRequest, response);
			}
		}
		return invokeRequest;
	}
}
