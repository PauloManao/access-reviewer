package project.webapp.accessreviewerapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import project.webapp.accessreviewerapp.entities.Image;


public interface ImageRepository extends JpaRepository<Image, Long>{

}
