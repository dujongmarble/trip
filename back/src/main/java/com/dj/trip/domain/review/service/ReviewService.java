package com.dj.trip.domain.review.service;

import com.dj.trip.domain.review.dto.request.CreateReviewRequest;
import com.dj.trip.domain.review.dto.request.GetReviewsRequest;
import com.dj.trip.domain.review.dto.request.ModifyReviewRequest;
import com.dj.trip.domain.review.dto.response.CreateReviewResponse;
import com.dj.trip.domain.review.dto.response.GetReviewResponse;
import com.dj.trip.domain.review.dto.response.GetReviewsResponse;
import com.dj.trip.domain.review.dto.response.ModifyReviewResponse;

public interface ReviewService {
    CreateReviewResponse createReview(CreateReviewRequest reviewRequest, String memberId);

    GetReviewResponse getReview(int reviewId, String memberId);

    GetReviewsResponse getReviews(GetReviewsRequest getReviewsRequest);

    ModifyReviewResponse modifyReview(int reviewId, ModifyReviewRequest modigyReviewRequest, String memberId);

    void deleteReview(int reviewId, String memberId);

    void updateHits(int reviewId);
}
