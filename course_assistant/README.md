以下是一份完整的 **README.md** 文档，适用于你的“课程智能助教软件项目”。你可以将其放置在项目根目录，并根据实际情况微调。


# 📖 课程智能助教软件

> 基于大语言模型和多模态交互的智能教学辅助系统，为高校师生提供7×24小时答疑、作业自动批改和个性化学习推荐服务。

[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)
[![Python](https://img.shields.io/badge/python-3.9%2B-blue)](https://www.python.org/)
[![Streamlit](https://img.shields.io/badge/UI-Streamlit-red)](https://streamlit.io/)
[![API](https://img.shields.io/badge/LLM-通义千问-blueviolet)](https://dashscope.aliyun.com/)

---

## ✨ 项目简介

当前高校师生比高达1:18，课后辅导压力巨大，通用AI工具常与课程内容脱节。**课程智能助教**通过集成先进的认知大模型（通义千问）和多模态交互技术（语音/公式识别），帮助学生即时解答疑难问题，自动批改编程作业，并根据错题数据推送个性化学习资源，有效减轻教师负担，提升学生学习效率。

---

## 🎯 核心功能

| 功能模块 | 描述 |
|---------|------|
| **智能答疑** | 支持文本、语音、图片（含数学公式）提问，5秒内返回详细解题步骤，回答与课程内容对齐。 |
| **作业自动批改** | 支持Python等编程作业的静态检查+单元测试，10分钟内完成50份作业批改，并生成错误报告。 |
| **个性化学习推荐** | 基于历史错题和薄弱点诊断，自动推送微课视频、同类练习题或虚拟仿真对话资源。 |
| **教师学情看板** | 可视化展示班级作业完成率、平均分、薄弱知识点分布及答疑热点词汇。 |
| **多课程切换** | 内置高等数学、计算机科学、大学物理、英语写作等课程上下文，AI回答更具针对性。 |

---

## 📝 使用说明

本应用已部署的公网
点击快速访问:https://softwareengineering-course-assistants.streamlit.app/

### 学生端
- 在主页下拉菜单中选择当前课程（如“高等数学”）。
- 通过文字、语音或上传图片（公式/题目）提问。
- 查看AI生成的分步解答，可进一步追问。
- 在“推荐学习”区域获取针对薄弱点的练习资源。
- 提交编程作业后，自动获取批改分数和错误详情。

### 教师端
- 登录后进入班级看板，查看整体学情。
- 导出作业批改报告（Excel/PDF）。
- 向班级或特定学生批量推送补充练习。

---

## 🏗️ 技术架构

| 技术分层 | 描述 |
|---------|------|
| **客户端层** | Web端（React/Vue） + 微信小程序 + App（可选） |
| **接入层** | Nginx + WebSocket + 负载均衡 |
| **业务层** | 微服务（答疑服务、推荐服务、批改服务、用户服务） |
| **算法层** | 通义千问LLM + 科大讯飞语音识别 + 推荐引擎 |
| **数据层** | MySQL（业务） + Redis（缓存） + OSS（文件存储） + Elasticsearch（日志） |

### 主要技术栈
- **后端框架**：Python FastAPI / Streamlit（演示版）
- **AI模型**：阿里云通义千问（qwen-max）
- **语音识别**：科大讯飞WebSocket API（准确率98%）
- **数据库**：MySQL 8.0 + Redis 7.0
- **文件存储**：阿里云OSS
- **前端**：React + TailwindCSS（专业版） / Streamlit（快速原型）

---

## 🚀 快速开始

### 环境要求
- Python 3.9+
- pip 21+
- 阿里云通义千问API Key（[免费申请](https://dashscope.aliyun.com/)）

### 安装步骤

1. **克隆仓库**
```bash
git clone https://github.com/your-username/course-assistant.git
cd course-assistant
```

2. **安装依赖**
```bash
pip install -r requirements.txt
```

3. **配置环境变量**  
   创建`.env`文件，填入你的API Key：
```env
DASHSCOPE_API_KEY=sk-xxxxxxxxxxxxxxx
```

4. **启动演示版（Streamlit）**
```bash
streamlit run main.py
```
   浏览器自动打开 `http://localhost:8501`

5. **（可选）启动生产版（FastAPI）**
```bash
uvicorn api.main:app --reload --port 8000
```

---

## 📂 项目结构

```
course-assistant/
├── main.py                 # Streamlit 演示主程序
├── api/                    # FastAPI 生产版
│   ├── main.py            # API入口
│   ├── routers/           # 答疑/作业/用户路由
│   └── models/            # Pydantic模型
├── core/                   # 核心业务逻辑
│   ├── llm_client.py      # 通义千问调用封装
│   ├── grader.py          # 作业自动批改引擎
│   └── recommender.py     # 个性化推荐算法
├── static/                 # 前端资源（React build输出）
├── .env                    # 环境变量（API Key）
├── requirements.txt       # Python依赖
├── README.md              # 项目说明
└── LICENSE                # 开源许可证
```

---

## 🧪 测试示例

在聊天框输入以下问题测试回答效果：

- **数学**：`请用洛必达法则求极限 lim_{x→0} (sin x)/x`
- **编程**：`解释Python中的递归函数，并给出计算斐波那契数列的例子`
- **物理**：`牛顿第二定律的微分形式是什么？`

---

## 📈 开发计划与里程碑

| 阶段 | 时间 | 任务 |
|------|------|------|
| MVP发布 | 2026年5月 | 实现基础答疑 + 作业批改原型 |
| 内测优化 | 2026年6月 | 邀请500名学生测试，收集反馈 |
| 教师端上线 | 2026年8月 | 完成学情看板、批量推送功能 |
| 多模态升级 | 2026年10月 | 集成语音识别和公式图片解析 |

---

## 🤝 贡献指南

欢迎任何形式的贡献！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交改动 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启一个 Pull Request

请确保代码符合 PEP8 规范，并补全单元测试。

---


**如果这个项目对你有帮助，请给我们一个 ⭐️ Star！**