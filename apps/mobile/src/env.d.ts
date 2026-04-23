/**
 * G-Zang Mobile Type Declarations
 * Augments third-party library types for compatibility
 */

/// <reference types="@dcloudio/types" />

// Declare wx global for WeChat miniprogram
declare const wx: WechatMiniprogram.Wx

interface Window {
  wx?: WechatMiniprogram.Wx
}

// Augment uni-ui component types to accept CSS variable color strings
