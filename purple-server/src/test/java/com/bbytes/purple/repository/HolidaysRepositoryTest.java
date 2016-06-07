package com.bbytes.purple.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bbytes.purple.PurpleBaseApplicationTests;
import com.bbytes.purple.domain.Holiday;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.utils.TenancyContextHolder;

/**
 * @author aditya
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HolidaysRepositoryTest extends PurpleBaseApplicationTests {



	Organization abc;
	List<Holiday> days;
	Holiday holiDay;

	@Before
	public void setUp() {
		abc = new Organization("abc", "ABC-Org");

		days = new ArrayList<Holiday>();
		days.add(new Holiday(new Date(15 - 02 - 2016)));
		days.add(new Holiday(new Date(2016 - 03 - 07)));
		days.add(new Holiday(new Date(2016 - 01 - 26)));
		days.add(new Holiday(new Date(2016 - 02 - 14)));
		days.add(new Holiday(new Date(2016 - 8 - 15)));
		days.add(new Holiday(new Date(2016 - 12 - 25)));
		days.add(new Holiday(new Date(2016 - 9 - 28)));

		TenancyContextHolder.setTenant(abc.getOrgId());
		organizationRepository.save(abc);
		holiDaysRepository.save(days);
	}

	@After
	public void clearDB() {
		TenancyContextHolder.setTenant(abc.getOrgId());
		organizationRepository.delete(abc);
		holiDaysRepository.delete(days);
	}

	/**
	 * Save the Holiday into MongoDb
	 */
	@Test
	public void saveHolidayTest() {
		TenancyContextHolder.setTenant(abc.getOrgId());
		holiDaysRepository.save(days);
	}

	/**
	 * Deleting Holiday ByName from MongoDb
	 */
	@Test
	public void deleteHolidayeByName() {
		TenancyContextHolder.setTenant(abc.getOrgId());
		Holiday holidays = holiDaysRepository.findByHolidayName("Diwali");
		holiDaysRepository.delete(holidays);
	}

	/**
	 * Deleting All Holiday from MongoDb
	 */
	@Test
	public void deleteAllHolidayTest() {
		TenancyContextHolder.setTenant(abc.getOrgId());
		List<Holiday> holidays = holiDaysRepository.findAll();
		holiDaysRepository.delete(holidays);
	}

	/**
	 * Total No. of Holiday
	 */
	@Test
	public void getHolidayList() {

		TenancyContextHolder.setTenant(abc.getOrgId());
		List<Holiday> holidays = new ArrayList<Holiday>();
		holidays = holiDaysRepository.findAll();
		int arr = holidays.size();

		assertThat(arr, is(7));
	}

	@Test
	public void updateHoliday() {

		List<Holiday> holidays = holiDaysRepository.findAll();
		holiDay = holidays.get(0);
		holiDay.setHolidayDate(new Date(2016 - 05 - 11));
		holiDaysRepository.save(holiDay);

		assertThat(holidays.get(0).getHolidayDate(), is(new Date(2016 - 05 - 11)));

	}

}
