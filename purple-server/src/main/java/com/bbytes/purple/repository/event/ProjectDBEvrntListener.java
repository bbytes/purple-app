package com.bbytes.purple.repository.event;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.repository.ProjectRepository;
import com.bbytes.purple.repository.UserRepository;
import com.bbytes.purple.service.StatusService;
import com.mongodb.DBObject;

@Component
@Profile("saas")
public class ProjectDBEvrntListener extends AbstractMongoEventListener<Project> {

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StatusService statusService;

	/**
	 * Remove the project from the user list when the project is deleted -
	 * Cascade delete
	 */
	@Override
	public void onBeforeDelete(BeforeDeleteEvent<Project> event) {
		final DBObject projectDeleted = event.getSource();
		Project project = projectRepository.findOne(projectDeleted.get("projectId").toString());

		List<User> usersToBeSaved = new ArrayList<>();
		if (project != null && project.getUser() != null) {
			for (User user : project.getUser()) {
				user.getProjects().remove(project);
				usersToBeSaved.add(user);
			}
		}
		List<Status> statusFromDB = statusService.getStatusByProject(project);
		statusService.delete(statusFromDB);
		userRepository.save(usersToBeSaved);
	}

}
