package com.backend.java.perfume.controller;

import com.backend.java.perfume.dto.review.ReviewRequest;
import com.backend.java.perfume.dto.review.ReviewResponse;
import com.backend.java.perfume.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.backend.java.perfume.constants.PathConstants.API_V1_REVIEW;
import static com.backend.java.perfume.constants.PathConstants.PERFUME_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_REVIEW)
public class ReviewController {

    private final ReviewMapper reviewMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping(PERFUME_ID)
    public ResponseEntity<List<ReviewResponse>> getReviewsByPerfumeId(@PathVariable Long perfumeId) {
        return ResponseEntity.ok(reviewMapper.getReviewsByPerfumeId(perfumeId));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> addReviewToPerfume(@Valid @RequestBody ReviewRequest reviewRequest,
                                                             BindingResult bindingResult) {
        ReviewResponse review = reviewMapper.addReviewToPerfume(reviewRequest, reviewRequest.getPerfumeId(), bindingResult);
        messagingTemplate.convertAndSend("/topic/reviews/" + reviewRequest.getPerfumeId(), review);
        return ResponseEntity.ok(review);
    }
}
