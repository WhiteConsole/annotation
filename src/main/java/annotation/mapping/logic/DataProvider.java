package annotation.mapping.logic;

import annotation.mapping.platform.exception.InternalServerErrorException;
import annotation.mapping.platform.exception.WrongParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public final class DataProvider {
	private static final String ID = "id";
	private static final String DEMO_ID = "demoId";
	private static final String BODY = "body";
	private static final String STATUS = "status";
	private Map<String, Object> dataMap;
	private static DataProvider dataProvider = new DataProvider();

	private DataProvider() {
		dataMap = new HashMap<>();
		dataMap.put("key", "value");
	}

	public static DataProvider getInstance() {
		return dataProvider;
	}

	public String provideGetMethodData(Map<String, Object> req) {
		try {
			if (req.get(DEMO_ID) == null) {
				throw new WrongParameterException("demoId is required");
			}
			Map status = Map.of(DEMO_ID , req.get(DEMO_ID));
			dataMap.put(BODY, status);
			return new ObjectMapper().writeValueAsString(dataMap);
		} catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Something went wrong");
		}
	}

	public String provideDeleteMethodData(Map<String, Object> params) {
		try {
			if (params.get(ID) == null) {
				throw new WrongParameterException("id is required");
			}
			Map<String, Object> data = Map.of(STATUS, "deleted", BODY, Map.of(ID, params.get(ID)));
			return new ObjectMapper().writeValueAsString(data);
		} catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Something went wrong");
		}
	}

	public String providePutMethodData(String userId, Map<String, Object> request) {
		return buildPutAdPostData(userId, request, "updated");
	}

	public String providePostMethodData(String userId, Map<String, Object> request) {
		return buildPutAdPostData(userId, request, "added");
	}

	private String buildPutAdPostData(String userId, Map<String, Object> request, String status) {
		try {
			if (userId == null) {
				throw new WrongParameterException("userId header is required");
			}
			Map<String, Object> bodyMap = new HashMap<>(Map.of("userId", userId));
			bodyMap.putAll(request);
			Map<String, Object> data = Map.of(STATUS, status ,BODY, bodyMap);
			return new ObjectMapper().writeValueAsString(data);
		} catch (JsonProcessingException e) {
			throw new WrongParameterException("Request body should be in JSON format");
		}
	}
}
