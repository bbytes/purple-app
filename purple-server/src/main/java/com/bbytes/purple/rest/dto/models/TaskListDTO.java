package com.bbytes.purple.rest.dto.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bbytes.purple.domain.User;

import lombok.Data;

/**
 * @author aditya
 *
 */

@Data
public class TaskListDTO implements Serializable {

	private static final long serialVersionUID = 5187445361068841595L;
	
	private String taskListId;

	private String name;

	private String projectId;
}
