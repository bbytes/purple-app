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
import org.springframework.beans.factory.annotation.Autowired;

import com.bbytes.purple.PurpleApplicationTests;
import com.bbytes.purple.domain.Holidays;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.utils.TenancyContextHolder;

/**
 * @author aditya
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HolidaysRepositoryTest extends PurpleApplicationTests {

	@Autowired
	private HolidaysRepository holiDaysRepository;

	@Autowired
	private OrganizationRepository orgRepository;

	Organization abc;
	List<Holidays> days;
	Holidays holiDay;

	@Before
	public void setUp() {
		abc = new Organization("abc", "ABC-Org");

		days = new ArrayList<Holidays>();
		days.add(new Holidays("Holi", new Date(15 - 02 - 2016), abc));
		days.add(new Holidays("Mahashivratri", new Date(2016 - 03 - 07), abc));
		days.add(new Holidays("Republic Day", new Date(2016 - 01 - 26), abc));
		days.add(new Holidays("Valentine Day", new Date(2016 - 02 - 14), abc));
		days.add(new Holidays("Independence Day", new Date(2016 - 8 - 15), abc));
		days.add(new Holidays("X-Mas Day", new Date(2016 - 12 - 25), abc));
		days.add(new Holidays("Diwali", new Date(2016 - 9 - 28), abc));

		TenancyContextHolder.setTenant(days.get(0).getOrganization().getOrgId());
		orgRepository.save(abc);
		holiDaysRepository.save(days);
	}

	@After
	public void clearDB() {
		TenancyContextHolder.setTenant(days.get(0).getOrganization().getOrgId());
		orgRepository.delete(abc);
		holiDaysRepository.delete(days);
	}

	/**
	 * Save the Holiday into MongoDb
	 */
	@Test
	public void saveHolidayTest() {
		TenancyContextHolder.setTenant(days.get(0).getOrganization().getOrgId());
		holiDaysRepository.save(days);
	}

	/**
	 * Deleting Holiday ByName from MongoDb
	 */
	@Test
	public void deleteHolidayeByName() {
		TenancyContextHolder.setTenant(days.get(0).getOrganization().getOrgId());
		Holidays holidays = holiDaysRepository.findByHolidayName("Diwali");
		holiDaysRepository.delete(holidays);
	}

	/**
	 * Deleting All Holiday from MongoDb
	 */
	@Test
	public void deleteAllHolidayTest() {
		TenancyContextHolder.setTenant(days.get(0).getOrganization().getOrgId());
		List<Holidays> holidays = holiDaysRepository.findAll();
		holiDaysRepository.delete(holidays);
	}

	/**
	 * Total No. of Holiday
	 */
	@Test
	public void getHolidayList() {

		TenancyContextHolder.setTenant(days.get(0).getOrganization().getOrgId());
		List<Holidays> holidays = new ArrayList<Holidays>();
		holidays = holiDaysRepository.findAll();
		int arr = holidays.size();

		assertThat(arr, is(7));
	}

	@Test
	public void updateHoliday() {

		List<Holidays> holidays = holiDaysRepository.findAll();
		holiDay = holidays.get(0);
		holiDay.setHolidayDate(new Date(2016 - 05 - 11));
		holiDaysRepository.save(holiDay);

		assertThat(holidays.get(0).getHolidayDate(), is(new Date(2016 - 05 - 11)));

	}

}
