package com.gzang.app.service;

import java.util.List;

/**
 * 设备会话管理服务接口
 * BR005: 多设备登录限制
 *
 * @author G-Zang Team
 */
public interface DeviceSessionService {

    /**
     * 注册设备会话
     * 当用户登录时调用，记录新的设备会话
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param deviceType 设备类型 (web/ios/android/wechat)
     * @param deviceName 设备名称
     * @param ipAddress IP地址
     */
    void registerSession(Long userId, String deviceId, String deviceType, String deviceName, String ipAddress);

    /**
     * 获取用户的所有设备会话
     *
     * @param userId 用户ID
     * @return 设备会话列表
     */
    List<DeviceSession> getUserSessions(Long userId);

    /**
     * 获取用户当前设备数量
     *
     * @param userId 用户ID
     * @return 设备数量
     */
    int getUserSessionCount(Long userId);

    /**
     * 验证设备会话是否有效
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否有效
     */
    boolean isSessionValid(Long userId, String deviceId);

    /**
     * 移除设备会话
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     */
    void removeSession(Long userId, String deviceId);

    /**
     * 移除所有其他设备的会话（保留当前设备）
     *
     * @param userId 用户ID
     * @param currentDeviceId 当前设备ID
     */
    void removeOtherSessions(Long userId, String currentDeviceId);

    /**
     * 移除用户所有设备会话
     *
     * @param userId 用户ID
     */
    void removeAllSessions(Long userId);

    /**
     * 检查并移除过期会话
     */
    void cleanupExpiredSessions();

    /**
     * 设备会话信息
     */
    class DeviceSession {
        private String deviceId;
        private String deviceType;
        private String deviceName;
        private String ipAddress;
        private long loginTime;
        private long lastActiveTime;
        private boolean isCurrent;

        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public String getDeviceType() { return deviceType; }
        public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        public long getLoginTime() { return loginTime; }
        public void setLoginTime(long loginTime) { this.loginTime = loginTime; }
        public long getLastActiveTime() { return lastActiveTime; }
        public void setLastActiveTime(long lastActiveTime) { this.lastActiveTime = lastActiveTime; }
        public boolean isCurrent() { return isCurrent; }
        public void setCurrent(boolean current) { isCurrent = current; }
    }
}
