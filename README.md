![logo](img/rhp.png)
---

The Remote Hacker Probe is a Threat Emulation and Red Teaming Framework built to be easy to use.

- The Remote Hacker Probe is Feature Rich! Including,
Keystroke Logging, Reflective DLL Injection, Reverse Shell, Password Recovery, etc.
- Remote Hacker Probe Core is open source ~~and constantly improved.~~ and is no longer being updated or worked on.
- Fast and Lightweight, Remote Hacker Probe is coded in Java meaning it will run anywhere in a JRE and Client in C++ for Native Windows.

The Remote Hacker Probe had 2 versions, Open Source and Professional Version. That have been merged into one and the source code of the Professional Version is also now open source. [For more information click here](https://quantumcored.com/index.php/2021/04/14/end-of-the-remote-hacker-probe/).

_Please do not use the Remote Hacker Probe or any Software for Black Hat Activity. The Remote Hacker Probe is made for **Authorized Penetration testing**, Demonstrations and Reasearch. The Author is not responsible for any sort of misuse or damage caused by the program._

---

### Installation & Usage
1. Download Java 11+. Most preferrably [Java 15](https://www.oracle.com/java/technologies/javase-jdk15-downloads.html).
2. Download the Zip Attached in the [Latest Release](https://github.com/quantumcored/remote_hacker_probe/releases).
3. Run the file run-on-*linux*.desktop OR run-on-*windows*.bat
4. [Getting Started with Remote Hacker Probe](https://quantumcored.com/index.php/2021/02/24/getting-started-with-the-remote-hacker-probe/) or see [Video.](https://youtu.be/5iDR0XTFtso)

---

### Server Features :
- Visually Appealing and Theme able Graphical User Interface featuring Dark, Light, Solarized Dark and Solarized Light themes.
- Built for ease and usability, Remote Hacker Probe is extremely easy to use and Set up.
- The Server is coded in Java meaning it is Cross Platform! It will run anywhere in a Java Runtime Environment (JRE).
- Event Logging.
- High Speed File Upload / Download.

### Main Features :

#### Probe Client : 

The Probe Client is a Standalone EXE containing all malicious code. This is the file used to take remote control over a Computer.

- Reflective DLL Injection (Custom + Read Output / Pass Parameters to DLL)
- Download, Upload, Delete and browse the entire file System.
- Reverse Shell, Full Access to the command line.
- Scan Remote Network for hosts.
- Port scan hosts in the Target Network.
- Scan Remote Network for hosts vulerable to Eternal Blue
- Get Process Information by Process name.
- Geolocate Client using IP Address.
- Shutdown / Restart the Remote PC.
- Grab screenshot of the Remote PC.
- Record Microphone input.
- Add to Startup Persistence on command.
- Display Message box.
- Open URLS in the default browser.
- USB Infection.
- Active Window logging.
- UAC Status shown in main table.
- Client Path shown in main table.
- Keylogger.
- Password Recovery.
  - Pidgin.
  - FileZilla.
  - Vault & IE.
  - WinSCP.
  - WiFi.
  - Credential Manager.
- Task Manager.

#### Reflective Loader Client : 

The Reflective Loader Client is stripped of most features except Reflective DLL Injection. IT establishes connection and runs Payloads in memory from the server. Using the Reflective Loader Client you have the advantage of running Completely in memory.

- Reflective Probe Payload (Runs the Probe Client in memory)
- Message Box Payload (Displays a Message Box as the process it was injected into)
- Open URL Payload (Opens a url as the process it was injected into)
- Elevation Payload (Triggers UAC Prompt for the Process it was injected into as ‘WindowsDefender.exe’)
- Add / View Windows Defender Exclusions.
- Reverse Shell.
- Task Manager.
- Reflective DLL Injection (Custom + Read Output / Pass Parameters to DLL)
---
### Tutorials and Posts
- [Getting Started with Remote Hacker Probe](https://quantumcored.com/index.php/2021/02/24/getting-started-with-the-remote-hacker-probe/)
-  [Getting Started with Remote Hacker Probe(Video)](https://youtu.be/5iDR0XTFtso)
- [Running Completely in Memory using Remote Hacker Probe’s new DLL Loader Payload](https://quantumcored.com/index.php/2021/03/11/running-completely-in-memory-using-remote-hacker-probes-new-dll-loader-payload/)
- [Beginners guide to Reflective DLL Injection](https://quantumcored.com/index.php/2021/03/26/beginners-guide-to-reflective-dll-injection/) to write your own dlls. 
- [V.2 Changelogs](https://quantumcored.com/index.php/2021/04/13/remote-hacker-probe-v-2-changelogs/)
- [End of Remote Hacker Probe](https://quantumcored.com/index.php/2021/04/14/end-of-the-remote-hacker-probe/)
---
### Bugs
- Remote Shell Upload Vulnerability in RHP Server, Fixed. :heavy_check_mark:
- Unauthorized File Upload to RHP Server, Fixed. :heavy_check_mark:
- Reflective Loader fails. :heavy_check_mark:
---

### Screenshots
![1](img/pic.PNG)


### Thanks to 
- [Passcat Project.](https://github.com/twelvesec/passcat)
- Swing Authors.
- [@stephenfewer](https://github.com/stephenfewer/) (https://github.com/stephenfewer/ReflectiveDLLInjection)
- [@bhassani](https://github.com/bhassani/) (https://github.com/bhassani/EternalBlueC)
#### Developer
Hi my name's [Fahad](https://github.com/quantumcore).
You may contact me, on [Discord](https://discordapp.com/invite/8snh7nx) or [My Website](https://quantumcored.com/)

#### LICENSE
[VIEW LICENSE](https://github.com/quantumcored/remote_hacker_probe/blob/main/LICENSE) 

The Developer is not responsible for any misuse or Damage caused by the program. This is created only to innovate InfoSec and **YOU**. :point_left:

#### Donate
Help me with my future projects. Thank you.
[Donate with Crypto](https://commerce.coinbase.com/checkout/cebcb394-f73e-4990-98b9-b3fdd852358f)
