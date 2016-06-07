package com.bbytes.purple.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.purple.domain.Holiday;

public interface HolidaysRepository extends MongoRepository<Holiday , String>{
	
	Holiday findByHolidayName(String holidayName);

}
