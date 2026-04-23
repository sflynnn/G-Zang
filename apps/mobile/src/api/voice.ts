/**
 * 语音记账相关 API
 */
import { api } from './index';

export interface VoiceIntentResult {
  originalText: string;
  amount: number | null;
  date: string | null;
  categoryId: number | null;
  categoryName: string | null;
  type: 1 | 2 | null; // 1-收入, 2-支出
  remark: string | null;
  confidence: number;
  success: boolean;
  errorMessage?: string;
}

/**
 * 上传语音文件进行识别
 */
export async function uploadVoice(
  filePath: string,
  fileName: string
): Promise<VoiceIntentResult> {
  return api.upload<VoiceIntentResult>({
    url: '/voice/recognize',
    filePath,
    name: 'file',
    formData: { fileName },
  });
}

/**
 * 解析记账意图（文字输入）
 */
export async function parseIntent(text: string): Promise<VoiceIntentResult> {
  return api.post('/voice/parse-intent', { text });
}

/**
 * 格式化语音识别结果用于显示
 */
export function formatVoiceResult(result: VoiceIntentResult): string {
  if (!result.success) {
    return result.errorMessage || '识别失败';
  }

  const parts: string[] = [];

  if (result.categoryName) {
    parts.push(result.categoryName);
  }

  if (result.amount !== null) {
    parts.push(`¥${result.amount.toFixed(2)}`);
  }

  if (result.type === 1) {
    parts.unshift('收入');
  } else if (result.type === 2) {
    parts.unshift('支出');
  }

  return parts.join(' - ') || result.originalText;
}

export default {
  uploadVoice,
  parseIntent,
  formatVoiceResult
};
