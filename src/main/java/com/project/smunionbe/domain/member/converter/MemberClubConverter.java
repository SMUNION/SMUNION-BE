package com.project.smunionbe.domain.member.converter;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.DepartmentRepository;
import com.project.smunionbe.domain.member.dto.response.MemberClubResponseDTO;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceErrorCode;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MemberClubConverter {

    public MemberClubResponseDTO.MemberClubResponse toResponse(MemberClub memberClub, Club club, Department department) {
        return new MemberClubResponseDTO.MemberClubResponse(
                memberClub.getId(),
                department.getName(),
                club.getName(),
                memberClub.getNickname()
        );
    }

    public List<MemberClubResponseDTO.MemberClubResponse> toResponseList(
            List<MemberClub> memberClubs, Map<Long, Club> clubMap, Map<Long, Department> departmentMap) {

        return memberClubs.stream()
                .map(memberClub -> {
                    Club club = clubMap.get(memberClub.getClub().getId());
                    Department department = departmentMap.get(club.getId());
                    return toResponse(memberClub, club, department);
                })
                .collect(Collectors.toList());
    }
}
