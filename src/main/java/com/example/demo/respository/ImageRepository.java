package com.example.demo.respository;
import com.example.demo.entity.Image;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ImageRepository extends CrudRepository<Image, Integer> {
    Image findById(Long imageId);

     List<Image> findByTagsContaining(String tag);
//    List<Image> findImagesByTag(String tagName);
}