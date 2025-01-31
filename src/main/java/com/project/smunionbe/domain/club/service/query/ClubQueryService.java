package com.project.smunionbe.domain.club.service.query;


import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.dto.response.DepartmentResDTO;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.exception.ClubErrorCode;
import com.project.smunionbe.domain.club.exception.ClubException;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.GalleryRepository;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;
import com.project.smunionbe.domain.notification.attendance.repository.AttendanceRepository;
import com.project.smunionbe.domain.notification.fee.dto.response.FeeResDTO;
import com.project.smunionbe.domain.notification.fee.repository.FeeNoticeRepository;
import com.project.smunionbe.domain.notification.vote.dto.response.VoteResDTO;
import com.project.smunionbe.domain.notification.vote.repository.VoteNoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubQueryService {
    private final MemberClubRepository memberClubRepository;
    private final ClubRepository clubRepository;
    private final AttendanceRepository attendanceRepository;
    private final FeeNoticeRepository feeNoticeRepository;
    private final VoteNoticeRepository voteNoticeRepository;
    private final GalleryRepository galleryRepository;

    public ClubResDTO.GetMemberClubListResDTO getAllMemberClubList(
            Long memberClubId
    ) {
        if (!memberClubRepository.existsById(memberClubId)) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }

        Long clubId = memberClubRepository.findById(memberClubId).get().getClub().getId();

        // 데이터 변환
        List<ClubResDTO.MemberClubResponse> memberClubResponseList = memberClubRepository.findAllByClubId(clubId)
                .stream()
                .map(memberClub -> new ClubResDTO.MemberClubResponse(
                        memberClub.getMember().getId(),
                        memberClub.getNickname(),
                        memberClub.getDepartment().getName()
                ))
                .toList();

        return new ClubResDTO.GetMemberClubListResDTO(memberClubResponseList);
    }

    public ClubResDTO.ClubDetailResDTO getclubDetailResDTO(
            Long memberClubId
    ) {
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.INVALID_MEMBER_CLUB));
        Club club = clubRepository.findById(memberClub.getClub().getId())
                .orElseThrow(() -> new ClubException(ClubErrorCode.CLUB_NOT_FOUND));

        Long clubId = club.getId();


        List<AttendanceResDTO.AttendanceDetailResponse> attendanceDetailResponses = attendanceRepository.findAllByClubId(clubId)
                .stream()
                .map(attendanceNotice -> new AttendanceResDTO.AttendanceDetailResponse(
                        attendanceNotice.getId(),
                        attendanceNotice.getTitle(),
                        attendanceNotice.getContent(),
                        attendanceNotice.getTarget(),
                        attendanceNotice.getDate(),
                        attendanceNotice.getCreatedAt()
                        ))
                .toList();

        List<FeeResDTO.FeeNoticeResponse> feeNoticeResponseList = feeNoticeRepository.findAllByClubId(clubId)
                .stream()
                .map(feeNotice -> new FeeResDTO.FeeNoticeResponse(
                        feeNotice.getId(),
                        feeNotice.getTitle(),
                        feeNotice.getContent(),
                        feeNotice.getTarget(),
                        feeNotice.getAmount(),
                        feeNotice.getBank(),
                        feeNotice.getAccountNumber(),
                        feeNotice.getParticipantCount(),
                        feeNotice.getDate(),
                        feeNotice.getCreatedAt()

                        ))
                .toList();

        List<VoteResDTO.VoteResponse> voteResponses = voteNoticeRepository.findAllByClubId(clubId)
                .stream()
                .map(voteNotice -> new VoteResDTO.VoteResponse(
                        voteNotice.getId(),
                        voteNotice.getTitle(),
                        voteNotice.getContent(),
                        voteNotice.getTarget(),
                        voteNotice.getDate(),
                        voteNotice.isAllowDuplicate(),
                        voteNotice.isAnonymous(),
                        voteNotice.getCreatedAt()
                        ))
                .toList();

        List<GalleryResDTO.GetGalleryResDTO> galleryResDTOS = galleryRepository.findAllByClubId(memberClub.getClub().getId())
                .stream()
                .map(gallery -> new GalleryResDTO.GetGalleryResDTO(
                        gallery.getId(),
                        gallery.getName(),
                        gallery.getThumbnailUrl()
                ))
                .toList();

        return new ClubResDTO.ClubDetailResDTO(
                club.getName(),
                club.getDescription(),
                club.getThumbnailUrl(),
                attendanceDetailResponses,
                feeNoticeResponseList,
                voteResponses,
                galleryResDTOS
        );
    }




}
