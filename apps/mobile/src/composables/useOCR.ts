/**
 * OCR 识别 Composable
 */
import { ref } from 'vue'

export interface OCRResult {
  amount?: number
  category?: string
  merchant?: string
  date?: string
  rawText: string
  confidence: number
}

export function useOCR() {
  const isProcessing = ref(false)
  const lastResult = ref<OCRResult | null>(null)

  const recognizeImage = async (filePath: string): Promise<OCRResult | null> => {
    isProcessing.value = true

    try {
      // #ifdef APP-PLUS
      const ocr = uni.requireNativePlugin('plus-ocr')
      return await new Promise((resolve) => {
        ocr.recognize(
          { filePath, type: 'receipt' },
          (res: any) => {
            if (res.result) {
              const result: OCRResult = {
                rawText: res.result.text || '',
                confidence: res.result.confidence || 0.8,
                amount: parseAmount(res.result.text),
                category: parseCategory(res.result.text),
                merchant: parseMerchant(res.result.text),
                date: parseDate(res.result.text)
              }
              lastResult.value = result
              resolve(result)
            } else {
              resolve(null)
            }
          },
          () => resolve(null)
        )
      })
      // #endif

      // #ifdef MP-WEIXIN
      const wxOCR = wx.createPluginNamespace('ocr')
      // @ts-ignore
      return new Promise((resolve) => {
        // 微信小程序 OCR 插件调用
        resolve(null)
      })
      // #endif

      // #ifdef H5
      // H5 使用云函数或其他 OCR API
      return null
      // #endif
    } catch (error) {
      return null
    } finally {
      isProcessing.value = false
    }
  }

  // 从文本中解析金额
  const parseAmount = (text: string): number | undefined => {
    const patterns = [
      /[¥￥]?\s*(\d+\.?\d*)/g,
      /总计[：:]?\s*[¥￥]?\s*(\d+\.?\d*)/gi,
      /合计[：:]?\s*[¥￥]?\s*(\d+\.?\d*)/gi,
      /实付[：:]?\s*[¥￥]?\s*(\d+\.?\d*)/gi
    ]

    for (const pattern of patterns) {
      const matches = text.match(pattern)
      if (matches) {
        const amounts = matches.map(m => parseFloat(m.replace(/[¥￥\s]/g, '')))
        return amounts[amounts.length - 1]
      }
    }
    return undefined
  }

  // 从文本中解析分类
  const parseCategory = (text: string): string | undefined => {
    const categories = ['餐饮', '交通', '购物', '娱乐', '医疗', '教育', '住宿', '通讯']
    for (const cat of categories) {
      if (text.includes(cat)) return cat
    }
    return undefined
  }

  // 从文本中解析商家
  const parseMerchant = (text: string): string | undefined => {
    const match = text.match(/^(.+?)[，。\s]/m)
    return match ? match[1].trim() : undefined
  }

  // 从文本中解析日期
  const parseDate = (text: string): string | undefined => {
    const pattern = /(\d{4}[-/年]\d{1,2}[-/月]\d{1,2}日?)/g
    const match = text.match(pattern)
    return match ? match[0] : undefined
  }

  return {
    isProcessing,
    lastResult,
    recognizeImage
  }
}
