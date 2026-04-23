/**
 * 离线同步 Composable
 */
import { ref, computed } from 'vue'
import type { OfflineRecord, SyncStatus } from '@/types/device'

const STORAGE_KEY = 'offline_records'
const MAX_RETRY = 3

export function useOfflineSync() {
  const offlineRecords = ref<OfflineRecord[]>([])
  const syncStatus = ref<SyncStatus>({
    pending: 0,
    syncing: false,
    lastSyncTime: undefined
  })

  const pendingCount = computed(() => offlineRecords.value.length)

  const loadOfflineRecords = () => {
    try {
      const data = uni.getStorageSync(STORAGE_KEY)
      if (data) {
        offlineRecords.value = JSON.parse(data)
      }
    } catch (error) {
      // ignore
    }
  }

  const saveOfflineRecords = () => {
    try {
      uni.setStorageSync(STORAGE_KEY, JSON.stringify(offlineRecords.value))
      syncStatus.value.pending = offlineRecords.value.length
    } catch (error) {
      // ignore
    }
  }

  const addOfflineRecord = (record: Omit<OfflineRecord, 'id' | 'timestamp' | 'retryCount'>) => {
    const newRecord: OfflineRecord = {
      ...record,
      id: `${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
      timestamp: new Date().toISOString(),
      retryCount: 0
    }
    offlineRecords.value.push(newRecord)
    saveOfflineRecords()
  }

  const removeOfflineRecord = (id: string) => {
    offlineRecords.value = offlineRecords.value.filter(r => r.id !== id)
    saveOfflineRecords()
  }

  const syncOfflineRecords = async (syncFn: (record: OfflineRecord) => Promise<boolean>) => {
    if (syncStatus.value.syncing || offlineRecords.value.length === 0) return

    syncStatus.value.syncing = true

    try {
      for (const record of offlineRecords.value) {
        if (record.retryCount >= MAX_RETRY) {
          // 超过最大重试次数，移除记录
          removeOfflineRecord(record.id)
          continue
        }

        try {
          const success = await syncFn(record)
          if (success) {
            removeOfflineRecord(record.id)
          } else {
            record.retryCount++
            saveOfflineRecords()
          }
        } catch {
          record.retryCount++
          saveOfflineRecords()
        }
      }

      syncStatus.value.lastSyncTime = new Date().toISOString()
    } finally {
      syncStatus.value.syncing = false
    }
  }

  const clearOfflineRecords = () => {
    offlineRecords.value = []
    saveOfflineRecords()
  }

  // 初始化加载
  loadOfflineRecords()

  return {
    offlineRecords,
    syncStatus,
    pendingCount,
    addOfflineRecord,
    removeOfflineRecord,
    syncOfflineRecords,
    clearOfflineRecords,
    loadOfflineRecords
  }
}
