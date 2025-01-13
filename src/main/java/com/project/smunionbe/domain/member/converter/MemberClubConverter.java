package com.project.smunionbe.domain.member.converter;

import com.project.smunionbe.domain.club.dto.request.ClubReqDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.entity.MemberClub;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberClubConverter {

    public static MemberClub toMemberClub(Member member, Club club, Department department, String nickName, Boolean isStaff) {

        return MemberClub.builder()
                .member(member)
                .club(club)
                .department(department)
                .nickname(nickName)
                .is_Staff(isStaff)
                .build();
    }
}
