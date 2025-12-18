package com.example.bnb.access.smartlock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SmartLockTargetTest {
  @Test
  void parse_yalePrefix_routesToYale() {
    SmartLockTarget t = SmartLockTarget.parse("yale:device-123");
    assertEquals(SmartLockProvider.YALE, t.provider());
    assertEquals("device-123", t.deviceId());
  }

  @Test
  void parse_seamPrefix_routesToSeam() {
    SmartLockTarget t = SmartLockTarget.parse("seam:dev_abc");
    assertEquals(SmartLockProvider.SEAM, t.provider());
    assertEquals("dev_abc", t.deviceId());
  }

  @Test
  void parse_noPrefix_defaultsToSeam() {
    SmartLockTarget t = SmartLockTarget.parse("dev_any");
    assertEquals(SmartLockProvider.SEAM, t.provider());
    assertEquals("dev_any", t.deviceId());
  }
}

