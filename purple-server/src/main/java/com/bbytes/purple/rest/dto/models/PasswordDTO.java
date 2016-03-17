package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class PasswordDTO implements Serializable {

	private static final long serialVersionUID = -8038110478828131503L;

	private String oldPassword;

	private String newPassword;

}
