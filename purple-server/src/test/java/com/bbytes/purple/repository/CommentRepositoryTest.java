package com.bbytes.purple.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bbytes.purple.PurpleBaseApplicationTests;
import com.bbytes.purple.domain.Comment;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.domain.Project;
import com.bbytes.purple.domain.Reply;
import com.bbytes.purple.domain.Status;
import com.bbytes.purple.domain.User;
import com.bbytes.purple.utils.TenancyContextHolder;

/**
 * @author aditya
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CommentRepositoryTest extends PurpleBaseApplicationTests {

	Organization abc;
	User user;
	Project project;
	Status status;
	Comment comment;
	List<Reply> reply;
	Reply reply1, reply2, reply3, reply4, reply5;

	@Before
	public void setUp() {
		abc = new Organization("abc", "abc-org");
		TenancyContextHolder.setTenant(abc.getOrgId());

		organizationRepository.save(abc);

		user = new User("user1", "user1@org.in");
		user.setOrganization(abc);
		userRepository.save(user);

		project = new Project("purple-app", "5:00 PM");
		projectRepository.save(project);

		status = new Status("purple", "revil", 2, new Date());
		status.setUser(user);
		status.setProject(project);
		statusRepository.save(status);

		comment = new Comment("hyyy i did purple", user, status);
		commentRepository.save(comment);

		organizationRepository.save(abc);

	}

	@After
	public void cleanUp() {
		organizationRepository.deleteAll();
		commentRepository.deleteAll();
		projectRepository.deleteAll();
		statusRepository.deleteAll();
		userRepository.deleteAll();
	}

	/**
	 * saving Comment
	 */

	@Test
	public void saveComment() {
		TenancyContextHolder.setTenant(abc.getOrgId());
		commentRepository.save(comment);
	}

	/**
	 * saving Reply to Comment
	 */

	@Test
	public void saveRepliesToComment() {

		TenancyContextHolder.setTenant(abc.getOrgId());

		reply1 = new Reply("it nice");
		reply1.setUser(user);
		reply2 = new Reply("awesome");
		reply2.setUser(user);
		reply3 = new Reply("good job");
		reply3.setUser(user);
		reply4 = new Reply("fantastic");
		reply4.setUser(user);
		reply5 = new Reply("Conratulations");
		reply5.setUser(user);

		reply = new ArrayList<Reply>();
		reply.add(reply1);
		reply.add(reply2);
		reply.add(reply3);
		reply.add(reply4);
		reply.add(reply5);

		comment.setReplies(reply);
		commentRepository.save(comment);

	}

	/**
	 * Delete particular reply among list of reply
	 */

	@Test
	public void deleteParticularReply() {

		reply = new ArrayList<Reply>();
		reply.add(new Reply("it nice"));
		reply.add(new Reply("awesome"));
		reply.add(new Reply("good job"));
		reply.add(new Reply("fantastic"));
		reply.add(new Reply("Conratulations"));

		String lastId = reply.get(reply.size() - 3).getReplyId().toString();
		comment.setReplies(reply);
		commentRepository.save(comment);

		int initSize = comment.getReplies().size();

		List<Reply> temp = comment.getReplies();
		List<Reply> toBeRemoved = new ArrayList<>();

		for (Reply a : temp) {
			System.out.println(a);
			if (a.getReplyId().toString().equals(lastId))
				toBeRemoved.add(a);
		}

		temp.removeAll(toBeRemoved);
		comment.setReplies(temp);
		commentRepository.save(comment);
		int afterDeleteSize = comment.getReplies().size();

		// check if one reply got deleted
		Assert.assertEquals(1, initSize - afterDeleteSize);
	}

	/**
	 * Delete All Reply of Comment
	 */

	@Test
	public void deleteAllreplyOfComment() {
		TenancyContextHolder.setTenant(abc.getOrgId());

		reply = new ArrayList<Reply>();
		reply.add(new Reply("it nice"));
		reply.add(new Reply("awesome"));
		reply.add(new Reply("good job"));
		reply.add(new Reply("fantastic"));
		reply.add(new Reply("Conratulations"));

		comment.setReplies(reply);
		commentRepository.save(comment);

		for (Reply a : reply) {
			System.out.println(a);
		}

		List<Reply> temp = comment.getReplies();
		temp.removeAll(temp);
		comment.setReplies(temp);
		commentRepository.save(comment);
		System.out.println("**");
		for (Reply a : temp) {
			System.out.println(a);
		}

	}

	/**
	 * Update the Comment
	 */

	@Test
	public void updateComment() {
		TenancyContextHolder.setTenant(abc.getOrgId());
		List<Comment> comment = commentRepository.findAll();
		comment.get(0).setCommentDesc("HYY");
		commentRepository.save(comment);
	}

	/**
	 * No. of Comment
	 */

	@Test
	public void getComment() {
		TenancyContextHolder.setTenant(abc.getOrgId());
		List<Comment> comment = commentRepository.findAll();
		assert (comment.size() > 1);
	}

}
