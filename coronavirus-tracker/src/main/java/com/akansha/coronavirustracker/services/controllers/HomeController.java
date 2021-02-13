package com.akansha.coronavirustracker.services.controllers;

import com.akansha.coronavirustracker.models.LocationStats;
import com.akansha.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller // not RestController bcz this will render UI
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService ;


    //will point to the name of the template
    @GetMapping("/")
    public String home(Model model)
    {
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(stat-> stat.getLatestTotalCases()).sum();
        int totalNewCases= allStats.stream().mapToInt(stat -> stat.getDiffFromPreviousDay()).sum();
        model.addAttribute("locationStats", coronaVirusDataService.getAllStats());
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
        return "home";
    }
}
