package com.bbytes.purple.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HTML5AngularRedirectController {

	@RequestMapping(value = "/{[path:[^\\.]*}")
	public String redirect() {
		return "forward:/";
	}
}