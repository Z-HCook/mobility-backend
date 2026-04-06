package com.wasel.backend.service;

import com.wasel.backend.dto.VerifyReportRequest;
import com.wasel.backend.model.*;
import com.wasel.backend.repository.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class IncidentVerificationService {

    private final ReportRepository reportRepo;
    private final UserRepository userRepo;
    private final IncidentRepository incidentRepo;
    private final ReportModerationLogRepository logRepo;
    private final CheckpointRepository checkpointRepo;
    private final CheckpointHistoryRepository checkpointHistoryRepo;
    private final IncidentRepository incidentRepository;
    private final AlertService alertService;

    public IncidentVerificationService(
            ReportRepository reportRepo,
            UserRepository userRepo,
            IncidentRepository incidentRepo,
            ReportModerationLogRepository logRepo,
            CheckpointRepository checkpointRepo,
            CheckpointHistoryRepository checkpointHistoryRepo,
            IncidentRepository incidentRepository,
            AlertService alertService
    ) {
        this.reportRepo = reportRepo;
        this.userRepo = userRepo;
        this.incidentRepo = incidentRepo;
        this.logRepo = logRepo;
        this.checkpointRepo = checkpointRepo;
        this.checkpointHistoryRepo = checkpointHistoryRepo;
        this.incidentRepository = incidentRepository;
        this.alertService = alertService;
    }

    @Transactional
    // ✅ نستخدم @Caching لأننا نريد مسح أكثر من مخزن (Incidents و History)
    @Caching(evict = {
            @CacheEvict(value = "incidents", allEntries = true),
            @CacheEvict(value = "checkpointHistory", allEntries = true),
            @CacheEvict(value = "checkpointHistoryRange", allEntries = true)
    })
    public String verifyReport(VerifyReportRequest request) {

        // 1️⃣ جلب التقرير
        Report report = reportRepo.findById(request.getReportId()).orElse(null);
        if (report == null) return "Report not found";

        // 2️⃣ التأكد أنه promoted
        if (!Boolean.TRUE.equals(report.getIsPromoted())) {
            return "Report is not eligible for verification";
        }

        // 3️⃣ جلب الـ moderator
        User moderator = userRepo.findById(request.getModeratorId()).orElse(null);
        if (moderator == null) return "Moderator not found";

        // 4️⃣ تحقق من صلاحيات
        if (!moderator.getRole().equalsIgnoreCase("admin")
                && !moderator.getRole().equalsIgnoreCase("moderator")) {
            return "Unauthorized";
        }

        // 5️⃣ تحقق إذا تم التحقق مسبقاً
        if ("verified".equalsIgnoreCase(report.getStatus())) {
            return "Report already verified";
        }

        // 6️⃣ إنشاء Incident
        Incident incident = new Incident();
        incident.setTitle(report.getCategory());
        incident.setDescription(report.getDescription());
        incident.setType(mapCategoryToType(report.getCategory()));
        incident.setSeverity("medium");
        incident.setStatus("verified");
        incident.setLatitude(report.getLatitude());
        incident.setLongitude(report.getLongitude());
        incident.setReportedBy(report.getUserId());
        incident.setVerifiedBy(moderator.getId());
        incident.setCreatedAt(LocalDateTime.now());
        incident.setUpdatedAt(LocalDateTime.now());
        incidentRepo.save(incident); // ✔️ حفظ Incident أولاً

        alertService.createAlertsForIncident(incident);



        // 7️⃣ ربط التقرير بالـ Incident
        report.setStatus("verified");
        report.setLinkedIncidentId(incident.getId());
        reportRepo.save(report);

        // 8️⃣ تسجيل اللوج
        ReportModerationLog log = new ReportModerationLog();
        log.setReportId(report.getId());
        log.setModeratorId(moderator.getId());
        log.setAction("VERIFY");
        log.setNote("Created incident ID: " + incident.getId());
        log.setCreatedAt(LocalDateTime.now());
        logRepo.save(log);

        // 9️⃣ إضافة سجل CheckpointHistory إذا موجود Checkpoint
        if (report.getLinkedCheckpointId() != null) {
            Checkpoint checkpoint = checkpointRepo.findById(report.getLinkedCheckpointId()).orElse(null);
            if (checkpoint != null) {
                CheckpointHistory history = new CheckpointHistory();
                history.setCheckpoint(checkpoint);
                history.setIncident(incident); // ✔️ الآن الـ incident محفوظ
                history.setInsAt(LocalDateTime.now());
                checkpointHistoryRepo.save(history);
            }
        }

        return "Report verified and incident created";
    }

    // تحويل التصنيف إلى نوع Incident
    private String mapCategoryToType(String category) {
        return switch (category.toLowerCase()) {
            case "traffic" -> "delay";
            case "safety" -> "accident";
            case "weather" -> "weather";
            default -> "closure";
        };
    }
}