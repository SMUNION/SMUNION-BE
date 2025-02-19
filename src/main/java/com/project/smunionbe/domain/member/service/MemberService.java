package com.project.smunionbe.domain.member.service;

import com.project.smunionbe.domain.community.service.ImageService;
import com.project.smunionbe.domain.email.service.DefaultEmailSender;
import com.project.smunionbe.domain.member.converter.MemberConverter;
import com.project.smunionbe.domain.member.dto.request.MemberRequestDTO;
import com.project.smunionbe.domain.member.dto.response.MemberResponseDTO;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.exception.AuthErrorCode;
import com.project.smunionbe.domain.member.exception.AuthException;
import com.project.smunionbe.domain.member.exception.MemberErrorCode;
import com.project.smunionbe.domain.member.exception.MemberException;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context; // Context 임포트
import org.thymeleaf.spring6.SpringTemplateEngine;


import java.security.SecureRandom;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final DefaultEmailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private final DefaultEmailSender defaultEmailSender;


    @Transactional
    public Long save(MemberRequestDTO.CreateMemberDTO dto) {
        // 1. 입력값 검증
        // 이메일 검사
        if (dto.email() == null || dto.email().isEmpty()) {
            throw new MemberException(MemberErrorCode.INVALID_MEMBER_EMAIL);
        }
        if (!dto.email().matches("^[A-Za-z0-9+_.-]+@sangmyung.kr")) { //상명대 이메일로만 가입 가능
            throw new MemberException(MemberErrorCode.INVALID_EMAIL_FORMAT);
        }

        // 비밀번호 검사
        if (dto.password() == null || dto.password().isEmpty()) {
            throw new MemberException(MemberErrorCode.INVALID_MEMBER_PASSWORD);
        }
        if (dto.password().length() < 8 || !dto.password().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]+$")) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD_FORMAT);
        }

        // 이름 검사
        if (dto.name() == null || dto.name().isEmpty()) {
            throw new MemberException(MemberErrorCode.INVALID_MEMBER_NAME);
        }

        // 전공 검사
        if (dto.major() == null || dto.major().isEmpty()) {
            throw new MemberException(MemberErrorCode.INVALID_MEMBER_MAJOR);
        }

        // 2. 중복 이메일 체크
        if (memberRepository.existsByEmail(dto.email())) {
            Member deletedMember = memberRepository.findByEmail(dto.email())
                    .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
            if (deletedMember.getDeletedAt() == null) {
                throw new MemberException(MemberErrorCode.DUPLICATE_MEMBER_EMAIL);
            }
        }

        // 3. DTO를 엔티티로 변환
        Member member = memberConverter.toMember(dto);

        // 4. 멤버 저장
        try {
            if (memberRepository.existsByEmail(dto.email())) {
                Member deletedMember = memberRepository.findByEmail(dto.email())
                        .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
                memberRepository.delete(deletedMember);
            }
            return memberRepository.save(member).getId();
        } catch (Exception e) {
            // 데이터 저장 실패 시 예외 처리
            throw new MemberException(MemberErrorCode.MEMBER_REGISTRATION_FAILED);
        }
    }

    @Transactional
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
    }


    // 회원 인증 메서드
    @Transactional
    public Member authenticate(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 확인 (입력한 비밀번호와 DB에 저장된 비밀번호 비교)
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_MEMBER_PASSWORD);
        }

        // 탈퇴한 회원이면 로그인 차단
        if (member.isDeleted()) {
            throw new AuthException(AuthErrorCode.MEMBER_DELETED);
        }

        return member;
    }


    // 회원 프로필 조회
    @Transactional(readOnly = true)
    public MemberResponseDTO.MemberProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        return memberConverter.toMemberProfile(member, extractStudentNumber(member.getEmail()));
    }

    // 이메일에서 학번만 추출
    private String extractStudentNumber(String email) {
        return email.split("@")[0];
    }

    // 회원 탈퇴
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new AuthException(AuthErrorCode.MEMBER_ALREADY_DELETED);
        }

        //회원 탈퇴 Soft Delete 수행
        member.delete();
    }

    //비밀번호 변경
    @Transactional
    public void changePassword(Long memberId, MemberRequestDTO.ChangePasswordDTO dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(dto.currentPassword(), member.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        // 새 비밀번호 유효성 검사
        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new AuthException(AuthErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        // 비밀번호 형식 검사
        if (dto.newPassword().length() < 8 || !dto.newPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]+$")) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD_FORMAT);
        }

        // 새로운 비밀번호 암호화 & 저장
        String encodedPassword = passwordEncoder.encode(dto.newPassword());
        member.updatePassword(encodedPassword);
        memberRepository.save(member);
    }


    //프로필 이미지 추가 / 수정
    @Transactional
    public MemberResponseDTO.MemberProfileImageResponse updateProfileImage(Long memberId, MultipartFile image) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 기존 프로필 사진 삭제 (기존 사진이 있을 경우)
        if (member.getProfileImage() != null) {
            imageService.deleteImageFromS3(member.getProfileImage());
        }

        // 새 프로필 사진 업로드
        String profileImageUrl = imageService.uploadImageToS3(image);

        // 회원 엔티티에 업데이트
        member.updateProfileImage(profileImageUrl);
        memberRepository.save(member);

        return memberConverter.toMemberProfileImage(member, profileImageUrl);
    }

    // 회원의 이미지 조회
    @Transactional(readOnly = true)
    public MemberResponseDTO.MemberProfileImageResponse getProfileImage(Long memberId) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));


        String profileImage = member.getProfileImage() != null ? member.getProfileImage() : null;
        return memberConverter.toMemberProfileImage(member, profileImage);
    }

    // 회원의 프로필 이미지 삭제
    @Transactional
    public void deleteProfileImage(Long memberId) {
        //회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        //프로필 이미지가 없는 경우 예외 처리
        if (member.getProfileImage() == null) {
            throw new MemberException(MemberErrorCode.PROFILE_IMAGE_NOT_FOUND);
        }

        //기존 프로필 이미지 S3에서 삭제
        imageService.deleteImageFromS3(member.getProfileImage());

        //DB에서 프로필 이미지 정보 제거 (null로 설정)
        member.updateProfileImage(null);
        memberRepository.save(member);
    }

    // 비밀번호 찾기
    @Transactional
    public void findPassword(MemberRequestDTO.FindPasswordDTO dto) {
        Member member = memberRepository.findByEmail(dto.email())
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        // 1. 임시 비밀번호 생성
        String temporaryPassword = generateTemporaryPassword();

        // 2. 비밀번호 암호화 및 저장
        member.updatePassword(passwordEncoder.encode(temporaryPassword));
        memberRepository.save(member);

        // 3. 이메일 전송
        sendTemporaryPasswordEmail(dto.email(), member.getName(), temporaryPassword);
    }

    private String generateTemporaryPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$!%*?&";
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }

    private void sendTemporaryPasswordEmail(String email, String name, String temporaryPassword) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("temporaryPassword", temporaryPassword);

        String mailBody = templateEngine.process("EmailTemporaryPasswordTemplate", context); // 이메일 본문을 생성
        defaultEmailSender.sendMail("회원가입 인증번호 메일입니다.", email, mailBody); // 이메일을 전송
    }




}
