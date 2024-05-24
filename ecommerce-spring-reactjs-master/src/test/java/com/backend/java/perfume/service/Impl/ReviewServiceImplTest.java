package com.backend.java.perfume.service.Impl;

import com.backend.java.perfume.domain.Perfume;
import com.backend.java.perfume.domain.Review;
import com.backend.java.perfume.repository.PerfumeRepository;
import com.backend.java.perfume.repository.ReviewRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReviewServiceImplTest {

    @Autowired
    private ReviewServiceImpl reviewService;

    @MockBean
    private PerfumeRepository perfumeRepository;

    @MockBean
    private ReviewRepository reviewRepository;

    @Test
    public void addReviewToPerfume() {
        List<Review> reviewList = new ArrayList<>();
        Review review = new Review();
        review.setRating(5);
        reviewList.add(review);
        Perfume perfume = new Perfume();
        perfume.setId(123L);
        perfume.setReviews(reviewList);

        when(perfumeRepository.findById(123L)).thenReturn(Optional.of(perfume));
        when(reviewRepository.save(review)).thenReturn(review);
        reviewService.addReviewToPerfume(review, 123L);
        assertEquals(123L, perfume.getId());
        assertNotNull(perfume.getReviews());
        verify(perfumeRepository, times(1)).findById(123L);
        verify(reviewRepository, times(1)).save(review);
    }
}
