# RepairKit

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![CodeQL](https://github.com/Foulest/RepairKit/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/Foulest/RepairKit/actions/workflows/github-code-scanning/codeql)
[![Downloads](https://img.shields.io/github/downloads/Foulest/RepairKit/total.svg)](https://github.com/Foulest/RepairKit/releases)

**RepairKit** is an all-in-one Java-based Windows repair and maintenance toolkit.

[Softpedia](https://softpedia.com/get/System/OS-Enhancements/RepairKit.shtml)
• [MajorGeeks](https://m.majorgeeks.com/files/details/repairkit.html)
• [Uptodown](https://repairkit.en.uptodown.com/windows)
• [AlternativeTo](https://alternativeto.net/software/repairkit/about)
• [SoftDownload](https://softdownload.com.br/repare-pc-automaticamente-repairkit.html)

## Features

### **Automatic Repairs**

Automatically perform a comprehensive system cleanup and repair, including:

- Deleting restrictive system policies
- Running registry and system tweaks
- Removing pre-installed bloatware
- Cleaning unnecessary junk files
- Repairing various disk issues
- Scanning for malware with security software

Every function in the repair is fully customizable, allowing you to enable or disable specific repairs as needed using
the configuration files located in the **config** folder. **Only modify these files if you know what you are doing.**

> **Note:** The malware scan automatically runs a quick scan with Windows Defender. In the event that Windows Defender
> is disabled or unavailable, a quick scan is performed with Sophos Scan & Clean instead.

![Automatic Repairs](https://i.imgur.com/su1R4PO.png)

### **Useful Programs**

Access essential software tools for system maintenance, including:

- **[CPU-Z](https://cpuid.com/softwares/cpu-z.html)**: Identify your system hardware details.
- **[HWMonitor](https://cpuid.com/softwares/hwmonitor.html)**: Monitor hardware temperatures and specs.
- **[Emsisoft Scan](https://emsisoft.com/en/home/emergency-kit)**: Scan for malware using Emsisoft Emergency Kit.
- **[Sophos Scan](https://sophos.com/en-us/free-tools/virus-removal-tool)**: Scan for malware using Sophos Scan & Clean.

- **[TreeSize](https://jam-software.com/treesize_free)**: Analyze and manage disk contents.
- **[Everything](https://voidtools.com)**: An ultra-fast file search engine.
- **[CrystalDiskInfo](https://sourceforge.net/projects/crystaldiskinfo)**: Monitor your disk drive's health and status.
- **[CrystalDiskMark](https://crystalmark.info/en/software/crystaldiskmark)**: Benchmark your disk drive's read/write
  speeds.

- **[Autoruns](https://learn.microsoft.com/en-us/sysinternals/downloads/autoruns)**: An alternative to Windows Startup
  Manager.
- **[Process Explorer](https://learn.microsoft.com/en-us/sysinternals/downloads/process-explorer)**: An alternative to
  Windows Task Manager.
- **[Process Monitor](https://learn.microsoft.com/en-us/sysinternals/downloads/procmon)**: Monitor system activity and
  processes.
- **[BlueScreenView](https://nirsoft.net/utils/blue_screen_view.html)**: View and analyze Windows BSOD crash dumps.

- **[Winget-AutoUpdate](https://github.com/Romanitho/Winget-AutoUpdate)**: Automatically update installed programs using
  Winget.
- **[NVCleanstall](https://techpowerup.com/download/techpowerup-nvcleanstall)**: A lightweight NVIDIA graphics card
  driver updater.
- **[DisplayDriverUninstaller](https://guru3d.com/files-details/display-driver-uninstaller-download.html)**: Remove
  display drivers and packages.

![Useful Programs (Page 1/2)](https://i.imgur.com/TivGxNG.png)

Additionally, RepairKit provides links to useful software tools, including:

- **[7-Zip](https://7-zip.org)**: Link to the open-source file archiver.

- **[Bitwarden](https://bitwarden.com/download/#downloads-web-browser)**: Link to the open-source password manager.
- **[Sophos Home](https://home.sophos.com)**: Link to the Sophos Home antivirus software.
- **[uBlock Origin](https://ublockorigin.com)**: Link to the powerful ad-blocker browser extension.
- **[TrafficLight](https://bitdefender.com/solutions/trafficlight.html)**: Link to Bitdefender's TrafficLight browser
  extension.

- **[Notepad++](https://notepad-plus-plus.org)**: Link to the open-source text editor.
- **[Twinkle Tray](https://twinkletray.com)**: Link to the monitor brightness control software.
- **[FanControl](https://getfancontrol.com)**: Link to the fan speed control software.

![Useful Programs (Page 2/2)](https://i.imgur.com/xFjfd34.png)

### **System Shortcuts**

Quickly access important Windows utilities like:

- Apps & Features
- Startup Apps
- Windows Update
- Windows Security
- Task Manager

![System Shortcuts](https://i.imgur.com/OHUhZGf.png)

## Download and Run

Not sure how to download and run RepairKit? Follow these steps:

1. Download the latest version of RepairKit from
   the [Releases page](https://github.com/Foulest/RepairKit/releases/latest) (click the `RepairKit-X.X.X.zip` file).
2. Extract the ZIP file to a folder on your computer. You can use a program like [7-Zip](https://7-zip.org) to extract
   the contents.
3. Double-click the `RepairKit-X.X.X.exe` file to run the program.
4. If Windows Defender SmartScreen blocks the app, click `More info` and then `Run anyway`.
5. Click `Yes` on the User Account Control prompt.

RepairKit will now open, and you can start using its features.

## Compiling

1. Clone the repository.
2. Open a command prompt/terminal to the repository directory.
3. Run `gradlew createExe` on Windows, or `./gradlew createExe` on macOS or Linux.
4. The built `RepairKit-X.X.X.zip` file will be in the `build` folder.

## Getting Help

For support or queries, please open an issue in the [Issues section](https://github.com/Foulest/RepairKit/issues).
