package com.project.smunionbe.domain.club.controller;


import com.project.smunionbe.domain.club.dto.request.DepartmentReqDTO;
import com.project.smunionbe.domain.club.dto.request.GalleryReqDTO;
import com.project.smunionbe.domain.club.dto.response.DepartmentResDTO;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.service.command.DepartmentCommandService;
import com.project.smunionbe.domain.club.service.query.DepartmentQueryService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/department")
@Tag(name = "동아리 부서 API", description = "동아리 부서 관련 CRUD 및 기능 API")
public class DepartmentController {

    private final DepartmentCommandService departmentCommandService;
    private final DepartmentQueryService departmentQueryService;

    @PostMapping("/create")
    @Operation(
            summary = "동아리 부서 생성 API",
            description = "동아리 멤버가 부서를 생성하는 API 입니다."
    )
    public CustomResponse<DepartmentResDTO.CreateDepartmentDTO> createDepartment(
            @RequestBody @Valid DepartmentReqDTO.CreateDepartmentDTO request
    ) {

        DepartmentResDTO.CreateDepartmentDTO response = departmentCommandService.createDepartment(request);

        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/getAll/{clubId}")
    @Operation(
            summary = "동아리 부서 전제 조회 API",
            description = "특정 동아리의 모든 부서를 조회합니다."
    )

    public CustomResponse<DepartmentResDTO.GetDepartmentListResDTO> getAllDepartment(
            @PathVariable Long clubId
    ) {
        DepartmentResDTO.GetDepartmentListResDTO response =
                departmentQueryService.getAllDepartment(clubId);
        return CustomResponse.onSuccess(response);
    }


    @DeleteMapping("/{memberId}/{departmentId}")
    @Operation(
            summary = "부서 삭제 API",
            description = "특정 부서를 삭제합니다."
    )
    public CustomResponse<String> deleteDepartment(
            @PathVariable Long departmentId,
            @PathVariable Long memberId
    ) {
        departmentCommandService.deleteDepartment(departmentId, memberId);
        return CustomResponse.onSuccess("부서가 성공적으로 삭제되었습니다.");
    }

    @PostMapping("/{departmentId}")
    @Operation(
            summary = "승인 코드 생성 API",
            description = "초대에 필요한 승인 코드를 생성합니다."
    )
    public CustomResponse<String> createInviteCode(
            @PathVariable String departmentId
    ) {
        String code = departmentCommandService.createInviteCode(departmentId);
        return CustomResponse.onSuccess(code);
    }
}
