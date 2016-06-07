package com.bbytes.purple.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bbytes.purple.domain.Status;
import com.bbytes.purple.utils.CSVWriter;

@Service
public class UtilityService {

	private static final Logger logger = LoggerFactory.getLogger(UtilityService.class);

	public File getCSV(String fileName, List<Status> statusList) {

		File csvFile = null;
		CSVWriter csv = null;

		try {
			csvFile = File.createTempFile(fileName, ".csv");
			csv = new CSVWriter(new FileWriter(csvFile));

			String[] header = new String[] { "Project" , "User", "Worked On", "Working on" ,"Blocker" ,"Hours" ,"Date"};
			csv.writeNext(header);
			
			if (statusList != null && !statusList.isEmpty()) {
				for (Status status : statusList) {
					String[] data = new String[header.length];
					data[0] = status.getProject().getProjectName();
					data[1] = status.getUser().getName();
					data[2] = Jsoup.parse(status.getWorkedOn() != null ? status.getWorkedOn() : "").text();
					data[3] = Jsoup.parse(status.getWorkingOn() != null ? status.getWorkingOn() : "").text();
					data[4] = Jsoup.parse(status.getBlockers() != null ? status.getBlockers() : "").text();
					data[5] = Double.toString(status.getHours());
					data[6] = new DateTime(status.getDateTime()).toString();
					csv.writeNext(data);
				}

			} else {
				csv.writeNext(new String[] { "No Data" });
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (csv != null) {
				try {
					csv.close();
				} catch (IOException ie) {
					logger.error(ie.getMessage());
				}
			}
		}

		return csvFile;

	}
}