package com.project.smunionbe.domain.notification.attendance.controller;

import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.member.service.ClubSelectionService;
import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;
import com.project.smunionbe.domain.notification.attendance.service.command.AttendanceCommandService;
import com.project.smunionbe.domain.notification.attendance.service.query.AttendanceQueryService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices/attendance")
@Tag(name = "출석 공지 API", description = "출석 공지 관련 CRUD 및 기능 API")
public class AttendanceController {

    private final AttendanceCommandService attendanceCommandService;
    private final AttendanceQueryService attendanceQueryService;
    private final ClubSelectionService clubSelectionService;

    @PostMapping("")
    @Operation(summary = "출석 공지 생성 API", description = "멤버가 속한 동아리에서 새로운 출석 공지를 생성합니다.")
    public ResponseEntity<CustomResponse<String>> createAttendance(
            @RequestBody @Valid AttendanceReqDTO.CreateAttendanceDTO request,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        attendanceCommandService.createAttendance(request, selectedMemberClubId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED, "출석 공지가 성공적으로 생성되었습니다."));
    }

    @GetMapping("/status")
    @Operation(summary = "미출석 인원 조회 API", description = "특정 출석 공지에서 아직 출석하지 않은 멤버를 조회합니다.")
    public CustomResponse<AttendanceResDTO.AttendanceAbsenteesResponse> getAbsentees(
            @RequestParam("attendanceId") Long attendanceId,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        AttendanceResDTO.AttendanceAbsenteesResponse response = attendanceQueryService.getAbsentees(attendanceId, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }

    @GetMapping("")
    @Operation(summary = "출석 공지 목록 조회 API", description = "특정 동아리의 모든 출석 공지를 커서 기반 페이지네이션으로 조회합니다.")
    public CustomResponse<AttendanceResDTO.AttendanceListResponse> getAttendances(
            @RequestParam(value = "cursor", required = false) Long cursor,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        AttendanceResDTO.AttendanceListResponse response = attendanceQueryService.getAttendances(cursor, size, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/{attendanceId}")
    @Operation(summary = "출석 공지 상세 조회 API", description = "특정 출석 공지의 상세 정보를 조회합니다.")
    public CustomResponse<AttendanceResDTO.AttendanceDetailResponse> getAttendanceDetail(
            @PathVariable("attendanceId") Long attendanceId,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        AttendanceResDTO.AttendanceDetailResponse response = attendanceQueryService.getAttendanceDetail(attendanceId, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }

    @PatchMapping("/{attendanceId}")
    @Operation(summary = "출석 공지 수정 API", description = "기존 출석 공지의 내용을 수정합니다.")
    public CustomResponse<String> updateAttendance(
            @PathVariable("attendanceId") Long attendanceId,
            @RequestBody @Valid AttendanceReqDTO.UpdateAttendanceRequest request,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        attendanceCommandService.updateAttendance(attendanceId, request, selectedMemberClubId);

        return CustomResponse.onSuccess("출석 공지 수정 성공");
    }

    @DeleteMapping("/{attendanceId}")
    @Operation(summary = "출석 공지 삭제 API", description = "특정 출석 공지를 삭제합니다.")
    public CustomResponse<String> deleteAttendance(
            @PathVariable("attendanceId") Long attendanceId,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        attendanceCommandService.deleteAttendance(attendanceId, selectedMemberClubId);

        return CustomResponse.onSuccess("출석 공지가 성공적으로 삭제되었습니다.");
    }

    @PostMapping("/verify")
    @Operation(summary = "출석 상태 업데이트 API", description = "특정 출석 공지에 대해 사용자의 출석 상태를 업데이트합니다.")
    public CustomResponse<String> verifyAttendance(
            @RequestBody @Valid AttendanceReqDTO.VerifyAttendanceRequest request,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        attendanceCommandService.verifyAttendance(request, selectedMemberClubId);

        return CustomResponse.onSuccess("출석이 완료되었습니다.");
    }

    @GetMapping("/{attendanceId}/code")
    @Operation(summary = "출석 공지 난수 조회 API", description = "특정 출석 공지에 대한 난수를 가져옵니다.")
    public CustomResponse<String> getAttendanceCode(
            @PathVariable("attendanceId") Long attendanceId,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        String code = attendanceQueryService.getAttendanceCode(attendanceId, selectedMemberClubId);

        return CustomResponse.onSuccess(code);
    }
}
