/**
 * 
 */
package com.mb.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Anand
 *
 */
@CrossOrigin
@RestController
public class TestController {

	@RequestMapping(value="/test",method = RequestMethod.GET,produces = "application/json")
	public String test() {
		return "Test is running";
	}
}
