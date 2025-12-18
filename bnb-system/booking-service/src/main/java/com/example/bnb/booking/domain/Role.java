package com.example.bnb.booking.domain;

import java.util.Comparator;
import java.util.List;

/**
 * Application roles ordered by privilege (higher = more privilege).
 */
public enum Role {
  GUEST(10),
  STAFF(20),
  MANAGER(30),
  ADMIN(40),
  SUPER_ADMIN(50);

  private final int privilegeLevel;

  Role(int privilegeLevel) {
    this.privilegeLevel = privilegeLevel;
  }

  public int getPrivilegeLevel() {
    return privilegeLevel;
  }

  public static Role highestPrivilege() {
    return List.of(values()).stream()
        .max(Comparator.comparingInt(Role::getPrivilegeLevel))
        .orElseThrow();
  }
}

