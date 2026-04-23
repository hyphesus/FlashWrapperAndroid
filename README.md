# ⚡ FlashWrapper

An Android app that loads Flash games via Ruffle emulator in a WebView.

---

## 📱 How to Build the APK (from your phone)

### Step 1 — Create a GitHub account
Go to **github.com** and sign up (free).

### Step 2 — Create a new repository
- Tap **+** → **New repository**
- Name it: `FlashWrapper`
- Set to **Public**
- Tap **Create repository**

### Step 3 — Upload the files
- In the new repo, tap **Add file → Upload files**
- Upload ALL the files from this zip, keeping the folder structure exactly as-is
- Commit with message: `Initial commit`

> ⚠️ Important: The folder structure must be preserved exactly.
> Use a file manager app that can upload folders, or upload zip and extract.

### Step 4 — Wait for the build (~3-5 minutes)
- Go to the **Actions** tab in your repo
- You'll see a workflow running called **"Build APK"**
- Wait for the green checkmark ✅

### Step 5 — Download your APK
- Click on the completed workflow run
- Scroll down to **Artifacts**
- Tap **FlashWrapper-debug** to download the APK
- Install it on your phone (allow unknown sources in settings)

---

## 🎮 How to Use

1. Open the app
2. Paste the URL of your Flash game page
3. Tap **LAUNCH GAME**
4. The page loads with Ruffle injected automatically
5. Tap **⟳** to rotate between portrait/landscape
6. Tap **✕** to go back

---

## ⚠️ Notes

- Ruffle is loaded from CDN (`unpkg.com`) — internet required
- Not all Flash games are compatible with Ruffle (ActionScript 3 works best)
- If a game doesn't load, try finding a direct `.swf` URL instead of the game page
- The app remembers your last 5 URLs
