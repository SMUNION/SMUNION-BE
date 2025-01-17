package com.project.smunionbe.domain.notification.fee.service.query;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.fee.converter.FeeNoticeConverter;
import com.project.smunionbe.domain.notification.fee.dto.response.FeeResDTO;
import com.project.smunionbe.domain.notification.fee.entity.FeeNotice;
import com.project.smunionbe.domain.notification.fee.exception.FeeErrorCode;
import com.project.smunionbe.domain.notification.fee.exception.FeeException;
import com.project.smunionbe.domain.notification.fee.repository.FeeNoticeRepository;
import com.project.smunionbe.domain.notification.fee.repository.FeeStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FeeQueryService {

    private final FeeNoticeRepository feeNoticeRepository;
    private final FeeStatusRepository feeStatusRepository;
    private final MemberClubRepository memberClubRepository;

    public FeeResDTO.FeeNoticeListResponse getFeeNotices(
            Long clubId, Long cursor, int size, Long memberClubId) {

        // 1. MemberClub 검증 (사용자가 해당 동아리 멤버인지 확인)
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        // 2. 사용자가 요청한 clubId와 memberClub의 clubId가 일치하는지 확인
        if (!memberClub.getClub().getId().equals(clubId)) {
            throw new FeeException(FeeErrorCode.ACCESS_DENIED);
        }

        // 3. 회비 공지 데이터 조회 (통합 메서드 사용)
        List<FeeNotice> feeNotices = feeNoticeRepository.findByClubIdWithCursor(
                clubId, cursor, PageRequest.of(0, size)
        );

        // 4. DTO 변환
        List<FeeResDTO.FeeNoticeResponse> fees = feeNotices.stream()
                .map(FeeNoticeConverter::toFeeNoticeResponse)
                .toList();

        // 5. 다음 페이지 여부 확인
        boolean hasNext = feeNotices.size() == size;
        Long nextCursor = hasNext ? feeNotices.get(feeNotices.size() - 1).getId() : null;

        // 6. 결과 반환
        return new FeeResDTO.FeeNoticeListResponse(fees, hasNext, nextCursor);
    }

    public FeeResDTO.FeeNoticeResponse getFeeNoticeDetail(Long feeId, Long memberClubId) {
        // 1. MemberClub 검증 (동아리 멤버인지 확인)
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        // 2. FeeNotice 조회
        FeeNotice feeNotice = feeNoticeRepository.findById(feeId)
                .orElseThrow(() -> new FeeException(FeeErrorCode.FEE_NOTICE_NOT_FOUND));

        // 3. 동아리 검증 (회비 공지가 같은 동아리에 속해 있는지 확인)
        if (!feeNotice.getClub().getId().equals(memberClub.getClub().getId())) {
            throw new FeeException(FeeErrorCode.ACCESS_DENIED);
        }

        // 4. DTO 변환
        return FeeNoticeConverter.toFeeNoticeResponse(feeNotice);
    }

    public FeeResDTO.UnpaidMembersResponse getUnpaidMembers(Long feeId, Long memberClubId) {
        // 1. MemberClub 검증 (동아리 멤버인지 확인)
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        // 2. FeeNotice 조회
        FeeNotice feeNotice = feeNoticeRepository.findById(feeId)
                .orElseThrow(() -> new FeeException(FeeErrorCode.FEE_NOTICE_NOT_FOUND));

        // 3. 동아리 검증 (회비 공지가 같은 동아리에 속해 있는지 확인)
        if (!feeNotice.getClub().getId().equals(memberClub.getClub().getId())) {
            throw new FeeException(FeeErrorCode.ACCESS_DENIED);
        }

        // 4. 미납부 멤버 조회
        List<MemberClub> unpaidMembers = feeStatusRepository.findUnpaidMembersByFeeNotice(feeNotice);

        // 5. DTO 변환
        List<FeeResDTO.UnpaidMemberResponse> unpaidMemberResponses = unpaidMembers.stream()
                .map(member -> new FeeResDTO.UnpaidMemberResponse(member.getId(), member.getNickname()))
                .toList();

        // 6. 결과 반환
        return new FeeResDTO.UnpaidMembersResponse(feeId, unpaidMemberResponses);
    }
}
