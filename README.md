# jTTPlayer

---

## 简介

基于JavaFX + mpv / bass 开发的音乐播放器，尝试复刻千千静听。

---

## 版权声明
<b>本项目除源码外，其他所有版权归`千千静听`所有</b>

## 开发/测试环境

- macOS Monterey 、Windows 10 x64 (虚拟机)
- [Zulu Java](https://www.azul.com/downloads/#zulu) 1.8.0_482 (带JavaFX版本)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/other/) 2023.2.8 (社区版)
- [Gradle](https://gradle.org/) 8.12.1

## 截图预览
<img src="https://gitee.com/rive08/resources/raw/master/jTTPlayer/temp/snap-03.png" alt="snap-03" width="128">
<img src="https://gitee.com/rive08/resources/raw/master/jTTPlayer/temp/snap-01.png" alt="snap-01" width="128">
<img src="https://gitee.com/rive08/resources/raw/master/jTTPlayer/temp/snap-02.png" alt="snap-02" width="128">
<img src="https://gitee.com/rive08/resources/raw/master/jTTPlayer/temp/snap-04.png" alt="snap-04" width="128">
<img src="https://gitee.com/rive08/resources/raw/master/jTTPlayer/temp/snap-05.png" alt="snap-05" width="128">
<img src="https://gitee.com/rive08/resources/raw/master/jTTPlayer/temp/snap-06.png" alt="snap-06" width="128">

## 核心功能

- <b>界面</b>：播放器主界面、播放列表、歌词页、均衡器、迷你模式、菜单等
- <b>皮肤系统</b>：不支持`原版.skn`文件，需要`移植`；请参阅：[Wiki - 皮肤](Wiki.md)
- <b>播放器内核</b>：目前支持mpv、bass，后续可能考虑插件方式扩展
- <b>频谱</b>：依赖播放器内核，mpv不支持
- <b>均衡器</b>：目前仅支持10段均衡器；依赖播放器内核，bass不支持
- <b>音频Tag标签编辑</b>：`平台兼容性`，macOS完全支持；Windows暂不支持
- <b>桌面歌词</b>：部分支持
  
## For普通用户

- `使用时遇到问题`，请优先参阅文档：[Wiki.md](Wiki.md)
- 安装版本选择，请参阅下文：`下载说明`
- <b>下载安装后，需配置`千千选项 - 播放 - 播放器内核`才可以正常播放</b>
- 皮肤设置：`千千选项 - 皮肤 - 皮肤根目录`
- 千千选项：播放器主界面、桌面歌词、迷你模式、播放器`Logo`等，右键唤出主菜单，点击`主菜单 - 千千选项`
- 播放器内核、皮肤资源获取：请前往[资源库项目](https://github.com/GeekLee2012/jTTPlayer-Resources.git)下载
- 播放器内核`路径设置`：解压得到bass目录后，目录下有`不同系统平台子目录`，请选择`与自己的平台相应`目录
- mpv内核：不支持`频谱`；请参阅：[Wiki - 播放器内核 - mpv](Wiki.md) 
- bass内核：不支持`10段均衡器`；请参阅：[Wiki - 播放器内核 - bass](Wiki.md)
- 皮肤设置：`千千选项 - 皮肤 - 皮肤根目录`
- 播放器主菜单 - 查看：`打开/关闭`播放列表、均衡器、歌词秀、音乐窗、迷你模式等
- 关闭`迷你模式`：部分皮肤在切换至迷你模式后，无`返回正常模式`按钮。点击`播放器主菜单 - 查看 - 迷你模式`，即可返回`正常模式`

## 下载说明

> 目前仅支持macOS、Windows平台，暂不支持Linux平台  

`安装包命名规范`：jTTPlayer - <平台> - [架构] - [Bundle/Folder] - [Mini] - <版本号>.<文件后缀>  
`注意`：普通用户一般建议下载`非 Mini`版本；当然，若想尝试`Mini`版本，试一试也无妨！

### 规范
- <>表示一定存在；[]表示不一定存在
- 平台：包括macOS、Windows、Linux等
- 架构：`若不标明，即为通用指令架构`；通常为x86、x86_64、x64、amd64、arm64、aarch64等，具体请自行AI
- Bundle：单文件安装包/程序包；安装/解压缩后为.app、.exe的单文件程序
- Folder：目录安装包；安装后，会在`安装目录`下生成目录；或解压缩后生成目录；程序文件包含在该目录里面
- macOS-xx-Bundle：<b>`不支持macOS 14及以上`</b>  
- macOS-xx-Folder：<b>`macOS 14及以上请选择该安装包`</b>
- Mini：见名知意，文件大小`相对迷你`，`不包含运行环境`；用户`须自行配置JDK/JRE`运行环境后，方可运行
- zip压缩包：一般为`免安装`版本，即解压缩后，在JDK/JRE运行环境有效时，无需再进行安装可直接运行

---

> 推荐下载（普通用户）
### Windows
-  jTTPlayer-Windows-x64-Folder-<版本号>.exe

### macOS < 14
- jTTPlayer-macOS-Universal-Bundle-<版本号>.dmg
- jTTPlayer-macOS-Universal-Folder-<版本号>.dmg

### macOS >= 14
- jTTPlayer-macOS-Universal-Folder-<版本号>.dmg

### macOS amd64（Intel芯片版本）
- jTTPlayer-macOS-amd64-Bundle-<版本号>.dmg

## Mini版本（进阶用户、开发者）  
> <b>`注意`：JDK/JRE必须为`带JavaFX 1.8.0_xxx`版本，非1.8版本将无法运行</b> 

Mini版本为作者自研，并移除JRE后打包的版本；若按下面方式配置仍无法使用，请下载非Mini的完整版本  


### Windows
在Windows平台上，设置了JDK/JRE运行环境`固定的查找顺序`，如图所示：  
<img src="https://gitee.com/rive08/resources/raw/master/jTTPlayer/temp/jre-search-sequence.png" alt="JRE查找顺序" width="258">  
> 翻译一下，`JRE查找顺序`： 
1. 同级jre目录：目录名称为`jre`，且与jTTPlayer.exe在`相同目录`下
2. 环境变量：变量名称为`TTP_JAVA`、或者`TTP_JDK`；专为jTTPlayer自定义的，配置其中一个即可
3. 注册表、标准路径（安装JDK/JRE时的默认路径）
4. 环境变量：变量名称为`JAVA_HOME`、或者`JDK_HOME`

> 了解`JRE查找顺序`后，该如何配置？思路如下：
- 选择上面顺序的其中一个方式设置，让jTTPlayer知道上哪里找到JRE即可
- (不推荐)`顺序1`的方式，大概可直接忽略，采用同级jre目录，这和直接使用`非Mini`版本区别不大
- (推荐)`顺序2`的方式，配置环境变量`TTP_JAVA`、或者`TTP_JDK`；两个环境变量选一个配置即可
- Windows环境变量配置，请自行AI
- (不推荐) `顺序3`的方式，配置注册表，相对复杂，也容易出错，一般用户搞不定
- (推荐、也不推荐)`顺序4`的方式，若环境变量`JAVA_HOME`或者`JDK_HOME`指向`带JavaFX 1.8.0_xxx`版本则推荐，反之不推荐

> <b>简而言之，在Windows上，配置环境变量`TTP_JAVA`即可，变量值为JDK/JRE的`根目录`路径。</b>


### macOS
在macOS平台上，`JRE查找顺序`也是适用的，具体顺序内容参阅`上面Windows小节`。   
虽然并不支持配置环境变量的方式，但可通过`修改配置文件`，指定JDK/JRE路径。  
不同安装包、压缩包，所需要修改的配置文件、文件内容都不同。

#### amd64-Bundle-Mini 压缩包
- 解压缩，得到jTTPlayer.app
- 找到配置文件： jTTPlayer.app --> 右键菜单 --> Show Package Contents（显示包内容）
  --> Contents --> Java -->  jTTPlayer.cfg
- 用文本编辑器，打开配置文件`jTTPlayer.cfg`。修改指定内容，并保存即可。找到内容：
```
app.runtime=$APPDIR/PlugIns/Java.runtime
```
修改为：
```
app.runtime=自己本机上的带JavaFX的JDK/JRE 1.8路径
```


#### Universal-Bundle-Mini 压缩包
- 解压缩，得到jTTPlayer.app
- 找到配置文件： jTTPlayer.app --> 右键菜单 --> Show Package Contents（显示包内容）
    --> Contents --> Info.plist
- 用文本编辑器，打开配置文件`Info.plist`。找到内容：
```
<key>SearchSequence</key>
<array>
```
在`<array>行`下面，插入新行，保存即可。
```
<key>SearchSequence</key>
<array>
<string>R自己本机上的带JavaFX的JDK/JRE 1.8路径</string>
```
插入行，内容格式为：`<string>R【指定路径】</string>`。  
`注意`：`<string>`后面千万别漏了`R`字母。


#### Universal-Folder-Mini 压缩包
- 解压缩后，得到jTTPlayer目录，找到目录下的jTTPlayer.app
- 其余步骤，同上`Universal-Bundle-Mini 压缩包`

---

## 致谢
- 界面设计：千千静听
- 播放器内核：mpv、bass
- 开源项目：jaudiotagger、jackson、pinyin4j、opencc4j、okhttp、NativeBass等
- 千千静听皮肤以及相关的作者们
  
## 赞赏支持

<div>
  <p>若您觉得本项目有所帮助，欢迎扫描下方二维码赞赏，感谢支持 ~</p>
  <img src="https://gitee.com/rive08/resources/raw/master/jTTPlayer/temp/mm-reward-qrcode.png" alt="赞赏码" width="258">
</div>
