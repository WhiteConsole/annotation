package annotation.mapping.util;

import annotation.mapping.platform.exception.WrongParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class AnnotationUtil {
	private static ObjectMapper mapper = new ObjectMapper();

	private AnnotationUtil() {
	}

	public static Map<String, Object> toMap(String data) {
		try {
			return mapper.readValue(data, Map.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		throw new WrongParameterException("Invalid request body");
	}

}
