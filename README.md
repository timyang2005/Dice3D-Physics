# 🎲 Dice3D - 3D物理骰子

一款基于 JBullet 物理引擎的 Android 3D 骰子应用，使用 OpenGL ES 3.0 渲染，支持真实的物理碰撞和投掷效果。

## ✨ 功能特性

- **3D 物理模拟** — 基于 JBullet 引擎的真实物理碰撞、弹跳和滚动
- **OpenGL ES 3.0 渲染** — Phong 光照模型、实时阴影、程序化网格地板
- **体感摇骰** — 加速度计检测摇晃手势，摇手机即可投掷
- **触控交互** — 点击投掷、拖拽旋转视角、双指缩放/平移
- **D6 骰子** — 支持 1-6 颗 D6 骰子同时投掷
- **莫兰迪色系** — 7 种莫兰迪色骰子外观可选
- **历史记录** — 自动保存每次投掷结果，Room 数据库持久化
- **音效与震动** — 投掷和碰撞时的音效与触觉反馈
- **深色模式** — 跟随系统自动切换

## 🛠 技术栈

| 层级 | 技术 |
|------|------|
| UI | Jetpack Compose + Material 3 |
| 渲染 | OpenGL ES 3.0 + GLSL |
| 物理 | JBullet (Java Bullet 移植) |
| 架构 | MVVM + StateFlow |
| 数据 | Room + DataStore |
| 构建 | Gradle + Kotlin + KSP |

## 📱 系统要求

- Android 7.0 (API 24) 及以上
- OpenGL ES 3.0 支持

## 📦 下载

从 [Releases](https://github.com/timyang2005/Dice3D-Physics/releases) 页面下载最新 APK。

## 🏗 项目结构

```
app/src/main/java/com/dice3d/
├── physics/          # JBullet 物理引擎封装
│   ├── PhysicsWorld.kt
│   ├── DiceRigidBody.kt
│   ├── DiceShapeFactory.kt
│   └── DiceResultCalculator.kt
├── renderer/         # OpenGL 渲染层
│   ├── DiceRenderer.kt
│   ├── DiceModel.kt
│   ├── TableRenderer.kt
│   ├── ShaderProgram.kt
│   ├── MatrixState.kt
│   └── ObjLoader.kt
├── viewmodel/        # ViewModel 层
├── ui/screens/       # Compose UI
├── sensor/           # 摇晃检测
├── audio/            # 音效管理
├── haptic/           # 触觉反馈
├── data/             # Room + DataStore
└── model/            # 数据模型
```

## 🔧 构建

```bash
./gradlew assembleDebug
```

APK 输出路径：`app/build/outputs/apk/debug/app-debug.apk`

## 📄 开源协议

MIT License
