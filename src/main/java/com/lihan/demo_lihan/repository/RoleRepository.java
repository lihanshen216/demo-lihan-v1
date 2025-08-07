package com.lihan.demo_lihan.repository;

import com.lihan.demo_lihan.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 根据角色代码查找角色
     */
    Optional<Role> findByRoleCode(String roleCode);

    /**
     * 根据角色名称查找角色
     */
    Optional<Role> findByRoleName(String roleName);

    /**
     * 查找所有启用的角色
     */
    List<Role> findByIsEnabledTrue();

    /**
     * 根据角色代码列表查找角色
     */
    @Query("SELECT r FROM Role r WHERE r.roleCode IN :roleCodes")
    List<Role> findByRoleCodeIn(@Param("roleCodes") List<String> roleCodes);

    /**
     * 检查角色代码是否已存在
     */
    boolean existsByRoleCode(String roleCode);

    /**
     * 检查角色名称是否已存在
     */
    boolean existsByRoleName(String roleName);
}