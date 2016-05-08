package com.bbytes.purple.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import com.bbytes.purple.PurpleBaseApplicationTests;
import com.bbytes.purple.domain.ProjectUserCountStats;
import com.bbytes.purple.utils.TenancyContextHolder;

public class TestAnalyticsService extends PurpleBaseApplicationTests {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	StatusAnalyticsService analyticsService;

	@Test
	public void testAggr() {

		TenancyContextHolder.setTenant("BEYOND_BYTES");

		System.out.println("--------------------------------------------");

		Iterable<ProjectUserCountStats> result1 = analyticsService.getProjectPerDayCountHours();
		System.out.println("Project PerDay CountHours");
		for (ProjectUserCountStats dbObject : result1) {
			System.out.println(dbObject);
		}

		System.out.println("--------------------------------------------");

		Iterable<ProjectUserCountStats> result2 = analyticsService.getUserPerDayCountHours();
		System.out.println("User PerDay CountHours");
		for (ProjectUserCountStats dbObject : result2) {
			System.out.println(dbObject);
		}

		System.out.println("--------------------------------------------");

		Iterable<ProjectUserCountStats> result4 = analyticsService.getProjectUserCountHours();
		System.out.println("User Project  CountHours");
		for (ProjectUserCountStats dbObject : result4) {
			System.out.println(dbObject);
		}

		System.out.println("--------------------------------------------");

		Iterable<ProjectUserCountStats> result5 = analyticsService.getProjectPerMonthCountHours();
		System.out.println("Project Month CountHours");
		for (ProjectUserCountStats dbObject : result5) {
			System.out.println(dbObject);
		}

		System.out.println("--------------------------------------------");

		AggregationResults<ProjectUserCountStats> result6 = (AggregationResults<ProjectUserCountStats>) analyticsService
				.getUserPerMonthCountHours();
		System.out.println("User Month CountHours");
		for (ProjectUserCountStats dbObject : result6.getMappedResults()) {
			System.out.println(dbObject);
		}

		System.out.println("--------------------------------------------");
	}

	@Test
	public void testAggrWithDateRange() {

		TenancyContextHolder.setTenant("BEYOND_BYTES");

		System.out.println("--------------------------------------------");

		System.out.println("Start Date " + DateTime.now().minusDays(1).withTimeAtStartOfDay().toString("MM/dd/YYYY"));
		System.out.println("End Date " + DateTime.now().withTimeAtStartOfDay().toString("MM/dd/YYYY"));

		Iterable<ProjectUserCountStats> result3 = analyticsService.getUserProjectPerDayCountHours(
				DateTime.now().minusDays(1).withTimeAtStartOfDay().toDate(),
				DateTime.now().withTimeAtStartOfDay().toDate());
		System.out.println("User Project PerDay CountHours with date range ");
		for (ProjectUserCountStats dbObject : result3) {
			System.out.println(dbObject);
		}
	}
}
