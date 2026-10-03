package com.example.signal.ui.theme

import androidx.compose.ui.graphics.Color

// ── Echo palette (mint / sage / green) ──────────────────────
val EchoMintLight      = Color(0xFFE8F5E9)   // very light mint background
val EchoMint           = Color(0xFFB2DFBC)   // stat card / chip / accent fill
val EchoMintBright     = Color(0xFF81C995)   // brighter mint for decorative circles
val EchoDarkGreen      = Color(0xFF1B3A2D)   // capture card bg
val EchoDarkGreenLight = Color(0xFF2D5A45)   // capture card lighter shade
val EchoBlack          = Color(0xFF1A1A1A)   // near-black text & bottom nav
val EchoDeleteRed      = Color(0xFFFFCDD2)   // soft red delete chip bg
val EchoDeleteRedText  = Color(0xFFC62828)   // delete chip text

// ── Surfaces ────────────────────────────────────────────────
val EchoBackground     = Color(0xFFF1F8E9)   // page-level background
val EchoSurface        = Color(0xFFFFFFFF)   // cards
val EchoSurfaceSoft    = Color(0xFFF5F5F5)   // subtle card tint

// ── Text ────────────────────────────────────────────────────
val EchoTextPrimary    = Color(0xFF1A1A1A)
val EchoTextSecondary  = Color(0xFF616161)
val EchoTextMuted      = Color(0xFF9E9E9E)

// ── Borders ─────────────────────────────────────────────────
val EchoBorder         = Color(0xFFE0E0E0)

// ── Backward-compatible aliases so existing code still compiles ──
val SignalBlue         = EchoDarkGreen
val SignalBlueBright   = EchoMintBright
val SignalViolet       = EchoMint
val SignalPurple       = EchoDarkGreen
val SignalLime         = EchoMintBright
val SignalMint         = EchoMint

val SignalBackground   = EchoBackground
val SignalSurface      = EchoSurface
val SignalSurfaceSoft  = EchoSurfaceSoft

val SignalTextPrimary   = EchoTextPrimary
val SignalTextSecondary = EchoTextSecondary
val SignalTextMuted     = EchoTextMuted

val SignalBorder       = EchoBorder

// Source colors (kept for any legacy reference)
val InstagramPink = Color(0xFFFF4F81)
val YouTubeRed    = Color(0xFFFF0033)
val WebBlue       = Color(0xFF4285F4)
val FileOrange    = Color(0xFFFF9F43)