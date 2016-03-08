package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Holidays;

public interface HolidaysRepository extends MongoRepository<Holidays , String>{
	
	Holidays findByHolidayName(String holidayName);

}
