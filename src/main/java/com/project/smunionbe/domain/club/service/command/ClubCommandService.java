package com.project.smunionbe.domain.club.service.command;


import com.project.smunionbe.domain.club.converter.ClubConverter;
import com.project.smunionbe.domain.club.dto.request.ClubReqDTO;
import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.exception.ClubErrorCode;
import com.project.smunionbe.domain.club.exception.ClubException;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import com.project.smunionbe.domain.notification.attendance.converter.AttendanceConverter;
import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceErrorCode;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ClubCommandService {
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;

    public ClubResDTO.CreateClubDTO createClub(ClubReqDTO.CreateClubDTO request, String email) {
        if (!memberRepository.existsByEmail((email))) {
            throw new ClubException(ClubErrorCode.MEMBER_NOT_FOUND);
        }

        Club club = ClubConverter.toClub(request);
        clubRepository.save(club);


        return ClubConverter.toClubResponse(club);

    }

    public void updateClub(Long clubId, ClubReqDTO.UpdateClubRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ClubException(ClubErrorCode.CLUB_NOT_FOUND));

        // 권한 확인 코드 추가 필요


        club.update(request.name(), request.description(), request.thumbnailUrl());
    }
}
