package com.bbytes.purple.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Holidays;
import com.bbytes.purple.repository.HolidaysRepository;

@Service
public class HolidaysService extends AbstractService<Holidays, String>{
	
	@Autowired
	private HolidaysRepository holidaysRepository; 
	
	@Autowired
	public HolidaysService(HolidaysRepository holidaysRepository) {
		super(holidaysRepository);
	}

	public Holidays getHoliddayById(String holidayId)
	{
		return holidaysRepository.findOne(holidayId);
	}
	
	public Holidays getHolidaybyName(String holidayName)
	{
		return holidaysRepository.findByHolidayName(holidayName);
	}

}
