/**
 * Credit card brand data
 * SVG icons sourced from:
 * - simple-icons (CC0-1.0) - Visa, Mastercard, AMEX, JCB, Diners, Maestro, Discover, Elo
 * - Custom SVG icons matching brand guidelines for UI consistency
 *
 * All icons use consistent 64x64 viewBox with brand-appropriate colors.
 */

export interface CreditCardBrand {
  /** Brand code */
  code: string
  /** Chinese name */
  name: string
  /** English name */
  nameEn: string
  /** Brand color */
  color?: string
  /**
   * SVG icon path relative to /static/icons/
   * null = text-based SVG placeholder (for proprietary brands without open-source SVGs)
   */
  iconPath?: string | null
}

export const creditCardBrands: CreditCardBrand[] = [
  {
    code: 'UNIONPAY',
    name: '中国银联',
    nameEn: 'UnionPay',
    color: '#E21836',
    iconPath: '/static/icons/unionpay.svg',
  },
  {
    code: 'VISA',
    name: 'Visa',
    nameEn: 'Visa',
    color: '#1A1F71',
    iconPath: '/static/icons/visa.svg',
  },
  {
    code: 'MASTERCARD',
    name: 'Mastercard',
    nameEn: 'Mastercard',
    color: '#EB001B',
    iconPath: '/static/icons/mastercard.svg',
  },
  {
    code: 'AMEX',
    name: '美国运通',
    nameEn: 'American Express',
    color: '#2E77BC',
    iconPath: '/static/icons/amex.svg',
  },
  {
    code: 'JCB',
    name: 'JCB',
    nameEn: 'JCB',
    color: '#003087',
    iconPath: '/static/icons/jcb.svg',
  },
  {
    code: 'DINERS',
    name: '大来卡',
    nameEn: 'Diners Club',
    color: '#004A97',
    iconPath: '/static/icons/diners.svg',
  },
  {
    code: 'MAESTRO',
    name: 'Maestro',
    nameEn: 'Maestro',
    color: '#0064A4',
    iconPath: '/static/icons/maestro.svg',
  },
  {
    code: 'DISCOVER',
    name: 'Discover',
    nameEn: 'Discover',
    color: '#E65C00',
    iconPath: '/static/icons/discover.svg',
  },
  {
    code: 'ELO',
    name: 'Elo',
    nameEn: 'Elo',
    color: '#FF6600',
    iconPath: '/static/icons/elo.svg',
  },
  {
    code: 'HIPERCARD',
    name: 'Hipercard',
    nameEn: 'Hipercard',
    color: '#0066CC',
    iconPath: null,
  },
  {
    code: 'MIR',
    name: 'Mir',
    nameEn: 'Mir',
    color: '#003087',
    iconPath: null,
  },
  {
    code: 'VPAY',
    name: 'V PAY',
    nameEn: 'V PAY',
    color: '#003087',
    iconPath: null,
  },
]

export default creditCardBrands
