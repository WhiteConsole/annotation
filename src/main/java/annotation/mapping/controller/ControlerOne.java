package annotation.mapping.controller;


import annotation.mapping.annotation.Controller;
import annotation.mapping.annotation.arguments.Body;
import annotation.mapping.annotation.arguments.HttpHeader;
import annotation.mapping.annotation.arguments.RequestMap;
import annotation.mapping.annotation.httpmethod.DeleteMapping;
import annotation.mapping.annotation.httpmethod.GetMapping;
import annotation.mapping.annotation.httpmethod.PostMapping;
import annotation.mapping.annotation.httpmethod.PutMapping;
import annotation.mapping.logic.DataProvider;
import annotation.mapping.util.AnnotationUtil;

import java.util.Map;

@Controller
public class ControlerOne {

	@GetMapping(value = "/demo/get")
	public String getMethodTest(@RequestMap Map<String, Object> req) {
		return DataProvider.getInstance().provideGetMethodData(req);
	}

	@PostMapping(value = "/demo/add")
	public String postMethod(@HttpHeader(name = "userId") String userId,
							 @Body String body) {
		return DataProvider.getInstance().providePostMethodData(userId, AnnotationUtil.toMap(body));
	}

	@PutMapping(value = "/demo/update")
	public String puttMethod(@Body String body,
							  @HttpHeader(name = "userId") String userId) {
		return DataProvider.getInstance().providePutMethodData(userId, AnnotationUtil.toMap(body));
	}

	@DeleteMapping(value = "/demo/delete")
	public String deleteMethod(@Body String body) {
		return DataProvider.getInstance().provideDeleteMethodData(AnnotationUtil.toMap(body));
	}
}
