# Closetly（衣柜记录 + 今日搭配）— 零基础拿到 APK 的最短路径

这个仓库是一个可直接在 GitHub Actions 上自动打包的 Android 示例项目（本地不需要装 Android Studio）。

## 你能得到什么
- 记录衣物：拍照（系统相册选择）+ 类别 + 颜色
- 搜索：用“类别/颜色”关键词快速筛
- 今日搭配：按“温度”简单生成上衣+裤子+鞋，并可一键记录“已穿”
- 统计：总数、重复购买（按 类别+颜色）、常穿、闲置

## 如何拿到 APK（不写代码也能做）
1. 把整个项目上传到你的 GitHub 仓库（直接把文件夹内容拖进 “Add file -> Upload files” 也行）
2. 打开仓库的 **Actions** 页面
3. 点左侧工作流：**Build APK (Debug)**
4. 点 **Run workflow**（或给 main 分支提交一次也会自动跑）
5. 运行结束后，在该 Run 的页面底部 **Artifacts** 下载 `Closetly-debug-apk`
6. 解压后得到 `.apk`，传到手机安装

> 注：Debug APK 只能用于自己安装测试；要上架商店需要签名与 release 配置。

