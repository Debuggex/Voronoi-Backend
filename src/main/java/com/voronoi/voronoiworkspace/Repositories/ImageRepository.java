package com.voronoi.voronoiworkspace.Repositories;

import com.voronoi.voronoiworkspace.Entities.Images;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Images,Long> {
}
