# Publish this repo to GitHub

The portfolio links to `https://github.com/KwBuansi/Old-Projects`. Create the empty repo on GitHub, then push from this folder.

## Steps

1. On GitHub: **New repository** → name: `Old-Projects` → Public → **do not** add README (this folder already has one).
2. In PowerShell:

```powershell
cd "c:\Users\Kwasi_Davidson\Downloads\Old\Old-Projects"
git remote add origin https://github.com/KwBuansi/Old-Projects.git
git branch -M main
git push -u origin main
```

If `origin` already exists:

```powershell
git push -u origin main
```

After pushing, portfolio project cards that point at `Old-Projects/tree/main/...` will work.
