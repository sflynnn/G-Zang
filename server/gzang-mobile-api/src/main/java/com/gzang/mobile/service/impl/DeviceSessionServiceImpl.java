package com.gzang.mobile.service.impl;

import com.gzang.app.service.DeviceSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 设备会话管理服务实现
 * BR005: 多设备登录限制
 * 使用内存存储实现（生产环境应使用Redis）
 *
 * @author G-Zang Team
 */
@Service
public class DeviceSessionServiceImpl implements DeviceSessionService {

    private static final Logger log = LoggerFactory.getLogger(DeviceSessionServiceImpl.class);

    // 最大允许设备数
    private static final int MAX_DEVICES_PER_USER = 5;

    // 会话有效期（毫秒），默认7天
    private static final long SESSION_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    // 用户会话存储: userId -> List<DeviceSession>
    private final Map<Long, List<DeviceSession>> userSessions = new ConcurrentHashMap<>();

    @Override
    public void registerSession(Long userId, String deviceId, String deviceType, 
                                 String deviceName, String ipAddress) {
        log.info("注册设备会话: userId={}, deviceId={}, deviceType={}", userId, deviceId, deviceType);

        List<DeviceSession> sessions = userSessions.computeIfAbsent(userId, k -> new ArrayList<>());

        // 检查是否已存在该设备
        Optional<DeviceSession> existingSession = sessions.stream()
                .filter(s -> s.getDeviceId().equals(deviceId))
                .findFirst();

        if (existingSession.isPresent()) {
            // 更新现有会话
            DeviceSession session = existingSession.get();
            session.setLastActiveTime(System.currentTimeMillis());
            log.info("更新现有设备会话: userId={}, deviceId={}", userId, deviceId);
        } else {
            // 检查设备数量限制
            if (sessions.size() >= MAX_DEVICES_PER_USER) {
                // 移除最旧的会话
                DeviceSession oldest = sessions.stream()
                        .min(Comparator.comparingLong(DeviceSession::getLastActiveTime))
                        .orElse(null);
                if (oldest != null) {
                    sessions.remove(oldest);
                    log.info("达到最大设备数限制，移除最旧设备: userId={}, removedDevice={}", userId, oldest.getDeviceId());
                }
            }

            // 添加新会话
            DeviceSession newSession = new DeviceSession();
            newSession.setDeviceId(deviceId);
            newSession.setDeviceType(deviceType);
            newSession.setDeviceName(deviceName);
            newSession.setIpAddress(ipAddress);
            newSession.setLoginTime(System.currentTimeMillis());
            newSession.setLastActiveTime(System.currentTimeMillis());
            newSession.setCurrent(true);

            // 将其他会话标记为非当前
            sessions.forEach(s -> s.setCurrent(false));
            sessions.add(newSession);

            log.info("新增设备会话: userId={}, deviceId={}, 当前设备数: {}", userId, deviceId, sessions.size());
        }
    }

    @Override
    public List<DeviceSession> getUserSessions(Long userId) {
        List<DeviceSession> sessions = userSessions.getOrDefault(userId, Collections.emptyList());
        // 清理过期会话
        long now = System.currentTimeMillis();
        return sessions.stream()
                .filter(s -> now - s.getLastActiveTime() < SESSION_EXPIRE_TIME)
                .collect(Collectors.toList());
    }

    @Override
    public int getUserSessionCount(Long userId) {
        return getUserSessions(userId).size();
    }

    @Override
    public boolean isSessionValid(Long userId, String deviceId) {
        List<DeviceSession> sessions = getUserSessions(userId);
        return sessions.stream()
                .anyMatch(s -> s.getDeviceId().equals(deviceId));
    }

    @Override
    public void removeSession(Long userId, String deviceId) {
        List<DeviceSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.removeIf(s -> s.getDeviceId().equals(deviceId));
            log.info("移除设备会话: userId={}, deviceId={}", userId, deviceId);
        }
    }

    @Override
    public void removeOtherSessions(Long userId, String currentDeviceId) {
        List<DeviceSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            int removed = sessions.size();
            sessions.removeIf(s -> !s.getDeviceId().equals(currentDeviceId));
            
            // 标记当前设备为当前
            sessions.forEach(s -> s.setCurrent(s.getDeviceId().equals(currentDeviceId)));
            
            log.info("移除其他设备会话: userId={}, currentDeviceId={}, 移除数量: {}", 
                    userId, currentDeviceId, removed - sessions.size());
        }
    }

    @Override
    public void removeAllSessions(Long userId) {
        userSessions.remove(userId);
        log.info("移除用户所有设备会话: userId={}", userId);
    }

    @Override
    public void cleanupExpiredSessions() {
        long now = System.currentTimeMillis();
        int cleanedCount = 0;

        for (List<DeviceSession> sessions : userSessions.values()) {
            int beforeSize = sessions.size();
            sessions.removeIf(s -> now - s.getLastActiveTime() >= SESSION_EXPIRE_TIME);
            cleanedCount += beforeSize - sessions.size();
        }

        if (cleanedCount > 0) {
            log.info("清理过期会话: 清理数量={}", cleanedCount);
        }
    }
}
