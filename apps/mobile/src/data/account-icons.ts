/**
 * Account icon data - comprehensive SVG icon library (no emoji)
 * Organized by category with AppleIcon name references + custom SVG paths
 */

export type AccountIconCategory =
  | 'fund'        // 资金账户
  | 'credit'      // 信用账户
  | 'payment'     // 第三方支付
  | 'investment'  // 投资账户
  | 'utility'     // 公共事业
  | 'recharge'    // 充值账户
  | 'receivable'  // 应收/应付
  | 'other'       // 其他

export interface AccountIcon {
  /** Unique identifier */
  id: string
  /** Display name */
  name: string
  /** Category */
  category: AccountIconCategory
  /**
   * Icon source:
   * - 'apple': Use AppleIcon component with this name
   * - '/static/icons/xxx.svg': Use img tag with this path
   */
  src: string
  /**
   * Fill type: 'stroke' = line icon (AppleIcon), 'solid' = solid/colored icon (SVG img)
   */
  fillType: 'stroke' | 'solid'
}

const fundIcons: AccountIcon[] = [
  { id: 'cash', name: '现金', category: 'fund', src: '/static/icons/cash.svg', fillType: 'solid' },
  { id: 'wallet', name: '钱包', category: 'fund', src: 'wallet', fillType: 'stroke' },
  { id: 'bank-card', name: '银行卡', category: 'fund', src: 'bank', fillType: 'stroke' },
  { id: 'passbook', name: '存折', category: 'fund', src: '/static/icons/passbook.svg', fillType: 'solid' },
  { id: 'cheque', name: '支票', category: 'fund', src: '/static/icons/cheque.svg', fillType: 'solid' },
  { id: 'piggy-bank', name: '储蓄罐', category: 'fund', src: 'savings', fillType: 'stroke' },
  { id: 'vault', name: '金库', category: 'fund', src: '/static/icons/vault.svg', fillType: 'solid' },
  { id: 'safe', name: '保险箱', category: 'fund', src: '/static/icons/safe.svg', fillType: 'solid' },
  { id: 'gold-bar', name: '金条', category: 'fund', src: '/static/icons/gold-bar.svg', fillType: 'solid' },
  { id: 'money-bag', name: '钱袋', category: 'fund', src: '/static/icons/money-bag.svg', fillType: 'solid' },
  { id: 'treasury', name: '国库', category: 'fund', src: '/static/icons/treasury.svg', fillType: 'solid' },
  { id: 'rmb', name: '人民币', category: 'fund', src: '/static/icons/rmb.svg', fillType: 'solid' },
  { id: 'dollar', name: '美元', category: 'fund', src: '/static/icons/dollar.svg', fillType: 'solid' },
  { id: 'euro', name: '欧元', category: 'fund', src: '/static/icons/euro.svg', fillType: 'solid' },
]

const creditIcons: AccountIcon[] = [
  { id: 'credit-card', name: '信用卡', category: 'credit', src: 'credit-card', fillType: 'stroke' },
  { id: 'unionpay-card', name: '银联卡', category: 'credit', src: '/static/icons/unionpay.svg', fillType: 'solid' },
  { id: 'visa-card', name: 'Visa卡', category: 'credit', src: '/static/icons/visa.svg', fillType: 'solid' },
  { id: 'mastercard-card', name: 'Mastercard', category: 'credit', src: '/static/icons/mastercard.svg', fillType: 'solid' },
  { id: 'amex-card', name: '运通卡', category: 'credit', src: '/static/icons/amex.svg', fillType: 'solid' },
  { id: 'ant-credit', name: '花呗', category: 'credit', src: '/static/icons/ant-credit.svg', fillType: 'solid' },
  { id: 'jd-white', name: '京东白条', category: 'credit', src: '/static/icons/jd-white.svg', fillType: 'solid' },
  { id: 'meiituan-credit', name: '美团月付', category: 'credit', src: '/static/icons/meituan-pay.svg', fillType: 'solid' },
]

const paymentIcons: AccountIcon[] = [
  { id: 'alipay', name: '支付宝', category: 'payment', src: '/static/icons/alipay.svg', fillType: 'solid' },
  { id: 'wechat-pay', name: '微信支付', category: 'payment', src: '/static/icons/wechat2.svg', fillType: 'solid' },
  { id: 'cloud-pay', name: '云闪付', category: 'payment', src: '/static/icons/cloud-pay.svg', fillType: 'solid' },
  { id: 'meituan-pay', name: '美团支付', category: 'payment', src: '/static/icons/meituan-pay.svg', fillType: 'solid' },
  { id: 'douyin-pay', name: '抖音支付', category: 'payment', src: '/static/icons/douyin-pay.svg', fillType: 'solid' },
  { id: 'qq-pay', name: 'QQ钱包', category: 'payment', src: '/static/icons/qq-pay.svg', fillType: 'solid' },
  { id: 'paypal', name: 'PayPal', category: 'payment', src: '/static/icons/paypal.svg', fillType: 'solid' },
  { id: 'apple-pay', name: 'Apple Pay', category: 'payment', src: '/static/icons/apple-pay.svg', fillType: 'solid' },
  { id: 'samsung-pay', name: 'Samsung Pay', category: 'payment', src: '/static/icons/samsung-pay.svg', fillType: 'solid' },
  { id: 'unionpay-quick', name: '银联快捷', category: 'payment', src: '/static/icons/unionpay.svg', fillType: 'solid' },
]

const investmentIcons: AccountIcon[] = [
  { id: 'stock', name: '股票', category: 'investment', src: '/static/icons/stock.svg', fillType: 'solid' },
  { id: 'fund', name: '基金', category: 'investment', src: '/static/icons/fund.svg', fillType: 'solid' },
  { id: 'bond', name: '债券', category: 'investment', src: '/static/icons/bond.svg', fillType: 'solid' },
  { id: 'futures', name: '期货', category: 'investment', src: '/static/icons/futures.svg', fillType: 'solid' },
  { id: 'btc', name: '比特币', category: 'investment', src: '/static/icons/btc.svg', fillType: 'solid' },
  { id: 'eth', name: '以太坊', category: 'investment', src: '/static/icons/eth.svg', fillType: 'solid' },
  { id: 'house', name: '房产', category: 'investment', src: 'housing', fillType: 'stroke' },
  { id: 'car', name: '车辆', category: 'investment', src: '/static/icons/car.svg', fillType: 'solid' },
  { id: 'gold', name: '黄金', category: 'investment', src: '/static/icons/gold.svg', fillType: 'solid' },
  { id: 'chart-up', name: '投资图表', category: 'investment', src: 'chart', fillType: 'stroke' },
  { id: 'piggy', name: '理财', category: 'investment', src: 'savings', fillType: 'stroke' },
  { id: 'diamond', name: '钻石', category: 'investment', src: '/static/icons/diamond.svg', fillType: 'solid' },
]

const utilityIcons: AccountIcon[] = [
  { id: 'water', name: '水费', category: 'utility', src: '/static/icons/water.svg', fillType: 'solid' },
  { id: 'electricity', name: '电费', category: 'utility', src: '/static/icons/electricity.svg', fillType: 'solid' },
  { id: 'gas', name: '燃气费', category: 'utility', src: '/static/icons/gas.svg', fillType: 'solid' },
  { id: 'heating', name: '暖气费', category: 'utility', src: '/static/icons/heating.svg', fillType: 'solid' },
  { id: 'property', name: '物业费', category: 'utility', src: '/static/icons/property.svg', fillType: 'solid' },
  { id: 'phone-bill', name: '话费', category: 'utility', src: '/static/icons/phone.svg', fillType: 'solid' },
  { id: 'internet', name: '网费', category: 'utility', src: '/static/icons/internet.svg', fillType: 'solid' },
  { id: 'tv', name: '电视费', category: 'utility', src: '/static/icons/tv.svg', fillType: 'solid' },
]

const rechargeIcons: AccountIcon[] = [
  { id: 'bus-card', name: '公交卡', category: 'recharge', src: '/static/icons/bus-card.svg', fillType: 'solid' },
  { id: 'metro-card', name: '地铁卡', category: 'recharge', src: '/static/icons/metro-card.svg', fillType: 'solid' },
  { id: 'campus-card', name: '校园卡', category: 'recharge', src: '/static/icons/campus-card.svg', fillType: 'solid' },
  { id: 'gas-card', name: '加油卡', category: 'recharge', src: '/static/icons/gas-card.svg', fillType: 'solid' },
  { id: 'member-card', name: '会员卡', category: 'recharge', src: '/static/icons/member-card.svg', fillType: 'solid' },
  { id: 'game-card', name: '游戏点卡', category: 'recharge', src: '/static/icons/game-card.svg', fillType: 'solid' },
  { id: 'gift-card', name: '礼品卡', category: 'recharge', src: '/static/icons/gift-card.svg', fillType: 'solid' },
  { id: 'vip-card', name: 'VIP卡', category: 'recharge', src: '/static/icons/vip-card.svg', fillType: 'solid' },
]

const receivableIcons: AccountIcon[] = [
  { id: 'receivable', name: '应收账款', category: 'receivable', src: '/static/icons/receivable.svg', fillType: 'solid' },
  { id: 'payable', name: '应付账款', category: 'receivable', src: '/static/icons/payable.svg', fillType: 'solid' },
  { id: 'borrow', name: '借款', category: 'receivable', src: '/static/icons/borrow.svg', fillType: 'solid' },
  { id: 'lend', name: '出借', category: 'receivable', src: '/static/icons/lend.svg', fillType: 'solid' },
  { id: 'loan', name: '贷款', category: 'receivable', src: '/static/icons/loan.svg', fillType: 'solid' },
  { id: 'mortgage', name: '房贷', category: 'receivable', src: '/static/icons/mortgage.svg', fillType: 'solid' },
  { id: 'debt', name: '债务', category: 'receivable', src: 'flag', fillType: 'stroke' },
  { id: 'invoice', name: '发票', category: 'receivable', src: '/static/icons/invoice.svg', fillType: 'solid' },
]

const otherIcons: AccountIcon[] = [
  { id: 'default', name: '默认', category: 'other', src: 'book', fillType: 'stroke' },
  { id: 'custom', name: '自定义', category: 'other', src: 'edit', fillType: 'stroke' },
  { id: 'handshake', name: '商务', category: 'other', src: '/static/icons/handshake.svg', fillType: 'solid' },
  { id: 'store', name: '商户', category: 'other', src: '/static/icons/store.svg', fillType: 'solid' },
  { id: 'company', name: '公司', category: 'other', src: '/static/icons/company.svg', fillType: 'solid' },
  { id: 'group', name: '团体', category: 'other', src: 'users', fillType: 'stroke' },
]

const allIcons: AccountIcon[] = [
  ...fundIcons,
  ...creditIcons,
  ...paymentIcons,
  ...investmentIcons,
  ...utilityIcons,
  ...rechargeIcons,
  ...receivableIcons,
  ...otherIcons,
]

export const accountIconsByCategory: Record<AccountIconCategory, AccountIcon[]> = {
  fund: fundIcons,
  credit: creditIcons,
  payment: paymentIcons,
  investment: investmentIcons,
  utility: utilityIcons,
  recharge: rechargeIcons,
  receivable: receivableIcons,
  other: otherIcons,
}

export const categoryLabels: Record<AccountIconCategory, string> = {
  fund: '资金账户',
  credit: '信用账户',
  payment: '第三方支付',
  investment: '投资账户',
  utility: '公共事业',
  recharge: '充值账户',
  receivable: '应收/应付',
  other: '其他',
}

export function getIconById(id: string): AccountIcon | undefined {
  return allIcons.find(icon => icon.id === id)
}

export function searchIcons(keyword: string): AccountIcon[] {
  if (!keyword.trim()) return allIcons
  const kw = keyword.toLowerCase()
  return allIcons.filter(icon =>
    icon.name.toLowerCase().includes(kw) ||
    icon.id.toLowerCase().includes(kw)
  )
}

export default allIcons
