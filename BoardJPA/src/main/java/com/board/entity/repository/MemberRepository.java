package com.board.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.dto.MemberDTO;
import com.board.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity,String>{
	public MemberEntity findByAuthkey(String authkey);
}
