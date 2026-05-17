# SynBio Crafter（合成生物学工艺）

[![Minecraft](https://img.shields.io/badge/Minecraft-26.1.2-brightgreen)](https://minecraft.net)
[![Fabric](https://img.shields.io/badge/Fabric%20Loader-%3E%3D0.18.6-orange)](https://fabricmc.net)
[![Java](https://img.shields.io/badge/Java-%3E%3D25-blue)](https://adoptium.net)
[![License](https://img.shields.io/badge/License-CC0%201.0-lightgrey)](LICENSE)

[English](README.md)

一款通过游戏化玩法教授**合成生物学**的 Minecraft 教育模组，由PekingHSC 为 **iGEM 2026** 竞赛开发。

> 🧬 提取 DNA → 🧪 组装质粒 → 🦠 转化细菌 → 💊 表达蛋白 → ⚔️ 生物强化装备

---

## 游戏流程

| 步骤 | 你在游戏里做什么 | 对应的真实科学 |
|------|------------------|----------------|
| 1. **采集** | 击杀生物获得「未知生物组织」 | 从自然界采集样本 |
| 2. **鉴定** | 将组织放入合成台，鉴定出 DNA 元件 | 基因测序与注释 |
| 3. **组装** | 在质粒组装台组合调节元件与 CDS | 分子克隆 |
| 4. **转化** | 感受态大肠杆菌 + 工程质粒 → 工程菌 | 细菌转化 |
| 5. **表达** | 工程菌 + 培养基在合成台合成 → 蛋白质 | 蛋白表达 |
| 6. **强化** | 在锻造台用纯化蛋白强化装备 | 蛋白质工程 |

---

## 功能概览

### 5 类 DNA 调节元件（各 4 个稀有度）

| 元件 | 符号 | 真实生物学作用 |
|------|------|----------------|
| **启动子（Promoter）** | →（箭头） | 启动转录 |
| **5′ UTR** | 5′ | 核糖体结合位点 |
| **3′ UTR** | 3′ | Poly-A 尾 / 终止子 |
| **增强子（Enhancer）** | ✦（星形） | 提升表达量 |
| **SINEB2** | S | 增强翻译的逆转座子 |

稀有度：绿（约 65%）→ 蓝（约 25%）→ 紫（约 8%）→ 金（约 2%）。稀有度越高，表达倍率加成越强。

### 8 种 CDS 基因（编码序列）

| 基因 | 战利品来源 | 蛋白效果（锻造强化） |
|------|------------|----------------------|
| **GFP** | 发光鱿鱼 | 诊断工具——扫描质粒 |
| **角蛋白（Keratin）** | 僵尸 | 护甲韧性 + 击退抗性 |
| **肌球蛋白（Myosin）** | 牛 | 剑伤害 / 靴子移速 |
| **端粒酶（Telomerase）** | 末影人 | 耐久 → 经验修补 → 无法破坏 |
| **蛛丝蛋白（Spider Silk）** | 蜘蛛 | 弓/弩力量附魔 |
| **荧光素酶（Luciferase）** | 烈焰人 | 头盔夜视 |
| **PETase** | 溺尸 | 方块回收（玻璃→沙子，羊毛→线） |
| **冰核蛋白（Ice Nucleation）** | 流浪者 | 靴子冰霜行者 |

### 方块与机器

- **质粒组装台** — 9 格装配站（1 个质粒核心槽 + 8 个元件槽）

### 四级培养基体系

| 等级 | 培养基 | 用途 |
|------|--------|------|
| 普通 | 铁锭 + 小麦 + 糖 | 一级蛋白 |
| 进阶 | 海晶碎片 / 发光浆果 | 二级蛋白 |
| 高级 | 末影珍珠 / 紫颂果 / 烈焰粉 | 三级蛋白 |
| 至尊 | 金苹果 | 四级蛋白 |

### 生物安全

表达蛋白质会产生副产物**废培养基**。附近废培养基 ≥10 个时获得中毒；≥30 个时额外获得反胃。将废培养基烧制成骨粉可“灭菌处理”，对应现实中的高压灭菌概念。

---

## 安装

### 环境要求

- **Minecraft** 26.1.2
- **Fabric Loader** ≥ 0.18.6
- **Fabric API**（26.1.2 对应最新版）
- **Java** ≥ 25

### 安装步骤

1. 为 Minecraft 26.1.2 安装 [Fabric Loader](https://fabricmc.net/use/)
2. 下载 [Fabric API](https://modrinth.com/mod/fabric-api)，放入 `.minecraft/mods/`
3. 从 [Releases](https://github.com/sebaWEI/PekingHSC2026MinecraftMod/releases) 下载 `synbio-1.0.0.jar`
4. 将 `synbio-1.0.0.jar` 放入 `.minecraft/mods/`
5. 使用 Fabric 配置档启动游戏

---

## 从源码构建

```bash
git clone https://github.com/sebaWEI/PekingHSC2026MinecraftMod.git
cd PekingHSC2026MinecraftMod
./gradlew build
```

构建产物：`build/libs/synbio-1.0.0.jar`

需要 JDK 25+；项目已包含 Gradle Wrapper。

---

## 生存模式入门

1. 击杀生物收集「未知生物组织」（约 25% 掉落，敌对生物约 34%）
2. 在背包或合成格中放入**单个**组织，鉴定隐藏的 DNA 元件
3. 收集 9 个同稀有度调节元件，通过合成升级稀有度（9 合 1）
4. 击杀特定生物获得 CDS 掉落（见上表）
5. 合成「质粒组装台」（8 个铁锭围一圈合成台）
6. 组装：中心放「空质粒」，周围放入 DNA 元件
7. 合成「氯化钙」（石头 + 水桶），与野生大肠杆菌合成 → 感受态菌
8. 感受态菌 + 工程质粒 → 工程化大肠杆菌
9. 工程菌 + 培养基在合成台合成 → 蛋白质 + 废培养基
10. 在锻造台使用蛋白质 → 强化装备

## 项目结构

```
src/main/java/name/modid/
├── IgemMod.java              # 模组入口
├── block/                    # 方块与方块实体
├── component/                # 数据组件与效果
├── item/                     # 物品与自定义物品类
├── loot/                     # 战利品表修改
├── recipe/                   # 自定义配方
└── screen/                   # GUI 界面处理器
```

---

## 致谢

- **开发者**：PekingHSC，iGEM 2026
- **技术栈**：[Fabric Loom](https://fabricmc.net/wiki/documentation:fabric_loom)，Minecraft 26.1.2（Mojang 映射）
- **特别感谢**：iGEM 社区，以及 Human Practices 所倡导的责任科学理念

## 许可证

CC0 1.0 Universal — 公共领域奉献。可自由用于任何用途，无需署名。
