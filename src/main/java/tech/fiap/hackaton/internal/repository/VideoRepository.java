package tech.fiap.hackaton.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.fiap.hackaton.internal.entity.Video;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByPersonId(Long personId);
}
