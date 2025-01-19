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
import com.project.smunionbe.domain.notification.fee.entity.FeeStatus;
import com.project.smunionbe.domain.notification.fee.exception.FeeErrorCode;
import com.project.smunionbe.domain.notification.fee.exception.FeeException;
import com.project.smunionbe.domain.notification.fee.repository.FeeNoticeRepository;
import com.project.smunionbe.domain.notification.fee.repository.FeeStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeeCommandService {

    private final MemberClubRepository memberClubRepository;
    private final FeeNoticeRepository feeNoticeRepository;
    private final FeeStatusRepository feeStatusRepository;

    public void createFeeNotice(FeeReqDTO.CreateFeeNoticeRequestDTO request, Long memberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new FeeException(FeeErrorCode.ACCESS_DENIED);
        }

        // 2. 동아리 정보 추출
        Club club = memberClub.getClub();

        // 3. FeeNotice 생성
        FeeNotice feeNotice = FeeNoticeConverter.toFeeNotice(request, club);
        feeNoticeRepository.save(feeNotice);

        log.info("회비 공지가 생성되었습니다. feeNoticeId: {}, clubId: {}", feeNotice.getId(), club.getId());
    }

    public void updatePaymentStatus(Long feeId, Long memberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new FeeException(FeeErrorCode.ACCESS_DENIED);
        }

        // 2. FeeNotice 조회
        FeeNotice feeNotice = feeNoticeRepository.findById(feeId)
                .orElseThrow(() -> new FeeException(FeeErrorCode.FEE_NOTICE_NOT_FOUND));

        // 3. FeeStatus 업데이트
        FeeStatus feeStatus = feeStatusRepository.findByFeeNoticeAndMemberClub(feeNotice, memberClub)
                .orElseGet(() -> FeeStatus.builder()
                        .feeNotice(feeNotice)
                        .memberClub(memberClub)
                        .build());

        feeStatus.setPaid(true); // 납부 상태 업데이트
        feeStatusRepository.save(feeStatus);

        log.info("회비 납부 상태가 업데이트되었습니다. feeId: {}, memberClubId: {}", feeId, memberClubId);
    }

    public void updateFeeNotice(Long feeId, FeeReqDTO.UpdateFeeNoticeRequest request, Long memberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new FeeException(FeeErrorCode.ACCESS_DENIED);
        }

        // 2. FeeNotice 조회
        FeeNotice feeNotice = feeNoticeRepository.findById(feeId)
                .orElseThrow(() -> new FeeException(FeeErrorCode.FEE_NOTICE_NOT_FOUND));

        // 3. FeeNotice 수정
        feeNotice.update(
                request.title(),
                request.content(),
                request.amount(),
                request.bank(),
                request.accountNumber(),
                request.date(),
                request.participantCount()
        );

        log.info("회비 공지가 수정되었습니다. feeId: {}, memberClubId: {}", feeId, memberClubId);
    }
}
