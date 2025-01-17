package com.project.smunionbe.domain.notification.fee.service.command;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import com.project.smunionbe.domain.notification.fee.converter.FeeNoticeConverter;
import com.project.smunionbe.domain.notification.fee.dto.request.FeeReqDTO;
import com.project.smunionbe.domain.notification.fee.entity.FeeNotice;
import com.project.smunionbe.domain.notification.fee.repository.FeeNoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FeeCommandService {

    private final MemberClubRepository memberClubRepository;
    private final FeeNoticeRepository feeNoticeRepository;

    public void createFeeNotice(FeeReqDTO.CreateFeeNoticeRequestDTO request, Long memberClubId) {
        // 1. MemberClub 확인
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        // 2. 동아리 정보 추출
        Club club = memberClub.getClub();

        // 3. FeeNotice 생성
        FeeNotice feeNotice = FeeNoticeConverter.toFeeNotice(request, club);
        feeNoticeRepository.save(feeNotice);

        log.info("회비 공지가 생성되었습니다. feeNoticeId: {}, clubId: {}", feeNotice.getId(), club.getId());
    }
}
