# 取件码助手

一个帮助用户自动提取短信中快递取件信息的Android应用。无需联网，所有数据均存储在本地，保护用户隐私。

## 功能特点
- 📱 **自动提取信息**：智能识别并提取短信中的快递公司、取件码、驿站名称、地址和联系电话
- 📋 **卡片式展示**：以直观的卡片列表形式展示所有未取快递信息
- ✅ **交互操作**：支持左滑删除和点击标记已取功能
- 🔒 **隐私保护**：完全离线运行，所有数据仅存储在本地设备
- ⚙️ **规则自定义**：支持自定义正则表达式规则，适配不同快递公司的短信格式
- 📌 **桌面小组件**：添加桌面小组件，快速查看未取快递信息
- 🎨 **动态配色**：支持Material You动态配色，适配不同系统主题
- 🔍 **搜索功能**：支持按快递公司、取件码或驿站名称搜索快递信息

## 应用截图
<!-- 请添加应用截图 -->
| 主界面 | 规则设置 | 桌面小组件 |
|--------|----------|------------|
| ![主界面](screenshots/main.png) | ![规则设置](screenshots/settings.png) | ![桌面小组件](screenshots/widget.png) |

## 项目结构
```
app/
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/example/expresscodehelper/
│   │   │   ├── MainActivity.kt          # 主活动
│   │   │   ├── SmsReceiver.kt           # 短信接收器
│   │   │   ├── ExpressInfo.kt           # 快递信息实体类
│   │   │   ├── RegexRule.kt             # 正则规则实体类
│   │   │   ├── ExpressAdapter.kt        # 快递列表适配器
│   │   │   ├── RuleSettingActivity.kt   # 规则设置活动
│   │   │   ├── CustomRuleActivity.kt    # 自定义规则活动
│   │   │   ├── AppDatabase.kt           # 数据库实例
│   │   │   ├── ExpressDao.kt            # 数据库访问接口
│   │   │   └── ExpressWidgetProvider.kt # 桌面小组件提供者
│   │   └── res/
│   │       ├── layout/                  # 布局文件
│   │       ├── drawable/                # 图片资源
│   │       ├── values/                  # 字符串、样式等
│   │       └── xml/                     # 小组件配置等
│   └── test/                            # 测试代码
├── build.gradle                         # 模块构建文件
└── proguard-rules.pro                   # 混淆规则
.github/
└── workflows/
    └── build.yml                        # GitHub Actions工作流
build.gradle                             # 项目构建文件
settings.gradle                          # 项目设置
README.md                                # 项目说明
```

## 技术栈
- **开发语言**：Kotlin
- **UI框架**：AndroidX、Material Design
- **数据存储**：Room数据库
- **异步处理**：协程、Flow
- **构建工具**：Gradle
- **CI/CD**：GitHub Actions

## 编译和安装
1. 使用Android Studio打开项目
2. 等待Gradle同步完成
3. 连接Android设备或启动模拟器
4. 点击"Run"按钮编译并安装应用

## 使用说明
1. 首次打开应用，授予读取和接收短信权限
2. 应用会自动扫描设备上已有的短信并提取快递信息
3. 新收到的短信会被实时处理并添加到列表中
4. 点击卡片可标记快递为已取
5. 左滑卡片可删除快递信息
6. 点击设置按钮可自定义正则提取规则
7. 长按桌面可添加小组件

## 权限说明
- **读取短信权限**：用于扫描和提取已有的短信中的快递信息
- **接收短信权限**：用于实时获取新收到的短信并处理
- **存储权限**：用于保存应用数据和日志
- **小组件权限**：用于在桌面显示快递信息

## 贡献指南
欢迎各位开发者贡献代码，改进功能或修复bug。贡献步骤：
1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 开源许可
[MIT License](LICENSE)

## 联系方式
如有问题或建议，请提交Issue或联系开发者：
- Email: example@example.com
- GitHub: [@username](https://github.com/username)