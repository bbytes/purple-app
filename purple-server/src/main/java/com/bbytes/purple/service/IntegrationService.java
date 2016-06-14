package com.bbytes.purple.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Integration;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.IntegrationRepository;
import com.bbytes.purple.utils.ErrorHandler;

@Service
public class IntegrationService extends AbstractService<Integration, String> {

	private IntegrationRepository integrationRepository;

	@Autowired
	private ProjectService projectService;

	@Autowired
	public IntegrationService(IntegrationRepository integrationRepository) {
		super(integrationRepository);
		this.integrationRepository = integrationRepository;
	}

	public Integration getIntegrationByUser(User user) {
		return integrationRepository.findByUser(user);
	}

	public boolean integrationExist(User user) {
		boolean state = integrationRepository.findByUser(user) == null ? false : true;
		return state;
	}

	public Integration connectToJIRA(User user, String basicAuth, String jiraBaseURL) throws PurpleException {
		Integration integration = null;
		try {
			if (!integrationExist(user)) {
				integration = new Integration();
				integration.setJiraBasicAuthHeader(basicAuth);
				integration.setJiraBaseURL(jiraBaseURL);
				integration.setUser(user);
				integrationRepository.save(integration);
			} else {
				integration = getIntegrationByUser(user);
				integration.setJiraBasicAuthHeader(basicAuth);
				integration = integrationRepository.save(integration);
			}
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.JIRA_CONNECTION_FAILED);
		}
		return integration;

	}

	public Integration getJIRAConnection(User user) throws PurpleException {
		Integration integration = null;

		try {
			integration = getIntegrationByUser(user);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.JIRA_CONNECTION_FAILED);
		}
		return integration;
	}

	public void addJiraProjects(String jsonText, User user) throws PurpleException {
		List<String> jiraProjectList = new LinkedList<String>();
		List<String> finalProjectListToBeSaved = new LinkedList<String>();

		try {
			JSONArray jsonarray = new JSONArray(jsonText);
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				String name = jsonobject.getString("name");
				jiraProjectList.add(name);
			}
			List<Project> list = projectService.findAll();
			List<String> projectListFromDB = new ArrayList<String>();
			for (Project project : list) {
				projectListFromDB.add(project.getProjectName().toLowerCase());
			}

			for (String jiraProject : jiraProjectList) {
				// make sure we dont add project with same name but different
				// case Eg : ReCruiz and recruiz are same
				if (!projectListFromDB.contains(jiraProject.toLowerCase()))
					finalProjectListToBeSaved.add(jiraProject);
			}

			for (String project : finalProjectListToBeSaved) {
				Project addProject = new Project(project);
				addProject.setOrganization(user.getOrganization());
				addProject = projectService.save(addProject);
			}
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.JIRA_CONNECTION_FAILED);
		}
	}

}
