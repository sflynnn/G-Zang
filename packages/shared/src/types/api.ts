// API相关类型定义

export interface ApiConfig {
  baseURL: string;
  timeout: number;
}

export interface RequestConfig {
  headers?: Record<string, string>;
  params?: Record<string, any>;
  data?: any;
}

// HTTP状态码
export enum HttpStatus {
  OK = 200,
  CREATED = 201,
  BAD_REQUEST = 400,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  INTERNAL_SERVER_ERROR = 500
}

// 业务状态码
export enum BusinessCode {
  SUCCESS = 0,
  PARAM_ERROR = 400,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  SERVER_ERROR = 500
}
