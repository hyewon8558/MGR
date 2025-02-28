package com.MGR.entity;

import com.MGR.Dto.MemberFormDto;
import com.MGR.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Setter @Getter @ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    private String birth;

    @Column
    private Boolean isSuspended;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {

        Member member = new Member();

        member.setEmail(memberFormDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberFormDto.getPassword()));

        member.setName(memberFormDto.getName());
        member.setNickname(memberFormDto.getNickname());
        member.setBirth(memberFormDto.getBirth());
        member.setRole(Role.ADMIN);
        member.setIsSuspended(false);

        return member;
    }
}
