package com.project.smunionbe.domain.club.dto.response;

import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;
import com.project.smunionbe.domain.notification.fee.dto.response.FeeResDTO;
import com.project.smunionbe.domain.notification.vote.dto.response.VoteResDTO;

import java.util.List;

public record ClubResDTO() {

    public record CreateClubDTO(
            Long clubID
    ) {
    }

    public record UpdateClubRequest(
            String name,
            String description,
            String thumbnailUrl
    ){
    }

    public record MemberClubResponse(
            Long memberId,
            String nickname,
            String department

    ){
    }

    public record GetMemberClubListResDTO(
            List<ClubResDTO.MemberClubResponse> memberClubResponseList
    ){
    }

    public record ClubDetailResDTO(
            String name,
            String description,
            String thumbnailUrl,
            List<AttendanceResDTO.AttendanceDetailResponse> attendanceDetailResponseList,
            List<FeeResDTO.FeeNoticeResponse> feeNoticeResponseList,
            List<VoteResDTO.VoteResponse> voteResponseList,
            List<GalleryResDTO.GetGalleryResDTO> getGalleryResDTOList

    ){
    }
}
