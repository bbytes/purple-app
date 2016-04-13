package com.bbytes.purple.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HTML5AngularRedirectController {

	@RequestMapping("/web/{page}/**")
	public String app(@PathVariable String page) {
		return "redirect:/#" + page;
	}
}