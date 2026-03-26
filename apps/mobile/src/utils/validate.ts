// G-Zang 移动端验证工具函数

// 手机号验证
export const isValidPhoneNumber = (phone: string): boolean => {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone.replace(/\s/g, ''))
}

// 邮箱验证
export const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

// 密码强度验证
export const validatePasswordStrength = (password: string): {
  isValid: boolean
  score: number
  message: string
} => {
  let score = 0
  let messages: string[] = []

  // 长度检查
  if (password.length >= 8) {
    score += 1
  } else {
    messages.push('密码长度至少8位')
  }

  // 大写字母检查
  if (/[A-Z]/.test(password)) {
    score += 1
  } else {
    messages.push('包含大写字母')
  }

  // 小写字母检查
  if (/[a-z]/.test(password)) {
    score += 1
  } else {
    messages.push('包含小写字母')
  }

  // 数字检查
  if (/\d/.test(password)) {
    score += 1
  } else {
    messages.push('包含数字')
  }

  // 特殊字符检查
  if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
    score += 1
  } else {
    messages.push('包含特殊字符')
  }

  const isValid = score >= 3
  const message = isValid ? '密码强度良好' : `密码强度不足：${messages.join('、')}`

  return { isValid, score, message }
}

// 金额验证
export const isValidAmount = (amount: string | number): boolean => {
  const num = typeof amount === 'string' ? parseFloat(amount) : amount
  return !isNaN(num) && num >= 0 && num <= 99999999.99
}

// 交易备注验证
export const validateTransactionRemark = (remark: string): {
  isValid: boolean
  message: string
} => {
  if (remark.length > 200) {
    return { isValid: false, message: '备注不能超过200个字符' }
  }

  // 检查特殊字符
  const specialChars = /[<>\"'&]/
  if (specialChars.test(remark)) {
    return { isValid: false, message: '备注包含非法字符' }
  }

  return { isValid: true, message: '' }
}

// 用户名验证
export const validateUsername = (username: string): {
  isValid: boolean
  message: string
} => {
  if (username.length < 3) {
    return { isValid: false, message: '用户名至少3个字符' }
  }

  if (username.length > 20) {
    return { isValid: false, message: '用户名不能超过20个字符' }
  }

  const usernameRegex = /^[a-zA-Z0-9_-]+$/
  if (!usernameRegex.test(username)) {
    return { isValid: false, message: '用户名只能包含字母、数字、下划线和连字符' }
  }

  return { isValid: true, message: '' }
}

// 表单验证器
export const createValidator = (rules: Array<{
  field: string
  required?: boolean
  validator?: (value: any) => boolean
  message: string
}>) => {
  return (data: Record<string, any>) => {
    const errors: Record<string, string> = {}

    for (const rule of rules) {
      const value = data[rule.field]

      // 必填验证
      if (rule.required && (value === undefined || value === null || value === '')) {
        errors[rule.field] = rule.message
        continue
      }

      // 自定义验证
      if (rule.validator && !rule.validator(value)) {
        errors[rule.field] = rule.message
      }
    }

    return {
      isValid: Object.keys(errors).length === 0,
      errors
    }
  }
}

// 交易表单验证器
export const validateTransactionForm = createValidator([
  {
    field: 'type',
    required: true,
    message: '请选择交易类型'
  },
  {
    field: 'amount',
    required: true,
    validator: (value) => isValidAmount(value),
    message: '请输入有效的金额'
  },
  {
    field: 'categoryId',
    required: true,
    message: '请选择分类'
  },
  {
    field: 'accountId',
    required: true,
    message: '请选择账户'
  },
  {
    field: 'transactionTime',
    required: true,
    message: '请选择交易时间'
  }
])

// 登录表单验证器
export const validateLoginForm = createValidator([
  {
    field: 'username',
    required: true,
    message: '请输入用户名'
  },
  {
    field: 'password',
    required: true,
    message: '请输入密码'
  }
])

// 注册表单验证器
export const validateRegisterForm = createValidator([
  {
    field: 'username',
    required: true,
    validator: (value) => validateUsername(value).isValid,
    message: '请输入有效的用户名'
  },
  {
    field: 'email',
    required: true,
    validator: (value) => isValidEmail(value),
    message: '请输入有效的邮箱地址'
  },
  {
    field: 'phone',
    required: true,
    validator: (value) => isValidPhoneNumber(value),
    message: '请输入有效的手机号'
  },
  {
    field: 'password',
    required: true,
    validator: (value) => validatePasswordStrength(value).isValid,
    message: '密码强度不足'
  },
  {
    field: 'confirmPassword',
    required: true,
    validator: (value, data) => value === data.password,
    message: '两次输入的密码不一致'
  }
])
