package com.wasel.backend.service;



import com.wasel.backend.model.Report;
import com.wasel.backend.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportDuplicateService {

    private final ReportRepository reportRepository;

    public ReportDuplicateService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private Report getRootReport(Report report) {
        while (report.getDuplicateOf() != null) {
            report = reportRepository.findById(report.getDuplicateOf()).orElse(report);
        }
        return report;
    }

    public Report handleDuplicate(Report report, LocalDateTime now) {

        LocalDateTime start = now.minusMinutes(30);
        LocalDateTime end = now.plusMinutes(30);

        List<Report> candidates = reportRepository.findSimilarReports(
                report.getCategory(), start, end
        );

        for (Report r : candidates) {
            double dist = distance(report.getLatitude(), report.getLongitude(),
                    r.getLatitude(), r.getLongitude());

            if (dist < 0.2) {

                Report root = getRootReport(r);

                report.setDuplicateOf(root.getId());

                int duplicates = root.getDuplicateCount() + 1;
                int votes = reportRepository.countVotes(root.getId());

                double newScore = (duplicates * 1.0) + (votes * 0.7);

                root.setDuplicateCount(duplicates);
                root.setCredibilityScore((float) newScore);

                if (newScore >= 15) {
                    root.setIsPromoted(true);
                }

                reportRepository.save(root);
                return root;
            }
        }

        report.setDuplicateCount(0);
        return null;
    }
}