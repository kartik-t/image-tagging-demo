package com.example.demo.controller;


import com.example.demo.entity.Image;
import com.example.demo.respository.ImageRepository;
import com.example.demo.util.ImageTaggingUtility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class ImageTaggingController {
    @Autowired
    ImageRepository imageRepository;

    /**
     * Returns all images if no objects are passed into the URL parameters, otherwise searches for passed in tags.
     *
     * @param objects This parameter dictates which tags are being searched for.
     * @return Either all images or ones with specific tags.
     */
    @GetMapping("/images")
    ResponseEntity<List<Image>> getAllImages(@RequestParam(value = "objects", required=false) String objects) {
        List<Image> result = null;
        if(StringUtils.isBlank(objects)) {
            result = (List<Image>) imageRepository.findAll();
        }
        else {
            result = new ArrayList<>();
            objects = objects.toLowerCase();
            String[] tagsToSearch = StringUtils.split(objects, ",");
            for(String tag : tagsToSearch) {
                tag = StringUtils.trim(tag);
                tag = StringUtils.replace(tag,"\"","");
                result.addAll(imageRepository.findByTagsContaining(tag));
            }
        }
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    /**
     * Returns specific entry based on Image.id
     *
     * @param id Image.id (unique identifier assigned to the image)
     * @return Returns the Image row with the given Image.id
     */
    @GetMapping("/images/{id}")
    public ResponseEntity<?> getImageById(@PathVariable String id) {
        if(StringUtils.isBlank(id)) {
            return new ResponseEntity<>("Parameter [imageId] cannot be blank.", HttpStatus.BAD_REQUEST);
        }
        Long imageId = null;
        try{
            imageId = Long.parseLong(id);
        } catch (Throwable t) {
            System.out.println("Parameter [imageId] must be valid Long value.");
            return new ResponseEntity<>("Parameter [imageId] must be valid Long value.", HttpStatus.BAD_REQUEST);
        }
        Image result = imageRepository.findById(imageId);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    /**
     * Calls Imagga API to get image detection tags and then creates new Image rows in the repository.
     *
     * @param request custom request class that contains url, filePath, label, and the objectDetection boolean.
     * @return Image is returned with the relevant metadata.
     */
    @PostMapping(value="/images")
    ResponseEntity<?> tagImage(@RequestBody  ImagePostRequest request) {
        Image result = null;
        if(request.isObjectDetection()) {
            if(StringUtils.isBlank(request.getFilePath()) && StringUtils.isBlank(request.getUrl())){
                return new ResponseEntity<>("Either file path or URL are required for the image.", HttpStatus.BAD_REQUEST);
            }
            result = new Image();
            result.setLabel(request.getLabel());
            result.setUrl(request.getUrl());
            List<String> tags = null;
            // Call Imagga API here
            if(StringUtils.isBlank(request.getFilePath())){
                tags = ImageTaggingUtility.getTagsFromImageUrl(request);
                result.setUrl(request.getUrl());
            } else {
                tags = ImageTaggingUtility.getTagsFromFile(request);
                result.setFilePath(request.getFilePath());
            }
            // Parse tags
            StringBuilder builder = new StringBuilder();
            if(tags != null && tags.size() != 0) {
                for(String t : tags) {
                    builder.append(t).append(";");
                }
                result.setTags(builder.toString());
            } else {
                result.setTags("No tags found. See console for errors.");
            }
        } else {
            // Create image without tags as objectDetection was false.
            result = new Image();
            result.setLabel(request.getLabel());
            result.setUrl(request.getUrl());
            result.setFilePath(request.getFilePath());
        }
        imageRepository.save(result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}