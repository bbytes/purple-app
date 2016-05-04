package com.bbytes.purple.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.ConfigSetting;
import com.bbytes.purple.domain.Holiday;
import com.bbytes.purple.domain.Organization;
import com.bbytes.purple.exception.PurpleException;
import com.bbytes.purple.repository.ConfigSettingRepository;
import com.bbytes.purple.rest.dto.models.ConfigSettingDTO;
import com.bbytes.purple.utils.ErrorHandler;
import com.bbytes.purple.utils.GlobalConstants;

/**
 * Config setting service
 * 
 * @author akshay
 *
 */
@Service
public class ConfigSettingService extends AbstractService<ConfigSetting, String> {

	private ConfigSettingRepository configSettingRepository;

	@Autowired
	public ConfigSettingService(ConfigSettingRepository configSettingRepository) {
		super(configSettingRepository);
		this.configSettingRepository = configSettingRepository;

	}

	public ConfigSetting getConfigSettingbyOrganization(Organization org) {

		return configSettingRepository.findByOrganization(org);
	}

	public ConfigSetting saveNotification(ConfigSettingDTO configSettingDTO, Organization org) throws PurpleException {
		List<Holiday> holidayList = new ArrayList<Holiday>();
		ConfigSetting configSetting = new ConfigSetting();
		ConfigSetting updateConfig = new ConfigSetting();
		if (configSettingDTO != null) {
			DateFormat format = new SimpleDateFormat(GlobalConstants.DATE_HOLIDAY_FORMAT);
			try {
				if (configSettingDTO.getHolidayDate() != null) {
					for (String holidaydate : configSettingDTO.getHolidayDate()) {
						Holiday holiday = new Holiday(format.parse(holidaydate));
						holidayList.add(holiday);
					}
				}
				configSetting = getConfigSettingbyOrganization(org);
				if (configSetting != null) {
					configSetting.setCaptureHours(configSettingDTO.isCaptureHours());
					configSetting.setWeekendNotification(configSettingDTO.isWeekendNotification());
					configSetting.setStatusEnable(configSettingDTO.getStatusEnable());
					configSetting.setOrganization(org);
					configSetting.setHolidays(holidayList);
					configSetting = configSettingRepository.save(configSetting);
				} else {
					updateConfig.setCaptureHours(configSettingDTO.isCaptureHours());
					updateConfig.setWeekendNotification(configSettingDTO.isWeekendNotification());
					updateConfig.setStatusEnable(configSettingDTO.getStatusEnable());
					updateConfig.setOrganization(org);
					updateConfig.setHolidays(holidayList);
					configSetting = configSettingRepository.save(updateConfig);

				}
			} catch (Throwable e) {
				throw new PurpleException(e.getMessage(), ErrorHandler.NOTIFICATION_FAILED);
			}
		}
		return configSetting;
	}

	public ConfigSetting getConfigSetting(Organization org) throws PurpleException {
		ConfigSetting configSetting = null;
		try {
			configSetting = getConfigSettingbyOrganization(org);
		} catch (Throwable e) {
			throw new PurpleException(e.getMessage(), ErrorHandler.NOTIFICATION_FAILED);
		}
		return configSetting;
	}
}
