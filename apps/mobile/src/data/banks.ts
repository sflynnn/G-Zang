/**
 * Bank data for China
 * Data source: icongo/bank-logos (MIT License) + existing local SVGs
 * 50+ banks organized by first letter (A-Z + #)
 */

export interface BankItem {
  /** Bank code (e.g. ICBC) */
  code: string
  /** Chinese name */
  name: string
  /** First letter for grouping: A-Z or # */
  letter: string
  /** Brand color (optional) */
  color?: string
  /** SVG icon path relative to /static/icons/ (null = use text fallback) */
  iconPath?: string | null
  /** Pinyin initials for search (e.g. gsyh) */
  pinyin?: string
}

// Group A
const banksA: BankItem[] = [
  { code: 'ICBC', name: '中国工商银行', letter: 'A', color: '#E30613', iconPath: '/static/icons/icbc.svg', pinyin: 'gsyh' },
  { code: 'ABC', name: '中国农业银行', letter: 'A', color: '#2E8B57', iconPath: '/static/icons/abc.svg', pinyin: 'zgnyyh' },
  { code: 'BOC', name: '中国银行', letter: 'A', color: '#C40000', iconPath: '/static/icons/boc.svg', pinyin: 'zgyh' },
  { code: 'CCB', name: '中国建设银行', letter: 'A', color: '#003087', iconPath: '/static/icons/ccb.svg', pinyin: 'zgjsyh' },
]

// Group C
const banksC: BankItem[] = [
  { code: 'COMM', name: '交通银行', letter: 'C', color: '#003087', iconPath: '/static/icons/comm.svg', pinyin: 'jtyh' },
  { code: 'CMB', name: '招商银行', letter: 'C', color: '#C40000', iconPath: '/static/icons/cmb.svg', pinyin: 'zsyh' },
  { code: 'CMBC', name: '中国民生银行', letter: 'C', color: '#003087', iconPath: '/static/icons/cmbc.svg', pinyin: 'zgmsyh' },
  { code: 'CITIC', name: '中信银行', letter: 'C', color: '#E30613', iconPath: '/static/icons/citic.svg', pinyin: 'zxyh' },
  { code: 'CIB', name: '兴业银行', letter: 'C', color: '#003087', iconPath: '/static/icons/cib.svg', pinyin: 'xyyh' },
  { code: 'SPD', name: '浦发银行', letter: 'C', color: '#003087', iconPath: '/static/icons/spdb.svg', pinyin: 'pfyh' },
  { code: 'HXB', name: '华夏银行', letter: 'C', color: '#C40000', iconPath: '/static/icons/hxb.svg', pinyin: 'hxyh' },
  { code: 'CEB', name: '中国光大银行', letter: 'C', color: '#003087', pinyin: 'zggdyh' },
  { code: 'CGB', name: '广发银行', letter: 'C', color: '#E30613', pinyin: 'gfyh' },
  { code: 'PINGAN', name: '平安银行', letter: 'C', color: '#C40000', pinyin: 'payh' },
  { code: 'PSBC', name: '中国邮政储蓄银行', letter: 'C', color: '#003087', iconPath: '/static/icons/psbc.svg', pinyin: 'zgyzcxyh' },
  { code: 'BOB', name: '北京银行', letter: 'C', color: '#003087', pinyin: 'bjyh' },
  { code: 'SHB', name: '上海银行', letter: 'C', color: '#003087', pinyin: 'shyh' },
  { code: 'CZB', name: '浙商银行', letter: 'C', color: '#003087', pinyin: 'zsyh' },
  { code: 'BOJ', name: '北京农商银行', letter: 'C', color: '#003087', pinyin: 'bjnsyh' },
  { code: 'SHRCB', name: '上海农商银行', letter: 'C', color: '#003087', pinyin: 'shnsyh' },
]

// Group G
const banksG: BankItem[] = [
  { code: 'GDB', name: '广东发展银行', letter: 'G', color: '#C40000', pinyin: 'gdfzyh' },
  { code: 'GZCB', name: '广州银行', letter: 'G', color: '#003087', pinyin: 'gzyh' },
  { code: 'BOG', name: '贵州银行', letter: 'G', color: '#003087', pinyin: 'gzyh2' },
]

// Group H
const banksH: BankItem[] = [
  { code: 'HSCB', name: '恒生银行', letter: 'H', color: '#C40000', pinyin: 'hsyh' },
  { code: 'HSBC', name: '汇丰银行', letter: 'H', color: '#E30613', pinyin: 'hfyh' },
  { code: 'HZB', name: '杭州银行', letter: 'H', color: '#003087', pinyin: 'hzyh' },
  { code: 'HUNAN', name: '湖南银行', letter: 'H', color: '#003087', pinyin: 'hnyh' },
]

// Group J
const banksJ: BankItem[] = [
  { code: 'JLB', name: '江苏银行', letter: 'J', color: '#003087', pinyin: 'jsyh' },
  { code: 'NBCB', name: '宁波银行', letter: 'J', color: '#003087', pinyin: 'nbyh' },
  { code: 'NJCB', name: '南京银行', letter: 'J', color: '#003087', pinyin: 'njyh' },
]

// Group M
const banksM: BankItem[] = [
  { code: 'MINTAI', name: '民泰银行', letter: 'M', color: '#003087', pinyin: 'mtyh' },
]

// Group N
const banksN: BankItem[] = [
  { code: 'NBC', name: '宁波通商银行', letter: 'N', color: '#003087', pinyin: 'nbtsyh' },
  { code: 'NYB', name: '南洋商业银行', letter: 'N', color: '#003087', pinyin: 'nysyyh' },
]

// Group S
const banksS: BankItem[] = [
  { code: 'SPAB', name: '四川天府银行', letter: 'S', color: '#003087', pinyin: 'sctfyh' },
  { code: 'SJB', name: '盛京银行', letter: 'S', color: '#003087', pinyin: 'sjyh' },
  { code: 'SZSB', name: '苏州银行', letter: 'S', color: '#003087', pinyin: 'szyh' },
  { code: 'XIB', name: '厦门国际银行', letter: 'S', color: '#003087', pinyin: 'xmgjyh' },
  { code: 'SCB', name: '渣打银行', letter: 'S', color: '#003087', pinyin: 'zdyh' },
  { code: 'SC', name: '标准银行', letter: 'S', color: '#003087', pinyin: 'bzyh' },
  { code: 'DBS', name: '星展银行', letter: 'S', color: '#003087', pinyin: 'xzyh' },
  { code: 'UBS', name: '瑞银', letter: 'S', color: '#003087', pinyin: 'ryh' },
]

// Group Y
const banksY: BankItem[] = [
  { code: 'YLB', name: '齐鲁银行', letter: 'Y', color: '#003087', pinyin: 'qlyh' },
  { code: 'YRB', name: '日照银行', letter: 'Y', color: '#003087', pinyin: 'rzyh' },
  { code: 'CAMB', name: '国泰世华银行', letter: 'Y', color: '#003087', pinyin: 'gtshyh' },
]

// Group Z
const banksZ: BankItem[] = [
  { code: 'ZJRC', name: '浙江省农村信用社', letter: 'Z', color: '#003087', pinyin: 'zjsnxs' },
  { code: 'CZB', name: '稠州银行', letter: 'Z', color: '#003087', pinyin: 'czyh' },
]

// Group #
const banksOther: BankItem[] = [
  { code: 'CITI', name: '花旗银行', letter: '#', color: '#003087', pinyin: 'hqyh' },
  { code: 'EWB', name: '华美银行', letter: '#', color: '#003087', pinyin: 'hmyh' },
  { code: 'DBSHK', name: '星展银行(香港)', letter: '#', color: '#003087', pinyin: 'xzyhxg' },
  { code: 'BOA', name: '美国银行', letter: '#', color: '#003087', pinyin: 'mgyh' },
  { code: 'JPM', name: '摩根大通', letter: '#', color: '#003087', pinyin: 'mgdt' },
  { code: 'DB', name: '德意志银行', letter: '#', color: '#003087', pinyin: 'dzyzyh' },
]

// Combine all banks
const allBanks: BankItem[] = [
  ...banksA,
  ...banksC,
  ...banksG,
  ...banksH,
  ...banksJ,
  ...banksM,
  ...banksN,
  ...banksS,
  ...banksY,
  ...banksZ,
  ...banksOther,
]

/**
 * Group banks by letter for display
 */
export const banksByLetter = (() => {
  const groups: Record<string, BankItem[]> = {}
  const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ#'.split('')

  letters.forEach(letter => {
    const group = allBanks.filter(b => b.letter === letter)
    if (group.length > 0) {
      groups[letter] = group
    }
  })

  return groups
})()

/**
 * Get all available letter groups (excluding empty ones)
 */
export const bankLetterGroups = Object.keys(banksByLetter)

/**
 * Search banks by name or pinyin
 */
export function searchBanks(keyword: string): BankItem[] {
  if (!keyword.trim()) return allBanks
  const kw = keyword.toLowerCase()
  return allBanks.filter(b =>
    b.name.includes(kw) ||
    b.code.toLowerCase().includes(kw) ||
    (b.pinyin && b.pinyin.includes(kw))
  )
}

export default allBanks
