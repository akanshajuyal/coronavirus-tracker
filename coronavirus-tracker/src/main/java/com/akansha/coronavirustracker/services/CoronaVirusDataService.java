package com.akansha.coronavirustracker.services;

import com.akansha.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {


    private static String VIRUS_DATA_URL ="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    public void setAllStats(List<LocationStats> allStats) {
        this.allStats = allStats;
    }


    //function to get the data
    @PostConstruct //tells spring to execute this function after creating the instance of this service
    @Scheduled(cron ="* * 1 * * *" ) //to run the method on the daily basis
    public void fetchVirusData() throws IOException, InterruptedException
    {
        List<LocationStats> newStats = new ArrayList<>();
        HttpClient client =HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().
                uri(URI.create(VIRUS_DATA_URL)).build();

       HttpResponse<String> httpResponse=
               client.send(request, HttpResponse.BodyHandlers.ofString());

       System.out.println(httpResponse.body());
       //Default- format of the csv
        //Reader is used to parse through the data

        //instance of reader that parses string
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);



        for (CSVRecord record : records) {
            LocationStats locationStats = new LocationStats();
            locationStats.setState(record.get("Province/State"));
            locationStats.setCountry(record.get("Country/Region"));
            locationStats.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));

            int latestCases=Integer.parseInt(record.get(record.size()-1));
            int previousDayCases=Integer.parseInt(record.get(record.size()-2));
            locationStats.setLatestTotalCases(latestCases);
            locationStats.setDiffFromPreviousDay(latestCases-previousDayCases);
            String id = record.get("Province/State");
            newStats.add(locationStats);



        }

        this.allStats= newStats;
    }
}
