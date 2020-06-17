package com.deep.tcpservice.bean;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Dao层接口
 */
public interface UserTableRepository extends JpaRepository<UserTable, Integer> {

    List<UserTable> findByUsernameLike(String username);

    List<UserTable> findByIdLike(int id);

    /**
     * 更新头像路径
     * @param id
     * @param path
     */
    @Modifying
    @Query("update UserTable m set m.headerPath=:path where m.id=:id")
    void updateHeaderById(int id, String path);
}
