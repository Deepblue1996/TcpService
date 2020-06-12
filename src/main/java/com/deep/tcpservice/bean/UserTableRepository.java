package com.deep.tcpservice.bean;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Dao层接口
 */
public interface UserTableRepository extends JpaRepository<UserTable, Integer> {

    List<UserTable> findByUsernameLike(String username);

    List<UserTable> findByIdLike(int id);

}
