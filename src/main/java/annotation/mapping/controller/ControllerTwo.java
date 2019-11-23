package annotation.mapping.controller;

import annotation.mapping.annotation.Controller;
import annotation.mapping.annotation.httpmethod.DeleteMapping;
import annotation.mapping.annotation.httpmethod.GetMapping;
import annotation.mapping.annotation.httpmethod.PostMapping;
import annotation.mapping.annotation.httpmethod.PutMapping;

@Controller("/demo")
public class ControllerTwo {

	@GetMapping
	public String getMethod() {
		return "{\n" + "\t\"status\": \"GET method is called\"\n" + "}";
	}

	@GetMapping("/get/two")
	public String getMethodTwo() {
		return "{\n" + "\t\"status\": \"GET method is called\"\n" + "}";
	}

	@PostMapping
	public String postMethod() {
		return "{\n" + "\t\"status\": \"POST method is called\"\n" + "}";
	}

	@PutMapping
	public String putMethod() {
		return "{\n" + "\t\"status\": \"PUE method is called\"\n" + "}";
	}

	@DeleteMapping
	public String pdeleteMethod() {
		return "{\n" + "\t\"status\": \"DELETE method is called\"\n" + "}";
	}
}
