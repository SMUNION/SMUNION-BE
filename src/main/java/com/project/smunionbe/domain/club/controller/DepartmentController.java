package com.project.smunionbe.domain.club.controller;


import com.project.smunionbe.domain.club.dto.request.DepartmentReqDTO;
import com.project.smunionbe.domain.club.dto.request.GalleryReqDTO;
import com.project.smunionbe.domain.club.dto.response.DepartmentResDTO;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.service.command.DepartmentCommandService;
import com.project.smunionbe.domain.club.service.query.DepartmentQueryService;
import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.member.service.ClubSelectionService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/department")
@Tag(name = "동아리 부서 API", description = "동아리 부서 관련 CRUD 및 기능 API")
public class DepartmentController {

    private final DepartmentCommandService departmentCommandService;
    private final DepartmentQueryService departmentQueryService;
    private final ClubSelectionService clubSelectionService;

    @PostMapping("/create")
    @Operation(
            summary = "동아리 부서 생성 API",
            description = "동아리 멤버가 부서를 생성하는 API 입니다."
    )
    public CustomResponse<DepartmentResDTO.CreateDepartmentDTO> createDepartment(
            @RequestBody @Valid DepartmentReqDTO.CreateDepartmentDTO request,
            @AuthenticationPrincipal CustomUserDetails auth,
            HttpSession session

    ) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, auth.getMember().getId());

        DepartmentResDTO.CreateDepartmentDTO response = departmentCommandService.createDepartment(request, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/getAll")
    @Operation(
            summary = "동아리 부서 전제 조회 API",
            description = "특정 동아리의 모든 부서를 조회합니다."
    )

    public CustomResponse<DepartmentResDTO.GetDepartmentListResDTO> getAllDepartment(
            @AuthenticationPrincipal CustomUserDetails auth,
            HttpSession session

    ) {
        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, auth.getMember().getId());

        DepartmentResDTO.GetDepartmentListResDTO response =
                departmentQueryService.getAllDepartment(selectedMemberClubId);
        return CustomResponse.onSuccess(response);
    }


    @DeleteMapping("/{departmentId}")
    @Operation(
            summary = "부서 삭제 API",
            description = "특정 부서를 삭제합니다."
    )
    public CustomResponse<String> deleteDepartment(
            @PathVariable Long departmentId,
            @AuthenticationPrincipal CustomUserDetails auth,
            HttpSession session

    ) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, auth.getMember().getId());

        departmentCommandService.deleteDepartment(departmentId, selectedMemberClubId);
        return CustomResponse.onSuccess("부서가 성공적으로 삭제되었습니다.");
    }

    @PostMapping("/{departmentId}")
    @Operation(
            summary = "승인 코드 생성 API",
            description = "초대에 필요한 승인 코드를 생성합니다."
    )
    public CustomResponse<String> createInviteCode(
            @PathVariable String departmentId,
            @AuthenticationPrincipal CustomUserDetails auth,
            HttpSession session

    ) {
        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, auth.getMember().getId());

        String code = departmentCommandService.createInviteCode(departmentId, selectedMemberClubId);
        return CustomResponse.onSuccess(code);
    }
}
