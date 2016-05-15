package com.bbytes.purple.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.ProjectUserCountStats;
import com.bbytes.purple.domain.Status;

@Service
public class StatusAnalyticsService {

	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * Group by User , Project , day wise.
	 */
	public Iterable<ProjectUserCountStats> getUserProjectPerDayCountHours() {
		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				project().and("dateTime").extractDayOfYear().as("dayOfYear") 
						.and("hours").as("hours").and("user").as("user").and("project").as("project"),
				group("project", "user", "dayOfYear").sum("hours").as("hours").count().as("status_count"),
				sort(Direction.DESC, "status_count"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);

		return result;
	}
	
	/**
	 * Group by User , Project , day wise.
	 */
	public Iterable<ProjectUserCountStats> getUserProjectPerDayCountHours(Date startDate , Date endDate) {
		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				match(Criteria.where("dateTime").gte(startDate).lte(endDate)),
				project().and("dateTime").extractDayOfYear().as("dayOfYear")
						.and("hours").as("hours").and("user").as("user").and("project").as("project"),
				group("project", "user", "dayOfYear").sum("hours").as("hours").count().as("status_count"),
				sort(Direction.DESC, "status_count"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);

		return result;
	}

	/**
	 * Group by User , day wise.
	 */
	public Iterable<ProjectUserCountStats> getUserPerDayCountHours(Date startDate , Date endDate) {
		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				match(Criteria.where("dateTime").gte(startDate).lte(endDate)),
				project().and("dateTime").extractDayOfYear().as("dayOfYear") 
						.and("hours").as("hours").and("user").as("user"),
				group("user", "dayOfYear").sum("hours").as("hours").count().as("status_count"),
				sort(Direction.DESC, "status_count"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);

		return result;
	}

	
	/**
	 * Group by User , day wise.
	 */
	public Iterable<ProjectUserCountStats> getUserPerDayCountHours() {
		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				project().and("dateTime").extractDayOfYear().as("dayOfYear") //
						.and("hours").as("hours").and("user").as("user"),
				group("user", "dayOfYear").sum("hours").as("hours").count().as("status_count"),
				sort(Direction.DESC, "status_count"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);

		return result;
	}

	/**
	 * Group by Project , day wise.
	 */
	public Iterable<ProjectUserCountStats> getProjectPerDayCountHours(Date startDate , Date endDate) {
		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				match(Criteria.where("dateTime").gte(startDate).lte(endDate)),
				project().and("dateTime").extractDayOfYear().as("dayOfYear") //
						.and("hours").as("hours").and("project").as("project"),
				group("project", "dayOfYear").sum("hours").as("hours").count().as("status_count"),
				sort(Direction.DESC, "status_count"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);

		return result;
	}
	
	/**
	 * Group by Project , day wise.
	 */
	public Iterable<ProjectUserCountStats> getProjectPerDayCountHours() {
		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				project().and("dateTime").extractDayOfYear().as("dayOfYear") //
						.and("hours").as("hours").and("project").as("project"),
				group("project", "dayOfYear").sum("hours").as("hours").count().as("status_count"),
				sort(Direction.DESC, "status_count"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);

		return result;
	}

	/**
	 * Group by Project and user wise
	 */
	public Iterable<ProjectUserCountStats> getProjectUserCountHours(Date startDate , Date endDate) {
		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				match(Criteria.where("dateTime").gte(startDate).lte(endDate)),
				project().and("hours").as("hours").and("user").as("user").and("project").as("project"),
				group("project", "user").sum("hours").as("hours").count().as("status_count"),
				sort(Direction.DESC, "status_count"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);

		return result;
	}
	
	/**
	 * Group by Project and user wise
	 */
	public Iterable<ProjectUserCountStats> getProjectUserCountHours() {
		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				project().and("hours").as("hours").and("user").as("user").and("project").as("project"),
				group("project", "user").sum("hours").as("hours").count().as("status_count"),
				sort(Direction.DESC, "status_count"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);

		return result;
	}

	/**
	 * Group by Project hours
	 */
	public Iterable<ProjectUserCountStats> getProjectPerMonthCountHours(Date startDate , Date endDate) {
		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				match(Criteria.where("dateTime").gte(startDate).lte(endDate)),
				project().and("dateTime").extractMonth().as("month").and("hours").as("hours").and("project").as("project"),
				group("project","month").sum("hours").as("hours").count().as("status_count"),
				sort(Direction.DESC, "status_count"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);

		return result;
	}
	
	/**
	 * Group by Project hours
	 */
	public Iterable<ProjectUserCountStats> getProjectPerMonthCountHours() {
		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				project().and("dateTime").extractMonth().as("month").and("hours").as("hours").and("project").as("project"),
				group("project","month").sum("hours").as("hours").count().as("status_count"),
				sort(Direction.DESC, "status_count"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);

		return result;
	}

	/**
	 * Group by User hours
	 */
	public Iterable<ProjectUserCountStats> getUserPerMonthCountHours(Date startDate , Date endDate) {
		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				match(Criteria.where("dateTime").gte(startDate).lte(endDate)),
				project().and("dateTime").extractMonth().as("month").and("hours").as("hours").and("user").as("user"),
				group("user","month").sum("hours").as("hours").count().as("status_count"),
				sort(Direction.DESC, "status_count"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);

		return result;
	}
	
	/**
	 * Group by User hours
	 */
	public Iterable<ProjectUserCountStats> getUserPerMonthCountHours() {
		TypedAggregation<ProjectUserCountStats> aggregation = newAggregation(ProjectUserCountStats.class,
				project().and("dateTime").extractMonth().as("month").and("hours").as("hours").and("user").as("user"),
				group("user","month").sum("hours").as("hours").count().as("status_count"),
				sort(Direction.DESC, "status_count"));

		AggregationResults<ProjectUserCountStats> result = mongoTemplate.aggregate(aggregation, Status.class,
				ProjectUserCountStats.class);

		return result;
	}
}