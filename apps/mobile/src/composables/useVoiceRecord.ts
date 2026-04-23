/**
 * 语音录制 Composable
 */
import { ref, onUnmounted } from 'vue'

export interface VoiceRecordResult {
  filePath: string
  duration: number
  fileSize: number
}

export function useVoiceRecord() {
  const isRecording = ref(false)
  const recordingTime = ref(0)
  const recordResult = ref<VoiceRecordResult | null>(null)

  let recorderManager: any = null
  let timer: ReturnType<typeof setInterval> | null = null

  const initRecorder = () => {
    // #ifdef APP-PLUS
    recorderManager = uni.requireNativePlugin('plus-audio')
    if (recorderManager) {
      recorderManager.onStop((res: any) => {
        recordResult.value = {
          filePath: res.tempFilePath,
          duration: res.duration,
          fileSize: res.fileSize
        }
      })
    }
    // #endif

    // #ifdef MP-WEIXIN
    recorderManager = wx.getRecorderManager()
    recorderManager.onStop((res: any) => {
      recordResult.value = {
        filePath: res.tempFilePath,
        duration: res.duration,
        fileSize: res.fileSize
      }
    })
    // #endif
  }

  const startRecording = () => {
    if (isRecording.value) return

    initRecorder()

    // #ifdef APP-PLUS
    recorderManager?.start({
      format: 'mp3',
      sampleRate: 16000,
      numberOfChannels: 1,
      encodeBitRate: 48000,
      duration: 60000 // 最大 60 秒
    })
    // #endif

    // #ifdef MP-WEIXIN
    recorderManager?.start({
      format: 'mp3',
      sampleRate: 16000,
      numberOfChannels: 1,
      encodeBitRate: 48000,
      duration: 60000
    })
    // #endif

    isRecording.value = true
    recordingTime.value = 0

    timer = setInterval(() => {
      recordingTime.value++
      // 最大录制 60 秒
      if (recordingTime.value >= 60) {
        stopRecording()
      }
    }, 1000)
  }

  const stopRecording = (): Promise<VoiceRecordResult | null> => {
    return new Promise((resolve) => {
      if (timer) {
        clearInterval(timer)
        timer = null
      }

      // #ifdef APP-PLUS
      recorderManager?.stop()
      // #endif

      // #ifdef MP-WEIXIN
      recorderManager?.stop()
      // #endif

      isRecording.value = false

      setTimeout(() => {
        resolve(recordResult.value)
      }, 100)
    })
  }

  const cancelRecording = () => {
    if (timer) {
      clearInterval(timer)
      timer = null
    }

    // #ifdef APP-PLUS
    recorderManager?.stop()
    // #endif

    // #ifdef MP-WEIXIN
    recorderManager?.stop()
    // #endif

    isRecording.value = false
    recordingTime.value = 0
    recordResult.value = null
  }

  const formatDuration = (seconds: number): string => {
    const mins = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }

  onUnmounted(() => {
    if (timer) {
      clearInterval(timer)
    }
    cancelRecording()
  })

  return {
    isRecording,
    recordingTime,
    recordResult,
    startRecording,
    stopRecording,
    cancelRecording,
    formatDuration
  }
}
