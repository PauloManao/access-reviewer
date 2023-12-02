package project.webapp.accessreviewerapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import project.webapp.accessreviewerapp.entities.ReviewReport;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
	
}
